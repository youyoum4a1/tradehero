package com.tradehero.chinabuild.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;

/**
 * Created by palmer on 14/12/12.
 */
public class TimeLineReportDialogLayout extends LinearLayout{

    private TimeLineReportMenuClickListener menuClickListener;

    @InjectView(R.id.textview_discovery_discuss_send_report_a)TextView reportATV;
    @InjectView(R.id.textview_discovery_discuss_send_report_b)TextView reportBTV;
    @InjectView(R.id.textview_discovery_discuss_send_report_c)TextView reportCTV;
    @InjectView(R.id.textview_discovery_discuss_send_report_d)TextView reportDTV;

    public TimeLineReportDialogLayout(Context context) {
        super(context);
    }

    public TimeLineReportDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLineReportDialogLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        reportATV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuClickListener!=null){
                    menuClickListener.onItemClickListener(1);
                }
            }
        });
        reportBTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuClickListener!=null){
                    menuClickListener.onItemClickListener(2);
                }
            }
        });
        reportCTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuClickListener!=null){
                    menuClickListener.onItemClickListener(3);
                }
            }
        });
        reportDTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuClickListener!=null){
                    menuClickListener.onItemClickListener(4);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setMenuClickListener(TimeLineReportMenuClickListener menuClickListener){
        this.menuClickListener = menuClickListener;
    }

    public interface TimeLineReportMenuClickListener{
        public void onItemClickListener(int position);
    }

}
