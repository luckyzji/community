package com.luckyzj.community.dao;

import com.luckyzj.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository//@Mapper一个也可用，idea会报红色
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //@Param用于起别名，如果只有一个参数，并且在<if>里面使用，必须起别名
    int selectDiscussPostRows(@Param("userId") int uerId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);
}
