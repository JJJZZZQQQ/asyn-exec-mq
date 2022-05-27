package com.github.jjjzzzqqq.asynexecmq.kafka.consumer;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.jjjzzzqqq.asynexecmq.entity.Credit;
import com.github.jjjzzzqqq.asynexecmq.entity.Enroll;
import com.github.jjjzzzqqq.asynexecmq.service.ICourseService;
import com.github.jjjzzzqqq.asynexecmq.service.ICreditService;
import com.github.jjjzzzqqq.asynexecmq.service.IEnrollService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.github.jjjzzzqqq.asynexecmq.common.KafkaConstant.TOPIC_CREDIT;

@Component
public class CreditListener {

    private Logger logger = LoggerFactory.getLogger(CreditListener.class);

    @Resource
    private IEnrollService enrollService;

    @Resource
    private ICreditService creditService;

    @Resource
    private ICourseService courseService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 注意：此处由于有重复消息，可能有
     */
    @KafkaListener(topics = TOPIC_CREDIT, groupId = "rushClass")
    public void onMessage(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        Optional<?> message = Optional.ofNullable(record.value());

        // 1. 判断消息是否存在
        if (!message.isPresent()) {
            return;
        }

        String lockKey = "course";
        // 2. 处理 MQ 消息
        try {

            //1.转换对象
            Long courseId = JSONUtil.toBean((String) message.get(), Long.class);
            //查出所有报名记录
            lockKey = "course:" + courseId;

            LambdaQueryWrapper<Enroll> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Enroll::getCourseId, courseId);
            List<Enroll> enrollList = enrollService.list(queryWrapper);

            //3.消费

            //3.1
            //由于消息存在重复发送的情况，所以可能出现多个消费者并发消费一条重复的消息
            //对该课程id加分布式锁，防止两个消费者并发消费同一条重复的消息，导致判重逻辑失效
            //如
            //A:判断是否存在记录，不存在
            //B:判断是否存在记录，不存在
            //A:插入数据
            //B:插入数据
            //这种情况我们只需要保证一个消费者消费一条相同的消息即可，而消息是否相同就是由courseId区分的
            redisTemplate.opsForValue().setIfAbsent(lockKey, 1, 1, TimeUnit.HOURS);
            int score = courseService.getById(courseId).getCredit();
            for (Enroll enroll : enrollList) {
                Long userId = enroll.getUserId();
                QueryWrapper<Credit> countWrapper = new QueryWrapper<>();
                //判断是否存在该条记录,幂等处理,也可以通过记录一张消费courseId表的方式来判重,也可以通过Redis SetNx命令来判重
                int count = creditService.count(countWrapper.eq("course_id", courseId).eq("user_id", userId));
                if(count > 0) {
                    continue;
                }

                Credit credit = new Credit();
                credit.setCourseId(courseId);
                credit.setUserId(userId);
                credit.setSource("选修课");
                credit.setScore(score);
                creditService.save(credit);
            }

            // 4. 消息消费完成
            ack.acknowledge();
        } catch (Exception e) {
            // 学分添加环节失败，消费者会进行重试
            logger.error("消费MQ消息，失败 topic：{} message：{}", topic, message.get());
            throw e;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

}
