package com.tradehero.th.fragments.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.Bind;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.activities.IdentityPromptActivity;
import com.tradehero.th.activities.SignUpLiveActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.live.LiveCallToActionFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.fastfill.FastFillUtil;
import com.tradehero.th.persistence.prefs.ShowCallToActionFragmentPreference;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func2;

public class TrendingLiveFragmentUtil extends BaseLiveFragmentUtil
{
    @Bind(R.id.live_fragment_container) FrameLayout liveFragmentContainer;
    @Bind(R.id.pager) ViewPager pager;
    @Nullable private LiveCallToActionFragment callToActionFragment;

    @Inject @ShowCallToActionFragmentPreference BooleanPreference showCallToActionFragment;

    private Subscription laterClickedSubscription;

    public TrendingLiveFragmentUtil(Fragment f, View view)
    {
        super(f, view);
    }

    @Override public void setCallToAction(boolean isLive)
    {
        if (isLive)
        {
            if (showCallToActionFragment.get())
            {
                setCallToActionFragmentVisible();
            }
            else
            {
                showCallToActionBubbleVisible();
            }
        }
        else
        {
            if (showCallToActionFragment.get())
            {
                setCallToActionFragmentGone();
            }
            else
            {
                showCallToActionBubbleGone();
            }
        }
    }

    private void setCallToActionFragmentVisible()
    {
        liveFragmentContainer.setVisibility(View.VISIBLE);
        if (callToActionFragment == null)
        {
            callToActionFragment = new LiveCallToActionFragment();
            callToActionFragment.setArguments(fragment.getArguments());
        }

        laterClickedSubscription = callToActionFragment.getOnLaterClickedSubscribtion().subscribe(new Action1<View>()
        {
            @Override public void call(View view)
            {
                setCallToActionFragmentGone();
            }
        });

        if (!callToActionFragment.isAdded())
        {
            fragment.getChildFragmentManager().beginTransaction().replace(R.id.live_fragment_container, callToActionFragment).commit();
            pager.setVisibility(View.GONE);
        }
    }

    private void setCallToActionFragmentGone()
    {
        if (callToActionFragment != null && callToActionFragment.isAdded())
        {
            fragment.getChildFragmentManager().beginTransaction().remove(callToActionFragment).commit();
        }
        if (laterClickedSubscription != null)
        {
            laterClickedSubscription.unsubscribe();
        }
        liveFragmentContainer.setVisibility(View.GONE);
        pager.setVisibility(View.VISIBLE);
        showCallToActionFragment.set(false); //Only display fragment once, the next one should be a bubble
    }
}
