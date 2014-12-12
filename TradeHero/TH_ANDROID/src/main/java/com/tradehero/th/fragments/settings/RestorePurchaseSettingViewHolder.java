package com.tradehero.th.fragments.settings;

import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.billing.report.PurchaseReportResult;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.MarketSegment;
import javax.inject.Inject;
import rx.Observer;
import rx.internal.util.SubscriptionList;

public class RestorePurchaseSettingViewHolder extends OneSettingViewHolder
{
    @NonNull protected final THBillingInteractorRx billingInteractorRx;
    @NonNull protected SubscriptionList subscriptions;

    //<editor-fold desc="Constructors">
    @Inject public RestorePurchaseSettingViewHolder(
            @NonNull THBillingInteractorRx billingInteractorRx)
    {
        this.billingInteractorRx = billingInteractorRx;
        this.subscriptions = new SubscriptionList();
    }
    //</editor-fold>

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        PreferenceCategory container =
                (PreferenceCategory) preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_account_group));
        if (Constants.TAP_STREAM_TYPE.marketSegment.equals(MarketSegment.CHINA))
        // TODO this is not so good. It should depend on Billing Module
        {
            if (container != null && clickablePref != null)
            {
                container.removePreference(clickablePref);
            }
        }
    }

    @Override public void destroyViews()
    {
        subscriptions.unsubscribe();
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
            //noinspection unchecked
            subscriptions.add(billingInteractorRx.restorePurchasesAndClear()
                    .subscribe(new Observer<PurchaseReportResult>()
                    {
                        @Override public void onNext(PurchaseReportResult o)
                        {
                            THToast.show("restored " + o.reportedPurchase.getProductIdentifier());
                        }

                        @Override public void onCompleted()
                        {
                            THToast.show("restore completed");
                        }

                        @Override public void onError(Throwable e)
                        {
                            THToast.show("restore error " + e);
                        }
                    }));
        }
    }
}
