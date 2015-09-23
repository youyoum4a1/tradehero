package com.tradehero.th.fragments.base;

import android.support.v4.app.Fragment;
import android.view.View;

public class BaseLiveFragmentUtil
{
    public static BaseLiveFragmentUtil createFor(Fragment f, View view)
    {
        return new BaseLiveFragmentUtil(f, view);
    }

    protected BaseLiveFragmentUtil(Fragment f, View view)
    {
    }

    public static void setDarkBackgroundColor(boolean isLive, View... views)
    {
    }

    public static void setBackgroundColor(boolean isLive, View... views)
    {
    }

    public static void setSelectableBackground(boolean isLive, View... views)
    {
    }

    public void setCallToAction(boolean isLive)
    {
    }

    public void onDestroy()
    {
    }

    public void onDestroyView()
    {
    }

    public void onResume()
    {
    }

    public void launchPrompt()
    {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    }
}
