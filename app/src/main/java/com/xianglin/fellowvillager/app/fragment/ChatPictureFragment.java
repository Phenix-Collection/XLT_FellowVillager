package com.xianglin.fellowvillager.app.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.adapter.HorizontalListViewAdapter;
import com.xianglin.fellowvillager.app.chat.PhotoSelectorActivity;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.chat.controller.SendMsgController;
import com.xianglin.fellowvillager.app.chat.widget.HorizontalListView;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.utils.FileUtils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 */
@EFragment(R.layout.fragment_chat_picture)
public class ChatPictureFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @ViewById(R.id.hsv_images)
    HorizontalListView picListView;
    @ViewById(R.id.tv_album)
    TextView tvAlbum;
    @ViewById(R.id.tv_send_num)
    TextView tvImageNum;

    @Click(R.id.tv_album)
    void albumSelect() {
        Intent intent = new Intent(mBaseActivity, PhotoSelectorActivity.class);
        startActivityForResult(intent, 1101);
        mBaseActivity.animBottomToTop();
    }

    private HorizontalListViewAdapter picListAdapter;
    public static int SHOW_IMAGE_NUM = 10;//发送图片下部可以显示的图片张数（包含前后两张拍照和更多）
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 8;//从相机获取图片
    private static final int REQUEST_CODE_PICK_IMAGE = 9;//从相册获取图片
    private static final int FROM_ALBUM = 8;
    private List<PhotoModel> selected;

    @AfterInject//初始化图片路径列表
    public void initValue() {

    }

    @AfterViews
    public void init() {
        picListAdapter = new HorizontalListViewAdapter(mContext, BorrowConstants.pathList);
        picListAdapter.notifyDataSetChanged();
        picListView.setAdapter(picListAdapter);
        picListView.setOnItemClickListener(this);
    }


    private String cameraName;

    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraName = System.currentTimeMillis() + ".jpg";
            File tempFile = new File(FileUtils.IMG_SAVE_PATH_PHOTO + cameraName);
            if (tempFile.exists()) tempFile.delete();
            tempFile.getParentFile().mkdirs();
            getImageByCamera.putExtra(
                    MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
        } else {
            tip("请确认已经插入SD卡");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            String path = FileUtils.IMG_SAVE_PATH_PHOTO + cameraName;
            File tempFile = new File(path);
            if (!tempFile.exists()){
                return;
            }
            List<PhotoModel> tmpList = new ArrayList<>();
            PhotoModel model = new PhotoModel(path);
            tmpList.add(model);
            SendMsgController.getInstance().sendPicture(tmpList);
        }
        /*从选取相册返回，在此发送选中图片*/
        if (resultCode == FROM_ALBUM) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            selected = (List<PhotoModel>) bundle.getSerializable("photoList");
            if (selected == null) {
                return;
            }
            SendMsgController.getInstance().sendPicture(selected);
        }
    }

    public void saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<PhotoModel> list = new ArrayList<PhotoModel>();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list != null) {
            list.clear();
        }
        if (BorrowConstants.pathList.get(position).getOriginalPath().equals("add")) {
            getImageFromCamera();
        } else if (BorrowConstants.pathList.get(position).getOriginalPath().equals("more")) {
            Intent intent = new Intent(mBaseActivity, PhotoSelectorActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("fromChatPicture", 1);
            bundle.putString("albumName", "最近照片");
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        } else {
            list.clear();
            Log.e("onItemClick", BorrowConstants.pathList.get(position).getOriginalPath() + "----------------" + (position - 1));
            list.add(BorrowConstants.pathList.get(position));
            SendMsgController.getInstance().sendPicture(list);
        }
    }


}
