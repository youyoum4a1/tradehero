package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.loopj.android.image.SmartImageView;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.graphics.GaussianGrayscaleTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.ImageUrlView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.application.CircularImageView;
import com.tradehero.th.models.User;
import com.tradehero.th.utills.DateUtils;
import com.tradehero.th.utills.YUtils;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 3:39 PM To change this template use File | Settings | File Templates. */
public class TrendingUserView extends FrameLayout implements DTOView<UserSearchResultDTO>
{
    private static final String TAG = TrendingUserView.class.getSimpleName();
    private static Picasso mPicasso;

    private TextView userName;
    private TextView profitIndicator;
    private TextView stockPercentage;
    private TextView date;
    private CircularImageView userImage;
    private SmartImageView peopleBgImage;


    //<editor-fold desc="Constructors">
    public TrendingUserView(Context context)
    {
        super(context);    
    }

    public TrendingUserView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TrendingUserView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    protected void init ()
    {
        if (mPicasso == null)
        {
            Cache lruFileCache = null;
            try
            {
                lruFileCache = new LruMemFileCache(getContext());
            }
            catch (Exception e)
            {
                THLog.e(TAG, "Failed to create LRU", e);
            }

            mPicasso = new Picasso.Builder(getContext())
                    .downloader(new UrlConnectionDownloader(getContext()))
                    .memoryCache(lruFileCache)
                    .build();
            mPicasso.setDebugging(true);
        }

        userName = (TextView) findViewById(R.id.user_name);
        profitIndicator = (TextView) findViewById(R.id.profit_indicator);
        stockPercentage = (TextView) findViewById(R.id.stock_percentage);
        date = (TextView) findViewById(R.id.date);
        userImage = (CircularImageView) findViewById(R.id.user_image);
        peopleBgImage = (SmartImageView) findViewById(R.id.people_bg_image);
    }

    @Override protected void onFinishInflate()
    {
        THLog.i(TAG, "OnFinishInflate");
        super.onFinishInflate();
        init();
    }

    @Override public void display(UserSearchResultDTO user)
    {
        userName.setText(user.userthDisplayName);

        if (user.userMarkingAsOfUtc != null && user.userMarkingAsOfUtc.length() > 0)
        {
            date.setText(DateUtils.getFormatedTrendDate(user.userMarkingAsOfUtc));
            date.setTextColor(Color.BLACK);
        }
        else
        {
            date.setText("N/A");
            date.setTextColor(Color.GRAY);
        }

        if (user.userRoiSinceInception != null)
        {
            double roi = user.userRoiSinceInception.doubleValue();
            if (!Double.isNaN(roi))
            {
                profitIndicator.setVisibility(View.VISIBLE);
                roi = roi * 100;

                if (roi >= 1)
                {
                    profitIndicator.setText(getContext().getString(R.string.positive_prefix));
                    profitIndicator.setTextColor(Color.GREEN);
                    stockPercentage.setText(String.format("%.2f", roi) + "%");
                    stockPercentage.setTextColor(Color.GREEN);
                }
                else
                {
                    profitIndicator.setText(getContext().getString(R.string.negative_prefix));
                    profitIndicator.setTextColor(Color.RED);
                    roi = Math.abs(roi);
                    stockPercentage.setText(String.format("%.2f", roi) + "%");
                    stockPercentage.setTextColor(Color.RED);
                }
            }
            else
            {
                profitIndicator.setVisibility(View.GONE);
                stockPercentage.setText("N/A");
                stockPercentage.setTextColor(Color.RED);
            }
        }
        else
        {
            profitIndicator.setVisibility(View.GONE);
            stockPercentage.setText("N/A");
            stockPercentage.setTextColor(Color.RED);
        }

        if (user.userPicture != null && user.userPicture.length() > 0)
        {
            // TODO
        }
    }
}
