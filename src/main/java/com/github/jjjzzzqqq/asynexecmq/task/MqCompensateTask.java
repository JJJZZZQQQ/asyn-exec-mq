package com.github.jjjzzzqqq.asynexecmq.task;

import com.github.jjjzzzqqq.asynexecmq.common.MQState;
import com.github.jjjzzzqqq.asynexecmq.kafka.producer.CreditProducer;
import com.github.jjjzzzqqq.asynexecmq.mapper.CourseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 1.定时任务扫描库表，补偿发送MQ
 * <br>
 * 2.为防止MySQL深分页导致的慢SQL
 *   利用Id自增的特性,通过where条件筛选，保证每次的扫描行数在常数级别
 *
 * @author jjjzzzqqq.github.io
 * @since  2022/5/28  19:42
 */
@Component
public class MqCompensateTask {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private CreditProducer creditProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(MqCompensateTask.class);

    private final static String LOCK_KEY = "scan_course_task";

    /**
     * 每天3，8，15，20点执行一次
     */
    @Scheduled(cron = "0 0 3,8,15,20 * * ? ")
    public void scanCourseDbByMqState() {
        try {
            redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, 1, 30, TimeUnit.MINUTES);

            //1.获取表中最大的courseId
            Long maxId = courseMapper.getMaxId();
            Long beginId = 0L;

            //2.循环遍历，直到beginId超过 maxId,为了防止MySQL全表扫描深分页导致的慢查询
            while (beginId < maxId) {
                List<Long> idList = courseMapper.getCourseIdListByMqFail(beginId, 500);
                reissueMQ(idList);
                //重置beginId
                beginId = idList.get(idList.size() - 1);
            }

        } catch (Exception e) {
            LOGGER.error("扫描course表，补偿MQ消息定时任务执行失败 e = {}", e.getMessage());
        } finally {
            redisTemplate.delete(LOCK_KEY);
        }
    }

    public void reissueMQ(List<Long> courseIdList) {
        for (Long courseId : courseIdList) {
            ListenableFuture<SendResult<String, Object>> future = creditProducer.sendCredit(courseId);
            future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                @Override
                public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                    //2.1 update mqState = 1
                    courseMapper.updateMqState(courseId, MQState.SUCCESS.getCode());
                }

                @Override
                public void onFailure(Throwable throwable) {
                    //2.2 update mqState = 2
                    courseMapper.updateMqState(courseId, MQState.FAIL.getCode());
                }
            });
        }
    }
}
