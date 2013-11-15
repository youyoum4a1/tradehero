package com.tradehero.th.fragments.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;

/** Created with IntelliJ IDEA. User: tho Date: 11/15/13 Time: 3:41 PM Copyright (c) TradeHero */
public class TutorialFragment extends SherlockFragment
{
    public static final String TUTORIAL_LAYOUT = TutorialFragment.class.getName();

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int tutorialLayout = getArguments().getInt(TUTORIAL_LAYOUT);
        return inflater.inflate(tutorialLayout, container, false);
    }
}
