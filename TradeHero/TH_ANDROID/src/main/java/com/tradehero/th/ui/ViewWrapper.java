package com.tradehero.th.ui;

import android.view.ViewGroup;

public interface ViewWrapper
{
    ViewGroup get(ViewGroup viewGroup);

    ViewWrapper DEFAULT = new ViewWrapper()
    {
        @Override public ViewGroup get(ViewGroup viewGroup)
        {
            return viewGroup;
        }
    };
}
