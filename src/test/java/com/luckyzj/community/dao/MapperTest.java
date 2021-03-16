package com.luckyzj.community.dao;

import com.luckyzj.community.CommunityApplication;
import com.luckyzj.community.entity.DiscussPost;
import com.luckyzj.community.entity.LoginTicket;
import com.luckyzj.community.entity.Message;
import com.luckyzj.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @Author ZJ
 * @Date 2021-01-31
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelect(){
        User user = userMapper.selectById(150);
        System.out.println(user);

        user=userMapper.selectByUsername("liubei");
        System.out.println(user);

        user=userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void insert(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setEmail("test@qq.com");
        user.setSalt("abd");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int row=userMapper.insertUser(user);
        System.out.println(row);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int row =userMapper.updateStatus(150,1);
        System.out.println(row);

        row = userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(row);

        row = userMapper.updatePassword(150,"hello");
        System.out.println(row);

    }

    @Test
    public void testSelectPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post :
                list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testLoginTicketInsert(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("123456");
        loginTicket.setUserId(151);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testLoginTicketSelect(){
        LoginTicket loginTicket=loginTicketMapper.selectLoginTicket("123456");
        System.out.println(loginTicket);
    }

    @Test
    public void testLoginTicketUpdate(){
        loginTicketMapper.updateStatus("123456",1);
    }


    @Test
    public void testSelectLetters(){
        List<Message> list=messageMapper.selectConversations(111,0,20);
        for(Message message : list){
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list=messageMapper.selectLetters("111_112",0,10);
        for(Message message : list){
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);
    }
}
