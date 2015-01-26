package com.tradehero.th.fragments.timeline;

import android.support.annotation.NonNull;

@Deprecated // Use Rx
public interface TimelineProfileClickListener
{
    void onBtnClicked(@NonNull TimelineFragment.TabType tabType);
}
