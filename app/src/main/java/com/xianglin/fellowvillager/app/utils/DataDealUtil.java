package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建人：chengshengli
 * 创建时间：2016/3/4 11:01  11 01
 * 修改人：chengshengli
 * 修改时间：2016/3/4 11:01  11 01
 * 修改备注：
 */
public class DataDealUtil {

    private static final String TAG = DataDealUtil.class.getSimpleName();
    public static void setGender(TextView tv_sex, FigureMode.FigureGender gender) {
        LogCatLog.d(TAG,"gender name"+gender.name());
        if (gender == FigureMode.FigureGender.UNKNOWN) {
            tv_sex.setText("不明");
        } else if (gender == FigureMode.FigureGender.MALE) {
            tv_sex.setText("男");
        } else if (gender == FigureMode.FigureGender.FEMALE){
            tv_sex.setText("女");
        } else {
            tv_sex.setText("不公开");
        }
    }

    public static List<FigureMode> getAllFigureMode(){
        List<FigureMode> figureModeList=new ArrayList<FigureMode>();
        Map<String, FigureMode> figureModeMap= ContactManager.getInstance().getAllFigureTable();
        Iterator iterator=figureModeMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, FigureMode> entry = (Map.Entry)iterator.next();
            String key = entry.getKey();
            FigureMode value = entry.getValue();
            figureModeList.add(value);

        }
        return figureModeList;
    }

    public static List<FigureMode> getActiveFigureMode(){
        List<FigureMode> figureModeList=new ArrayList<FigureMode>();
        Map<String, FigureMode> figureModeMap= ContactManager.getInstance().getFigureTable();
        Iterator iterator=figureModeMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, FigureMode> entry = (Map.Entry)iterator.next();
            String key = entry.getKey();
            FigureMode value = entry.getValue();
            if(value.getFigureStatus()== FigureMode.Status.ACTIVE){
                figureModeList.add(value);
            }

        }
        return figureModeList;
    }

    public static void setGender(Button btn,FigureMode.Status status) {
        if (status == FigureMode.Status.ACTIVE) {
            btn.setText(btn.getContext().getString(R.string.str_fix_role));
        } else {
            btn.setText(btn.getContext().getString(R.string.str_free_role));
        }
    }

