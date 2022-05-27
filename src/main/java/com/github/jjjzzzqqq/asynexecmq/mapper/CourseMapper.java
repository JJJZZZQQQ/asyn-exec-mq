package com.github.jjjzzzqqq.asynexecmq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.jjjzzzqqq.asynexecmq.entity.Course;
import org.apache.ibatis.annotations.Param;

public interface CourseMapper extends BaseMapper<Course> {

    Integer updateMqState(@Param("courseId") Long courseId, @Param("mqState") Integer mqState);

}