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


    @InjectView(R.id.textview_discovery_discuss_send_share_wechat)TextView shareToWechat;
    @InjectView(R.id.textview_discovery_discuss_send_share_moment)TextView shareToMoment;
    @InjectView(R.id.textview_discovery_discuss_send_delete)TextView deleteTV;
    @InjectView(R.id.textview_discovery_discuss_send_report)TextView reportTV;
    @InjectView(R.id.view_divider_discuss_delete)View deleteDividerV;
    @InjectView(R.id.view_divider_discuss_report)View reportDividerV;

    //For Administrator
    @InjectView(R.id.linearlayout_administrator_manage_timeline)LinearLayout managerLL;
    @InjectView(R.id.textview_administrator_top)TextView topTV;
    @InjectView(R.id.textview_administrator_learning)TextView learningTV;
    @InjectView(R.id.textview_administrator_favorite)TextView favoriteTV;
    @InjectView(R.id.textview_administrator_production)TextView productionTV;
    @InjectView(R.id.textview_administrator_delete_timeline)TextView deleteTimeLineTV;

    @InjectView(R.id.view_divider_administrator_top)View topDividerV;
    @InjectView(R.id.view_divider_administrator_learning)View learningDividerV;


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
        shareToWechat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onShareToWechatClick();
                }
            }
        });
        shareToMoment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onShareToMomentClick();
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
        topTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onTopClick();
                }
            }
        });
        favoriteTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onFavoriteClick();
                }
            }
        });
        productionTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onProductionClick();
                }
            }
        });
        learningTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener != null) {
                    menuClickListener.onLearningClick();
                }
            }
        });
        deleteTimeLineTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuClickListener!=null) {
                    menuClickListener.onDeleteTimeLineClick();
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

    public void setBtnStatus(boolean isDeleteAllowed, boolean isReportAllowed, boolean isManager, boolean isTop, boolean isProduction, boolean isFavorite, boolean isLearning) {
        if (isDeleteAllowed && deleteTV != null && deleteDividerV!=null) {
            deleteTV.setVisibility(View.VISIBLE);
            deleteDividerV.setVisibility(View.VISIBLE);
        }else {
            deleteTV.setVisibility(View.GONE);
            deleteDividerV.setVisibility(View.GONE);
        }
        if (isReportAllowed && reportTV != null && reportDividerV!=null) {
            reportTV.setVisibility(View.VISIBLE);
            reportDividerV.setVisibility(View.VISIBLE);
        }else{
            reportTV.setVisibility(View.GONE);
            reportDividerV.setVisibility(View.GONE);
        }
        if(managerLL==null){
            return;
        }
        if(isManager){
            managerLL.setVisibility(View.VISIBLE);
            if(isTop){
                topTV.setText(R.string.administrator_remove_top);
                topTV.setVisibility(View.VISIBLE);
                topDividerV.setVisibility(View.VISIBLE);
            }else{
                topTV.setText(R.string.administrator_add_top);
                topTV.setVisibility(View.VISIBLE);
                topDividerV.setVisibility(View.VISIBLE);
            }
            if(isProduction){
                productionTV.setText(R.string.administrator_remove_production);
            }else{
                productionTV.setText(R.string.administrator_add_production);
            }
            if(isFavorite){
                favoriteTV.setText(R.string.administrator_remove_favorite);
            }else {
                favoriteTV.setText(R.string.administrator_add_favorite);
            }
            if(isLearning){
                learningTV.setText(R.string.administrator_remove_learning);
            }else{
                learningTV.setText(R.string.administrator_add_learning);
            }
        }else {
            managerLL.setVisibility(View.GONE);
        }
    }


    public interface TimeLineDetailMenuClickListener {
        public void onReportClick();
        public void onDeleteClick();
        public void onShareToWechatClick();
        public void onShareToMomentClick();
        public void onFavoriteClick();
        public void onProductionClick();
        public void onTopClick();
        public void onLearningClick();
        public void onDeleteTimeLineClick();
    }
}
