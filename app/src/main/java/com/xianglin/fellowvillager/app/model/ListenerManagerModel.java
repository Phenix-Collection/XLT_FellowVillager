package com.xianglin.fellowvillager.app.model;

import com.xianglin.fellowvillager.app.longlink.listener.MessageListener;

/**
 *
 * 数据监听管理 模型
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-30
 */
public class ListenerManagerModel {


    /**
     * 具体监听类型枚举
     */
    public  enum  ListenerType{
        MAINPAGE(0x111,"主页页面"),// 主页 整个APP
        HOMEPAGE(0x222,"临时聊天页面"),// 临时聊天界面
        CHATPAGE(0x333,"聊天页面");// 主聊天界面

        private int stateCode ;

        private String msg;

        ListenerType(int  stateCode,String msg){

            this.stateCode = stateCode;
            this.msg  = msg;
        }


        public int getStateCode() {
            return stateCode;
        }

        public ListenerType setStateCode(int stateCode) {
            this.stateCode = stateCode;
            return this;
        }

        public String getMsg() {
            return msg;
        }

        public ListenerType setMsg(String msg) {
            this.msg = msg;
            return this;
        }
    }

    /**
     * 监听类型
     */
    public  ListenerType listenerType;

    /**
     * 消息回调监听
     */
    public MessageListener messageListener;


    /**
     * 对方聊天ID
     * 注:对方聊天ID 是在主聊天界面 需要填写 非主聊天界面 不需要填写
     * 原因：数据底层判断 需要进行当前聊天人和 数据过来的聊天人 对比
     * 所以主聊天界面 在监听集合 之对应一个，如果主聊天界面，添加两个
     * ，底层会把之前的给推出监听集合 ，保证监听集合只对应一条数据
     */
    public    String   chatId;


    public ListenerManagerModel(Builder builder){
        this.listenerType = builder.listenerType;
        this.messageListener = builder.messageListener;
        this.chatId = builder.chatId;

    }


    public static class Builder{

        /**
         * 监听类型
         */
        public  ListenerType listenerType;

        /**
         * 消息回调监听
         */
        private  MessageListener messageListener;


        /**
         * 对方聊天ID
         * 注：联系人 是在主聊天界面 需要填写 非主聊天界面 不需要填写
         * 原因：数据底层判断 需要进行当前聊天人和 数据过来的聊天人 对比
         * 所以主聊天界面 在监听集合 之对应一个，如果主聊天界面，添加两个
         * ，底层会把之前的给推出监听集合 ，保证监听集合只对应一条数据
         */
        private   String   chatId;



        /**
         * @param listenerType
         * @return
         */
        public  Builder listenerType(ListenerType listenerType){
            this.listenerType = listenerType;
            return this;
        }


        /**
         *
         * @param messageListener
         * @return
         */
        public Builder  messageListener(MessageListener messageListener){
            this.messageListener = messageListener;
            return this;
        }

        /**
         *
         * @param chatId
         * @return
         */
        public Builder contact(String  chatId){
            this.chatId = chatId;
            return this;
        }

        public ListenerManagerModel build(){
           return new  ListenerManagerModel(this);
        }




    }


}
