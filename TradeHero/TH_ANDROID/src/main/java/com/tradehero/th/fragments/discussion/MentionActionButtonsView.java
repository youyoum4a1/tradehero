package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectViews;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.social.AllRelationsFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.rx.view.ViewArrayObservable;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.functions.Func1;

public class MentionActionButtonsView extends LinearLayout
{
    @Inject DashboardNavigator navigator;

    @NotNull private String returnFragmentName;

    @InjectViews({R.id.btn_mention, R.id.btn_security_tag})
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
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
    }

    public void setReturnFragmentName(@NotNull String returnFragmentName)
    {
        this.returnFragmentName = returnFragmentName;
    }

    @NotNull public Observable<HasSelectedItem> getSelectedItemObservable()
    {
        return ViewArrayObservable.clicks(buttons, false)
                .map(new Func1<View, HasSelectedItem>()
                {
                    @Override public HasSelectedItem call(View view)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString(DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT, returnFragmentName);
                        switch(view.getId())
                        {
                            case R.id.btn_mention:
                                return navigator.pushFragment(AllRelationsFragment.class, bundle);
                            case R.id.btn_security_tag:
                                return navigator.pushFragment(SecuritySearchFragment.class, bundle);
                        }
                        throw new IllegalArgumentException("Unhandled view " + view);
                    }
                });
    }
}
