package com.tradehero.th.fragments.timeline;

import org.jetbrains.annotations.NotNull;

public interface TimelineProfileClickListener
{
    void onBtnClicked(@NotNull TimelineFragment.TabType tabType);
}
