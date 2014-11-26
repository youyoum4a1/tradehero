package com.tradehero.th.fragments.competition;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.competition.CompetitionPreSeasonDTO;
import com.tradehero.th.api.competition.CompetitionPreseasonShareFormDTOFactory;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseShareableDialogFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.competition.CompetitionPreseasonCacheRx;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class CompetitionPreseasonDialogFragment extends BaseShareableDialogFragment
{
    public static final String TAG = CompetitionPreseasonDialogFragment.class.getName();

    private static final String KEY_PROVIDER_ID = CompetitionPreseasonDialogFragment.class.getName() + ".providerId";
    private static final int CONTENT_VIEW_INDEX = 0;
    private static final int LOADING_VIEW_INDEX = 1;
    private static final int ERROR_VIEW_INDEX = 2;
    private static final int SHARED_ERROR_VIEW_INDEX = 3;

    @Inject Picasso picasso;
    @Inject ProviderCacheRx providerCacheRx;
    @Inject CompetitionPreseasonCacheRx competitionPreseasonCacheRx;
    @Inject DashboardNavigator navigator;
    @Inject ProviderServiceWrapper providerServiceWrapper;
    @Inject Lazy<CompetitionPreseasonShareFormDTOFactory> competitionPreseasonShareFormDTOFactoryLazy;
    @Inject Lazy<WeChatDTOFactory> weChatDTOFactoryLazy;
    @Inject Lazy<SocialSharer> socialSharerLazy;

    @InjectView(R.id.preseason_viewflipper) ViewFlipper viewFlipper;
    @InjectView(R.id.preseason_title_image) ImageView imgTitle;
    @InjectView(R.id.preseason_prize_image) ImageView imgPrize;
    @InjectView(R.id.preseason_prize_description) MarkdownTextView textDescription;
    @InjectView(R.id.preseason_prize_title) TextView textTitle;

    private ProviderId providerId;
    private ProviderDTO providerDTO;
    private CompetitionPreSeasonDTO competitionPreSeasonDTO;
    private Subscription shareSubscription;

    public static CompetitionPreseasonDialogFragment newInstance(ProviderId providerId)
    {
        CompetitionPreseasonDialogFragment c = new CompetitionPreseasonDialogFragment();
        Bundle b = new Bundle();
        b.putBundle(KEY_PROVIDER_ID, providerId.getArgs());
        c.setArguments(b);
        return c;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TH_Dialog);
        return inflater.inflate(R.layout.competition_preseason_dialog, container, false);
    }

    @OnClick(R.id.close)
    public void onCloseClicked()
    {
        getDialog().dismiss();
    }

    @OnClick(R.id.preseason_share)
    public void onShareClicked()
    {
        List<SocialNetworkEnum> shareList = getEnabledSharePreferences();
        if (shareList.isEmpty())
        {
            alertDialogUtil.popWithNegativeButton(
                    getActivity(),
                    R.string.link_select_one_social,
                    R.string.link_select_one_social_description,
                    R.string.ok);
        }
        else if (providerDTO != null)
        {
            share(shareList);
        }
    }

    private void share(List<SocialNetworkEnum> shareList)
    {
        if (shareList.contains(SocialNetworkEnum.WECHAT))
        {
            //We handle WeChat separately since it's share locally on the client side.
            shareList.remove(SocialNetworkEnum.WECHAT);
            shareToWeChat();
        }

        if (!shareList.isEmpty())
        {
            shareSubscription = AndroidObservable.bindFragment(this,
                    providerServiceWrapper.sharePreSeason(competitionPreseasonShareFormDTOFactoryLazy.get().createFrom(shareList, providerId)))
                    .subscribe(new Observer<BaseResponseDTO>()
                    {
                        @Override public void onCompleted()
                        {

                        }

                        @Override public void onError(Throwable e)
                        {
                            showShareFailed();
                        }

                        @Override public void onNext(BaseResponseDTO baseResponseDTO)
                        {
                            showShareSuccess();
                        }
                    });
            showLoadingDialog();
        }
        else
        {
            dismiss();
        }
    }

    private void shareToWeChat()
    {
        WeChatDTO weChatDTO = weChatDTOFactoryLazy.get().createFrom(getActivity(), competitionPreSeasonDTO);
        socialSharerLazy.get().share(weChatDTO);
    }

    @OnClick(R.id.preseason_prize_tncs)
    public void onTncsClicked()
    {
        if (competitionPreSeasonDTO.tncUrl != null)
        {
            Bundle b = new Bundle();
            WebViewFragment.putUrl(b, competitionPreSeasonDTO.tncUrl);
            navigator.pushFragment(WebViewFragment.class, b);
            dismiss();
        }
    }

    @OnClick(R.id.preseason_share_retry)
    public void onRetryClicked()
    {
        share(getEnabledSharePreferences());
    }

    private void getProviderIdFromArgs()
    {
        this.providerId = new ProviderId(getArguments().getBundle(KEY_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getProviderIdFromArgs();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        AndroidObservable.bindFragment(this, providerCacheRx.get(providerId))
                .subscribe(new Subscriber<Pair<ProviderId, ProviderDTO>>()
                {
                    @Override public void onStart()
                    {
                        super.onStart();
                        showLoadingDialog();
                    }

                    @Override public void onCompleted()
                    {

                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "error on fetching provider %d", providerId.key);
                    }

                    @Override public void onNext(Pair<ProviderId, ProviderDTO> providerIdProviderDTOPair)
                    {
                        linkAndShowLogo(providerIdProviderDTOPair.second);
                    }
                });
        AndroidObservable.bindFragment(this, competitionPreseasonCacheRx.get(providerId))
                .subscribe(new Subscriber<Pair<ProviderId, CompetitionPreSeasonDTO>>()
                {
                    @Override public void onCompleted()
                    {

                    }

                    @Override public void onError(Throwable e)
                    {
                        showError();
                        Timber.e(e, "Error loading preseason for providerId %d", providerId.key);
                    }

                    @Override public void onNext(Pair<ProviderId, CompetitionPreSeasonDTO> providerIdCompetitionPreSeasonDTOPair)
                    {
                        linkAndInitViews(providerIdCompetitionPreSeasonDTOPair.second);
                    }
                });
    }

    private void showLoadingDialog()
    {
        showViewAtIndex(LOADING_VIEW_INDEX);
    }

    private void showContent()
    {
        showViewAtIndex(CONTENT_VIEW_INDEX);
    }

    private void showError()
    {
        showViewAtIndex(ERROR_VIEW_INDEX);
    }

    private void showShareSuccess()
    {
        THToast.show(R.string.content_shared);
        dismiss();
    }

    private void showShareFailed()
    {
        showViewAtIndex(SHARED_ERROR_VIEW_INDEX);
    }

    private void showViewAtIndex(int index)
    {
        if (viewFlipper.getDisplayedChild() != index)
        {
            viewFlipper.setDisplayedChild(index);
        }
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        unsubscribe(shareSubscription);
        super.onDismiss(dialog);
    }

    private void linkAndShowLogo(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        if (providerDTO.navigationLogoUrl != null)
        {
            picasso.load(providerDTO.navigationLogoUrl)
                    .into(imgTitle);
        }
    }

    private void linkAndInitViews(CompetitionPreSeasonDTO competitionPreSeasonDTO)
    {
        this.competitionPreSeasonDTO = competitionPreSeasonDTO;
        showContent();
        if (competitionPreSeasonDTO.prizeImageUrl != null)
        {
            picasso.load(competitionPreSeasonDTO.prizeImageUrl)
                    .fit()
                    .centerInside()
                    .into(imgPrize);
        }
        textDescription.setText(competitionPreSeasonDTO.content);
        textTitle.setText(competitionPreSeasonDTO.headline);
    }

    @Override public void onDestroyView()
    {
        unsubscribe(shareSubscription);
        super.onDestroyView();
    }
}
