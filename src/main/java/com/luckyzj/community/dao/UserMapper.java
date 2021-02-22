package com.luckyzj.community.dao;

import com.luckyzj.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author ZJ
 * @Date 2021-01-30
 */
@Mapper
@Repository
public interface UserMapper {
    User selectById(int id);
    User selectByUsername(String username);
    User selectByEmail(String email);

    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id,String password);

}
