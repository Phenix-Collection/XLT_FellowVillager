package com.xianglin.fellowvillager.app.utils;

import com.xianglin.fellowvillager.app.model.RecentMessageBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 最最近消息列表进行排序
 * @author pengyang
 * @version v 1.0.0 2016/3/22 13:08  XLXZ Exp $
 */
public class ComparatorMessage {

    public List<Comparator<RecentMessageBean>> mCmpList = new ArrayList<Comparator<RecentMessageBean>>();
    public ComparatorMessage(){

        mCmpList.add(compareMsgTimeDesc);

    }
    public void sort(List<RecentMessageBean> list) {

        Comparator<RecentMessageBean> cmp = new Comparator<RecentMessageBean>() {
            @Override
            public int compare(RecentMessageBean o1, RecentMessageBean o2) {
                for (Comparator<RecentMessageBean> comparator : mCmpList) {
                    if (comparator.compare(o1, o2) > 0) {
                        return 1;
                    } else if (comparator.compare(o1, o2) < 0) {
                        return -1;
                    }
                }
                return 0;
            }
        };
        Collections.sort(list, cmp);
    }

    /**
     * 按发送消息排序
     */
    private Comparator<RecentMessageBean> compareMsgTimeDesc = new Comparator<RecentMessageBean>() {

        @Override
        public int compare(RecentMessageBean lhs, RecentMessageBean rhs) {

            if (lhs.getCreatedate().compareTo(rhs.getCreatedate())< 0) {
                return 1;
            } else if (lhs.getCreatedate().compareTo(rhs.getCreatedate())> 0) {
                return -1;
            }
            return 0;
        }
    };
}