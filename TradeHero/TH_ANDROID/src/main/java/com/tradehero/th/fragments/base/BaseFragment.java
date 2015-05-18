package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.utils.DaggerUtils;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class BaseFragment extends Fragment {
    private static final String BUNDLE_KEY_IS_OPTION_MENU_VISIBLE = BaseFragment.class.getName() + ".isOptionMenuVisible";
    public static final boolean DEFAULT_IS_OPTION_MENU_VISIBLE = true;

    protected boolean isOptionMenuVisible;

    public static boolean getIsOptionMenuVisible(@Nullable Bundle args) {
        if (args == null) {
            return DEFAULT_IS_OPTION_MENU_VISIBLE;
        }
        return args.getBoolean(BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, DEFAULT_IS_OPTION_MENU_VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOptionMenuVisible = getIsOptionMenuVisible(getArguments());
        setHasOptionsMenu(true);
        ActionBar actionBar = getSupportActionBar();
        if ((actionBar != null) && (!hasOptionsMenu())) {
            actionBar.hide();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (hasOptionsMenu()) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    @Nullable
    protected ActionBar getSupportActionBar() {
        if (getActivity() != null) {

            ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            return actionbar;
        } else {
            Timber.e(new Exception(), "getActivity is Null");
            return null;
        }
    }

    protected void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
