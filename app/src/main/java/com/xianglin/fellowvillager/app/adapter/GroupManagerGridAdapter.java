package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.List;

/**
 * 项目名称：乡邻小站
 * 类描述：
 * 创建人：何正纬
 * 创建时间：2015/11/25 17:44
 * 修改人：hezhengwei
 * 修改时间：2015/11/25 17:44
 * 修改备注：
 */
public class GroupManagerGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<GroupMember> mData;
    private String grouptype;
    private FrameLayout flt_gridview;

    public GroupManagerGridAdapter(Context context, List<GroupMember> list, String grouptype) {
        this.mContext = context;
        this.mData = list;
        this.grouptype = grouptype;
    }

    public void setData(List<GroupMember> data) {
        mData = data;
    }

    /**
     * 刷新页面
     */
    public void refresh() {

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {

        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group_manager, parent, false);
            holder = new ViewHolder();
            flt_gridview = (FrameLayout) convertView.findViewById(R.id.item_grid_flt);
            holder.rlt_gridview = (RelativeLayout) convertView.findViewById(R.id.rlt_gridview);
            holder.Ximage = (ImageView) convertView.findViewById(R.id.item_img_group_manager);
            holder.Xname = (TextView) convertView.findViewById(R.id.item_text_group_manager);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Resources res = mContext.getResources();
        GroupMember mGroupMember = mData.get(position);
/*        if(mGroupMember.xlImgPath!=null){
//            Bitmap bmp = BitmapFactory.decodeResource(res,drawable);
//            Bitmap bmp =  BitmapFactory.decodeFile("路图片劲");
//            holder.Ximage.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bmp, 22));
            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.head);
            holder.Ximage.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bmp, 50));
        }else{
//            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.group_icon);
//            holder.Ximage.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bmp, 22));
        }*/


        holder.Ximage.setTag(mData.get(position).file_id);

        if ("O".equals(grouptype)) { //管理的群
            if (mGroupMember.xluserid.equals("ADD")) {

                holder.Ximage.setImageResource(R.drawable.group_manager_add);

            } else if (mGroupMember.xluserid.equals("DEL")) {
                //Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.group_manager_add);
                holder.Ximage.setImageResource(R.drawable.group_manager_delete);
            } else {

                ImageUtils.showCommonImage((Activity) mContext,holder.Ximage,
                        FileUtils.IMG_SAVE_PATH,mData.get(position).file_id, R.drawable.head);
            }

        } else {
            if (mGroupMember.xluserid.equals("ADD")) {
                Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.group_manager_add);
                holder.Ximage.setImageBitmap(bmp);
            } else {
                ImageUtils.showCommonImage((Activity) mContext,holder.Ximage,
                        FileUtils.IMG_SAVE_PATH,mData.get(position).file_id, R.drawable.head);
            }

        }


        holder.Xname.setText(mGroupMember.getUIName());

        return convertView;
    }

    void downloadImageHeader(final Activity activity, final ImageView imgView, final int position) {
        FileUtils.downloadFile(mContext, PersonSharePreference.getUserID(), mData.get(position).file_id + "",
                FileUtils.IMG_CACHE_HEADIMAGE_PATH, new FileMessageListener<FileTask>() {
                    @Override
                    public void success(int statusCode, final FileTask fileTask) {
                        LogCatLog.i("fileName", "------------------" + fileTask.fileName);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogCatLog.e("Test", "imgPath=" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + fileTask
                                        .fileName);


                                if(imgView.getTag()==null||"null".equals(imgView.getTag())){
                                    return;
                                }
                                // imgView.setImageBitmap(ImageUtils.decodeThumbnailsBitmap(FileUtils
                                // .IMG_CACHE_HEADIMAGE_PATH + fileTask.fileName));
                                ImageUtils.loadImage(imgView, "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + fileTask
                                        .fileName, activity.getResources().getDrawable(R.drawable.head));
                            }
                        });
                    }

                    @Override
                    public void handleing(int statusCode, FileTask fileTask) {

                    }

                    @Override
                    public void failure(int statusCode, FileTask fileTask) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgView.setImageResource(R.drawable.head);
                            }
                        });
                    }
                });
    }

    static class ViewHolder {
        ImageView Ximage;
        TextView Xname;
        RelativeLayout rlt_gridview;

    }

    public FrameLayout getFrameLayout() {
        return flt_gridview;
    }
}
