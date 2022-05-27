package com.github.jjjzzzqqq.asynexecmq.common;

public enum CourseState {

    WAIT("未开始", 0),
    ENROLL("报名中", 1),
    OPEN("进行中", 2),
    FINISH("已结束", 3);


    String desc;

    Integer code;

    CourseState(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
