package com.xianglin.fellowvillager.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形头像
 * 重写CircleImage，继承ImageView 
 * @author zhangxiang
 *
 */
public class CircleImage extends ImageView {  
    public CircleImage(Context context) {  
        super(context);  
    }  
  
    public CircleImage(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public CircleImage(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        Drawable drawable = getDrawable();  
        if (drawable == null) {  
            return;  
        }  
  
        if (getWidth() == 0 || getHeight() == 0) {  
            return;   
        }  
        BitmapDrawable bd = (BitmapDrawable)drawable;
        Bitmap b =  bd.getBitmap() ;
        if(b!=null) {
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

            Bitmap roundBitmap = getCroppedBitmap(bitmap, getWidth());
            canvas.drawBitmap(roundBitmap, 0, 0, null);
        }
    }  
    /* 
     * 对Bitmap裁剪，使其变成圆形，这步最关键 
     */  
    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {  
        Bitmap sbmp;  
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)  
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);  
        else  
            sbmp = bmp;  
  
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);  
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());  
  
        Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        paint.setFilterBitmap(true);  
        paint.setDither(true);        
        paint.setColor(Color.parseColor("#BAB399"));  
  
        Canvas c = new Canvas(output);          
        c.drawARGB(0, 0, 0, 0);  
        c.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f, sbmp.getWidth() / 2+0.1f, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        c.drawBitmap(sbmp, rect, rect, paint);  
  
        return output;  
    }  
}  
