package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;


public class AdView extends RelativeLayout
    implements DTOView<CompetitionZoneAdvertisementDTO>
{
    @InjectView(R.id.banner) ImageView banner;
    private AdDTO adDTO;

    @OnClick(R.id.banner) void onBannerClicked(ImageView banner)
    {
        if (adDTO != null)
        {
            Bundle bundle = new Bundle();
            String url = adDTO.redirectUrl + String.format("&userId=%d", currentUserId.get());
            bundle.putString(WebViewFragment.BUNDLE_KEY_URL, url);
            getNavigator().pushFragment(WebViewFragment.class, bundle);
        }
    }

    private Navigator getNavigator()
    {
        return ((NavigatorActivity) getContext()).getNavigator();
    }

    @Inject Lazy<Picasso> picasso;
    @Inject CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public AdView(Context context)
    {
        super(context);
    }

    public AdView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AdView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override public void display(CompetitionZoneAdvertisementDTO competitionZoneAdvertisementDTO)
    {
        if (competitionZoneAdvertisementDTO != null)
        {
            linkWith(competitionZoneAdvertisementDTO.getAdDTO(), true);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void linkWith(AdDTO adDTO, boolean andDisplay)
    {
        if (adDTO != null)
        {
            this.adDTO = adDTO;
        }
        else
        {
            this.adDTO = null;
        }

        if (andDisplay)
        {
            if (adDTO != null)
            {
                picasso.get().load(adDTO.bannerImageUrl)
                        .into(banner);
            }
            else
            {
                banner.setImageDrawable(null);
            }
        }
    }
}
