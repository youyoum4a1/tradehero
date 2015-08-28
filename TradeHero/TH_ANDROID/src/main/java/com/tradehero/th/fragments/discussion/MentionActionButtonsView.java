package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.social.AllRelationsRecyclerFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.rx.view.ViewArrayObservable;
import javax.inject.Inject;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.functions.Func1;

public class MentionActionButtonsView extends LinearLayout
{
    @Inject DashboardNavigator navigator;

    @NonNull private String returnFragmentName;

    @Bind({R.id.btn_mention, R.id.btn_security_tag})
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
                                return navigator.pushFragment(AllRelationsRecyclerFragment.class, bundle);
                            case R.id.btn_security_tag:
                                return navigator.pushFragment(SecuritySearchFragment.class, bundle);
                        }
                        throw new IllegalArgumentException("Unhandled view " + event);
                    }
                });
    }
}
