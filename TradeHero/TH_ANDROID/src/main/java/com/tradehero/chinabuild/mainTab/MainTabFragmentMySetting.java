package com.tradehero.chinabuild.mainTab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.AppInfoDTO;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.InviteFriendsFragment;
import com.tradehero.chinabuild.fragment.LoginSuggestDialogFragment;
import com.tradehero.chinabuild.fragment.MyProfileFragment;
import com.tradehero.chinabuild.fragment.SettingFragment;
import com.tradehero.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.chinabuild.fragment.message.NotificationFragment;
import com.tradehero.chinabuild.fragment.userCenter.MyMainPage;
import com.tradehero.chinabuild.fragment.userCenter.UserAccountPage;
import com.tradehero.chinabuild.fragment.userCenter.UserFansListFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserHeroesListFragment;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.SecurityOptPhoneNumBindFragment;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeManager;
import com.tradehero.livetrade.thirdPartyServices.haitong.HaitongUtils;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.DisplayLargeImageActivity;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MainTabFragmentMySetting extends AbsBaseFragment implements View.OnClickListener{

    DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> portfolioFetchListener;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject PortfolioCache portfolioCache;

    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = AbsBaseFragment.class.getName() + ".purchaseApplicablePortfolioId";

    @Inject protected CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Inject Lazy<UserProfileCache> userProfileCache;

    public UserProfileDTO userProfileDTO;

    //Show dialogfragment
    private LoginSuggestDialogFragment dialogFragment;
    private FragmentManager fm;

    @InjectView(R.id.textview_me_notification_count) TextView tvMeNotificationCount;
    @InjectView(R.id.imageview_me_new_version)ImageView ivNewVersion;

    @InjectView(R.id.imgMeHead) ImageView imgMeHead;
    @InjectView(R.id.tvMeName) TextView tvMeName;
    @InjectView(R.id.tvAllAmount) TextView tvAllAmount;
    @InjectView(R.id.tvAllHero) TextView tvAllHero;
    @InjectView(R.id.tvAllFans) TextView tvAllFans;
    @InjectView(R.id.tvEarning) TextView tvEarning;

    private Button openAccountBtn;
    private Button loginAccountBtn;

    @Inject Analytics analytics;
    @Inject LiveTradeManager tradeManager;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = createUserProfileFetchListener();
        portfolioCompactListFetchListener = createPortfolioCompactListFetchListener();
        fetchUserProfile();
        setHasOptionsMenu(true);

        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tab_fragment_me_layout, container, false);
        ButterKnife.inject(this, view);

        openAccountBtn = (Button)view.findViewById(R.id.security_open_account);
        loginAccountBtn = (Button)view.findViewById(R.id.security_firm_bargain);
        openAccountBtn.setOnClickListener(this);
        loginAccountBtn.setOnClickListener(this);

        userProfileCacheListener = createUserProfileFetchListener();
        portfolioFetchListener = createPortfolioCacheListener();
        return view;
    }


    @Override public void onResume() {
        super.onResume();
        fetchPortfolioCompactList();
        tvMeNotificationCount.setVisibility(View.GONE);
        fetchUserProfile();
        fetchPortfolio();

        AppInfoDTO appInfoDTO = THSharePreferenceManager.getAppVersionInfo(getActivity());
        if(appInfoDTO.isForceUpgrade() || appInfoDTO.isSuggestUpgrade()){
            ivNewVersion.setVisibility(View.VISIBLE);
        }else{
            ivNewVersion.setVisibility(View.GONE);
        }
    }

    @Override public void onDestroyView() {
        ButterKnife.reset(this);
        detachUserProfileCache();
        userProfileCacheListener = null;
        portfolioFetchListener = null;
        super.onDestroyView();
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

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.security_open_account:
                enterSecurityOpenAccount();
                break;
            case R.id.security_firm_bargain:
                enterSecurityFirmBargain();
                break;
        }
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

    private void initUserProfile(UserProfileDTO user) {
        if(tvMeName==null||tvAllFans==null||tvAllHero==null){
            return;
        }

        if (user != null) {
            if (imgMeHead != null) {
                if (user.picture != null) {
                    ImageLoader.getInstance().displayImage(user.picture, imgMeHead, UniversalImageLoader.getAvatarImageLoaderOptions());
                } else {
                    imgMeHead.setImageResource(R.drawable.avatar_default);
                }
            }
            final String avatarUrl;
            if(user != null){
                avatarUrl = user.picture;
                imgMeHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        displayLargeImg(avatarUrl);
                    }
                });
            } else {
                imgMeHead.setOnClickListener(null);
            }

            if (user.isVisitor) {
                tvMeName.setText(R.string.guest_user);
            } else {
                tvMeName.setText(user.getDisplayName());
            }
            tvAllFans.setText(String.valueOf(user.allFollowerCount));
            tvAllHero.setText(String.valueOf(user.getAllHeroCount()));
            //粉丝数达到10人
            if (user.allFollowerCount > 9) {
                int userId = currentUserId.toUserBaseKey().getUserId();
                if (THSharePreferenceManager.isShareDialogFANSMoreThanNineAvailable(userId, getActivity())) {
                    String moreThanNineFans = getString(R.string.share_amount_fans_num_summary);
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
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_PERSONAL_PAGE));
                gotoDashboard(MyProfileFragment.class.getName(), new Bundle());
                break;
            case R.id.rlMeDynamic:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_MY_MOMENT));
                enterMyMainPager();
                break;
            case R.id.rlMeMessageCenter:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_MESSAGE_CENTER));
                gotoDashboard(NotificationFragment.class.getName(), new Bundle());
                break;
            case R.id.rlMeInviteFriends:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_INVITE_FRIENDS));
                gotoDashboard(InviteFriendsFragment.class.getName(), new Bundle());
                break;
            case R.id.rlMeSetting:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MINE_SETTING));
                gotoDashboard(SettingFragment.class.getName(), new Bundle());
                break;
            case R.id.llItemAllAmount:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_TOTAL_PROPERTY));
                enterUserAllAmount();
                break;
            case R.id.llItemAllHero:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_STOCK_HEROES));
                enterHeroesListFragment();
                break;
            case R.id.llItemAllFans:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_STOCK_FOLLOWER));
                enterFollowersListFragment();
                break;
        }
    }

    public void enterUserAllAmount() {
        Bundle bundle = new Bundle();
        bundle.putInt(UserHeroesListFragment.BUNDLE_SHOW_USER_ID, currentUserId.toUserBaseKey().key);
        gotoDashboard(UserAccountPage.class.getName(), bundle);
    }

    private void enterHeroesListFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(UserHeroesListFragment.BUNDLE_SHOW_USER_ID, currentUserId.toUserBaseKey().key);
        gotoDashboard(UserHeroesListFragment.class.getName(), bundle);
    }

    private void enterFollowersListFragment(){
        Bundle bundle = new Bundle();
        bundle.putInt(UserHeroesListFragment.BUNDLE_SHOW_USER_ID, currentUserId.toUserBaseKey().key);
        gotoDashboard(UserFansListFragment.class.getName(), bundle);
    }

    public void enterMyMainPager()
    {
        gotoDashboard(MyMainPage.class.getName(), new Bundle());
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

    protected void linkWithApplicable() {
        fetchPortfolio();
    }

    private void linkWith(PortfolioCompactDTO cached) {
        if(tvAllAmount==null || tvEarning==null){
            return;
        }
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
        if(count <= 0){
            tvMeNotificationCount.setVisibility(View.GONE);
            return;
        }
        if(count > 99){
            count = 99;
        }
        tvMeNotificationCount.setText(String.valueOf(count));
        tvMeNotificationCount.setVisibility(View.VISIBLE);
    }


    private void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
    }

    protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        this.purchaseApplicableOwnedPortfolioId = purchaseApplicablePortfolioId;
        if(purchaseApplicableOwnedPortfolioId!=null)
        {
            linkWithApplicable();
        }
    }

    @Nullable public OwnedPortfolioId getApplicablePortfolioId()
    {
        return purchaseApplicableOwnedPortfolioId;
    }

    public static OwnedPortfolioId getApplicablePortfolioId(@Nullable Bundle args)
    {
        if (args != null)
        {
            if (args.containsKey(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE))
            {
                return new OwnedPortfolioId(args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE));
            }
        }
        return null;
    }

    protected void showSuggestLoginDialogFragment(String dialogContent){
        if(dialogFragment==null){
            dialogFragment =new LoginSuggestDialogFragment();
        }
        if(fm==null){
            fm = getActivity().getSupportFragmentManager();
        }
        dialogFragment.setContent(dialogContent);
        dialogFragment.show(fm, LoginSuggestDialogFragment.class.getName());
    }

    protected DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> createPortfolioCompactListFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactListFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactListFetchListener implements DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList>
    {
        protected BasePurchaseManagementPortfolioCompactListFetchListener()
        {
            // no unexpected creation
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            prepareApplicableOwnedPortolioId(value.getDefaultPortfolio());
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    protected void prepareApplicableOwnedPortolioId(@Nullable PortfolioCompactDTO defaultIfNotInArgs)
    {
        Bundle args = getArguments();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId(args);

        if (applicablePortfolioId == null && defaultIfNotInArgs != null)
        {
            applicablePortfolioId = defaultIfNotInArgs.getOwnedPortfolioId();
        }

        if (applicablePortfolioId != null)
        {
            linkWithApplicable(applicablePortfolioId, true);
        }
    }

    private void fetchPortfolioCompactList()
    {
        detachPortfolioCompactListCache();
        portfolioCompactListCache.register(currentUserId.toUserBaseKey(), portfolioCompactListFetchListener);
        portfolioCompactListCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    private void enterSecurityFirmBargain(){
        UserProfileDTO profileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (tradeManager.getLiveTradeServices().needCheckPhoneNumber() && profileDTO.phoneNumber == null) {
            getActivity().registerReceiver(new PhoneBindBroadcastReceiver(),
                    new IntentFilter(SecurityOptPhoneNumBindFragment.PHONE_NUM_BIND_SUCCESS));

            gotoDashboard(SecurityOptPhoneNumBindFragment.class.getName(), new Bundle());
        }
        else {
            tradeManager.getLiveTradeServices().login(getActivity(), "70000399", "111111", new LiveTradeCallback<LiveTradeSessionDTO>() {
                @Override
                public void onSuccess(LiveTradeSessionDTO liveTradeSessionDTO) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SecurityOptActivity.KEY_IS_FOR_ACTUAL, true);
                    bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
                    Intent intent = new Intent(getActivity(), SecurityOptActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
                }

                @Override
                public void onError(String errorCode, String errorContent) {

                }
            });
        }
    }

    private void enterSecurityOpenAccount(){
        HaitongUtils.openAnAccount(getActivity());
    }

    class PhoneBindBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            enterSecurityFirmBargain();
        }
    }

    private void displayLargeImg(final String url){
        if(TextUtils.isEmpty(url)){
            return;
        }
        Intent intent = new Intent(getActivity(), DisplayLargeImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DisplayLargeImageActivity.KEY_LARGE_IMAGE_URL, url);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
    }

}
