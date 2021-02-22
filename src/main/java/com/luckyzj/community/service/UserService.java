package com.luckyzj.community.service;

import com.luckyzj.community.dao.UserMapper;
import com.luckyzj.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author ZJ
 * @Date 2021-02-01
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
