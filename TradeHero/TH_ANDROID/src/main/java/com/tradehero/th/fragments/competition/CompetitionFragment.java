package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.InjectRoute;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderPrizePoolDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

abstract public class CompetitionFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionFragment.class.getName() + ".providerId";

    @Inject ProviderCacheRx providerCache;
    @Inject THRouter thRouter;

    @InjectRoute protected ProviderId providerId;
    @Nullable private Subscription providerCacheSubscription;
    protected ProviderDTO providerDTO;
    protected ProviderPrizePoolDTO providerPrizePoolDTO;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull public static ProviderId getProviderId(@NonNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        thRouter.inject(this, getArguments());
        // TODO improve thRouter so that it leaves the field empty instead of filling it with empty data.
        if (this.providerId == null || this.providerId.key == null)
        {
            this.providerId = getProviderId(getArguments());
        }
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchProviderDTO();
    }

    @Override public void onStop()
    {
        unsubscribe(providerCacheSubscription);
        providerCacheSubscription = null;
        super.onStop();
    }

    protected void fetchProviderDTO()
    {
        unsubscribe(providerCacheSubscription);
        providerCacheSubscription = AndroidObservable.bindFragment(
                this,
                providerCache.get(this.providerId))
                .subscribe(createProviderCacheObserver());
    }

    protected void linkWith(@NonNull ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;

        OwnedPortfolioId associatedPortfolioId = providerDTO.associatedPortfolio.getOwnedPortfolioId();
        putApplicablePortfolioId(getArguments(), associatedPortfolioId);

        prepareApplicableOwnedPortolioId(null);

        if (andDisplay)
        {
        }
    }

    @NonNull protected Observer<Pair<ProviderId, ProviderDTO>> createProviderCacheObserver()
    {
        return new CompetitionFragmentProviderCacheObserver();
    }

    protected class CompetitionFragmentProviderCacheObserver implements Observer<Pair<ProviderId, ProviderDTO>>
    {
        @Override public void onNext(Pair<ProviderId, ProviderDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (providerDTO == null)
            {
                THToast.show(getString(R.string.error_fetch_provider_info));
            }
            Timber.e("Error fetching the provider info", e);
        }
    }


}
