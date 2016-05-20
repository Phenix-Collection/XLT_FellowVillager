package com.xianglin.fellowvillager.app.utils.pinyin;

import com.xianglin.fellowvillager.app.model.GroupMember;

import java.util.Comparator;

/**
 * 项目名称：乡邻小站
 * 类描述：
 * 创建人：何正纬
 * 创建时间：2015/12/2 17:57
 * 修改人：hezhengwei
 * 修改时间：2015/12/2 17:57
 * 修改备注：
 */
public class PinyinComp implements Comparator<GroupMember> {

    @Override
    public int compare(GroupMember lhs, GroupMember rhs) {

        if (lhs.sortLetters.equals("@")
                || rhs.sortLetters.equals("#")) {
            return -1;
        } else if (lhs.sortLetters.equals("#")
                || rhs.sortLetters.equals("@")) {
            return 1;
        } else {
            return lhs.sortLetters.compareTo(rhs.sortLetters);
        }

    }
}
