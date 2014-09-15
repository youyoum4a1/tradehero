package com.tradehero.th.fragments.chinabuild.fragment.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th2.R;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import timber.log.Timber;

public class FragmentTest01 extends DashboardFragment
{
    @InjectView(R.id.tvTest) TextView tvTest;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.test_fragment01, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.tvTest)
    public void onTestClicked(View view)
    {
        Timber.d("onTestClicked FragmentTest01");
        getDashboardNavigator().pushFragment(FragmentTest02.class,new Bundle());
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

}
