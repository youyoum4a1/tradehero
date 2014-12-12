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
 * Dialog for TimeLine Detail Menu
 *
 * Created by palmer on 14/12/12.
 */
public class TimeLineDetailDialogLayout extends LinearLayout {


    @InjectView(R.id.textview_discovery_discuss_send_share)
    TextView shareTV;
    @InjectView(R.id.textview_discovery_discuss_send_delete)
    TextView deleteTV;
    @InjectView(R.id.textview_discovery_discuss_send_report)
    TextView reportTV;

    private TimeLineDetailMenuClickListener menuClickListener;


    public TimeLineDetailDialogLayout(Context context) {
        super(context);
    }

    public TimeLineDetailDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLineDetailDialogLayout(Context context, AttributeSet attrs, int defStyle) {
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
        shareTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onShareClick();
                }
            }
        });
        deleteTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onDeleteClick();
                }
            }
        });
        reportTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onReportClick();
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setMenuClickListener(TimeLineDetailMenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    public void setBtnStatus(boolean isDeleteAllowed, boolean isReportAllowed) {
        if (isDeleteAllowed && deleteTV != null) {
            deleteTV.setVisibility(View.VISIBLE);
        }else {
            deleteTV.setVisibility(View.GONE);
        }
        if (isReportAllowed && reportTV != null) {
            reportTV.setVisibility(View.VISIBLE);
        }else{
            reportTV.setVisibility(View.GONE);
        }
    }


    public interface TimeLineDetailMenuClickListener {
        public void onReportClick();

        public void onDeleteClick();

        public void onShareClick();
    }
}
