package com.androidth.general.fragments.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.androidth.general.R;
import com.androidth.general.activities.LiveActivityUtil;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.fragments.kyc.LiveCallToActionFragment;
import com.androidth.general.persistence.prefs.IsLiveTrading;
import com.androidth.general.persistence.prefs.ShowCallToActionFragmentPreference;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Subscription;
import rx.functions.Action1;

public class TrendingLiveFragmentUtil extends BaseLiveFragmentUtil
{
    @Bind(R.id.live_fragment_container) FrameLayout liveFragmentContainer;
//    @Nullable @Bind(R.id.pager) ViewPager pager;

    @Nullable private LiveCallToActionFragment callToActionFragment;

    @Inject @ShowCallToActionFragmentPreference
    BooleanPreference showCallToActionFragment;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    @Inject LiveActivityUtil liveActivityUtil;

    private Subscription laterClickedSubscription;

    public TrendingLiveFragmentUtil(Fragment f, View view)
    {
        super(f, view);
    }



    public void setCallToActionFragmentVisible(View viewToReplace)
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
                setCallToActionFragmentGone(viewToReplace);
                liveActivityUtil.switchLive(true, true, false);
            }
        });

        if (!callToActionFragment.isAdded())
        {
            fragment.getChildFragmentManager().beginTransaction().replace(R.id.live_fragment_container, callToActionFragment).commit();
            viewToReplace.setVisibility(View.GONE);
        }
    }

    public void setCallToActionFragmentGone(View viewToReplace)
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
        viewToReplace.setVisibility(View.VISIBLE);
    }



    @Override public void onResume()
    {
        super.onResume();

    }

    public static void setBackgroundColor(boolean isLive, View... views)
    {
        for (View v : views)
        {
            v.setBackgroundColor(v.getContext().getResources().getColor(isLive ? R.color.general_red_live : R.color.general_brand_color));
        }
    }

    public LiveActivityUtil getLiveActivityUtil() {
        return liveActivityUtil;
    }

    public FrameLayout getLiveFragmentContainer() {
        return liveFragmentContainer;
    }
}
