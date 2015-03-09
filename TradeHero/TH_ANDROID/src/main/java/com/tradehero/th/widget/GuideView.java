package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th.R;

/**
 * Created by palmer on 14-11-4.
 */
public class GuideView extends View {

    private int position_x_a = -1;
    private int position_y_a = -1;
    private int radius_a = -1;

    private int screen_width;

    private Paint mPaint;

    private Bitmap bitmap = null;
    private Canvas mCanvas = null;

    private Bitmap bitmap_a = null;
    private Bitmap bitmap_b = null;
    private Rect src = new Rect();
    private Rect dst = new Rect();

    private int current_type = -1;

    public final static int TYPE_GUIDE_STOCK_BUY = 2;
    public final static int TYPE_GUIDE_COMPETITION_EDIT = 3;

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAntiAlias(true);

        setFocusable(true);
    }

    public void draw(int position_y_a, int screen_width, int screen_height, int type){
        current_type = type;

        this.position_y_a = position_y_a;
        this.screen_width = screen_width;

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.guide_bg);
        bitmap = Bitmap.createBitmap(screen_width, screen_height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(bitmap);
        mCanvas.drawBitmap(bm, new Rect(0, 0, bm.getWidth(), bm.getHeight()), new Rect(0, 0, screen_width, screen_height), null);

        if(type == TYPE_GUIDE_COMPETITION_EDIT){
            bitmap_a = BitmapFactory.decodeResource(getResources(), R.drawable.guide_competition_edit_intro);
        }
        invalidate();
    }

    public void draw(int position_x_a, int position_y_a, int radius_a, int screen_width, int screen_height, int type) {
        current_type = type;

        this.position_x_a = position_x_a;
        this.position_y_a = position_y_a;
        this.radius_a = radius_a;
        this.screen_width = screen_width;

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.guide_bg);
        bitmap = Bitmap.createBitmap(screen_width, screen_height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(bitmap);
        mCanvas.drawBitmap(bm, new Rect(0, 0, bm.getWidth(), bm.getHeight()), new Rect(0, 0, screen_width, screen_height), null);

        if (type == TYPE_GUIDE_STOCK_BUY) {
            bitmap_a = BitmapFactory.decodeResource(getResources(), R.drawable.guide_stock_buy);
            bitmap_b = BitmapFactory.decodeResource(getResources(), R.drawable.guide_stock_bug_gesture);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (current_type == TYPE_GUIDE_STOCK_BUY) {
            mCanvas.drawRect(new Rect(0, position_y_a - radius_a, screen_width, position_y_a + radius_a), mPaint);
            canvas.drawBitmap(bitmap, 0, 0, null);
            int position_bitmap_a_x = position_x_a - 20;
            int position_bitmap_a_y = position_y_a - radius_a - bitmap_a.getHeight();
            canvas.drawBitmap(bitmap_a, position_bitmap_a_x, position_bitmap_a_y, null);

            int bitmapBWidth = bitmap_b.getWidth();
            int bitmapBHeight = bitmap_b.getHeight();
            int beginY = getHeight()/4;
            int beginX = (getWidth() - bitmapBWidth/2)/2;
            src.set(0,0, bitmapBWidth, bitmapBHeight);
            dst.set(beginX, beginY, beginX + bitmapBWidth/2, beginY + bitmapBHeight/2);
            canvas.drawBitmap(bitmap_b, src, dst, null);
        }

        if(current_type == TYPE_GUIDE_COMPETITION_EDIT){
            canvas.drawBitmap(bitmap, 0, 0, null);
            int bitmapHeight = bitmap_a.getHeight();
            int bitmapWidth = bitmap_a.getWidth();
            src.set(0, 0, bitmapWidth, bitmapHeight);
            dst.set(60, position_y_a, 60 + bitmapWidth*2/3, position_y_a + bitmapHeight*2/3);
            canvas.drawBitmap(bitmap_a, src, dst, null);
        }
    }

    public int getType() {
        return current_type;
    }

}