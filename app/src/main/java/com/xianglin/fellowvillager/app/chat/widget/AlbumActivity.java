package com.xianglin.fellowvillager.app.chat.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.adapter.AlbumAdapter;
import com.xianglin.fellowvillager.app.chat.PhotoPreviewActivity;
import com.xianglin.fellowvillager.app.chat.PhotoSelectorActivity;
import com.xianglin.fellowvillager.app.chat.model.AlbumModel;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.chat.utils.PhotoSelectorDomain;
import com.xianglin.fellowvillager.app.chat.utils.Util;
import com.xianglin.fellowvillager.app.widget.TopView;

import java.util.ArrayList;
import java.util.List;

/**
 * class describtion
 * Created by LiuHaoLiang.
 *
 * @author LiuHaoliang
 * @version v 1.0.0 2016/1/11 XLXZ Exp
 */
public class AlbumActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mLvAlbum;
    private AlbumAdapter albumAdapter;
    private static String RECCENT_PHOTO;
    public ArrayList<PhotoModel> selected;
    private PhotoSelectorDomain photoSelectorDomain;
    private static final int FROM_PHOTO_SELECTOR = 8;
    private static final int TO_PHOTO_SELECTOR = 1102;
    private TopView mTopView;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        if (getIntent().hasExtra("checkName")) {
           title = getIntent().getStringExtra("checkName");
        }else {
            title = "最近照片";
        }
        mTopView = (TopView) findViewById(R.id.topview);
        mTopView.getAppTitle().setText(title);
        mTopView.disLeftAll();
        mTopView.setRightTextViewText(R.string.cancel);
        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumActivity.this.finish();
            }
        });
        mLvAlbum = (ListView) findViewById(R.id.lv_albums);
        photoSelectorDomain = new PhotoSelectorDomain(getApplication());
        albumAdapter = new AlbumAdapter(getApplication(), new ArrayList<AlbumModel>());
        albumAdapter.setChecked(title);
        mLvAlbum.setAdapter(albumAdapter);
        mLvAlbum.setOnItemClickListener(this);
        photoSelectorDomain.updateAlbum(albumListener); // 更新相册信息

    }


    /**
     * 获取本地相册信息回调
     */
    public interface OnLocalAlbumListener {
        public void onAlbumLoaded(List<AlbumModel> albums);
    }

    private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
        @Override
        public void onAlbumLoaded(List<AlbumModel> albums) {
            albumAdapter.update(albums);
        }
    };


    @Override
    /** 相册列表点击事件 */
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
        for (int i = 0; i < parent.getCount(); i++) {
            AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
            if (i == position)
                album.setCheck(true);
            else
                album.setCheck(false);
        }
        albumAdapter.notifyDataSetChanged();
        mTopView.getAppTitle().setText(current.getName());
        Intent intent = new Intent(this, PhotoSelectorActivity.class);
        intent.putExtra("albumName", current.getName());
        setResult(100, intent);
        finish();
        animRightToLeft();
    }


}