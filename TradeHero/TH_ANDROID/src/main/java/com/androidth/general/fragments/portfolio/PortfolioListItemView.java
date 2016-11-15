package com.androidth.general.fragments.portfolio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.androidth.general.activities.SignUpLiveActivity;
import com.androidth.general.api.portfolio.LiveAccountPortfolioItemHeader;
import com.androidth.general.fragments.education.VideoView;
import com.androidth.general.models.live.THLiveManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.portfolio.DisplayablePortfolioDTO;
import com.androidth.general.api.portfolio.DisplayablePortfolioUtil;
import com.androidth.general.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOUtil;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedPercentage;
import com.androidth.general.utils.DateUtils;
import com.androidth.general.utils.GraphicUtil;
import javax.inject.Inject;
import rx.Subscription;

public class PortfolioListItemView extends RelativeLayout
        implements DTOView<DisplayablePortfolioDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;

    @Bind(R.id.portfolio_title) protected TextView title;
    @Bind(R.id.portfolio_description) protected TextView description;
    @Bind(R.id.roi_value) @Nullable protected TextView roiValue;
    @Bind(R.id.portfolio_image) @Nullable protected ImageView portfolioImage;
    @Bind(R.id.portfolio_live_video) protected android.widget.VideoView liveAccountVideo;
    @Bind(R.id.portfolio_common_item) protected RelativeLayout commonRowItem;

    private DisplayablePortfolioDTO displayablePortfolioDTO;
    @Nullable private Subscription userWatchlistSubscription;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PortfolioListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        unsubscribe(userWatchlistSubscription);
        userWatchlistSubscription = null;

        if (portfolioImage != null)
        {
            picasso.cancelRequest(portfolioImage);
        }
        super.onDetachedFromWindow();
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public void display(DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        this.displayablePortfolioDTO = displayablePortfolioDTO;

        displayTitle();
        displayDescription();
        displayRoiValue();
        displayImage();
    }

    public void display(Context context, LiveAccountPortfolioItemHeader liveAccountPortfolioItemHeader){

        if(THLiveManager.getInstance().getBrokerApplicationDTO()!=null){
            title.setText("TradeHero Live");
            description.setText(THLiveManager.getInstance().getBrokerApplicationDTO().applicationStatus);

        }else{
            setupLiveVideo(context, liveAccountPortfolioItemHeader);
        }
    }

    private void displayImage()
    {
        if (portfolioImage != null)
        {
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
            {
                final PortfolioDTO portfolioDTO = displayablePortfolioDTO.portfolioDTO;
                int imageResId = PortfolioCompactDTOUtil.getIconResId(portfolioDTO);
                picasso.load(imageResId)
                        .into(new Target()
                        {
                            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                            {
                                portfolioImage.setImageBitmap(bitmap);
                                Integer colorResId = PortfolioCompactDTOUtil.getIconTintResId(portfolioDTO);
                                if (colorResId != null)
                                {
                                    GraphicUtil.applyColorFilter(portfolioImage,
                                            getResources().getColor(colorResId));
                                }
                            }

                            @Override public void onBitmapFailed(Drawable errorDrawable)
                            {
                            }

                            @Override public void onPrepareLoad(Drawable placeHolderDrawable)
                            {
                            }
                        });
            }
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayTitle();
        displayDescription();
        displayRoiValue();
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(DisplayablePortfolioUtil.getLongTitle(getResources(),
                    displayablePortfolioDTO));
            title.setTextColor(DisplayablePortfolioUtil.getLongTitleTextColor(getContext(),
                    displayablePortfolioDTO));
        }
    }

    public void displayDescription()
    {
        TextView descriptionCopy = this.description;
        if (descriptionCopy != null)
        {
            String descriptionText = getDescription();
            descriptionCopy.setText(descriptionText);
            description.setVisibility(TextUtils.isEmpty(descriptionText) ? GONE : VISIBLE);
        }
    }

    public String getDescription()
    {
        return DisplayablePortfolioUtil.getLongSubTitle(getResources(), currentUserId, displayablePortfolioDTO);
    }

    public void displayRoiValue()
    {
        if (roiValue != null)
        {
            if (displayablePortfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO.roiSinceInception != null)
            {
                String format = displayablePortfolioDTO.portfolioDTO.isDefault()
                        ? getContext().getString(R.string.roi_since_inception_format)
                        : getContext().getString(R.string.roi_since_format, "%1$s",
                                DateUtils.getDisplayableDate(getContext().getResources(), displayablePortfolioDTO.portfolioDTO.getCreationDate(),
                                        R.string.data_format_d_mmm_yyyy));
                THSignedPercentage.builder(displayablePortfolioDTO.portfolioDTO.roiSinceInception * 100)
                        .withSign()
                        .signTypePlusMinusAlways()
                        .withDefaultColor()
                        .boldSign()
                        .boldValue()
                        .format(format)
                        .build()
                        .into(roiValue);
                roiValue.setVisibility(VISIBLE);
            }
            else if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
            {
                roiValue.setVisibility(GONE);
            }
            else if (displayablePortfolioDTO != null
                    && displayablePortfolioDTO.portfolioDTO != null
                    && displayablePortfolioDTO.portfolioDTO.isWatchlist)
            {
                roiValue.setVisibility(GONE);
            }
            else
            {
                roiValue.setVisibility(VISIBLE);
                roiValue.setText(R.string.na);
            }
        }
    }
    //</editor-fold>

    private void setupLiveVideo(Context context, LiveAccountPortfolioItemHeader liveAccountPortfolioItemHeader){
        liveAccountVideo.setVisibility(VISIBLE);
        String path = "android.resource://"+ context.getPackageName() +"/"+ R.raw.live_account_status_fresh;
        liveAccountVideo.setVideoPath(path);

        liveAccountVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        liveAccountVideo.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(context, SignUpLiveActivity.class);
                context.startActivity(intent);
                return false;
            }
        });

        commonRowItem.setVisibility(View.GONE);

        liveAccountVideo.start();

    }
}
