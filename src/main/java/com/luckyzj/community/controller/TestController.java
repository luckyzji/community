package com.luckyzj.community.controller;

import com.luckyzj.community.utils.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ZJ
 * @Date 2021-01-28
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String addStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //  通过  /student?name=test
    @RequestMapping(path = "/student",method = RequestMethod.GET)
    public void testRP(@RequestParam(name = "name",required = false,defaultValue = "name") String name){
        System.out.println(name);
    }


    // /student/test
    @RequestMapping(path = "/student/{name}",method = RequestMethod.GET)
    public void testPV(@PathVariable("name") String name){
        System.out.println(name);
    }

    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","李四");
        mav.addObject("age",40);
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","西电");
        model.addAttribute("age","70");
        return "/demo/view";
    }

    //响应json数据(一般在异步请求)
    //Java对象->json ->js对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","xiaoming");
        map.put("age",15);
        return map;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("name","xiaoming");
        map1.put("age",15);
        Map<String,Object> map2 = new HashMap<>();
        map2.put("name","lihua");
        map2.put("age",15);
        list.add(map1);
        list.add(map2);
        return list;
    }

    @RequestMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建Cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置Cookie生效范围
        cookie.setPath("/community/test");
        //设置cookie生存时间
        cookie.setMaxAge(60*10);
        //发送Cookie
        response.addCookie(cookie);
        return "set Cookie";
    }
    @RequestMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get Cookie";
    }




}
