package com.github.jjjzzzqqq.asynexecmq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.jjjzzzqqq.asynexecmq.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface CourseMapper extends BaseMapper<Course> {

    Integer updateMqState(@Param("courseId") Long courseId, @Param("mqState") Integer mqState);

    List<Long> getCourseIdListByMqFail(@Param("beginId") Long beginId , @Param("size") Integer size);

    Long getMaxId();
}