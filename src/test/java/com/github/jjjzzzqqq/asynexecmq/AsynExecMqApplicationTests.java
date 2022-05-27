package com.github.jjjzzzqqq.asynexecmq;

import cn.hutool.json.JSONUtil;
import com.github.jjjzzzqqq.asynexecmq.entity.Course;
import com.github.jjjzzzqqq.asynexecmq.entity.Credit;
import com.github.jjjzzzqqq.asynexecmq.entity.User;
import com.github.jjjzzzqqq.asynexecmq.mapper.CourseMapper;
import com.github.jjjzzzqqq.asynexecmq.mapper.UserMapper;
import com.github.jjjzzzqqq.asynexecmq.service.ICourseService;
import kafka.utils.Json;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.Resource;
import java.util.Date;

import static com.github.jjjzzzqqq.asynexecmq.common.KafkaConstant.TOPIC_CREDIT;

@SpringBootTest
class AsynExecMqApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ICourseService courseService;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Resource
    private CourseMapper courseMapper;
    @Test
    void contextLoads() {
    }

    @Test
    void mybatisPlusMapperTest() {
        User user = new User();
        user.setUserId(1L);
        user.setUserName("jiangziqi");
        user.setNickName("小迪加");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insert(user);
    }

    @Test
    void mybatisPlusServiceTest() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setCredit(1);
        course.setNumber(100);
        course.setName("Java核心技术");
        course.setSurplusNumber(100);
        course.setState(0);
        courseService.save(course);
    }

    /*@Test
    void kafkaTest() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setCredit(1);
        course.setNumber(100);
        course.setName("Java核心技术");
        course.setSurplusNumber(100);
        course.setState(0);
        kafkaTemplate.send(TOPIC_CREDIT, JSONUtil.toJsonStr(course));
    }*/

    @Test
    void updateMqState() {
        courseMapper.updateMqState(1L,1);
    }
}
