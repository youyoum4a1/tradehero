package com.tradehero.th.fragments.chinabuild;

import android.os.Bundle;
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
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.InviteFriendsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.MyProfileFragment;
import com.tradehero.th.fragments.chinabuild.fragment.SettingFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserAccountPage;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserFriendsListFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th2.R;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class MainTabFragmentMe extends AbsBaseFragment
{
    @Inject protected Picasso picasso;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    DTOCacheNew.Listener<OwnedPortfolioId, PortfolioDTO> portfolioFetchListener;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject PortfolioCache portfolioCache;

    @InjectView(R.id.rlCustomHeadView) RelativeLayout rlCustomHeadLayout;
    @InjectView(R.id.tvHeadLeft) TextView tvHeadLeft;
    @InjectView(R.id.tvHeadMiddleMain) TextView tvHeadTitle;
    @InjectView(R.id.tvHeadRight0) TextView tvHeadRight;

    @InjectView(R.id.rlMeDynamic) RelativeLayout rlMeDynamic;
    @InjectView(R.id.rlMeMessageCenter) RelativeLayout rlMeMessageCenter;
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
            initUserProfile(value);
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
            //linkWith(value);
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull OwnedPortfolioId key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_portfolio_info);
            Timber.d(getString(R.string.error_fetch_portfolio_info));
        }
    }

    private void initUserProfile(UserProfileDTO user)
    {
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
                tvMeName.setText(user.displayName);
            }
            tvAllFans.setText(String.valueOf(user.allFollowerCount));
            tvAllHero.setText(String.valueOf(user.heroIds == null ? 0 : user.heroIds.size()));
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
                gotoDashboard(MyProfileFragment.class.getName());
                break;
            case R.id.rlMeDynamic:
                Timber.d("clicked rlMeDynamic");
                enterMyMainPager();
                break;
            case R.id.rlMeMessageCenter:
                Timber.d("clicked rlMeMessageCenter");
                break;
            case R.id.rlMeInviteFriends:
                Timber.d("clicked rlMeInviteFriends");
                gotoDashboard(InviteFriendsFragment.class.getName());
                break;
            case R.id.rlMeSetting:
                Timber.d("clicked rlMeSetting");
                gotoDashboard(SettingFragment.class.getName());
                break;
            case R.id.llItemAllAmount:
                Timber.d("clicked llItemAllAmount");
                enterUserAllAmount();
                break;
            case R.id.llItemAllHero:
                Timber.d("clicked llItemAllHero");
                enterFriendsListFragment(UserFriendsListFragment.TYPE_FRIENDS_HERO);
                break;
            case R.id.llItemAllFans:
                Timber.d("clicked llItemAllFans");
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

    @Override public void onStop()
    {
        super.onStop();
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

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchUserProfile();
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
        //tvHeadRight.setVisibility(View.VISIBLE);
        tvHeadTitle.setVisibility(View.VISIBLE);
        tvHeadRight.setVisibility(View.VISIBLE);
        tvHeadTitle.setText(R.string.tab_main_me);
        //tvHeadRight.setText(R.string.settings);
    }

    protected void fetchUserProfile()
    {
        detachPortfolioCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
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
}
