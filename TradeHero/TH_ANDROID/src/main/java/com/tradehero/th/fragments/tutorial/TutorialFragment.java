package com.tradehero.th.fragments.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;

public class TutorialFragment extends SherlockFragment
{
    public static final String BUNDLE_KEY_TUTORIAL_LAYOUT = TutorialFragment.class.getName() + ".tutorialResId";

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int tutorialLayout = getArguments().getInt(BUNDLE_KEY_TUTORIAL_LAYOUT);
        return inflater.inflate(tutorialLayout, container, false);
    }
}
