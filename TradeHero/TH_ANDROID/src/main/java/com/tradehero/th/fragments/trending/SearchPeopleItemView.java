package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.GaussianTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.RoundedCornerTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.ImageUrlView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.persistence.user.UserSearchResultCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DateUtils;
import dagger.Lazy;
import java.util.concurrent.Future;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 3:39 PM To change this template use File | Settings | File Templates. */
public class SearchPeopleItemView extends FrameLayout implements DTOView<UserBaseKey>
{
    private static final String TAG = SearchPeopleItemView.class.getSimpleName();
    private static Transformation roundedShapeTransformation;
    private static Transformation backgroundTransformation;

    @Inject protected Picasso mPicasso;
    @Inject protected Lazy<UserSearchResultCache> userSearchResultCache;
    private TextView userName;
    private TextView profitIndicator;
    private TextView stockPercentage;
    private TextView date;
    //private CircularImageView userImage;
    private ImageUrlView userPhoto;
    private ImageUrlView peopleBgImage;
    private int defaultDrawable = R.drawable.superman_facebook;

    private UserBaseKey userKey;
    private UserSearchResultDTO userDTO;
    private boolean mAttachedToWindow;
    private int mVisibility;

    //<editor-fold desc="Constructors">
    public SearchPeopleItemView(Context context)
    {
        super(context);    
    }

