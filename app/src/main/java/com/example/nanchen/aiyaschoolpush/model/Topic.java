package com.example.nanchen.aiyaschoolpush.model;

import com.example.nanchen.aiyaschoolpush.json.IJsonModel;

import java.io.Serializable;

/**
 * @author nanchen
 * @fileName AiYaSchoolPush
 * @packageName com.example.nanchen.aiyaschoolpush.model
 * @date 2016/10/24  14:48
 */

public class Topic implements IJsonModel,Serializable {

    /**
     * 话题id
     */
    public long id;

    /**
     * 话题内容
     */
    public String content;

    /**
     * 话题作者
     */
    public User author;

    /**
     * 话题发起时间戳
     */
    public long createTs;


    /**
     * 我是否赞了该话题
     */
    public boolean isIPraised;

    /**
     * 总赞数
     */
    public int praiseCount;

    /**
     * 总评论数
     */
    public int commentCount;
}


