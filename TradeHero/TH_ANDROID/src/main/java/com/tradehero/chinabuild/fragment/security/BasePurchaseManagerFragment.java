package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.view.View;
import com.tradehero.chinabuild.cache.PortfolioCompactNewCache;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

import javax.inject.Inject;

abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;
    private DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO> portfolioCompactNewFetchListener;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    @Inject protected PortfolioCompactNewCache portfolioCompactNewCache;

    public static OwnedPortfolioId getApplicablePortfolioId(@Nullable Bundle args)
    {
        if (args != null)
        {
            if (args.containsKey(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE))
            {
                return new OwnedPortfolioId(args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE));
            }
        }
        return null;
    }

    abstract protected void initViews(View view);

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        portfolioCompactListFetchListener = createPortfolioCompactListFetchListener();
        portfolioCompactNewFetchListener = createPortfolioCompactNewFetchListener();
    }

    @Override public void onResume()
    {
        super.onResume();

        if(getCompetitionID()==0)
        {
            fetchPortfolioCompactList();
        }
        else//如果是非0则说明是 比赛相关的 股票详情页
        {
            fetchPortfolioCompactNew();
        }

    }

    @Override public void onStop()
    {
        detachPortfolioCompactListCache();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        portfolioCompactListFetchListener = null;
        super.onDestroy();
    }

    private void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
    }

    private void detachPortfolioCompactNewCache()
    {
        portfolioCompactNewCache.unregister(portfolioCompactNewFetchListener);
    }

    private void fetchPortfolioCompactList()
    {
        detachPortfolioCompactListCache();
        portfolioCompactListCache.register(currentUserId.toUserBaseKey(), portfolioCompactListFetchListener);
        portfolioCompactListCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public int getCompetitionID()
    {
        return 0;
    }

    private void fetchPortfolioCompactNew()
    {
        detachPortfolioCompactNewCache();
        PortfolioId key = new PortfolioId(getCompetitionID());
        portfolioCompactNewCache.register(key, portfolioCompactNewFetchListener);
        portfolioCompactNewCache.getOrFetchAsync(key);
    }

    protected void prepareApplicableOwnedPortolioId(@Nullable PortfolioCompactDTO defaultIfNotInArgs)
    {
        Bundle args = getArguments();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId(args);

        if (applicablePortfolioId == null && defaultIfNotInArgs != null)
        {
            applicablePortfolioId = defaultIfNotInArgs.getOwnedPortfolioId();
        }

        if (applicablePortfolioId == null)
        {
            Timber.e(new NullPointerException(), "Null applicablePortfolio");
        }
        else
        {
            linkWithApplicable(applicablePortfolioId, true);
        }
    }

    protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        this.purchaseApplicableOwnedPortfolioId = purchaseApplicablePortfolioId;
        if (andDisplay)
        {
        }
    }

    @Nullable public OwnedPortfolioId getApplicablePortfolioId()
    {
        return purchaseApplicableOwnedPortfolioId;
    }

    protected DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> createPortfolioCompactListFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactListFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactListFetchListener implements DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList>
    {
        protected BasePurchaseManagementPortfolioCompactListFetchListener()
        {
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            prepareApplicableOwnedPortolioId(value.getDefaultPortfolio());
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
        }
    }

    protected DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO> createPortfolioCompactNewFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactNewFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactNewFetchListener implements DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO>
    {
        protected BasePurchaseManagementPortfolioCompactNewFetchListener()
        {
        }

        @Override public void onDTOReceived(@NotNull PortfolioId key, @NotNull PortfolioCompactDTO value)
        {
            prepareApplicableOwnedPortolioId(value);
        }

        @Override public void onErrorThrown(@NotNull PortfolioId key, @NotNull Throwable error)
        {
        }
    }



}
