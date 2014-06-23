package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.thoj.route.InjectRoute;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.THRouter;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class CompetitionFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = CompetitionFragment.class.getName() + ".providerId";

    @InjectRoute protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    private DTOCache.Listener<ProviderId, ProviderDTO> providerCacheListener;
    private DTOCache.GetOrFetchTask<ProviderId, ProviderDTO> providerCacheFetchTask;
    protected ProviderSpecificResourcesDTO providerSpecificResourcesDTO;

    @Inject ProviderCache providerCache;
    @Inject ProviderSpecificResourcesFactory providerSpecificResourcesFactory;
    @Inject THRouter thRouter;

    public static void putProviderId(@NotNull Bundle args, @NotNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    public static ProviderId getProviderId(@NotNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        thRouter.inject(this, getArguments());
        if (this.providerId == null)
        {
            this.providerId = getProviderId(getArguments());
        }
        this.providerCacheListener = new CompetitionFragmentProviderCacheListener();
    }

    @Override public void onStart()
    {
        super.onStart();
        this.detachProviderFetchTask();
        this.providerCacheFetchTask = providerCache.getOrFetch(this.providerId, this.providerCacheListener);
        this.providerCacheFetchTask.execute();
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
        if (this.providerCacheFetchTask != null)
        {
            this.providerCacheFetchTask.setListener(null);
        }
        this.providerCacheFetchTask = null;
    }

    protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;
        providerSpecificResourcesDTO = providerSpecificResourcesFactory.createResourcesDTO(providerDTO);

        if (andDisplay)
        {
        }
    }

    protected class CompetitionFragmentProviderCacheListener implements DTOCache.Listener<ProviderId, ProviderDTO>
    {
        @Override public void onDTOReceived(ProviderId key, ProviderDTO value, boolean fromCache)
        {
            if (key.equals(CompetitionFragment.this.providerId))
            {
                CompetitionFragment.this.linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(ProviderId key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_info));
            Timber.e("Error fetching the provider info " + key, error);
        }
    }
}
