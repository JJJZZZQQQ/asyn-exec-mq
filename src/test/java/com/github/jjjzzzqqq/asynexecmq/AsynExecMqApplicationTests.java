package com.github.jjjzzzqqq.asynexecmq;

import com.github.jjjzzzqqq.asynexecmq.entity.Course;
import com.github.jjjzzzqqq.asynexecmq.entity.User;
import com.github.jjjzzzqqq.asynexecmq.mapper.UserMapper;
import com.github.jjjzzzqqq.asynexecmq.service.ICourseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
class AsynExecMqApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ICourseService courseService;


    @Test
    void contextLoads() {
    }

    @Test
    void mybatisPlusMapperTest(){
        User user = new User();
        user.setUserId(1L);
        user.setUserName("jiangziqi");
        user.setNickName("小迪加");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insert(user);
    }

    @Test
    void mybatisPlusServiceTest(){
        Course course = new Course();
        course.setCourseId(1L);
        course.setCredit(1);
        course.setNumber(100);
        course.setName("Java核心技术");
        course.setSurplusNumber(100);
        course.setState(0);
        courseService.save(course);
    }
}
