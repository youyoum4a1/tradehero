package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.tradehero.th.R;

/**
 * Created by palmer on 14/11/25.
 */
public class ABCDView extends View {

    private final String[] dividers = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};

    private Paint paint = new Paint();

    private OnCharTouchListener listener;

    private int textColor;

    public ABCDView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textColor = context.getResources().getColor(R.color.divider_text_color);
        paint.setColor(textColor);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int height = this.getHeight();
        int width = this.getWidth();
        int unit = height/(dividers.length* 5 +1);
        int textSize = unit * 4;
        if(width<textSize){
            unit = width/6;
            textSize = unit * 4;
        }
        paint.setTextSize(textSize);
        for(int num=0;num<dividers.length;num++) {
            canvas.drawText(dividers[num], (width - textSize)/2, unit*5*(num+1)+10, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getX()<0||event.getY()<0){
            listener.onTouchCancel();
            return true;
        }
        if(listener!=null){
            int height = (int)event.getY();
            int unit = (getHeight()-10)/27;
            int charIndex = height/unit;
            int extra = height/unit;
            if(extra==0){
                charIndex = charIndex - 1;
            }
            if(charIndex<0){
                charIndex = 0;
            }
            if(charIndex>26){
                charIndex = 26;
            }
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                listener.onTouchDown(dividers[charIndex]);
            }
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                listener.onTouchMove(dividers[charIndex]);
            }
            if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                listener.onTouchCancel();
            }
            if(event.getAction()== MotionEvent.ACTION_UP){
                listener.onTouchUp(dividers[charIndex]);
            }
            return true;
        }
        return true;
    }

    public interface OnCharTouchListener{

        public void onTouchDown(String divider);

        public void onTouchUp(String divider);

        public void onTouchCancel();

        public void onTouchMove(String divider);
    }


    public OnCharTouchListener getListener() {
        return listener;
    }

    public void setListener(OnCharTouchListener listener) {
        this.listener = listener;
    }

}
