package com.xianglin.fellowvillager.app.activity.group;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 修改群名称
 *
 * @author pengyang
 * @version v 1.0.0 2015/12/5 12:17  XLXZ Exp $
 */
@EActivity(R.layout.activity_group_set_name)
public class GroupSetNameActivity extends BaseActivity {
    private String result;

    @ViewById(R.id.et_group_name)
    EditText mGroupName;

    @ViewById(R.id.iv_clearBtn)
    ImageView clearBtn;

    @ViewById(R.id.topview)
    TopView mTopView;


    private GroupDBHandler mGroupDBHandler;
    private String toGroupId;
    private String groupName;

    //注解完成执行
    @AfterViews
    void initView() {

        toGroupId = getIntent().getExtras().getString("groupId");
        groupName = getIntent().getExtras().getString("groupName");
        mGroupDBHandler = new GroupDBHandler(this);
        mTopView.setAppTitle(R.string.tip_set_group_name);
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        mTopView.setRightTextViewText(R.string.tip_save);
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.head_text_color);
        if (csl != null) {
            mTopView.getRightTextView().setTextColor(csl);
        }
        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                result = mGroupName.getText().toString().trim();
                if (checkData()) {

                    Group group = GroupManager.getInstance().getGroup(toGroupId);

                    updateGroup(group.xlGroupID, group.figureId, result, "", "");
                    Utils.hideSoftKeyboard(mGroupName);
                }
            }
        });


        mGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(mGroupName.getText().toString())) {

                    if (editable.toString().length() > 30) {
                        mGroupName.setError("群聊名称最多30位");
                    }
                    checkData();

                    clearBtn.setVisibility(View.VISIBLE);
                } else {
                    clearBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        mGroupName.setText(groupName);
        mGroupName.setSelection(mGroupName.length());


    }

    @Click(R.id.iv_clearBtn)
    void clearBtn() {
        mGroupName.setText("");
        Utils.showSoftKeyboard(mGroupName);
    }

    @Background
    void updateGroup(String groudId, String figureId, String groupName, String avatarUrl, String description) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setGroupId(groudId);
        groupDTO.setFigureId(figureId);
        groupDTO.setGroupName(groupName);
        groupDTO.setAvatarUrl(avatarUrl);
        groupDTO.setDescription(description);
        groupDTO.setCreateTime(System.currentTimeMillis());
        SyncApi.getInstance().update(groupDTO, GroupSetNameActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {

                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });

        Group group = GroupManager.getInstance().getGroup(toGroupId);

        if (group != null) {
            group.xlGroupName = mGroupName.getText().toString();
//                //发送数据库变化的信号通知loader重新加载
//                XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler
//                        .SYNC_SIGNAL_URI, null);//addGroup里已经做了
            GroupManager.getInstance().addGroup(group);
            LogCatLog.e(TAG, "updateGroup : group = " + group);

            //数据是使用Intent返回
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra("result", group.xlGroupName);
            //设置返回数据
            GroupSetNameActivity.this.setResult(RESULT_OK, intent);
            //关闭Activity
            closeActivity();
        }
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", groupName);
        //设置返回数据
        GroupSetNameActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        closeActivity();
    }

    /**
     * 输入内容检查
     */
    private boolean checkData() {
        if (TextUtils.isEmpty(mGroupName.getText().toString().trim())) {
            tip("群聊名称为空，请输入群名称！");
            return false;
        } else if (!isTrueName(mGroupName.getText().toString().trim())) {
            tip("群聊名称有特殊字符");
            return false;
        } else if (mGroupName.getText().toString().trim().length() > 30) {
            tip("群聊名称过长");
            return false;
        } else if (mGroupName.getText().toString().trim().length() < 2) {
            tip("群聊名称过短");
            return false;
        }
        return true;
    }

    // 校验name只能是数字,英文字母和中文
    public static boolean isTrueName(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z、]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

}

