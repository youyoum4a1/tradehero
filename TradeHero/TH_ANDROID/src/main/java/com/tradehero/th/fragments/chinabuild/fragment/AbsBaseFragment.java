package com.tradehero.th.fragments.chinabuild.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th2.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-8-21.
 */
public class AbsBaseFragment extends Fragment
{

    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = AbsBaseFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE = AbsBaseFragment.class.getName() + ".thIntent";

    @Inject protected CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    public void gotoDashboard(String strFragment)
    {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME,strFragment);
        ActivityHelper.launchDashboard(this.getActivity(), args);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        DaggerUtils.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        portfolioCompactListFetchListener = createPortfolioCompactListFetchListener();
    }

    protected DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> createPortfolioCompactListFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactListFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactListFetchListener implements DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList>
    {
        protected BasePurchaseManagementPortfolioCompactListFetchListener()
        {
            // no unexpected creation
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            prepareApplicableOwnedPortolioId(value.getDefaultPortfolio());
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchPortfolioCompactList();
    }

    @Override public void onStop()
    {
        detachPortfolioCompactListCache();
        //detachPurchaseActionInteractor();
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

    private void fetchPortfolioCompactList()
    {
        detachPortfolioCompactListCache();
        portfolioCompactListCache.register(currentUserId.toUserBaseKey(), portfolioCompactListFetchListener);
        portfolioCompactListCache.getOrFetchAsync(currentUserId.toUserBaseKey());
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
        if(purchaseApplicableOwnedPortfolioId!=null)
        {
            linkWithApplicable();
        }
    }

    protected void linkWithApplicable()
    {

    }

    @Nullable public OwnedPortfolioId getApplicablePortfolioId()
    {
        return purchaseApplicableOwnedPortfolioId;
    }

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
}
