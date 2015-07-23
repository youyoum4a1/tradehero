package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.analytics.AnalyticsEventForm;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.competition.zone.AbstractCompetitionZoneListItemView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.utils.DateUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Date;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Actions;
import rx.subjects.PublishSubject;

public class AdView extends RelativeLayout
        implements DTOView<CompetitionZoneAdvertisementDTO>
{
    @Bind(R.id.banner) ImageView banner;
    @Inject UserServiceWrapper userServiceWrapper;
    @Nullable protected CompetitionZoneAdvertisementDTO viewDTO;

    @NonNull private PublishSubject<AbstractCompetitionZoneListItemView.UserAction> userActionSubject;

    @Inject Lazy<Picasso> picasso;
    @Inject CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    public AdView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.get().cancelRequest(banner);
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<AbstractCompetitionZoneListItemView.UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    @Override public void display(@NonNull CompetitionZoneAdvertisementDTO dto)
    {
        this.viewDTO = dto;
        if (banner != null)
        {
            picasso.get().cancelRequest(banner);
            if (dto.bannerResourceId != null)
            {
                banner.setBackgroundResource(dto.bannerResourceId);
            }
            else if (dto.bannerImageUrl != null)
            {
                picasso.get().load(dto.bannerImageUrl)
                        .into(banner);
            }
            AdDTO adDTO = dto.getAdDTO();
            if (adDTO != null)
            {
                sendAnalytics(adDTO, "served", dto.providerId);
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.banner) void onBannerClicked(ImageView banner)
    {
        if (viewDTO != null && viewDTO.getAdDTO() != null)
        {
            userActionSubject.onNext(new UserAction(viewDTO, viewDTO.getAdDTO()));
            sendAnalytics(viewDTO.getAdDTO(), "proceed", viewDTO.providerId);
        }
    }

    private void sendAnalytics(@NonNull final AdDTO adDTO, @NonNull final String event, @NonNull ProviderId providerId)
    {
        userServiceWrapper.sendAnalyticsRx(createBatchForm(adDTO, event, providerId))
                .subscribe(
                        Actions.empty(),
                        new ToastOnErrorAction1());
    }


    @NonNull private BatchAnalyticsEventForm createBatchForm(@NonNull AdDTO adDTO, @NonNull String event, @NonNull ProviderId providerId)
    {
        AnalyticsEventForm analyticsEventForm = new AnalyticsEventForm(
                event,
                DateUtils.getFormattedUtcDateFromDate(
                        getResources(),
                        new Date(System.currentTimeMillis())),
                adDTO.id,
                providerId.key,
                currentUserId.toUserBaseKey().getUserId());
        BatchAnalyticsEventForm batchAnalyticsEventForm = new BatchAnalyticsEventForm();
        batchAnalyticsEventForm.events = new ArrayList<>();
        batchAnalyticsEventForm.events.add(analyticsEventForm);
        return batchAnalyticsEventForm;
    }

    public static class UserAction extends AbstractCompetitionZoneListItemView.UserAction
    {
        @NonNull public final AdDTO adDTO;

        public UserAction(@NonNull CompetitionZoneDTO competitionZoneDTO,
                @NonNull AdDTO adDTO)
        {
            super(competitionZoneDTO);
            this.adDTO = adDTO;
        }
    }
}
