package com.androidth.general.fragments.alert;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.androidth.general.R;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.fragments.base.BaseDialogSupportFragment;
import com.androidth.general.models.alert.SecurityAlertCountingHelper;
import com.androidth.general.rx.ToastOnErrorAction1;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

abstract public class BaseAlertEditDialogFragment extends BaseDialogSupportFragment
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected SecurityAlertCountingHelper securityAlertCountingHelper;

    BaseAlertEditFragmentHolder viewHolder;

    private Unbinder unbinder;

    @NonNull @Override public Dialog onCreateDialog(@NonNull Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TH_StockAlert_Dialog);
        d.setCanceledOnTouchOutside(true);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.alert_edit_fragment, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(viewHolder, view);
        unbinder = ButterKnife.bind(viewHolder.alertSliderViewHolder, view);
    }

    @Override public void onStart()
    {
        super.onStart();
        viewHolder.onStart();
    }

    @Override public void onStop()
    {
        viewHolder.onStop();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        viewHolder = null;
        super.onDetach();
    }

    @OnClick(R.id.close)
    @Override public void dismiss()
    {
        super.dismiss();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.alert_menu_save)
    protected void conditionalSaveAlert(View view)
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                viewHolder.conditionalSaveAlert())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<AlertCompactDTO>()
                        {
                            @Override public void call(AlertCompactDTO t1)
                            {
                                handleAlertUpdated(t1);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    protected void handleAlertUpdated(@NonNull AlertCompactDTO alertCompactDTO)
    {
        dismiss();
    }
}
