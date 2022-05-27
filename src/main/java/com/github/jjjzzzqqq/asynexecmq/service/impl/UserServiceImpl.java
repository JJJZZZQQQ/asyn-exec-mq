package com.github.jjjzzzqqq.asynexecmq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jjjzzzqqq.asynexecmq.entity.User;
import com.github.jjjzzzqqq.asynexecmq.mapper.UserMapper;
import com.github.jjjzzzqqq.asynexecmq.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
