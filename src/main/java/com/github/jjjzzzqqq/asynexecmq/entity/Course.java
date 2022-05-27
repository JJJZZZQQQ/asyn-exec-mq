package com.github.jjjzzzqqq.asynexecmq.entity;

import com.github.jjjzzzqqq.asynexecmq.mapper.UserMapper;
import lombok.Data;

import javax.annotation.Resource;

/**
 * @author jjjzzzqqq.github.io
 * @since  2022/5/27  19:42
 */
@Data
public class Course {

    /**
     * 课程id
     */
    private Long courseId;

    private String name;

    /**
     * 对应学分
     */
    private Integer credit;

    /**
     * 课程可报名总人数
     */
    private Integer number;

    /**
     * 课程剩余报名人数
     */
    private Integer surplusNumber;

    /**
     * 课程状态（0 未开始，1 报名中，2进行中，3已结束）
     */
    private Integer state;
}
