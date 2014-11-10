package com.tradehero.th.fragments.competition;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.base.BaseShareableDialogFragment;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.widget.MarkdownTextView;
import javax.inject.Inject;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class CompetitionPreseasonDialogFragment extends BaseShareableDialogFragment
{
    public static final String TAG = CompetitionPreseasonDialogFragment.class.getName();

    private static final String KEY_PROVIDER_ID = CompetitionPreseasonDialogFragment.class.getName() + ".providerId";
    private static final int CONTENT_VIEW_INDEX = 0;
    private static final int LOADING_VIEW_INDEX = 1;

    @Inject Picasso picasso;
    @Inject ProviderCacheRx providerCacheRx;

    @InjectView(R.id.preseason_viewflipper) ViewFlipper viewFlipper;
    @InjectView(R.id.preseason_title_image) ImageView imgTitle;
    @InjectView(R.id.preseason_prize_image) ImageView imgPrize;
    @InjectView(R.id.preseason_prize_description) MarkdownTextView textDescription;

    private ProviderId providerId;
    private ProviderDTO providerDTO;

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
        if (getEnabledSharePreferences().isEmpty())
        {
            alertDialogUtil.popWithNegativeButton(
                    getActivity(),
                    R.string.link_select_one_social,
                    R.string.link_select_one_social_description,
                    R.string.ok);
        }
        else if (providerDTO == null)
        {
            share();
        }
    }

    private void share()
    {
        //TODO
    }

    @OnClick(R.id.preseason_prize_tncs)
    public void onTncsClicked()
    {
        //TODO
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
                        linkAndInitViews(providerIdProviderDTOPair.second);
                    }
                });
    }

    private void showLoadingDialog()
    {
        viewFlipper.setDisplayedChild(LOADING_VIEW_INDEX);
    }

    private void showContent()
    {
        viewFlipper.setDisplayedChild(CONTENT_VIEW_INDEX);
    }

    private void linkAndInitViews(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        showContent();
        if (providerDTO.navigationLogoUrl != null)
        {
            picasso.load(providerDTO.navigationLogoUrl).into(imgTitle);
        }
    }
}
