/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.longlink;

/**
 *  监听各类事件
 *
 * @author pengyang
 * @version v 1.0.0 2015/2/27 14:18  XLXZ Exp $
 */
public class XLNotifierEvent {
    private Object eventData = null;
    private XLNotifierEvent.Event event;

    public XLNotifierEvent() {
        this.event = XLNotifierEvent.Event.EventNewMessage;
    }

    public void setEvent(XLNotifierEvent.Event event) {
        this.event = event;
    }

    public XLNotifierEvent.Event getEvent() {
        return this.event;
    }

    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }

    public Object getData() {
        return this.eventData;
    }

    public static enum Event {
        EventNewMessage,//有新消息
        EventNewContact,//有新的联系人
        EventNewGroup;//有新的群
        //群解散通知 等
        private Event() {

        }
    }
}