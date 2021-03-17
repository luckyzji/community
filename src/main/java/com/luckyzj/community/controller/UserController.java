package com.luckyzj.community.controller;

import com.luckyzj.community.annotation.LoginRequired;
import com.luckyzj.community.entity.User;
import com.luckyzj.community.service.FollowService;
import com.luckyzj.community.service.LikeService;
import com.luckyzj.community.service.UserService;
import com.luckyzj.community.utils.CommunityConstant;
import com.luckyzj.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;


    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        //生成随机文件名
        filename = user.getUsername()+"_"+System.currentTimeMillis()+suffix;
        //确定文件存放路径
        File dest = new File(uploadPath+"/"+filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败"+e.getMessage());
        }
        //更新当前用户头像路径
        String headerUrl =domain+contextPath+"/user/header/"+filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath+"/"+fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try(
                //自动关闭
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
                ) {

            byte[] buffer = new byte[1204];
            int b=0;
            while ((b=fis.read(buffer) )!=-1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }

    @RequestMapping(path = "/password",method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, @CookieValue("ticket")String ticket, Model model){
        User user = hostHolder.getUser();
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("passwordMsg","新密码不能为空");
            return "/site/setting";
        }
        Map<String,Object> map=userService.updatePassword(user.getId(),oldPassword,newPassword);
        if(map.isEmpty()){
            userService.logout(ticket);
            model.addAttribute("ModPasswordMsg","密码修改成功，请重新登陆");
            return "redirect:/login";
        }
        else{
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/setting";
        }

    }

    /**
     * 个人主页
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user =userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount=likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount=followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注该目标
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed=followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }

        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }
}
