package com.luckyzj.community.service;


import com.luckyzj.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void like(int userId,int entityType,int entityId,int entityUserId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember=redisOperations.opsForSet().isMember(entityLikeKey,userId);
                redisOperations.multi();
                if(isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }
                else{
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                return redisOperations.exec();
            }
        });
    }

    /**
     * 查询某实体的点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人对实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0;
    }

    /**
     * 查询某个用户获得的赞
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count=(Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count==null?0:count.intValue();
    }

}
