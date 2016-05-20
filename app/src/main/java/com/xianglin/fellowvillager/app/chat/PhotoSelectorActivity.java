/**
 * 乡邻小站
 * Copyright (c) 2011-2015 xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.adapter.PhotoSelectorAdapter;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.chat.utils.PhotoSelectorDomain;
import com.xianglin.fellowvillager.app.chat.utils.Util;
import com.xianglin.fellowvillager.app.chat.widget.AlbumActivity;
import com.xianglin.fellowvillager.app.widget.TopView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengshengli
 * @version v 1.0.0 2015/12/9 12:37 XLXZ Exp $
 */
public class PhotoSelectorActivity extends BaseActivity implements
        PhotoItem.onItemClickListener, PhotoItem.onPhotoItemCheckedListener, OnClickListener {

    private static final int MAX_IMAGE = 9;
    public static final String KEY_MAX = "key_max";
    private static final int REQUEST_CAMERA = 1;

    public static String RECCENT_PHOTO;

    private GridView gvPhotos;
    private TextView tvSend;
    private PhotoSelectorDomain photoSelectorDomain;
    private PhotoSelectorAdapter photoAdapter;
    public ArrayList<PhotoModel> selected;
    private static final int TO_ALBUM = 8;
    private TopView mTopView;
    private String albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RECCENT_PHOTO = getResources().getString(R.string.recent_photos);
        Bundle bundle = getIntent().getExtras();
        if (getIntent().hasExtra("albumName"))
            albumName = bundle.getString("albumName");
        else
            albumName = RECCENT_PHOTO;
        setContentView(R.layout.activity_photoselector);
        selected = new ArrayList<>();
        mTopView = (TopView) findViewById(R.id.topview);

        mTopView.setAppTitle(albumName);

        mTopView.setRightTextViewText(R.string.cancel);
        mTopView.setLeftImageVisibility(View.VISIBLE);
        mTopView.getLeftlayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AlbumActivity.class);
                intent.putExtra("checkName", albumName);
                startActivityForResult(intent, 1000);
                animLeftToRight();
            }
        });

        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

        gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
        tvSend = (TextView) findViewById(R.id.tv_send);

        tvSend.setOnClickListener(this);

        photoAdapter = new PhotoSelectorAdapter(this, new ArrayList<PhotoModel>(), this, this);
        gvPhotos.setAdapter(photoAdapter);


        getData();
    }

    private void getData() {
        if (null != albumName) {
            if (albumName.equals(RECCENT_PHOTO)) {
                photoSelectorDomain.getReccent(reccentListener); // 更新最近照片
            } else {
                gvPhotos.setScrollY(0);
                photoSelectorDomain.getAlbum(albumName, reccentListener);
            }
            mTopView.setAppTitle(albumName);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send:
                ok();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == 100) {
            albumName = data.getStringExtra("albumName");
            getData();
            return;
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            ok();
        }
        if (requestCode == Util.CAMERA_PHOTO && resultCode == RESULT_OK) {

            if (Util.cameraFile != null && Util.cameraFile.exists()) {
                String str_choosed_img = Util.cameraFile.getAbsolutePath();
                PhotoModel cameraPhotoModel = new PhotoModel();
                cameraPhotoModel.setChecked(true);
                cameraPhotoModel.setOriginalPath(str_choosed_img);
                selected.add(cameraPhotoModel);

                MediaScannerConnection.scanFile(PhotoSelectorActivity.this,
                        new String[]{str_choosed_img}, null, null);

                if (photoAdapter.getCount() == 0) {
                    PhotoModel cameraFirst = new PhotoModel();
                    cameraFirst.setOriginalPath("default");
                    photoAdapter.add(0, cameraFirst);
                }
                if (selected.size() > 0) {
                    tvSend.setText("发送(" + selected.size() + "/9)");
                    photoAdapter.add(1, cameraPhotoModel);
                } else {
                    tvSend.setText("发送");
                    photoAdapter.add(cameraPhotoModel);
                }
            }
        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            PhotoModel photoModel = new PhotoModel(Util.query(
                    getApplicationContext(), data.getData()));
            if (selected.size() >= MAX_IMAGE) {
                Toast.makeText(
                        this,
                        String.format(
                                getString(R.string.max_img_limit_reached),
                                MAX_IMAGE), Toast.LENGTH_SHORT).show();
                photoModel.setChecked(false);
                photoAdapter.notifyDataSetChanged();
            } else {
                if (!selected.contains(photoModel)) {
                    selected.add(photoModel);
                }
            }
            ok();
        }
    }

    /**
     * 完成
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void ok() {
        if (selected.size() > MAX_IMAGE) {
            Toast.makeText(
                    this,
                    String.format(getString(R.string.max_img_limit_reached),
                            MAX_IMAGE), Toast.LENGTH_SHORT).show();
            return;
        } else if (selected.size() == 0) {
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra("photoList", selected);
            setResult(TO_ALBUM, intent);
            finish();
        }
    }


    /**
     * 清空选中的图片
     */
    private void reset() {
        selected.clear();
        tvSend.setText("发送");
        finish();
        animTopToBottom();
    }

    @Override
    /** 点击查看照片 */
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("album", albumName);
        Util.launchActivity(this, PhotoPreviewActivity.class, bundle);
    }

    @Override
    /** 照片选中状态改变之后 */
    public void onCheckedChanged(
            PhotoModel photoModel,
            CompoundButton buttonView,
            boolean isChecked
    ) {
        if (isChecked) {
            if (!selected.contains(photoModel))
                selected.add(photoModel);
        } else {
            selected.remove(photoModel);
        }

        if (selected.size() > MAX_IMAGE) {
            Toast.makeText(
                    this,
                    String.format(getString(R.string.max_img_limit_reached),
                            MAX_IMAGE), Toast.LENGTH_SHORT).show();
            return;
        } else if (selected.size() > 0) {
            tvSend.setText("发送(" + selected.size() + "/9)");
        } else {
            tvSend.setText("发送");
        }
    }

    /**
     * 获取本地图库照片回调
     */
    public interface OnLocalReccentListener {
        void onPhotoLoaded(List<PhotoModel> photos);
    }

    private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {

        @Override
        public void onPhotoLoaded(List<PhotoModel> photos) {
            for (PhotoModel model : photos) {
                if (selected.contains(model)) {
                    model.setChecked(true);
                }
            }
            photoAdapter.update(photos);
            gvPhotos.smoothScrollToPosition(0); // 滚动到顶端
        }
    };

    @Override
    public void onBackPressed() {
        reset();
    }
}
