package com.xianglin.fellowvillager.app.recyclerview.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.adapter.ContactAdapter;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;

import java.util.ArrayList;

/**
 * Created by Lison on 16/3/17.
 */
public class SameGroupViewHolder extends BaseViewHolder {

    private CircleImage group_icon;
    private TextView tv_minglu_contact_nick;
    private CircleImage ci_role_1, ci_role_2, ci_role_3;

    public SameGroupViewHolder(View view, BaseActivity activity) {
        super(view, activity);
    }

    public SameGroupViewHolder(View view, BaseActivity activity, View.OnClickListener clickListener) {
        super(view, activity, clickListener);
    }

    @Override
    protected void findViews(View view) {
        super.findViews(view);
        group_icon = (CircleImage) view.findViewById(R.id.group_icon);
        tv_minglu_contact_nick = (TextView) view.findViewById(R.id.tv_minglu_contact_nick);
        ci_role_1 = (CircleImage) view.findViewById(R.id.ci_role_1);
        ci_role_2 = (CircleImage) view.findViewById(R.id.ci_role_2);
        ci_role_3 = (CircleImage) view.findViewById(R.id.ci_role_3);
    }

    @Override
    public Group getModel() {
        return (Group) super.getModel();
    }

    @Override
    public void updateView(int position) {
        super.updateView(position);
        Group model = getModel();
        if (model == null) {
            group_icon.setImageResource(R.drawable.group_icon);
            tv_minglu_contact_nick.setText("");
            return;
        }
        if (TextUtils.isEmpty(model.xlGroupName)) {
            tv_minglu_contact_nick.setText("");
        } else {
            tv_minglu_contact_nick.setText(model.xlGroupName);
        }
            ArrayList<FigureMode> figureGroup = model.figureGroup;
            if (figureGroup == null || figureGroup.size() == 0) {
                return;
            }
        FigureMode[] figureModes = new FigureMode[figureGroup.size()];
        for (int i = 0; i < figureGroup.size(); i++) {
            figureModes[i] = figureGroup.get(i);
        }
        ContactAdapter.bigToSmallSort(figureModes);
        if (figureModes.length > 0) {
            figureGroup.clear();
            for (int i = 0; i < figureModes.length; i++) {
                figureGroup.add(figureModes[i]);
            }
        }
        model.figureGroup = figureGroup;
        if (figureGroup.size() == 1) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_1,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(0).getFigureImageid(),
                        R.drawable.head
                );
                ci_role_1.setVisibility(View.VISIBLE);
                ci_role_2.setVisibility(View.GONE);
                ci_role_3.setVisibility(View.GONE);
            } else if (figureGroup.size() == 2) {
            if (figureGroup.get(0) != null) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_2,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(0).getFigureImageid(),
                        R.drawable.head
                );

            }
            if (figureGroup.get(1) != null) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_1,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(1).getFigureImageid(),
                        R.drawable.head
                );
                ci_role_1.setVisibility(View.VISIBLE);
                ci_role_2.setVisibility(View.VISIBLE);
                ci_role_3.setVisibility(View.GONE);
            }
        } else if (figureGroup.size() == 3) {
            if (figureGroup.get(0) != null) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_3,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(0).getFigureImageid(),
                        R.drawable.head
                );
            }
            if (figureGroup.get(1) != null) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_2,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(1).getFigureImageid(),
                        R.drawable.head
                );
            }
            if (figureGroup.get(2) != null) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_1,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(2).getFigureImageid(),
                        R.drawable.head
                );
            }

            ci_role_1.setVisibility(View.VISIBLE);
            ci_role_2.setVisibility(View.VISIBLE);
            ci_role_3.setVisibility(View.VISIBLE);
        } else if (figureGroup.size() > 3) {
            if (figureGroup.get(0) != null) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_3,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(0).getFigureImageid(),
                        R.drawable.head
                );
            }
            if (figureGroup.get(1) != null) {
                ImageUtils.showCommonImage(
                        activity,
                        ci_role_2,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureGroup.get(1).getFigureImageid(),
                        R.drawable.head
                );
            }

            ci_role_1.setImageResource(R.drawable.more);
            ci_role_1.setVisibility(View.VISIBLE);
            ci_role_2.setVisibility(View.VISIBLE);
            ci_role_3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        rootView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Group group = getModel();
        if (group == null) {
            return;
        }

        String currentFigureId = ContactManager.getInstance().getCurrentFigureID();
        if (TextUtils.isEmpty(currentFigureId)) {
            currentFigureId =
                    group.figureGroup == null
                            ?
                            group.figureId
                            :
                            group.figureGroup.get(0).getFigureUsersid();
        }
        ChatMainActivity_//
                .intent(activity)//
                .currentFigureId(currentFigureId)
                .toChatId(group.xlGroupID)
                .titleName(group.xlGroupName)//
                .chatType(BorrowConstants.CHATTYPE_GROUP)//
                .start();

            activity.animLeftToRight();
    }
}
