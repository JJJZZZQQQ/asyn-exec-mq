package com.github.jjjzzzqqq.asynexecmq.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jjjzzzqqq.asynexecmq.entity.Course;

public interface ICourseService extends IService<Course> {
    /**
     * 结束课程
     */
    public boolean finishCourse(Long courseId);
}
