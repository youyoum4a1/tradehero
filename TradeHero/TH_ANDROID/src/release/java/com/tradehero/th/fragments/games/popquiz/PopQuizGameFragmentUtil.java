package com.tradehero.th.fragments.games.popquiz;

import android.support.annotation.Nullable;

public class PopQuizGameFragmentUtil
{
    public static boolean isPopQuizEnabled(){
        return getXwalkFragment() != null;
    }

    @Nullable public static Class getXwalkFragment()
    {
        return XWalkWebViewFragment.class;
    }
}
