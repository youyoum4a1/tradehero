package com.tradehero.common.billing.googleplay;

import org.jetbrains.annotations.Nullable;

public interface IABServiceListenerHolder
{
    void setListener(@Nullable IABServiceConnector.ConnectorListener listener);
    void onDestroy();
}
