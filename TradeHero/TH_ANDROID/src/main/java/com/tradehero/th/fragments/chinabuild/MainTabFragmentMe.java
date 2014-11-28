package com.tradehero.th.fragments.chinabuild;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.data.sp.THSharePreferenceManager;
import com.tradehero.th.fragments.chinabuild.fragment.*;
import com.tradehero.th.fragments.chinabuild.fragment.message.NotificationFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserAccountPage;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserFriendsListFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class MainTabFragmentMe extends AbsBaseFragment
{
    @Inject protected Picasso picasso;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> portfolioFetchListener;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject PortfolioCache portfolioCache;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;

    @InjectView(R.id.rlCustomHeadView) RelativeLayout rlCustomHeadLayout;
    @InjectView(R.id.tvHeadLeft) TextView tvHeadLeft;
    @InjectView(R.id.tvHeadMiddleMain) TextView tvHeadTitle;
    @InjectView(R.id.tvHeadRight0) TextView tvHeadRight;

    @InjectView(R.id.rlMeDynamic) RelativeLayout rlMeDynamic;
    @InjectView(R.id.rlMeMessageCenter) RelativeLayout rlMeMessageCenter;
    @InjectView(R.id.textview_me_notification_count) TextView tvMeNotificationCount;
    @InjectView(R.id.rlMeInviteFriends) RelativeLayout rlMeInviteFriends;
    @InjectView(R.id.rlMeSetting) RelativeLayout rlMeSetting;

    @InjectView(R.id.llItemAllAmount) LinearLayout llItemAllAmount;
    @InjectView(R.id.llItemAllHero) LinearLayout llItemAllHero;
    @InjectView(R.id.llItemAllFans) LinearLayout llItemAllFans;

    @InjectView(R.id.me_layout) RelativeLayout mMeLayout;
    @InjectView(R.id.imgMeHead) ImageView imgMeHead;
    @InjectView(R.id.tvMeName) TextView tvMeName;
    @InjectView(R.id.tvAllAmount) TextView tvAllAmount;
    @InjectView(R.id.tvAllHero) TextView tvAllHero;
    @InjectView(R.id.tvAllFans) TextView tvAllFans;
    @InjectView(R.id.tvEarning) TextView tvEarning;

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_tab_fragment_me_layout, container, false);
        ButterKnife.inject(this, view);
        InitView();

        userProfileCacheListener = createUserProfileFetchListener();
        portfolioFetchListener = createPortfolioCacheListener();

        return view;
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected void detachPortfolioCache()
    {
        portfolioCache.unregister(portfolioFetchListener);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new UserProfileFetchListener();
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            if(getActivity()==null){
                return;
            }
            initUserProfile(value);
            showUnreadNotificationCount(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    protected DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioCacheListener()
    {
        return new PortfolioCacheListener();
    }

    protected class PortfolioCacheListener implements DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO>
    {
        @Override public void onDTOReceived(@NotNull OwnedPortfolioId key, @NotNull PortfolioDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull OwnedPortfolioId key, @NotNull Throwable error)
        {

        }
    }

    private void initUserProfile(UserProfileDTO user)
    {
        if(tvMeName==null||tvAllFans==null||tvAllHero==null){
            return;
        }

        if (user != null)
        {
            if (user.picture != null && imgMeHead != null)
            {
                picasso.load(user.picture).placeholder(R.drawable.superman_facebook).fit().error(R.drawable.superman_facebook)
                        .centerInside().into(imgMeHead);
            }
            if (user.isVisitor)
            {
                tvMeName.setText(R.string.guest_user);
            }
            else
            {
                tvMeName.setText(user.getDisplayName());
            }
            tvAllFans.setText(String.valueOf(user.allFollowerCount));
            tvAllHero.setText(String.valueOf(user.getAllHeroCount()));
            //粉丝数达到10人
            if (user.allFollowerCount > 9)
            {
                int userId = currentUserId.toUserBaseKey().getUserId();
                if (THSharePreferenceManager.isShareDialogFANSMoreThanNineAvailable(userId, getActivity())) {
                    String moreThanNineFans = getActivity().getResources().getString(R.string.share_amount_fans_num_summary);
                    ShareDialogFragment.showDialog(getActivity().getSupportFragmentManager(),
                            getString(R.string.share_amount_fans_num_title), moreThanNineFans, THSharePreferenceManager.FANS_MORE_THAN_NINE, userId);
                    THSharePreferenceManager.FansMoreThanNineShowed = true;
                }
            }
        }
    }

    @OnClick({R.id.rlMeDynamic, R.id.rlMeMessageCenter, R.id.rlMeInviteFriends, R.id.rlMeSetting,
            R.id.llItemAllAmount, R.id.llItemAllHero, R.id.llItemAllFans, R.id.me_layout})
    public void onItemClicked(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.me_layout:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_PERSONAL_PAGE));
                gotoDashboard(MyProfileFragment.class.getName());
                break;
            case R.id.rlMeDynamic:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_MY_MOMENT));
                enterMyMainPager();
                break;
            case R.id.rlMeMessageCenter:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_MESSAGE_CENTER));
                gotoDashboard(NotificationFragment.class.getName());
                break;
            case R.id.rlMeInviteFriends:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_INVITE_FRIENDS));
                gotoDashboard(InviteFriendsFragment.class.getName());
                break;
            case R.id.rlMeSetting:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_SETTING));
                gotoDashboard(SettingFragment.class.getName());
                break;
            case R.id.llItemAllAmount:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_TOTAL_PROPERTY));
                enterUserAllAmount();
                break;
            case R.id.llItemAllHero:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_STOCK_HEROES));
                enterFriendsListFragment(UserFriendsListFragment.TYPE_FRIENDS_HERO);
                break;
            case R.id.llItemAllFans:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_STOCK_FOLLOWER));
                enterFriendsListFragment(UserFriendsListFragment.TYPE_FRIENDS_FOLLOWS);
                break;
        }
    }

    public void enterUserAllAmount()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_USER_ID, currentUserId.toUserBaseKey().key);
        gotoDashboard(UserAccountPage.class.getName(), bundle);
    }

    public void enterFriendsListFragment(int type)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_USER_ID, currentUserId.toUserBaseKey().key);
        bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_FRIENDS_TYPE, type);
        gotoDashboard(UserFriendsListFragment.class.getName(), bundle);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        detachUserProfileCache();
        userProfileCacheListener = null;
        portfolioFetchListener = null;
        super.onDestroyView();
    }

    public void enterMyMainPager()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, currentUserId.toUserBaseKey().key);
        gotoDashboard(UserMainPage.class.getName(), bundle);
    }

    @Override public void onResume()
    {
        super.onResume();
        tvMeNotificationCount.setVisibility(View.GONE);
        fetchUserProfile();
        fetchPortfolio();
    }

    @OnClick(R.id.tvHeadRight0)
    public void settingClicked()
    {
        gotoDashboard(SettingFragment.class.getName());
    }

    private void InitView()
    {
        rlCustomHeadLayout.setVisibility(View.VISIBLE);
        tvHeadLeft.setVisibility(View.GONE);
        tvHeadTitle.setVisibility(View.VISIBLE);
        tvHeadTitle.setText(R.string.tab_main_me);
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        //Get user profile from cache
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey(), false);

        //Get user profile from server 1 second later
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey(), true);
            }
        },1000);

    }



    protected void fetchPortfolio()
    {
        if (getApplicablePortfolioId() instanceof OwnedPortfolioId)
        {
            if (currentUserId.toUserBaseKey().equals((getApplicablePortfolioId()).getUserBaseKey()))
            {
                PortfolioCompactDTO cached = portfolioCompactCache.get((getApplicablePortfolioId()).getPortfolioIdKey());
                if (cached == null)
                {
                    detachPortfolioCache();
                    portfolioCache.register(getApplicablePortfolioId(), portfolioFetchListener);
                    portfolioCache.get(getApplicablePortfolioId());
                }
                else
                {
                    linkWith(cached);
                }
            }
        }
    }

    @Override
    protected void linkWithApplicable()
    {
        fetchPortfolio();
    }

    private void linkWith(PortfolioCompactDTO cached)
    {
        if (cached != null)
        {
            String valueString = String.format("%s %,.0f", cached.getNiceCurrency(),
                    cached.totalValue);
            tvAllAmount.setText(valueString);

            Double rsi = cached.roiSinceInception == null ? 0 : cached.roiSinceInception;
            THSignedNumber roi = THSignedPercentage.builder(rsi * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvEarning.setText(roi.toString());
            tvEarning.setTextColor(getResources().getColor(roi.getColorResId()));
        }
    }

    private void showUnreadNotificationCount(@NotNull UserProfileDTO value){
        if(tvMeNotificationCount==null){
            return;
        }
        int count = value.unreadNotificationsCount;
        if(count<=0){
            tvMeNotificationCount.setVisibility(View.GONE);
            return;
        }
        if(count>99){
            tvMeNotificationCount.setText(String.valueOf("99"));
            tvMeNotificationCount.setVisibility(View.VISIBLE);
            return;
        }
        tvMeNotificationCount.setText(String.valueOf(count));
        tvMeNotificationCount.setVisibility(View.VISIBLE);
    }
}
