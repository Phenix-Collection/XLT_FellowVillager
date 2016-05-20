package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.PinnedSectionListView;
import com.xianglin.fellowvillager.app.widget.SwipeLayout;
import com.xianglin.fellowvillager.app.widget.adapter.BaseSwipeAdapter;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 联系人列表适配器
 */
public class ContactAdapter extends BaseSwipeAdapter
        implements SectionIndexer, PinnedSectionListView.PinnedSectionListAdapter {
    private Context mContext;
    private List<Contact> mUserInfos = new ArrayList<Contact>();// 好友信息
    private int mLocationPosition = -1;

    private int size = 0;//解决多线程下可能崩溃的问题

    // 首字母集
    private List<String> mFriendsSections = new ArrayList<String>();
    private List<Integer> mFriendsPositions = new ArrayList<Integer>();
    ContactDBHandler contactDBHandler;
    /**是否来自相同联系人*/
    private boolean fromSameContact = false;

    public ContactAdapter(Context context) {
        this.mContext = context;
        // 排序(实现了中英文混排)
        //Collections.sort(this.UserInfos, new PinyinComparator());
        contactDBHandler = new ContactDBHandler(mContext);
    }

    /**
     * 构造器
     * @param context 上下文
     * @param fromSameContact 是否是从相同联系人进入此页面
     */
    public ContactAdapter(Context context, boolean fromSameContact) {
        this(context);
        this.fromSameContact = fromSameContact;
    }

    public ContactAdapter(Context context, List<Contact> userInfos,
                          List<String> friendsSections, List<Integer> friendsPositions) {

        this.mContext = context;
        this.mUserInfos = userInfos;
        mFriendsSections = friendsSections;
        mFriendsPositions = friendsPositions;
        contactDBHandler = new ContactDBHandler(mContext);
        size = userInfos.size();
    }

    public void setData(List<Contact> userInfos) {
        size =userInfos.size();
        this.mUserInfos = userInfos;
    }
    @Override
    public void notifyDataSetChanged() {
        size = mUserInfos.size();

        super.notifyDataSetChanged();
    }
    public void setAlphaSelection(List<String> friendsSections) {
        this.mFriendsSections = friendsSections;
    }

    public void setAlphaPostion(List<Integer> friendsPositions) {
        this.mFriendsPositions = friendsPositions;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Contact getItem(int position) {
        try {
            if(!mUserInfos.isEmpty()){
                return mUserInfos.get(position);
            }else{
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    @Override
    public View generateView(final int position, ViewGroup parent) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item,
                parent, false);
        return convertView;
    }
/*

    //获取字母
    public String getAlpha(Contact user) {
        String catalog;
        try {// 首子母
            catalog = PingYinUtil.converterToFirstSpell(user.xlUserName).substring(0, 1);
            char alpha = catalog.charAt(0);
            if ((alpha >= 65 && alpha <= 90) || (alpha >= 97 && alpha <= 122)) {
                catalog = catalog.toUpperCase();
            } else {
                catalog = "#";
            }
        } catch (Exception e) {
            catalog = "#";
        }
        return catalog;
    }
*/

    /*    public static String getAlpha(String userName) {
            String catalog = "#";
            try {// 首子母
                catalog = PingYinUtil.converterToFirstSpell(userName).substring(0, 1);
                char alpha = catalog.charAt(0);
                if ((alpha >= 65 && alpha <= 90) || (alpha >= 97 && alpha <= 122)) {
                    catalog = catalog.toUpperCase();
                } else {
                    catalog = "#";
                }
            } catch (Exception e) {
                catalog = "#";
            }
            return catalog;
        }*/
    public static String getAlpha2(String pinying) {
        String catalog = "#";
        try {// 首子母
            catalog = pinying.substring(0, 1);
            char alpha = catalog.charAt(0);
            if ((alpha >= 65 && alpha <= 90) || (alpha >= 97 && alpha <= 122)) {
                catalog = catalog.toUpperCase();
            } else {
                catalog = "#";
            }
        } catch (Exception e) {
            catalog = "#";
        }
        return catalog;
    }

    @Override
    public void fillValues(final int position, View convertView) {

        fillValues_(position, convertView);

    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * @param position
     * @return public static final int ITEM = 0;//内容
     * public static final int SECTION = 1;//标
     */
    @Override
    public int getItemViewType(int position) {
          Contact c= getItem(position);
           if(c==null){
               return 0;
           }
        return c.type;
    }


    @Override
    public int getPositionForSection(int section) {

        if (section < 0 || section >= mFriendsSections.size() || mFriendsPositions.size() == 0) {
            return -1;
        }
        return mFriendsPositions.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position < 0 || position >= getCount() || mFriendsPositions == null) {
            return -1;
        }
        int index = Arrays.binarySearch(mFriendsPositions.toArray(), position);
        return index >= 0 ? index : -index - 2;
    }

    @Override
    public Object[] getSections() {
        return mFriendsSections.toArray();
    }

    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;

    public void setOnRightItemClickListener(onRightItemClickListener listener) {
        mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Contact.SECTION;
    }

    /**
     * 填充数据
     *
     * @param position    位置
     * @param convertView view
     */
    private void fillValues_(final int position, View convertView) {

        if (getCount() <= position) return;
        Contact user = getItem(position);
        if (user == null) {
            return;
        }
        ImageView ivAvatar = com.xianglin.fellowvillager.app.utils
                .ViewHolder.get(convertView, R.id.contactitem_avatar_iv);
        TextView tvCatalog = com.xianglin.fellowvillager.app.utils
                .ViewHolder.get(convertView, R.id.contactitem_catalog);
        TextView tvNick = com.xianglin.fellowvillager.app.utils.ViewHolder
                .get(convertView, R.id.contactitem_nick);

        CircleImage ci_role_1 = com.xianglin.fellowvillager.app.utils
                .ViewHolder.get(convertView, R.id.ci_role_1);
        CircleImage ci_role_2 = com.xianglin.fellowvillager.app.utils
                .ViewHolder.get(convertView, R.id.ci_role_2);
        CircleImage ci_role_3 = com.xianglin.fellowvillager.app.utils
                .ViewHolder.get(convertView, R.id.ci_role_3);
        ci_role_1.setVisibility(View.GONE);
        ci_role_2.setVisibility(View.GONE);
        ci_role_3.setVisibility(View.GONE);

        final SwipeLayout swipeLayout = (SwipeLayout) convertView
                .findViewById(getSwipeLayoutResourceId(position));
        if (user.type == Contact.SECTION) {
            tvCatalog.setVisibility(View.VISIBLE);
            tvCatalog.setText(user.section);
            swipeLayout.setVisibility(View.GONE);
        } else {
            tvCatalog.setVisibility(View.GONE);
            swipeLayout.setVisibility(View.VISIBLE);
        }
        convertView.findViewById(R.id.ll_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    swipeLayout.close();
                    LogCatLog.e("Test", "current position=" + position);
                    mListener.onRightItemClick(v, position);
                }
            }
        });

        ImageUtils.showCommonImage((Activity) mContext, ivAvatar,
                FileUtils.IMG_CACHE_HEADIMAGE_PATH, user.file_id, R.drawable.head);

        if(user.contactLevel== Contact.ContactLevel.UMKNOWN){
            tvNick.setTextColor(mContext.getResources().getColor(R.color.app_text_color3));
        }else{
            tvNick.setTextColor(mContext.getResources().getColor(R.color.app_text_color));
        }
        tvNick.setText(user.getXlUserName());

        // “全部”时显示右边的小图像
        if (TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID()) || fromSameContact) {
            ArrayList<FigureMode> mList_FigureMode = user.figureGroup;
            if (mList_FigureMode == null) {
                return;
            }
            if (mList_FigureMode.size() == 1) {
                ci_role_1.setVisibility(View.VISIBLE);
                FigureMode mFigureMode = mList_FigureMode.get(0);
                if (mFigureMode != null) {
                    ImageUtils.showCommonImage((Activity) mContext, ci_role_1,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            mFigureMode.getFigureImageid(), R.drawable.head);
                } else {
                    ci_role_1.setImageResource(R.drawable.head);
                }

                // 测试使用
                //				holder.ci_role_1.setImageResource(R.drawable.head);
            } else if (mList_FigureMode.size() == 2) {
                ci_role_1.setVisibility(View.VISIBLE);
                ci_role_2.setVisibility(View.VISIBLE);
                FigureMode mFigureMode1 = mList_FigureMode.get(0);
                FigureMode mFigureMode2 = mList_FigureMode.get(1);
                if (mFigureMode1 != null && mFigureMode2 != null) {
                    if (mFigureMode1.getUpdateDate() > mFigureMode2.getUpdateDate()) {
                        ImageUtils.showCommonImage((Activity) mContext, ci_role_1,
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                mFigureMode2.getFigureImageid(), R.drawable.head);
                        ImageUtils.showCommonImage((Activity) mContext, ci_role_2,
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                mFigureMode1.getFigureImageid(), R.drawable.head);
                    } else {
                        ImageUtils.showCommonImage((Activity) mContext, ci_role_1,
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                mFigureMode1.getFigureImageid(), R.drawable.head);
                        ImageUtils.showCommonImage((Activity) mContext, ci_role_2,
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                mFigureMode2.getFigureImageid(), R.drawable.head);
                    }
                } else {
                    ci_role_1.setImageResource(R.drawable.head);
                    ci_role_2.setImageResource(R.drawable.head);
                }

                // 测试使用
                //				holder.ci_role_1.setImageResource(R.drawable.head);
                //				holder.ci_role_2.setImageResource(R.drawable.head);
            } else {
                ci_role_1.setVisibility(View.VISIBLE);
                ci_role_2.setVisibility(View.VISIBLE);
                ci_role_3.setVisibility(View.VISIBLE);
                ci_role_1.setImageResource(R.drawable.more);

                FigureMode[] mFigureModeArray = (FigureMode[])
                        mList_FigureMode.toArray(new FigureMode[mList_FigureMode.size()]);

                // 找出此人跟我的哪个角色是最近聊天的
                //bigToSmallSort(mFigureModeArray);
                if(mFigureModeArray==null||mFigureModeArray.length==0)
                    return;
                FigureMode mFigureMode1 = mFigureModeArray[0];
                FigureMode mFigureMode2 = mFigureModeArray[1];


                if (mFigureMode1 != null && mFigureMode2 != null) {
                    ImageUtils.showCommonImage((Activity) mContext, ci_role_2,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            mFigureMode2.getFigureImageid(), R.drawable.head);
                    ImageUtils.showCommonImage((Activity) mContext, ci_role_3,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            mFigureMode1.getFigureImageid(), R.drawable.head);
                } else {
                    ci_role_2.setImageResource(R.drawable.head);
                    ci_role_3.setImageResource(R.drawable.head);
                }

                // 测试使用
                //				holder.ci_role_2.setImageResource(R.drawable.head);
                //				holder.ci_role_3.setImageResource(R.drawable.head);
            }
        }


    }


    /**
     * 从大到小排序
     *
     * @param mFigureMode
     */
    public static void bigToSmallSort(FigureMode[] mFigureMode) {
        if (mFigureMode == null || mFigureMode.length == 0) {
            return;
        }
        FigureMode temp; // 记录临时中间值
        for (int i = 0; i < mFigureMode.length - 1; i++) {
            for (int j = i + 1; j < mFigureMode.length; j++) {
                if (mFigureMode[i] != null && mFigureMode[j] != null) {
                    if (mFigureMode[i].getUpdateDate() < mFigureMode[j].getUpdateDate()) { // 交换两数的位置
                        temp = mFigureMode[i];
                        mFigureMode[i] = mFigureMode[j];
                        mFigureMode[j] = temp;
                    }
                }
            }
        }
    }


}
