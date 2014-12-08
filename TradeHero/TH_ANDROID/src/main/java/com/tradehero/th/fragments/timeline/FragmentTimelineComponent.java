package com.tradehero.th.fragments.timeline;

import dagger.Component;

/**
 * Created by tho on 9/9/2014.
 */
@Component
public interface FragmentTimelineComponent
{
    void injectTimelineFragment(TimelineFragment target);
    void injectMeTimelineFragment(MeTimelineFragment target);
    void injectPushableTimelineFragment(PushableTimelineFragment target);
    void injectUserStatisticView(UserStatisticView target);
    void injectTimelineItemViewLinear(TimelineItemViewLinear target);
    void injectUserProfileCompactViewHolder(UserProfileCompactViewHolder target);
    void injectUserProfileDetailViewHolder(UserProfileDetailViewHolder target);
    void injectUserProfileResideMenuItem(UserProfileResideMenuItem target);
}
