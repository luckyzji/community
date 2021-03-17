package com.luckyzj.community.utils;


public class RedisKeyUtil {
    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER ="follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    /**
     * 某个实体的赞
     * like:entity:entityType:entityId ->set(userId)
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }


    /**
     * 某个用户的赞
     * like:user:userId -> int
     * @param userId
     * @return
     */
   public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
   }

    /**
     * 某个用户关注的实体
     * followee:user:userId:entityType - > zset(entityId,now)
     * @param userId
     * @param entityType
     * @return
     */
   public static String getFolloweeKey(int userId,int entityType){
       return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
   }

    /**
     * 某个实体拥有的粉丝
     * follower:entityType:entityId -> zset(userId,now)
     * @param entityType
     * @param entityId
     * @return
     */
   public static String getFollowerKey(int entityType,int entityId){
       return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
   }

    /**
     * 登录验证码的redisKey
     * @param owner
     * @return
     */
   public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
   }


    /**
     * 登录凭证
     * @param ticket
     * @return
     */
   public static String getTicketKey(String ticket){
       return PREFIX_TICKET+SPLIT+ticket;
   }

    /**
     * 用户
     * @param userId
     * @return
     */
   public static String getUserIdKey(int userId){
       return PREFIX_USER+SPLIT+userId;
   }

}
