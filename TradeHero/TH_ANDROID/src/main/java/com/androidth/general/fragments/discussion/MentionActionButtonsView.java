package com.androidth.general.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import com.androidth.general.common.fragment.HasSelectedItem;
import com.androidth.general.R;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.security.SecuritySearchFragment;
import com.androidth.general.fragments.social.AllRelationsRecyclerFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.rx.view.ViewArrayObservable;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.functions.Func1;

public class MentionActionButtonsView extends LinearLayout
{
    @Inject DashboardNavigator navigator;

    @NonNull private String returnFragmentName;

    @BindViews({R.id.btn_mention, R.id.btn_security_tag})
    View[] buttons;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public MentionActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
    }

    public void setReturnFragmentName(@NonNull String returnFragmentName)
    {
        this.returnFragmentName = returnFragmentName;
    }

    @NonNull public Observable<HasSelectedItem> getSelectedItemObservable()
    {
        return ViewArrayObservable.clicks(buttons, false)
                .map(new Func1<OnClickEvent, HasSelectedItem>()
                {
                    @Override public HasSelectedItem call(OnClickEvent event)
                    {
                        Bundle bundle = new Bundle();
                        DashboardNavigator.putReturnFragment(bundle, returnFragmentName);
                        switch(event.view().getId())
                        {
                            case R.id.btn_mention:
                                AllRelationsRecyclerFragment.putPerPage(bundle, AllRelationsRecyclerFragment.PREFERRED_PER_PAGE);
                                return navigator.pushFragment(AllRelationsRecyclerFragment.class, bundle);
                            case R.id.btn_security_tag:
                                return navigator.pushFragment(SecuritySearchFragment.class, bundle);
                        }
                        throw new IllegalArgumentException("Unhandled view " + event);
                    }
                });
    }
}