    public SearchPeopleItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SearchPeopleItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        THLog.i(TAG, "onFinishInflate");
        super.onFinishInflate();
        DaggerUtils.inject(this);
        init();
    }

    protected void init ()
    {
        if (roundedShapeTransformation == null)
        {
            roundedShapeTransformation = new RoundedShapeTransformation();
        }
        if (backgroundTransformation == null)
        {
            backgroundTransformation = new AbstractSequentialTransformation()
            {
                @Override public String key()
                {
                    return "toRoundedGaussianGrayscale2";
                }
            };
            ((AbstractSequentialTransformation) backgroundTransformation).add(new GrayscaleTransformation());
            ((AbstractSequentialTransformation) backgroundTransformation).add(new GaussianTransformation());
            ((AbstractSequentialTransformation) backgroundTransformation).add(new RoundedCornerTransformation(
                    getResources().getDimensionPixelSize(R.dimen.trending_grid_item_corner_radius),
                    getResources().getColor(R.color.black)));
        }

        userName = (TextView) findViewById(R.id.user_name);
        profitIndicator = (TextView) findViewById(R.id.profit_indicator);
        stockPercentage = (TextView) findViewById(R.id.stock_percentage);
        date = (TextView) findViewById(R.id.date);
        //userImage = (CircularImageView) findViewById(R.id.user_image);
        userPhoto = (ImageUrlView) findViewById(R.id.user_photo);
        peopleBgImage = (ImageUrlView) findViewById(R.id.people_bg_image);
    }

    @Override protected void onAttachedToWindow()
    {
        THLog.i(TAG, "Attached to Window");
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        conditionalLoadImages();
    }

    @Override protected void onWindowVisibilityChanged(int visibility)
    {
        THLog.i(TAG, "Visibility changed " + visibility);
        super.onWindowVisibilityChanged(visibility);
        this.mVisibility = visibility;
        conditionalLoadImages();
    }

    @Override protected void onDetachedFromWindow()
    {
        THLog.i(TAG, "Detached from Window");
        mAttachedToWindow = false;
        this.userKey = null;
        super.onDetachedFromWindow();
    }

    public boolean isMyUrlOk()
    {
        return (userDTO != null) &&
                (userDTO.userPicture != null) && // Yes, some urls can be null
                (userDTO.userPicture.length() > 0);
    }

    @Override public void display(UserBaseKey user)
    {
        this.userKey = user;
        display(userSearchResultCache.get().get(this.userKey));
    }

    public void display(UserSearchResultDTO userSearchResultDTO)
    {
        this.userDTO = userSearchResultDTO;

        if (userSearchResultDTO == null)
        {
            return;
        }

        if (userName != null)
        {
            userName.setText(userSearchResultDTO.userthDisplayName);
        }

        if (date != null)
        {
            if (userSearchResultDTO.userMarkingAsOfUtc != null)
            {
                date.setText(DateUtils.getFormattedUtcDate(userSearchResultDTO.userMarkingAsOfUtc));
                date.setTextColor(Color.BLACK);
            }
            else
            {
                date.setText(R.string.na);
                date.setTextColor(Color.GRAY);
            }
        }

        int profitIndicatorResId = 0;
        int profitIndicatorVisibility = View.GONE;
        int profitIndicatorColor = Color.RED;
        String stockPercentageText;
        int stockPercentageColor = Color.RED;
        if (userSearchResultDTO.userRoiSinceInception != null)
        {
            double roi = userSearchResultDTO.userRoiSinceInception;
            if (!Double.isNaN(roi))
            {
                profitIndicatorVisibility = View.VISIBLE;
                roi = roi * 100;

                if (roi >= 1)
                {
                    profitIndicatorResId = R.string.arrow_prefix_positive;
                    profitIndicatorColor = Color.GREEN;
                    stockPercentageText = String.format("%.2f", roi) + "%";
                    stockPercentageColor = Color.GREEN;
                }
                else
                {
                    profitIndicatorResId = R.string.arrow_prefix_negative;
                    roi = Math.abs(roi);
                    stockPercentageText = String.format("%.2f", roi) + "%";
                }
            }
            else
            {
                stockPercentageText = getResources().getString(R.string.na);
            }
        }
        else
        {
            stockPercentageText = getResources().getString(R.string.na);
        }

        if (profitIndicator != null)
        {
            if (profitIndicatorResId != 0)
            {
                profitIndicator.setText(profitIndicatorResId);
            }
            else
            {
                profitIndicator.setText("");
            }
            profitIndicator.setVisibility(profitIndicatorVisibility);
            profitIndicator.setTextColor(profitIndicatorColor);
        }
        if (stockPercentage != null)
        {
            stockPercentage.setText(stockPercentageText);
            stockPercentage.setTextColor(stockPercentageColor);
        }

        conditionalLoadImages();
    }

    public boolean canDisplayImages()
    {
        return (mVisibility == VISIBLE) && mAttachedToWindow;
    }

    public void conditionalLoadImages()
    {
        if (canDisplayImages())
        {
            loadImages();
        }
    }

    public void loadImages()
    {
        if (userPhoto != null)
        {
            userPhoto.setUrl(userDTO.userPicture);
        }
        if (peopleBgImage != null)
        {
            peopleBgImage.setUrl(userDTO.userPicture);
        }

        mPicasso.load(defaultDrawable)
                .transform(roundedShapeTransformation)
                .into(userPhoto);
        mPicasso.load(defaultDrawable)
            .transform(backgroundTransformation)
            .into(peopleBgImage);

        if (isMyUrlOk())
        {
            final Callback loadIntoBg = createLogoReadyCallback();

            Future<?> submitted = KnownExecutorServices.getCacheExecutor().submit(new Runnable()
            {
                @Override public void run()
                {
                    if (userPhoto != null)
                    {
                        THLog.i(TAG, "Loading Fore for " + userPhoto.getUrl());
                        mPicasso.load(userPhoto.getUrl())
                                .error(defaultDrawable)
                                .transform(roundedShapeTransformation)
                                .into(userPhoto, loadIntoBg);
                    }
                }
            });
        }
    }

    private Callback createLogoReadyCallback()
    {
        return new Callback()
        {
            @Override public void onError()
            {
                loadBg();
            }

            @Override public void onSuccess()
            {
                loadBg();
            }

            public void loadBg ()
            {
                if (peopleBgImage != null)
                {
                    THLog.i(TAG, "Loading Bg for " + peopleBgImage.getUrl());
                    transformForBackground
                            (
                                    mPicasso.load(peopleBgImage.getUrl()).error(defaultDrawable)
                            )
                    .into(peopleBgImage);
                }
            }
        };
    }

    private RequestCreator transformForBackground(RequestCreator requestCreator)
    {
        return requestCreator
            .resize(getWidth(), getHeight())
            .centerCrop()
            .transform(backgroundTransformation);
    }
}
