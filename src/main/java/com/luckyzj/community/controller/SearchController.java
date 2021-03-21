package com.luckyzj.community.controller;

import com.luckyzj.community.entity.DiscussPost;
import com.luckyzj.community.entity.Page;
import com.luckyzj.community.service.ElasticsearchService;
import com.luckyzj.community.service.LikeService;
import com.luckyzj.community.service.UserService;
import com.luckyzj.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult=
        elasticsearchService.searchDiscussPost(keyword,page.getCurrent()-1,page.getLimit());
        //聚合数据
        List<Map<String,Object>> discussposts = new ArrayList<>();
        if(searchResult!=null){
            for(DiscussPost post:searchResult){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));

                discussposts.add(map);
            }
        }
        model.addAttribute("discussposts",discussposts);
        model.addAttribute("keyword",keyword);
        //分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult==null?0: (int) searchResult.getTotalElements());
        return "/site/search";

    }
}
