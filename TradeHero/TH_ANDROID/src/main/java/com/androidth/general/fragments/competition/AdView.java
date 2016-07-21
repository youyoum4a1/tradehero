package com.androidth.general.fragments.competition;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

import com.androidth.general.activities.SignUpLiveActivity;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.analytics.AnalyticsEventForm;
import com.androidth.general.api.analytics.BatchAnalyticsEventForm;
import com.androidth.general.api.competition.AdDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.fragments.competition.zone.AbstractCompetitionZoneListItemView;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.utils.DateUtils;

import butterknife.Unbinder;
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
    @BindView(R.id.banner) ImageView banner;
    @Inject UserServiceWrapper userServiceWrapper;
    @Nullable protected CompetitionZoneAdvertisementDTO viewDTO;

    @NonNull private PublishSubject<AbstractCompetitionZoneListItemView.UserAction> userActionSubject;

    @Inject Lazy<Picasso> picasso;
    @Inject CurrentUserId currentUserId;

    private Context context;

    private Unbinder unbinder;

    //<editor-fold desc="Constructors">
    public AdView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        HierarchyInjector.inject(this);
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.get().cancelRequest(banner);
        unbinder.unbind();
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
            Intent kycIntent = new Intent(this.context, SignUpLiveActivity.class);
            kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, viewDTO.providerId.key);
            kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_JOIN_COMPETITION, false);
            this.context.startActivity(kycIntent);
//            userActionSubject.onNext(new UserAction(viewDTO, viewDTO.getAdDTO()));
//            sendAnalytics(viewDTO.getAdDTO(), "proceed", viewDTO.providerId);
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
