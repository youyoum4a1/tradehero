package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.view.View;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.lang.ref.WeakReference;

/**
 * It expects its Activity to implement THIABActorUser.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseManagerFragment extends DashboardFragment
        implements THIABActorUser
{
    public static final String TAG = BasePurchaseManagerFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";

    protected THIABUserInteractor userInteractor;
    protected WeakReference<THIABActor> billingActor = new WeakReference<>(null);

    abstract protected void initViews(View view);

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setBillingActor(((THIABActorUser) getActivity()).getBillingActor());
        createUserInteractor();
    }

    protected void createUserInteractor()
    {
        userInteractor = new THIABUserInteractor(getActivity(), getBillingActor(), getView().getHandler());
    }

    @Override public void onResume()
    {
        super.onResume();
        OwnedPortfolioId applicablePortfolioId = null;

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle portfolioIdBundle = args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE);
            if (portfolioIdBundle != null)
            {
                applicablePortfolioId = new OwnedPortfolioId(portfolioIdBundle);
            }
        }

        THLog.d(TAG, "pusrchase applicablePortfolio " + applicablePortfolioId);
        userInteractor.setApplicablePortfolioId(applicablePortfolioId);
    }

    @Override public void onPause()
    {
        userInteractor.onPause();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        userInteractor.onDestroy();
        userInteractor = null;
        super.onDestroyView();
    }

    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return userInteractor.getApplicablePortfolioId();
    }

    //<editor-fold desc="THIABActorUser">
    public THIABActor getBillingActor()
    {
        return billingActor.get();
    }

    public void setBillingActor(THIABActor billingActor)
    {
        this.billingActor = new WeakReference<>(billingActor);
    }
    //</editor-fold>
}
