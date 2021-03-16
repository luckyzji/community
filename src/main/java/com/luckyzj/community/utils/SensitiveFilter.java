package com.luckyzj.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换符
    private static final String REPLACEMENT ="***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try(
                InputStream is=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while((keyword=reader.readLine())!=null){
                this.addKeyWord(keyword);
            }


        }
        catch (IOException e){
            logger.error("加载敏感词失败"+e.getMessage());
        }
    }

    //敏感词添加到前缀树中
    private void addKeyWord(String keyword) {
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //指向子节点，进入下一轮循环
            tempNode = subNode;

            //结束标识
            if(i==keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }

    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin=0;
        //指针3
        int position=0;

        StringBuilder stringBuilder = new StringBuilder();

        while(begin<text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                //若指针1是根节点，将此符号计入结果，让指针2向下走一步
                if(tempNode==rootNode){
                    stringBuilder.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);

            if(tempNode ==null){
                //以begin开头的不是敏感词
                stringBuilder.append(text.charAt(begin));
                //进入下一个位置
                position=++begin;
                //重新进入根节点
                tempNode = rootNode;
            }
            else if(tempNode.isKeyWordEnd()){
                //发现敏感词
                stringBuilder.append(REPLACEMENT);
                //指针进入下一个位置
                begin=++position;
                //重新指向根结点
                tempNode=rootNode;
            }else {
                position++;
                if(position>=text.length()){
                    stringBuilder.append(text.charAt(begin));
                    position=++begin;
                }
            }
        }
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();

    }

    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80||c>0x9FFF);
    }


    /**
     * 前缀树
     */
    private class TrieNode{
        //关键字结束标识符
        private boolean isKeyWordEnd=false;

        //子节点key是下级字符，value是下级节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
