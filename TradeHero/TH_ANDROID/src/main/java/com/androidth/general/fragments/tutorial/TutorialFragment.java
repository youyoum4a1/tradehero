package com.androidth.general.fragments.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialFragment extends Fragment
{
    public static final String BUNDLE_KEY_TUTORIAL_LAYOUT = TutorialFragment.class.getName() + ".tutorialResId";

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int tutorialLayout = getArguments().getInt(BUNDLE_KEY_TUTORIAL_LAYOUT);
        return inflater.inflate(tutorialLayout, container, false);
    }
}
