package com.luckyzj.community.dao;

import com.luckyzj.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository//只用Mapper也可以，由于idea会报红加的Repository
@Deprecated//不推荐使用，重构引入redis
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectLoginTicket(String ticket);

    int updateStatus(String ticket,int status);
}
