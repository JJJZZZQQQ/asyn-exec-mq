package com.github.jjjzzzqqq.asynexecmq.kafka.producer;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

import static com.github.jjjzzzqqq.asynexecmq.common.KafkaConstant.TOPIC_CREDIT;

@Component
public class CreditProducer {

    private Logger logger = LoggerFactory.getLogger(CreditProducer.class);

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;


    /**
     * 发送课程积分处理消息
     *
     * @param
     */
    public ListenableFuture<SendResult<String, Object>> sendCredit(Long courseId) {
        logger.info("课程结束后MQ发送 courseId = {}" ,courseId);
        return kafkaTemplate.send(TOPIC_CREDIT, JSONUtil.toJsonStr(courseId));
    }

}
