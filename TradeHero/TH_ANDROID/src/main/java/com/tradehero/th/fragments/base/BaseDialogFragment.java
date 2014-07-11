package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.tradehero.th.utils.DaggerUtils;

public abstract class BaseDialogFragment extends SherlockDialogFragment
{
    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}
