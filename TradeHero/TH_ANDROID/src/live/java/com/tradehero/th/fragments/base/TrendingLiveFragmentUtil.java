package com.tradehero.th.fragments.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.Bind;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.activities.LiveActivityUtil;
import com.tradehero.th.fragments.live.LiveCallToActionFragment;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.persistence.prefs.ShowCallToActionFragmentPreference;
import javax.inject.Inject;
import rx.Subscription;
import rx.functions.Action1;

public class TrendingLiveFragmentUtil extends BaseLiveFragmentUtil
{
    @Bind(R.id.live_fragment_container) FrameLayout liveFragmentContainer;
    @Bind(R.id.pager) ViewPager pager;
    @Nullable private LiveCallToActionFragment callToActionFragment;

    @Inject @ShowCallToActionFragmentPreference BooleanPreference showCallToActionFragment;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    @Inject LiveActivityUtil liveActivityUtil;

    private Subscription laterClickedSubscription;

    public TrendingLiveFragmentUtil(Fragment f, View view)
    {
        super(f, view);
    }

    @Override public void setCallToAction(boolean isLive)
    {
        super.setCallToAction(isLive);
        //if (isLive)
        //{
        //    if (showCallToActionFragment.get())
        //    {
        //        setCallToActionFragmentVisible();
        //        showCallToActionBubbleGone();
        //    }
        //    else
        //    {
        //        showCallToActionBubbleVisible();
        //        setCallToActionFragmentGone();
        //    }
        //}
        //else
        //{
        //    setCallToActionFragmentGone();
        //    showCallToActionBubbleGone();
        //}
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
                //setCallToActionFragmentGone();
                liveActivityUtil.switchLive(false);
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
    }

    @Override protected void showCallToActionBubbleGone()
    {
        super.showCallToActionBubbleGone();
        showCallToActionFragment.set(true);
    }

    @Override public void onResume()
    {
        super.onResume();
        setCallToAction(isLiveTrading.get());
    }
}