//    public static void showGenderImg(Context context,TextView textViewName,
//                                     FigureMode figureMode,boolean isRemark){
//
//        if(isRemark&& TextUtils.isEmpty(figureMode.getFigureXlremarks())){
//            textViewName.setVisibility(View.GONE);
//            return;
//        }
//        textViewName.setText(figureMode.getFigureName());
//        if(figureMode.getFigureGender().name().equals(FigureMode.FigureGender.MALE.name())){
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
//                    context.getResources().getDrawable(R.drawable.man), null);
//        }else if (figureMode.getFigureGender().name().equals(FigureMode.FigureGender.FEMALE.name())){
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
//                    context.getResources().getDrawable(R.drawable.woman), null);
//        }else if(figureMode.getFigureGender().name().equals(FigureMode.FigureGender.UNKNOWN.name())){
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null,null,
//                    context.getResources().getDrawable(R.drawable.unknow),null);
//        }else{
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null,null,
//                    null,null);
//        }
//    }
//
//    public static void showGenderImg(Context context,TextView textViewName,
//                                     Contact contact,boolean isRemark){
//
//        if(isRemark&&(contact==null||TextUtils.isEmpty(contact.xlReMarks))){
//            textViewName.setVisibility(View.GONE);
//            return;
//        }
//        textViewName.setText(contact.xlReMarks);
//        if(contact.gender.equals(FigureMode.FigureGender.MALE.name())){
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
//                    context.getResources().getDrawable(R.drawable.man), null);
//        }else if (contact.gender.equals(FigureMode.FigureGender.FEMALE.name())){
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
//                    context.getResources().getDrawable(R.drawable.woman), null);
//        }else if (contact.gender.equals(FigureMode.FigureGender.UNKNOWN.name())){
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
//                    context.getResources().getDrawable(R.drawable.unknow), null);
//        }else{
//            textViewName.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
//        }
//    }

    public static void showGenderImg(Context context,TextView textViewName,
                                     FigureMode figureMode){

        textViewName.setText(figureMode.getUIname());
        if(figureMode.getFigureGender().name().equals(FigureMode.FigureGender.MALE.name())){
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.man), null);
        }else if (figureMode.getFigureGender().name().equals(FigureMode.FigureGender.FEMALE.name())){
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.woman), null);
        }else if (figureMode.getFigureGender().name().equals(FigureMode.FigureGender.PRIVATE.name())){
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.private_icon), null);
        }else{
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null,null,
                    context.getResources().getDrawable(R.drawable.unknow), null);
        }
    }

    public static void showGenderImg(Context context,TextView textViewName,
                                     Contact contact){
        if (ContactManager.getInstance().getCurrentFigure() == null) {
            textViewName.setText(contact.xlUserName);
        } else {
            textViewName.setText(TextUtils.isEmpty(contact.xlReMarks)?contact.xlUserName:contact.xlReMarks);
        }
        if(contact.gender.equals(FigureMode.FigureGender.MALE.name())){
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.man), null);
        }else if (contact.gender.equals(FigureMode.FigureGender.FEMALE.name())){
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.woman), null);
        }else if (contact.gender.equals(FigureMode.FigureGender.PRIVATE.name())){
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.private_icon), null);
        }else{
            textViewName.setCompoundDrawablesWithIntrinsicBounds(null,null,
                    context.getResources().getDrawable(R.drawable.unknow), null);
        }
    }

    public static  void setEstablishType(TextView textView,Contact.RelationEstablishType shipInfo){
        if(shipInfo== Contact.RelationEstablishType.PHONE_CONCACTS){
            textView.setText("通讯录");
        }else if(shipInfo== Contact.RelationEstablishType.NAME_CARD){
            textView.setText("好友推荐");
        }else if(shipInfo== Contact.RelationEstablishType.QRCODE){
            textView.setText("扫一扫");
        }else{
            textView.setText("附近的人");
        }
    }

    /**
     * 获取是否是私密模式
     * @param currentFigureId
     * @param figureUserId
     * @return
     */
    public static boolean isSecretMode(String currentFigureId,String figureUserId){
        String save_key=currentFigureId+"_"+figureUserId+"_mode";
        return Utils.getBooleanValue(save_key);
    }

    /**
     * 设置私密聊天模式
     * @param currentFigureId
     * @param figureUserId
     * @param isSecret
     */
    public static void setIsSecretMode(String currentFigureId,String figureUserId,boolean isSecret){
        String save_key=currentFigureId+"_"+figureUserId+"_mode";
        Utils.putBooleanValue(save_key,isSecret);
    }
    public static  String[] time_list={"10秒","20秒","30秒","1分钟"};
    public static  int[] TIME_COUNT={10,20,30,60};
    public static String getSecretTime(String currentFigureId,String figureUserId){
        String save_key=currentFigureId+"_"+figureUserId;
        int secret_index=getSecretIndex(currentFigureId,figureUserId);
        if(secret_index==-1){
            return "30s";
        }
        return time_list[secret_index];
    }

    /**
     * 获取私密消息索引位置
     * @param currentFigureId
     * @param figureUserId
     * @return
     */
    public static int getSecretIndex(String currentFigureId,String figureUserId){
        String save_key=currentFigureId+"_"+figureUserId;
        return Utils.getIntValue(save_key,2);//默认30s
    }

    /**
     * 保存私密消息索引 对应 time_list
     * @param currentFigureId
     * @param figureUserId
     * @param index
     */
    public static void setSecretTime(String currentFigureId,String figureUserId,int index){
        String save_key=currentFigureId+"_"+figureUserId;
        Utils.putIntValue(save_key, index);
    }

    public static void saveChatLeavePosition(String currentFigureId,String figureUserId,
                                             MessageBean messageBean){
        String save_key=currentFigureId+"_"+figureUserId+"_leave";
        Utils.putValue(save_key, messageBean.msgKey);
    }

    public static String getChatLeavePosition(String currentFigureId,String figureUserId){
        String save_key=currentFigureId+"_"+figureUserId+"_leave";
        return Utils.getValue(save_key);
    }

    public static void saveChatLeaveNoSecretPosition(String currentFigureId,String figureUserId,
                                             MessageBean messageBean){
        String save_key=currentFigureId+"_"+figureUserId+"_nosecret_leave";
        Utils.putValue(save_key, messageBean.msgKey);
    }

    public static String getChatLeaveNoSecretPosition(String currentFigureId,String figureUserId){
        String save_key=currentFigureId+"_"+figureUserId+"_nosecret_leave";
        return Utils.getValue(save_key);
    }


}
