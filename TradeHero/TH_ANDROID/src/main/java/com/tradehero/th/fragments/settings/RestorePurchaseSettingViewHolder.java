package com.tradehero.th.fragments.settings;

import android.support.v4.preference.PreferenceFragment;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.th.R;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class RestorePurchaseSettingViewHolder extends OneSettingViewHolder
{
    @NotNull protected final THBillingInteractor billingInteractor;
    @NotNull protected final Provider<THUIBillingRequest> billingRequestProvider;

    private Integer restoreRequestCode;
    private BillingPurchaseRestorer.OnPurchaseRestorerListener purchaseRestorerFinishedListener;

    //<editor-fold desc="Constructors">
    @Inject public RestorePurchaseSettingViewHolder(
            @NotNull THBillingInteractor billingInteractor,
            @NotNull Provider<THUIBillingRequest> billingRequestProvider)
    {
        this.billingInteractor = billingInteractor;
        this.billingRequestProvider = billingRequestProvider;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        purchaseRestorerFinishedListener = new BillingPurchaseRestorer.OnPurchaseRestorerListener()
        {
            @Override public void onPurchaseRestored(
                    int requestCode,
                    List restoredPurchases,
                    List failedRestorePurchases,
                    List failExceptions)
            {
                if (Integer.valueOf(requestCode).equals(restoreRequestCode))
                {
                    restoreRequestCode = null;
                }
            }
        };
    }

    @Override public void destroyViews()
    {
        purchaseRestorerFinishedListener = null;
        super.destroyViews();
    }

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_restore_purchases;
    }

    @Override protected void handlePrefClicked()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            if (restoreRequestCode != null)
            {
                billingInteractor.forgetRequestCode(restoreRequestCode);
            }
            //noinspection unchecked
            restoreRequestCode = billingInteractor.run(createRestoreRequest());
        }
    }

    protected THUIBillingRequest createRestoreRequest()
    {
        THUIBillingRequest request = billingRequestProvider.get();
        request.restorePurchase = true;
        request.startWithProgressDialog = true;
        request.popRestorePurchaseOutcome = true;
        request.popRestorePurchaseOutcomeVerbose = true;
        request.purchaseRestorerListener = purchaseRestorerFinishedListener;
        return request;
    }
}
