package com.github.jjjzzzqqq.asynexecmq.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jjjzzzqqq.asynexecmq.common.CourseState;
import com.github.jjjzzzqqq.asynexecmq.common.MQState;
import com.github.jjjzzzqqq.asynexecmq.entity.Course;
import com.github.jjjzzzqqq.asynexecmq.kafka.producer.CreditProducer;
import com.github.jjjzzzqqq.asynexecmq.mapper.CourseMapper;
import com.github.jjjzzzqqq.asynexecmq.mapper.CreditMapper;
import com.github.jjjzzzqqq.asynexecmq.service.ICourseService;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    private CreditProducer creditProducer;

    @Resource
    private CourseMapper courseMapper;

    @Override
    public boolean finishCourse(Long courseId) {

        //1.update course state
        LambdaUpdateWrapper<Course> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Course::getCourseId, courseId).set(Course::getState, CourseState.FINISH.getCode());
        boolean update = this.update(wrapper);

        if (!update) {
            return false;
        }

        //2.发送积分MQ
        ListenableFuture<SendResult<String, Object>> future = creditProducer.sendCredit(courseId);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //2.1 update mqState = 1
                courseMapper.updateMqState(courseId,MQState.SUCCESS.getCode());
            }
            @Override
            public void onFailure(Throwable throwable) {
                //2.2 update mqState = 2
                courseMapper.updateMqState(courseId,MQState.FAIL.getCode());
            }
        });

        return true;
    }
}
