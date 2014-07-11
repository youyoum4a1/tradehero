package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.tradehero.route.InjectRoute;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.thm.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.THRouter;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

abstract public class CompetitionFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionFragment.class.getName() + ".providerId";

    @InjectRoute protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    @Nullable private DTOCacheNew.Listener<ProviderId, ProviderDTO> providerCacheListener;

    @Inject ProviderCache providerCache;
    @Inject THRouter thRouter;

    public static void putProviderId(@NotNull Bundle args, @NotNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NotNull public static ProviderId getProviderId(@NotNull Bundle args)
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
        this.providerCacheListener = createProviderCacheListener();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchProviderDTO();
    }

    @Override public void onStop()
    {
        this.detachProviderFetchTask();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        this.providerCacheListener = null;
        super.onDestroy();
    }

    protected void detachProviderFetchTask()
    {
        providerCache.unregister(providerCacheListener);
    }

    protected void fetchProviderDTO()
    {
        this.detachProviderFetchTask();
        providerCache.register(this.providerId, this.providerCacheListener);
        providerCache.getOrFetchAsync(this.providerId);
    }

    protected void linkWith(@NotNull ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;

        OwnedPortfolioId associatedPortfolioId =
                new OwnedPortfolioId(currentUserId.toUserBaseKey(), providerDTO.associatedPortfolio);
        putApplicablePortfolioId(getArguments(), associatedPortfolioId);

        prepareApplicableOwnedPortolioId();

        if (andDisplay)
        {
        }
    }

    @NotNull protected DTOCacheNew.Listener<ProviderId, ProviderDTO> createProviderCacheListener()
    {
        return new CompetitionFragmentProviderCacheListener();
    }

    protected class CompetitionFragmentProviderCacheListener implements DTOCacheNew.HurriedListener<ProviderId, ProviderDTO>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull ProviderId key,
                @NotNull ProviderDTO value)
        {
            onDTOReceived(key, value);
        }

        @Override public void onDTOReceived(@NotNull ProviderId key, @NotNull ProviderDTO value)
        {
            if (key.equals(CompetitionFragment.this.providerId))
            {
                CompetitionFragment.this.linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(@NotNull ProviderId key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_info));
            Timber.e("Error fetching the provider info " + key, error);
        }
    }
}
