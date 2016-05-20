package com.xianglin.fellowvillager.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.widget.dialog.CardDialog;

import java.util.List;
import java.util.Map;

/**
 * 项目名称：乡邻小站
 * 类描述：
 * 创建人：何正纬
 * 创建时间：2015/11/18 15:36
 * 修改人：hezhengwei
 * 修改时间：2015/11/18 15:36
 * 修改备注：
 */

public class XExpandableListAdapter extends BaseExpandableListAdapter {

    private List<String> mGroupTitle;// 组名
    private Map<Integer, List<Group>> mChildren;// 每一组对应的child
    private LayoutInflater mInflater;
    private Context mContext;
    private Group mGroup;
    private String name ;
    private String isCard;

    public XExpandableListAdapter(Context context, List<String> group, Map<Integer, List<Group>> children, String name , String isCard) {
        this.mInflater = LayoutInflater.from(context);
        this.mGroupTitle = group;
        this.mChildren = children;
        this.mContext = context;
        this.name = name;
        this.isCard = isCard;
    }

    public void update(List<String> mGroupTitle, Map<Integer, List<Group>> mChildren, String name , String isCard) {
        this.mGroupTitle = mGroupTitle;
        this.mChildren = mChildren;
        this.name = name;
        this.isCard = isCard;
        notifyDataSetChanged();// 更新一下
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    public Object getChild(int groupPosition, int childPosition) {
        return mChildren.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return mChildren.get(groupPosition).size();
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder vHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_group_list_child, null);
            vHolder = new ViewHolder();
            vHolder.nick = (TextView) convertView.findViewById(R.id.contact_list_item_name);
            vHolder.head = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        mGroup = (Group) getChild(groupPosition, childPosition);
        //        mGroup =  mChildren.get(groupPosition).get(childPosition);
        final int a = groupPosition;
        final int b = childPosition;
        vHolder.nick.setText(mGroup.xlGroupName);
        vHolder.head.setImageResource(R.drawable.group_icon);

        convertView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //               CustomToast.showToast(mContext, a + "::" + b, 1);
                mGroup = (Group) getChild(a, b);
                if (!"true".equals(isCard)) {
                    ChatMainActivity_.intent(mContext).titleName(mGroup.xlGroupName)
                            .toChatId(mGroup.xlGroupID).chatType(BorrowConstants.CHATTYPE_GROUP).start();
                } else {
                    showCardDialog(name, mGroup.xlGroupName, null,mGroup.xlGroupID).show();
                }
            }
        });

//        convertView.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                mGroup = (Group) getChild(a, b);
//                new AlertDialog.Builder(mContext)
//                        .setMessage("确定删除 " + mGroup.xlGroupName + " 吗？")
//                        .setPositiveButton(android.R.string.ok,
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //长按删除事件
//                                    }
//                                })
//                        .setNegativeButton(android.R.string.cancel, null)
//                        .create().show();
//                return false;
//            }
//        });
        return convertView;
    }

    Dialog finalDialog = null;
    private Dialog showCardDialog(String title, String message, String fileId, final String xlId){
        Dialog dialog = null;
        CardDialog.Builder myBuilder = new CardDialog.Builder(mContext);
        myBuilder.setTitle("发送给  " + title);
        myBuilder.setMessage(message);
        myBuilder.setFileId(fileId);
        myBuilder.setType("1");
        myBuilder.setXlId(xlId);

     //   final BusinessCardBean businessCardBean = new BusinessCardBean.Builder().fileId(fileId).name(message).xlId(xlId).build();

        myBuilder.setBackButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (finalDialog != null) {
                    finalDialog.dismiss();
                }
            }
        });
/*        myBuilder.setConfirmButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                GroupListActivity activity = (GroupListActivity) mContext;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(NewCardActivity.KEYID,businessCardBean);
                intent.putExtras(bundle);
                activity.setResult(NewCardActivity.RESULT_OK, intent);
                activity.finish();
            }
        });*/
        dialog = myBuilder.create();
        finalDialog = dialog;
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        return dialog;
    }

    class ViewHolder {
        public TextView nick;
        public ImageView head;
    }


    public Object getGroup(int groupPosition) {
        return mGroupTitle.get(groupPosition);
    }

    public int getGroupCount() {
        return mGroupTitle.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * create group view and bind data to view
     */
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.item_group_list, null);
        }

        RelativeLayout mRelativelayout = (RelativeLayout) convertView.findViewById(R.id.group_item_layout);
        TextView groupName = (TextView) convertView.findViewById(R.id.group_name);
        groupName.setText(getGroup(groupPosition).toString());
        TextView onlineNum = (TextView) convertView.findViewById(R.id.online_count);
        onlineNum.setText(getChildrenCount(groupPosition) + "/" + getChildrenCount(groupPosition));

        ImageView indicator = (ImageView) convertView.findViewById(R.id.group_indicator);
        //        if (isExpanded)
        //            indicator.setImageResource(R.drawable.indicator_expanded);
        //        else
        //            indicator.setImageResource(R.drawable.indicator_unexpanded);

        //        if(getChildrenCount(groupPosition)==0){
        //            mRelativelayout.setVisibility(View.GONE);
        //        }else{
        //            mRelativelayout.setVisibility(View.VISIBLE);
        //        }
        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }
}

