package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.BaseShareableDialogFragment;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.widget.MarkdownTextView;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class CompetitionPreseasonDialogFragment extends BaseShareableDialogFragment
{
    public static final String TAG = CompetitionPreseasonDialogFragment.class.getName();

    private static final String KEY_PROVIDER_ID = CompetitionPreseasonDialogFragment.class.getName() + ".providerId";

    @Inject Picasso picasso;
    @Inject ProviderCacheRx providerCacheRx;

    @InjectView(R.id.preseason_share) Button btnShare;
    @InjectView(R.id.preseason_title_image) ImageView imgTitle;
    @InjectView(R.id.preseason_prize_image) ImageView imgPrize;
    @InjectView(R.id.preseason_prize_description) MarkdownTextView textDescription;
    @InjectView(R.id.preseason_prize_tncs) Button btnTncs;

    private ProviderId providerId;

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
        return inflater.inflate(R.layout.competition_preseason_dialog, container, false);
    }

    @OnClick(R.id.close)
    public void onCloseClicked()
    {
        getDialog().dismiss();
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
                .subscribe(new Observer<Pair<ProviderId, ProviderDTO>>()
                {
                    @Override public void onCompleted()
                    {

                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "error on fetching provider %d", providerId.key);
                    }

                    @Override public void onNext(Pair<ProviderId, ProviderDTO> providerIdProviderDTOPair)
                    {
                        initView(providerIdProviderDTOPair.second);
                    }
                });
    }

    private void initView(ProviderDTO providerDTO)
    {
        if (providerDTO.navigationLogoUrl != null)
        {
            picasso.load(providerDTO.navigationLogoUrl).into(imgTitle);
        }
    }
}
