package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.PinnedSectionListView;
import com.xianglin.fellowvillager.app.widget.SwipeLayout;
import com.xianglin.fellowvillager.app.widget.adapter.BaseSwipeAdapter;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.fellowvillager.app.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjibin on 16/3/17.
 */
public class BlackListAdapter extends BaseSwipeAdapter
        implements PinnedSectionListView.PinnedSectionListAdapter {

    private static final String TAG = FragmentMessageAdapter.class.getSimpleName();
    private Context mContext = null;

    private List<Contact> mUserBlacklist = new ArrayList<>();
    private List<Group> mGroupBlacklist = new ArrayList<>();

    private int mType;//0-user;1-group

    public BlackListAdapter(Context ctx) {
        mContext = ctx;
    }

    public void setUserData(List<Contact> blackList) {
        mUserBlacklist = blackList;
        mType = 0;
    }

    public void setGroupData(List<Group> blackList) {
        this.mGroupBlacklist = blackList;
        mType = 1;
    }

    @Override
    public int getCount() {
        if (0 == mType) {
            return mUserBlacklist.size();
        } else {
            return mGroupBlacklist.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (0 == mType) {
            return mUserBlacklist.get(position);
        } else {
            return mGroupBlacklist.get(position);
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
    public View generateView(int position, ViewGroup parent) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_blacklist,
                parent, false);
        return convertView;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        CircleImage ivAvatar = com.xianglin.fellowvillager.app.utils.ViewHolder
                .get(convertView, R.id.contactitem_avatar_iv);
        TextView tvNickName = com.xianglin.fellowvillager.app.utils.ViewHolder
                .get(convertView, R.id.contactitem_nick);
        CircleImage ivContactAvatar = com.xianglin.fellowvillager.app.utils.ViewHolder
                .get(convertView, R.id.circleimage_contact);
        final SwipeLayout swipeLayout = (SwipeLayout) convertView
                .findViewById(getSwipeLayoutResourceId(position));

        if (mType == 0) {
            Contact blackListContact = mUserBlacklist.get(position);
            LogCatLog.e(TAG, "11111111111 blackListContact = " + blackListContact);
            if (blackListContact != null) {
                tvNickName.setText(blackListContact.getUIName());
                if (!TextUtils.isEmpty(blackListContact.file_id)) {
                    ImageUtils.showCommonImage((Activity) mContext, ivAvatar,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            blackListContact.file_id, R.drawable.head);
                } else {
                    ivAvatar.setImageResource(R.drawable.head);
                }
                FigureMode figureMode = ContactManager.getInstance()
                        .getCurrentFigure(blackListContact.figureId);
                if (!TextUtils.isEmpty(figureMode.getFigureImageid())) {
                    ImageUtils.showCommonImage((Activity) mContext, ivContactAvatar,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            figureMode.getFigureImageid(), R.drawable.head);
                } else {
                    ivContactAvatar.setImageResource(R.drawable.head);
                }
            }
        } else if (mType == 1) {
            Group blackListGroup = mGroupBlacklist.get(position);
            LogCatLog.e(TAG, "11111122222 mGroupBlacklist = " + blackListGroup);
            if (blackListGroup != null) {
                tvNickName.setText(blackListGroup.xlGroupName);
                if (!TextUtils.isEmpty(blackListGroup.file_id)) {
                    ImageUtils.showCommonImage((Activity) mContext, ivAvatar,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            blackListGroup.file_id, R.drawable.head);
                } else {
                    ivAvatar.setImageResource(R.drawable.head);
                }
                FigureMode figureMode = ContactManager.getInstance()
                        .getCurrentFigure(blackListGroup.figureId);
                if (!TextUtils.isEmpty(figureMode.getFigureImageid())) {
                    ImageUtils.showCommonImage((Activity) mContext, ivContactAvatar,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            figureMode.getFigureImageid(), R.drawable.head);
                } else {
                    ivContactAvatar.setImageResource(R.drawable.head);
                }
            }
        }

        convertView.findViewById(R.id.ll_menu).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    swipeLayout.close();
                    mListener.onRightItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Contact.SECTION;
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

}
