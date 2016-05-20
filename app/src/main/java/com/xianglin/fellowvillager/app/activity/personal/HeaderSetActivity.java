package com.xianglin.fellowvillager.app.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.ShowBigImageActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.crop.Crop;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.AddressManager;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

/**
 * 
 * 个人头像
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_edt_header)
public class HeaderSetActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.iv_header)
    CircleImage iv_header;

    Uri mHeadImageUri;
    private String imgPath;
    private String tempPath;

    Uri headTempUri;
    private static final int REQUEST_CAMERA = 1;
    private boolean isSaveedHead;
    String figureId;
    String figureImgId;
    FigureMode figureMode;

    String operateType;

    @AfterViews
    void initViiew(){
        mTopView.setAppTitle("个人头像");
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        tempPath=FileUtils.IMG_SAVE_PATH_CAP_CUT+"temp.jpg";
        headTempUri = Uri.fromFile(new File(tempPath));
        operateType=getIntent().getStringExtra("operateType");
        LogCatLog.e(TAG,"operateType="+operateType);
        if(operateType!=null&&operateType.equals(BorrowConstants.TYPE_INFO)){
            figureId= getIntent().getStringExtra("figureId");
            figureMode=ContactManager.getInstance().getCurrentFigure(figureId);
            if(figureMode==null) return;
            figureImgId=figureMode.getFigureImageid();
            LogCatLog.e(TAG,"header figureImgId="+figureImgId);
            ImageUtils.showCommonImage(this, iv_header,
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH, figureImgId, R.drawable.head);
            imgPath=FileUtils.IMG_CACHE_HEADIMAGE_PATH
                    +AddressManager.addressManager.env+"_"+figureImgId + ".webp";
            mHeadImageUri = Uri.fromFile(new File(imgPath));


        }else{//新建角色
            mHeadImageUri = Uri.fromFile(new File(FileUtils.IMG_CACHE_HEADIMAGE_PATH,"temp.jpg"));
            iv_header.setImageBitmap(
                    ImageUtils.decodeThumbnailsBitmap(
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH + "temp.jpg"));
        }




    }

    @Click(R.id.rela_album)
    void fromAlbum(){
        Crop.pickImage(HeaderSetActivity.this);
    }

    @Click(R.id.rela_camera)
    void fromCamera(){
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, headTempUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Click(R.id.iv_header)
    void clickHeader(){
        if(TextUtils.isEmpty(figureImgId)){
            return;
        }
        ShowBigImageActivity_.intent(context).imgPath(imgPath).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            //拍照回来
            beginCrop(headTempUri);

        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            //图库
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {

            handleCrop(resultCode, result);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        LogCatLog.e(TAG,"result="+result+",resultCode="+resultCode);
        if (resultCode == RESULT_OK) {
            iv_header.setImageDrawable(null);
            isSaveedHead = true;
            iv_header.setImageURI(result != null ? Crop.getOutput(result) : mHeadImageUri);

            if(operateType.equals(BorrowConstants.TYPE_INFO)){
                uploadImage(figureId);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            tip(Crop.getError(result).getMessage());
            isSaveedHead = false;
        }
    }

    public void uploadImage(final String figureId){
        FileUtils.uploadFile(context, Long.parseLong(figureId), imgPath,
                new FileMessageListener<FileTask>() {
            @Override
            public void success(int i, FileTask fileTask) {
                setResult(Activity.RESULT_OK);

                FigureMode figureMode=ContactManager.getInstance().getCurrentFigure(figureId);
                figureMode.setFigureImageid(fileTask.fileID);
                ContactManager.getInstance().addFigureTable(figureMode);
                LogCatLog.e(TAG,"fileId="+fileTask.fileID);
//                imgPath=FileUtils.IMG_CACHE_HEADIMAGE_PATH
//                        +AddressManager.addressManager.env+"_"+fileTask.fileID+".webp";
            }

            @Override
            public void handleing(int i, FileTask fileTask) {

            }

            @Override
            public void failure(int i, FileTask fileTask) {

            }
        });
    }


    private void beginCrop(Uri source) {
        int x = getResources().getDimensionPixelSize(R.dimen.head_image_height);
        Crop.of(source, mHeadImageUri).withMaxSize(x, x).asSquare().start(this);
    }


}
