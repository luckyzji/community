package com.luckyzj.community.controller;

import com.luckyzj.community.entity.Comment;
import com.luckyzj.community.entity.DiscussPost;
import com.luckyzj.community.entity.Page;
import com.luckyzj.community.entity.User;
import com.luckyzj.community.service.CommentService;
import com.luckyzj.community.service.DiscussPostService;
import com.luckyzj.community.service.UserService;
import com.luckyzj.community.utils.CommunityConstant;
import com.luckyzj.community.utils.CommunityUtil;
import com.luckyzj.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"您还没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        //报错的情况将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
       DiscussPost discussPost= discussPostService.findDiscussPostById(discussPostId);
       model.addAttribute("post",discussPost);
       //作者
        User user=userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(discussPost.getCommentCount());

        //帖子评论
        List<Comment> commentList= commentService.findCommentsByEntity(ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());

        //评论的显示对象列表
        List<Map<String,Object>> commentVOList = new ArrayList<>();
        if(commentList!=null){
            for(Comment comment : commentList){
                Map<String , Object> commentVO = new HashMap<>();
                commentVO.put("comment",comment);
                commentVO.put("user",userService.findUserById(comment.getUserId()));

                //评论的回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);

                List<Map<String,Object>> replyVOList = new ArrayList<>();
                if(replyList!=null){
                    for(Comment reply : replyList){
                        Map<String,Object> replyVO = new HashMap<>();
                        //回复
                        replyVO.put("reply",reply);
                        //作者
                        replyVO.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target=reply.getTargetId() == 0? null:userService.findUserById(reply.getTargetId());
                        replyVO.put("target",target);
                        replyVOList.add(replyVO);
                    }
                }
                commentVO.put("replys",replyVOList);
                //回复数量
                int replyCount=commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVO.put("replyCount",replyCount);

                commentVOList.add(commentVO);
            }
        }
        model.addAttribute("comments",commentVOList);

        return "/site/discuss-detail";
    }

}
