package com.tradehero.th.fragments.social.follower;

import dagger.Component;

@Component
public interface FragmentSocialFollowerComponent
{
    void injectFollowerManagerFragment(FollowerManagerFragment target);
    void injectFollowerRevenueReportFragment(FollowerRevenueReportFragment target);
    void injectAllFollowerFragment(AllFollowerFragment target);
    void injectPremiumFollowerFragment(PremiumFollowerFragment target);
    void injectFreeFollowerFragment(FreeFollowerFragment target);
    void injectFollowerPayoutManagerFragment(FollowerPayoutManagerFragment target);
    void injectFollowerListItemView(FollowerListItemView target);
    void injectFollowerRevenueListItemView(FollowerRevenueListItemView target);
    void injectFollowerRoiListItemView(FollowerRoiListItemView target);
    void injectSendMessageFragment(SendMessageFragment target);
}
