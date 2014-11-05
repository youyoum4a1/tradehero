package com.tradehero.common.billing.googleplay;

import android.support.annotation.Nullable;

public interface IABServiceListenerHolder
{
    void setListener(@Nullable IABServiceConnector.ConnectorListener listener);
    void onDestroy();
}
