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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.analytics.AnalyticsEventForm;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.DateUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Date;
import javax.inject.Inject;

public class AdView extends RelativeLayout
        implements DTOView<CompetitionZoneDTO>
{
    @InjectView(R.id.banner) ImageView banner;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject Context context;
    private AdDTO adDTO;
    private int providerId;
    @Inject DashboardNavigator navigator;

    @OnClick(R.id.banner) void onBannerClicked(ImageView banner)
    {
        if (adDTO != null)
        {
            Bundle bundle = new Bundle();
            String url = adDTO.redirectUrl + String.format("&userId=%d", currentUserId.get());
            WebViewFragment.putUrl(bundle, url);
            navigator.pushFragment(WebViewFragment.class, bundle);
            sendAnalytics(adDTO, "proceed");
        }
    }

    @Inject Lazy<Picasso> picasso;
    @Inject CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public AdView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(CompetitionZoneDTO competitionZoneDTO)
    {
        display((CompetitionZoneAdvertisementDTO) competitionZoneDTO);
    }

    public void display(CompetitionZoneAdvertisementDTO competitionZoneAdvertisementDTO)
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
                // Ok, this is the only way I found to workaround this problem, converting url to a filename, and manually put 9-patch image
                // with that name to android resource folder.
                String bannerResourceFileName = getResourceFileName(adDTO.bannerImageUrl);
                int bannerResourceId = 0;
                if (bannerResourceFileName != null &&
                        (bannerResourceId = getResources().getIdentifier(bannerResourceFileName, "drawable", getContext().getPackageName())) != 0)
                {
                    banner.setBackgroundResource(bannerResourceId);
                }
                else
                {
                    picasso.get().cancelRequest(banner);
                    picasso.get().load(adDTO.bannerImageUrl)
                            .into(banner);
                }

                sendAnalytics(adDTO, "served");
            }
            else
            {
                banner.setImageDrawable(null);
            }
        }
    }

    private void sendAnalytics(final AdDTO adDTO, final String event)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override public void run()
            {
                try
                {
                    AnalyticsEventForm analyticsEventForm = new AnalyticsEventForm(event,
                            DateUtils.getFormattedUtcDateFromDate(context.getResources(),
                                    new Date(System.currentTimeMillis())), adDTO.id, providerId,
                            currentUserId.toUserBaseKey().getUserId());
                    BatchAnalyticsEventForm batchAnalyticsEventForm = new BatchAnalyticsEventForm();
                    batchAnalyticsEventForm.events = new ArrayList<>();
                    batchAnalyticsEventForm.events.add(analyticsEventForm);
                    userServiceWrapper.sendAnalytics(batchAnalyticsEventForm);
                }
                catch (Exception e)
                {
                    THToast.show(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private String getResourceFileName(String url)
    {
        if (url != null)
        {
            String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());

            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            if (fileName.contains("@"))
            {
                fileName = fileName.substring(0, fileName.lastIndexOf('@'));
            }
            return fileName.replace('-', '_').toLowerCase();
        }
        return null;
    }

    public void setProviderId(int providerId)
    {
        this.providerId = providerId;
    }
}
