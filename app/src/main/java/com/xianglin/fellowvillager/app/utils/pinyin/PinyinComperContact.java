package com.xianglin.fellowvillager.app.utils.pinyin;

import com.xianglin.fellowvillager.app.model.Contact;

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
public class PinyinComperContact implements Comparator<Contact> {


    @Override
    public int compare(Contact lhs, Contact rhs) {
        if (lhs.section.equals("@")
                || rhs.section.equals("#")) {
            return -1;
        } else if (lhs.section.equals("#")
                || rhs.section.equals("@")) {
            return 1;
        } else {
            return lhs.section.compareTo(rhs.section);
        }
    }
}
