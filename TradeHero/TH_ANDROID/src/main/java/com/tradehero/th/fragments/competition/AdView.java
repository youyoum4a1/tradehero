package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
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
import rx.Observable;
import rx.functions.Actions;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AdView extends RelativeLayout
        implements DTOView<CompetitionZoneDTO>
{
    @InjectView(R.id.banner) ImageView banner;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject Context context;
    private AdDTO adDTO;
    private int providerId;
    @Inject DashboardNavigator navigator;

    @SuppressWarnings("UnusedDeclaration")
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
            linkWith(competitionZoneAdvertisementDTO.getAdDTO());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void linkWith(AdDTO adDTO)
    {
        if (adDTO != null)
        {
            this.adDTO = adDTO;
        }
        else
        {
            this.adDTO = null;
        }

        if (adDTO != null)
        {
            // Ok, this is the only way I found to workaround this problem, converting url to a filename, and manually put 9-patch image
            // with that name to android resource folder.
            String bannerResourceFileName = null;
            try
            {
                bannerResourceFileName = getResourceFileName(adDTO.bannerImageUrl);
            } catch (StringIndexOutOfBoundsException e)
            {
                Timber.e(e, "When getting %s", adDTO.bannerImageUrl);
            }
            int bannerResourceId;
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

    private void sendAnalytics(@NonNull final AdDTO adDTO, @Nullable final String event)
    {
        Observable.just(Pair.create(adDTO, event))
                .subscribeOn(Schedulers.computation())
                .map(this::createBatchForm)
                .flatMap(userServiceWrapper::sendAnalyticsRx)
                .doOnError(e -> THToast.show(e.getMessage()))
                .subscribe(Actions.empty(), Actions.empty());
    }

    @NonNull private BatchAnalyticsEventForm createBatchForm(@NonNull Pair<AdDTO, String> pair)
    {
        return createBatchForm(pair.first, pair.second);
    }

    @NonNull private BatchAnalyticsEventForm createBatchForm(@NonNull AdDTO adDTO, @Nullable String event)
    {
        AnalyticsEventForm analyticsEventForm = new AnalyticsEventForm(
                event,
                DateUtils.getFormattedUtcDateFromDate(
                        context.getResources(),
                        new Date(System.currentTimeMillis())),
                adDTO.id,
                providerId,
                currentUserId.toUserBaseKey().getUserId());
        BatchAnalyticsEventForm batchAnalyticsEventForm = new BatchAnalyticsEventForm();
        batchAnalyticsEventForm.events = new ArrayList<>();
        batchAnalyticsEventForm.events.add(analyticsEventForm);
        return batchAnalyticsEventForm;
    }

    @Nullable private String getResourceFileName(@Nullable String url)
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
