package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.appserv.common.service.facade.model.UserFigureDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.ChooseRoleActivity;
import com.xianglin.fellowvillager.app.activity.group.GroupAddMemberActivity_;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.ThreadPool;
import com.xianglin.fellowvillager.app.widget.CircleImage;

import java.io.Serializable;
import java.util.List;

/**
 * 选择角色的Adapter
 * <p/>
 * author:王力伟 time：2016.2.27
 */
public class ChooseRoleAdapter extends RecyclerView.Adapter<ChooseRoleAdapter.ViewHolder> {

    private Context mContext;
    private List<FigureMode> mList_FigureMode;
    private OnItemClickListener mOnItemClickListener;
    private String goWhere;
    private Serializable mSerializable;//UserFigureDTO对象

    private String figureIdList;
    private String from;

    public ChooseRoleAdapter(Context context, List<FigureMode> list,
                             String goWhere, Serializable serializable) {
        this.mContext = context;
        this.mList_FigureMode = list;
        this.goWhere = goWhere;
        this.mSerializable = serializable;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contact_item_minglu, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mList_FigureMode == null || mList_FigureMode.size() == 0) {
            return;
        }
        FigureMode mFigureMode = mList_FigureMode.get(position);
        if (mFigureMode == null) {
            return;
        }
        ImageUtils.showCommonImage((Activity) mContext, holder.ci_minglu_contact_head,
                FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureMode.getFigureImageid(), R.drawable.head);
        holder.tv_minglu_contact_nick.setText(mFigureMode.getFigureName());
        holder.iv_minglu_contact_right.setVisibility(View.VISIBLE);

        onBindClick_(holder, position);
    }

    @Override
    public int getItemCount() {
        return mList_FigureMode == null || mList_FigureMode.size() == 0
                ? 0 : mList_FigureMode.size();
    }

    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_minglu_contact;
        // 角色头像
        CircleImage ci_minglu_contact_head;
        // 角色名称
        TextView tv_minglu_contact_nick;
        // 向右箭头
        ImageView iv_minglu_contact_right;

        public ViewHolder(View view) {
            super(view);
            ll_minglu_contact = (LinearLayout) view.findViewById(R.id.ll_minglu_contact);
            ci_minglu_contact_head = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head);
            tv_minglu_contact_nick = (TextView) view.findViewById(R.id.tv_minglu_contact_nick);
            iv_minglu_contact_right = (ImageView) view.findViewById(R.id.iv_minglu_contact_right);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private void onBindClick_(ViewHolder holder, final int position) {
        if (mOnItemClickListener == null) {

            holder.ll_minglu_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mList_FigureMode != null) {
                        // 某个角色
                        FigureMode mFigureMode = mList_FigureMode.get(position);
                        if (mFigureMode == null) {
                            return;
                        }
                        if (from != null && from.equals("add_group")) {
                            Intent intent = new Intent(mContext, GroupAddMemberActivity_.class);
                            intent.putExtra("selectCurFigureId", mFigureMode.getFigureUsersid());
                            ((ChooseRoleActivity) mContext).setResult(Activity.RESULT_OK, intent);
                            ((BaseActivity) mContext).animLeftToRight();
                            ((ChooseRoleActivity) mContext).finish();
                            return;
                        }
                        // 去聊天（附近的人/二维码）
                        if (BorrowConstants.NEAR_PEOPLE.equals(goWhere)
                                ||BorrowConstants.QRCODE_SCAN.equals(goWhere)) {
                            UserFigureDTO mUserFigureDTO = (UserFigureDTO) mSerializable;
                            if (mUserFigureDTO == null) {
                                return;
                            }
                            ChatMainActivity_//
                                    .intent(mContext)//
                                    .currentFigureId(mFigureMode.getFigureUsersid())// 选择的角色
                                    .toChatXlId(mUserFigureDTO.getUserId())
                                    .toChatId(mUserFigureDTO.getFigureId())// 附近的人
                                    .titleName(mUserFigureDTO.getNickName())//
                                    .headerImgId(mUserFigureDTO.getAvatarUrl())//
                                    .toChatName(mUserFigureDTO.getNickName())//
                                    .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                                    .start();
                        } else if (BorrowConstants.CHAT_CHAT.equals(goWhere)) {// 去聊天（在这个版本不做这块内容）
                            ChatMainActivity_//
                                    .intent(mContext)//
                                    .currentFigureId(mFigureMode.getFigureUsersid())// 选择的角色
                                    .toChatId(mFigureMode.getFigureUsersid())//
                                    .titleName(mFigureMode.getFigureName())//
                                    .headerImgId(mFigureMode.getFigureImageid())//
                                    .toChatName(mFigureMode.getFigureName())//
                                    .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                                    .start();
                        }

                        if (mContext instanceof BaseActivity) {
                            ((BaseActivity) mContext).animLeftToRight();
                            ThreadPool.getCachedThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(500);
                                    ((BaseActivity) mContext).finish();
                                }
                            });
                        }
                    }
                }
            });

        } else {

            holder.ll_minglu_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position);
                }
            });

        }
    }

}
