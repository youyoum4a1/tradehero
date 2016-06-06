package com.androidth.general.fragments.trending;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.users.UserSearchResultDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.graphics.ForSearchPeopleItemBackground;
import com.androidth.general.models.graphics.ForUserPhoto;
import com.androidth.general.utils.DateUtils;
import javax.inject.Inject;

public class SearchPeopleItemView extends FrameLayout implements DTOView<UserSearchResultDTO>
{
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject @ForSearchPeopleItemBackground Transformation backgroundTransformation;

    @Inject protected Picasso mPicasso;
    private TextView userName;
    private TextView profitIndicator;
    private TextView stockPercentage;
    private TextView date;
    private ImageView userPhoto;
    private ImageView peopleBgImage;
    private final int defaultDrawable = R.drawable.superman_facebook;

    @Nullable private UserSearchResultDTO userDTO;

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
        super.onFinishInflate();
        init();
    }

    protected void init()
    {
        HierarchyInjector.inject(this);
        userName = (TextView) findViewById(R.id.user_name);
        profitIndicator = (TextView) findViewById(R.id.profit_indicator);
        stockPercentage = (TextView) findViewById(R.id.stock_percentage);
        date = (TextView) findViewById(R.id.date);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        if (userPhoto != null)
        {
            userPhoto.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        peopleBgImage = (ImageView) findViewById(R.id.people_bg_image);
        if (peopleBgImage != null)
        {
            peopleBgImage.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        loadImages();
    }

    @Override protected void onDetachedFromWindow()
    {
        this.userDTO = null;
        loadDefaultImage();
        loadDefaultBackground();
        super.onDetachedFromWindow();
    }

    public boolean isMyUrlOk()
    {
        return (userDTO != null) &&
                (userDTO.userPicture != null) && // Yes, some urls can be null
                (userDTO.userPicture.length() > 0);
    }

    @Override public void display(@NonNull UserSearchResultDTO userSearchResultDTO)
    {
        this.userDTO = userSearchResultDTO;

        if (userName != null)
        {
            userName.setText(userSearchResultDTO.userthDisplayName);
        }

        if (date != null)
        {
            if (userSearchResultDTO.userMarkingAsOfUtc != null)
            {
                date.setText(DateUtils.getFormattedUtcDate(getResources(), userSearchResultDTO.userMarkingAsOfUtc));
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

        loadImages();
    }

    public void loadImages()
    {
        if (userPhoto != null && userDTO != null)
        {
            if (isMyUrlOk())
            {
                mPicasso.load(userDTO.userPicture)
                        .transform(peopleIconTransformation)
                        .into(userPhoto, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                loadDefaultImage();
                            }
                        });
            }
            else
            {
                loadDefaultImage();
            }
        }

        if (peopleBgImage != null && userDTO != null)
        {
            if (isMyUrlOk())
            {
                mPicasso.load(userDTO.userPicture)
                        .transform(backgroundTransformation)
                        .into(peopleBgImage, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                loadDefaultBackground();
                            }
                        });
            }
            else
            {
                loadDefaultBackground();
            }
        }
    }

    private void loadDefaultImage()
    {
        if (userPhoto != null)
        {
            mPicasso.load(defaultDrawable)
                    .transform(peopleIconTransformation)
                    .into(userPhoto);
        }
    }

    private void loadDefaultBackground()
    {
        if (peopleBgImage != null)
        {
            mPicasso.load(defaultDrawable)
                    .transform(backgroundTransformation)
                    .into(peopleBgImage);
        }
    }
}
