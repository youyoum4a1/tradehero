package com.ayondo.academy.fragments.competition;

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
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.analytics.AnalyticsEventForm;
import com.ayondo.academy.api.analytics.BatchAnalyticsEventForm;
import com.ayondo.academy.api.competition.AdDTO;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.competition.zone.AbstractCompetitionZoneListItemView;
import com.ayondo.academy.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.ayondo.academy.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.network.service.UserServiceWrapper;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import com.ayondo.academy.utils.DateUtils;
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
