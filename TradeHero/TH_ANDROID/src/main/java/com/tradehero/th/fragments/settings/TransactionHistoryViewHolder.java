package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import javax.inject.Inject;

public class TransactionHistoryViewHolder extends OneSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public TransactionHistoryViewHolder()
    {
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_transaction_history;
    }

    @Override protected void handlePrefClicked()
    {
        preferenceFragment.getNavigator().pushFragment(SettingsTransactionHistoryFragment.class);
    }
}
