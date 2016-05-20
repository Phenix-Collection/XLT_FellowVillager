package com.xianglin.fellowvillager.app.widget.tab;


import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.MainActivity;
import com.xianglin.fellowvillager.app.fragment.MainContactFragment_;
import com.xianglin.fellowvillager.app.fragment.MainHomeFragment_;
import com.xianglin.fellowvillager.app.fragment.MainMeFragment_;
import com.xianglin.fellowvillager.app.fragment.MainMessageFragment_;

public enum MainTab {

    HOME(
            MainActivity.TAB_HOME,
            R.string.menu_home,
            R.drawable.tab_home,
            MainHomeFragment_.class
    ),

    MESSAGE(
            MainActivity.TAB_MESSAGE,
            R.string.menu_message,
            R.drawable.tab_message,
            MainMessageFragment_.class
    ),

    FIGURE(
            MainActivity.TAB_FIGURE,
            R.string.menu_figure,
            -1,
            null
    ),

    CONTACT(
            MainActivity.TAB_CONTACT,
            R.string.menu_contact,
            R.drawable.tab_contact_list,
            MainContactFragment_.class
    ),

    ME(
            MainActivity.TAB_ME,
            R.string.menu_mine,
            R.drawable.tab_profile,
            MainMeFragment_.class
    );

    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private MainTab(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
