package com.xianglin.fellowvillager.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.utils.BitmapUtils;
import com.xianglin.fellowvillager.app.utils.Utils;

import java.io.File;
import java.util.List;

/**
 * class describtion
 * Created by LiuHaoLiang.
 *
 * @author LiuHaoliang
 * @version v 1.0.0 2016/1/13 XLXZ Exp
 */
public class HorizontalListViewAdapter extends DefaultAdapter {

    private List<PhotoModel> imagePathList;

    public HorizontalListViewAdapter(Context context, List<PhotoModel> imagePathList) {
        super(context, imagePathList);
        this.imagePathList = imagePathList;
    }

    private static class ViewHolder {
        private LinearLayout rlImage;
        private ImageView ivImage;
        private TextView tvImgDes;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_image, null);
            vh.ivImage = (ImageView) convertView.findViewById(R.id.iv_photo_lpsi);
            vh.rlImage = (LinearLayout) convertView.findViewById(R.id.rl_photo_lpsi);
            vh.tvImgDes = (TextView) convertView.findViewById(R.id.tv_img_descuption);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Bitmap bitmap = null;
        PhotoModel model = imagePathList.get(position);
        String path = model.getOriginalPath();
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) vh.ivImage.getLayoutParams(); // 取控件ivImage当前的布局参数
        int windowHieght = Utils.dipToPixel(mContext, 249) * 5 / 6;
        if (!TextUtils.isEmpty(path) && path.equals("add")) {
            vh.rlImage.setBackgroundResource(R.color.white);
            linearParams.width = 360 * Utils.dipToPixel(mContext, 235) * 5 / 579 / 6;
            linearParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            vh.ivImage.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件ivImage
            vh.ivImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            vh.ivImage.setImageResource(R.drawable.icon_carera_chat_send);
            vh.tvImgDes.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(path) && path.equals("more")) {
            vh.rlImage.setBackgroundResource(R.color.white);
            vh.tvImgDes.setVisibility(View.GONE);
            linearParams.width = 360 * Utils.dipToPixel(mContext, 235) * 5 / 579 / 6;
            vh.ivImage.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件ivImage
            vh.ivImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            vh.ivImage.setImageResource(R.drawable.icon_photo_more);
        } else {
            vh.tvImgDes.setVisibility(View.GONE);
            linearParams.height = windowHieght;
            vh.rlImage.setBackgroundResource(R.color.transparent);
            int bitmapWidth = -1;
            int bitmapHeight = -1;
            if (!(new File(path).exists())) {
                bitmapWidth = model.getmBitmap().getWidth();
                bitmapHeight = model.getmBitmap().getHeight();
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //设置为true,表示解析Bitmap对象，该对象不占内存
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(path, options);

                bitmapWidth = options.outWidth;
                bitmapHeight = options.outHeight;


                try {
                    int degree = BitmapUtils.readPictureDegree(path);
                    switch (degree) {
                        case 90:
                        case 270:
                            int num = bitmapHeight;
                            bitmapHeight = bitmapWidth;
                            bitmapWidth = num;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            linearParams.height = windowHieght;
            linearParams.width = bitmapWidth * Utils.dipToPixel(mContext, 235) * 5 / bitmapHeight / 6;  // 动态计算imageview宽
            vh.ivImage.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件ivImage
            vh.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);


            if (model.getmBitmap() == null) {
                bitmap = getSmallBitmap(path, windowHieght, bitmapWidth * Utils.dipToPixel(mContext, 235) * 5 / bitmapHeight / 6);
                model.setmBitmap(bitmap);
                BorrowConstants.pathList.remove(position);
                BorrowConstants.pathList.add(position, model);
            } else {
                bitmap = BorrowConstants.pathList.get(position).getmBitmap();
            }
            vh.ivImage.setImageBitmap(bitmap);

//            ImageLoader.getInstance().displayImage("file://" + path, vh.ivImage, ImageUtils.getDefOpetion());

        }

        return convertView;
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }
}
