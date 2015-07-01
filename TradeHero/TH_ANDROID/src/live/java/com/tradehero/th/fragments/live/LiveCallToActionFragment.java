package com.tradehero.th.fragments.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.activities.IdentityPromptActivity;
import com.tradehero.th.activities.SignUpLiveActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.rx.TimberOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func2;

public class LiveCallToActionFragment extends DashboardFragment
{
    @Inject DashboardNavigator navigator;
    @Inject FastFillUtil fastFill;

    @Bind(R.id.live_button_go_live) View goLiveButton;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_live_action_screen, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        onDestroyViewSubscriptions.add(Observable.combineLatest(
                ViewObservable.clicks(goLiveButton),
                fastFill.isAvailable(getActivity()),
                new Func2<OnClickEvent, Boolean, Boolean>()
                {
                    @Override public Boolean call(OnClickEvent onClickEvent, Boolean available)
                    {
                        return available;
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean fastFillAvailable)
                            {
                                navigator.launchActivity(fastFillAvailable
                                        ? IdentityPromptActivity.class
                                        : SignUpLiveActivity.class);
                                navigator.popFragment();
                            }
                        },
                        new TimberOnErrorAction("Failed to get FastFill available")));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.tradehero_live);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.live_button_later)
    public void onLaterButtonClicked(View v)
    {
        navigator.popFragment();
    }
}
