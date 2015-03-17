package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.functions.Action1;

abstract public class BaseAlertEditFragment extends DashboardFragment
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected SecurityAlertCountingHelper securityAlertCountingHelper;

    BaseAlertEditFragmentHolder viewHolder;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.alert_edit_fragment, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(viewHolder, view);
        ButterKnife.inject(viewHolder.alertSliderViewHolder, view);
        viewHolder.scrollView.setOnScrollChangedListener(dashboardBottomTabScrollViewScrollListener.get());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.alert_edit_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.alert_menu_save:
                conditionalSaveAlert();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStart()
    {
        super.onStart();
        viewHolder.onStart();
        viewHolder.fetchAlert();
    }

    @Override public void onStop()
    {
        viewHolder.onStop();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        viewHolder.scrollView.setOnScrollChangedListener(null);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        viewHolder = null;
        super.onDetach();
    }

    protected void conditionalSaveAlert()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                viewHolder.conditionalSaveAlert())
                .subscribe(
                        new Action1<AlertCompactDTO>()
                        {
                            @Override public void call(AlertCompactDTO t1)
                            {
                                handleAlertUpdated(t1);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable t1)
                            {
                                handleAlertUpdateFailed(t1);
                            }
                        }));
    }

    protected void handleAlertUpdated(@NonNull AlertCompactDTO alertCompactDTO)
    {
        navigator.get().popFragment();
    }

    protected void handleAlertUpdateFailed(@NonNull Throwable e)
    {
        THToast.show(new THException(e));
    }
}
