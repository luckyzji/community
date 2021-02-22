package com.luckyzj.community.service;

import com.luckyzj.community.dao.DiscussPostMapper;
import com.luckyzj.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ZJ
 * @Date 2021-02-01
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userID){
        return discussPostMapper.selectDiscussPostRows(userID);
    }
}
