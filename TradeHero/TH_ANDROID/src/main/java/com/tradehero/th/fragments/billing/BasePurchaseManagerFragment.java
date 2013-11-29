package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exceptions.IABAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exceptions.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.IABAlertDialogSKUUtil;
import com.tradehero.th.billing.googleplay.IABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THIABActorPurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABActorPurchaser;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * It expects its Activity to implement THIABActorUser.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseManagerFragment extends DashboardFragment
        implements THIABActorUser
{
    public static final String TAG = BasePurchaseManagerFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".portfolioId";

    protected THIABUserInteractor userInteractor;

    @Inject Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;

    protected WeakReference<THIABActor> billingActor = new WeakReference<>(null);

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
            Bundle portfolioIdBundle = args.getBundle(BUNDLE_KEY_PORTFOLIO_ID_BUNDLE);
            if (portfolioIdBundle != null)
            {
                applicablePortfolioId = new OwnedPortfolioId(portfolioIdBundle);
            }
        }

        userInteractor.setApplicablePortfolioId(applicablePortfolioId);
    }

    @Override public void onPause()
    {
        userInteractor.onPause();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");
        userInteractor.onDestroy();

        super.onDestroyView();
    }

    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return userInteractor.getApplicablePortfolioId();
    }

    protected boolean isBillingAvailable()
    {
        return getBillingActor().isBillingAvailable();
    }

    protected boolean hadErrorLoadingInventory()
    {
        return getBillingActor().hadErrorLoadingInventory();
    }

    protected boolean isInventoryReady()
    {
        return getBillingActor().isInventoryReady();
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
