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
public class TimeLineCommentDialogLayout extends LinearLayout {

    @InjectView(R.id.textview_discovery_discuss_send_comment)TextView commentTV;
    @InjectView(R.id.textview_discovery_discuss_send_report)TextView reportTV;
    @InjectView(R.id.view_divider_discuss_report)View reportDividerV;
    @InjectView(R.id.textview_discovery_discuss_send_delete)TextView deleteTV;
    @InjectView(R.id.view_divider_discuss_delete)View deleteDividerV;
    @InjectView(R.id.textview_discovery_discuss_send_apply)TextView applyTV;
    @InjectView(R.id.view_divider_discuss_apply)View applyDividerV;

    private TimeLineCommentMenuClickListener menuClickListener;

    public TimeLineCommentDialogLayout(Context context) {
        super(context);
    }

    public TimeLineCommentDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLineCommentDialogLayout(Context context, AttributeSet attrs, int defStyle) {
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
        commentTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onCommentClick();
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
        applyTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onApplyClick();
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setMenuClickListener(TimeLineCommentMenuClickListener menuClickListener){
        this.menuClickListener = menuClickListener;
    }

    public void setBtnStatus(boolean isReportAllowed, boolean isDeleteAllowed, boolean isApplyAllowed){
        if(isReportAllowed && reportTV !=null && reportDividerV!=null){
            reportTV.setVisibility(View.VISIBLE);
            reportDividerV.setVisibility(View.VISIBLE);
        }else{
            reportTV.setVisibility(View.GONE);
            reportDividerV.setVisibility(View.GONE);
        }
       if(isDeleteAllowed && deleteDividerV!=null && deleteTV!=null){
           deleteTV.setVisibility(View.VISIBLE);
           deleteDividerV.setVisibility(View.VISIBLE);
       }else{
           deleteDividerV.setVisibility(View.GONE);
           deleteTV.setVisibility(View.GONE);
       }
        if(isApplyAllowed && applyDividerV!=null && applyTV!=null){
            applyDividerV.setVisibility(View.VISIBLE);
            applyTV.setVisibility(View.VISIBLE);
        }else{
            applyDividerV.setVisibility(View.GONE);
            applyTV.setVisibility(View.GONE);
        }

    }


    public interface TimeLineCommentMenuClickListener{
        public void onCommentClick();
        public void onReportClick();
        public void onDeleteClick();
        public void onApplyClick();
    }
}
