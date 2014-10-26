package com.tradehero.th.fragments.chinabuild.fragment.userCenter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.WeiboUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-16.
 */
public class UserMainPage extends DashboardFragment
{

    public static final String BUNDLE_NEED_SHOW_PROFILE = "bundle_need_show_profile";//是否需要显示他的持仓再右上角
    public static final String BUNDLE_USER_BASE_KEY = "bundle_user_base_key";//用户ID

    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject UserProfileCache userProfileCacheA;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> currentUserProfileCacheListener;
    @Inject CurrentUserId currentUserId;

    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;
    private MiddleCallback<UserProfileDTO> freeUnFollowMiddleCallback;
    private MiddleCallback<TimelineDTO> timeLineMiddleCallback;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject protected Picasso picasso;

    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;

    @InjectView(R.id.includeUserHeader) LinearLayout includeUserHeader;

    @InjectView(R.id.imgMeHead) ImageView imgMeHead;
    @InjectView(R.id.tvMeName) TextView tvMeName;
    @InjectView(R.id.tvAllAmount) TextView tvAllAmount;
    @InjectView(R.id.tvAllHero) TextView tvAllHero;
    @InjectView(R.id.tvAllFans) TextView tvAllFans;
    @InjectView(R.id.tvEarning) TextView tvEarning;
    @InjectView(R.id.imgArrorRight) ImageView imgArrorRight;
    @InjectView(R.id.tvUserCared) TextView tvUserCared;

    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.imgEmpty) ImageView imgEmpty;

    @InjectView(R.id.llItemAllAmount) LinearLayout llItemAllAmount;
    @InjectView(R.id.llItemAllHero) LinearLayout llItemAllHero;
    @InjectView(R.id.llItemAllFans) LinearLayout llItemAllFans;

    private int userID;
    private boolean isNeedShowPorfolio;
    private UserBaseKey userBaseKey;
    private UserProfileDTO currentUserProfileDTO;

    private UserTimeLineAdapter adapter;

    private int maxID = -1;

    private boolean isMyMainPage = false;
    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    private String dialogContent;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userID = getUserID();
        isNeedShowPorfolio = getIsNeedShowPortfolio();
        if (userID != 0)
        {
            userBaseKey = new UserBaseKey(userID);
            if (userBaseKey.key.equals(currentUserId.toUserBaseKey().getUserId()))
            {
                isMyMainPage = true;
            }
        }
        Timber.d("UserID = " + userID);
        userProfileCacheListener = createUserProfileFetchListener();
        currentUserProfileCacheListener = createCurrentUserProfileFetchListener();
        adapter = new UserTimeLineAdapter(getActivity(), isMyMainPage);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        if (isMyMainPage)
        {
            setHeadViewMiddleMain("我的动态");
            includeUserHeader.setVisibility(View.GONE);
        }
        else
        {
            setHeadViewMiddleMain("TA的主页");
            if (isNeedShowPorfolio)
            {
                setHeadViewRight0("TA的持仓");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.user_main_page, container, false);
        ButterKnife.inject(this, view);
        refreshData();
        initView();

        if (adapter.getCount() == 0)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
        }

        return view;
    }

    public void refreshData()
    {
        if (adapter != null && adapter.getCount() > 0)
        {

        }
        else
        {
            startLoadding();
            fetchTimeLine();
        }
        fetchUserProfile();
        fetchCurrentUserProfile();
    }

    public void initView()
    {
        imgArrorRight.setVisibility(View.INVISIBLE);

        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        listTimeLine.setAdapter(adapter);
        listTimeLine.setEmptyView(imgEmpty);

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater()
        {
            @Override
            public void OnTimeLineItemClicked(int position)
            {
                Timber.d("Item position = " + position);
                enterTimeLineDetail((TimelineItemDTO) adapter.getItem(position));
            }

            @Override
            public void OnTimeLinePraiseClicked(int position)
            {
                Timber.d("Praise position = " + position);
            }

            @Override
            public void OnTimeLineCommentsClicked(int position)
            {
                Timber.d("Comments position = " + position);
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                comments(dto);
            }

            @Override
            public void OnTimeLineShareClied(int position)
            {
                Timber.d("Share position = " + position);
                shareToWechatMoment(adapter.getItemString(position));
            }
        });

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                fetchTimeLine();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");
                fetchTimeLineMore();
            }
        });
    }

    public void enterTimeLineDetail(AbstractDiscussionCompactDTO dto)
    {
        if (dto == null) return;
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSTION_ID, dto.getDiscussionKey().getArgs());
        pushFragment(TimeLineItemDetailFragment.class, bundle);
    }

    public void comments(AbstractDiscussionCompactDTO dto)
    {
        DiscussionKey discussionKey = dto.getDiscussionKey();
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                discussionKey.getArgs());
        pushFragment(DiscussSendFragment.class, bundle);
    }

    public void share(String strShare)
    {

        mShareSheetTitleCache.set(strShare);

        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                {
                    @Override
                    public void onShareRequestedClicked()
                    {

                    }
                });

        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }

    //Share to wechat moment and share to weibo on the background
    private void shareToWechatMoment(final String strShare)
    {
        String show  = getUnParsedText(strShare);
        if(TextUtils.isEmpty(show)){
            return;
        }
        UserProfileDTO updatedUserProfileDTO = userProfileCacheA.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (updatedUserProfileDTO.wbLinked)
            {
                String outputStr = show;
                String downloadCNTradeHeroWeibo = getActivity().getResources().getString(R.string.download_tradehero_android_app_on_weibo);
                outputStr = WeiboUtils.getShareContentWeibo(outputStr, downloadCNTradeHeroWeibo);
                InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(outputStr);
                userServiceWrapper.get().inviteFriends(
                        currentUserId.toUserBaseKey(), inviteFormDTO, new RequestCallback());
            }
        }
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = show;
        ((SocialSharerImpl)socialSharerLazy.get()).share(weChatDTO, getActivity());

    }

    private class RequestCallback implements Callback {

        @Override
        public void success(Object o, Response response) {

        }

        @Override
        public void failure(RetrofitError retrofitError) {

        }
    }

    public void startLoadding()
    {
        if (getActivity() != null)
        {
            alertDialogUtilLazy.get().showProgressDialog(getActivity(), "加载中");
        }
    }

    public void displayFollow()
    {
        if(tvUserCared == null) return;
        tvUserCared.setVisibility(View.VISIBLE);
        if (isFollowUser())
        {
            tvUserCared.setText("已关注");
            tvUserCared.setBackgroundResource(R.drawable.btn_cared_xml);
        }
        else
        {
            tvUserCared.setText("关注");
            tvUserCared.setBackgroundResource(R.drawable.btn_care_action_xml);
        }
    }

    @Override public void onClickHeadRight0()
    {
        enterUserPortfolio();
    }

    private void enterUserPortfolio()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, userID);
        bundle.putBoolean(PortfolioFragment.BUNLDE_NEED_SHOW_MAINPAGE, false);
        pushFragment(PortfolioFragment.class, bundle);
    }

    @OnClick(R.id.tvUserCared)
    public void onClickCare()
    {
        if (currentUserProfileDTO != null && currentUserProfileDTO.isVisitor && currentUserProfileDTO.getAllHeroCount() >= 5)
        {
            dialogContent = getActivity().getResources().getString(R.string.guest_user_dialog_summary);
            showSuggestLoginDialogFragment(dialogContent);
            return;
        }
        todoFollowing();
    }

    public void todoFollowing()
    {
        if (isFollowUser())
        {
            //去取消关注
            freeUnFollow(userBaseKey);
        }
        else
        {
            //去关注
            freeFollow(userBaseKey);
        }
    }

    public boolean isFollowUser()
    {
        return currentUserProfileDTO.isFollowingUser(userID);
    }

    public int getUserID()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            this.userID = getArguments().getInt(BUNDLE_USER_BASE_KEY, 0);
        }
        return userID;
    }

    public boolean getIsNeedShowPortfolio()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            this.isNeedShowPorfolio = getArguments().getBoolean(BUNDLE_NEED_SHOW_PROFILE, true);
        }
        return isNeedShowPorfolio;
    }

    @Override
    public void onStop()
    {

        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        detachUserProfileCache();
        detachTimeLineMiddleCallback();
        detachCurrentUserProfileCache();
        detachFreeFollowMiddleCallback();
        detachFreeUnFollowMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        userProfileCacheListener = null;
        timeLineMiddleCallback = null;
        freeFollowMiddleCallback = null;
        freeUnFollowMiddleCallback = null;
        timeLineMiddleCallback = null;

        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
            linkWith(value);
            finish();
        }

        @Override
        public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            finish();
        }

        private void finish()
        {
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createCurrentUserProfileFetchListener()
    {
        return new CurrentUserProfileFetchListener();
    }

    protected class CurrentUserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            setCurrentUserDTO(value);
        }

        @Override
        public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    protected void freeFollow(@NotNull UserBaseKey heroId)
    {
        alertDialogUtilLazy.get().showProgressDialog(getActivity(), getActivity().getString(
                R.string.following_this_hero));
        detachFreeFollowMiddleCallback();
        freeFollowMiddleCallback =
                userServiceWrapperLazy.get()
                        .freeFollow(heroId, new FreeFollowCallback());
    }

    protected void freeUnFollow(@NotNull UserBaseKey heroId)
    {
        alertDialogUtilLazy.get().showProgressDialog(getActivity(), getActivity().getString(
                R.string.unfollowing_this_hero));
        detachFreeUnFollowMiddleCallback();
        freeUnFollowMiddleCallback =
                userServiceWrapperLazy.get().unfollow(heroId, new FreeUnFollowCallback());
    }

    public class FreeUnFollowCallback implements retrofit.Callback<UserProfileDTO>
    {
        @Override
        public void success(UserProfileDTO userProfileDTO, Response response)
        {
            alertDialogUtilLazy.get().dismissProgressDialog();
            userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
            currentUserProfileDTO = userProfileDTO;
            displayFollow();
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    public class FreeFollowCallback implements retrofit.Callback<UserProfileDTO>
    {
        @Override
        public void success(UserProfileDTO userProfileDTO, Response response)
        {
            alertDialogUtilLazy.get().dismissProgressDialog();
            userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
            currentUserProfileDTO = userProfileDTO;
            displayFollow();
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    private void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
    }

    private void detachFreeUnFollowMiddleCallback()
    {
        if (freeUnFollowMiddleCallback != null)
        {
            freeUnFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeUnFollowMiddleCallback = null;
    }

    private void detachTimeLineMiddleCallback()
    {
        if (timeLineMiddleCallback != null)
        {
            timeLineMiddleCallback.setPrimaryCallback(null);
        }
        timeLineMiddleCallback = null;
    }

    public void setCurrentUserDTO(UserProfileDTO userDTO)
    {
        this.currentUserProfileDTO = userDTO;
        displayFollow();
    }

    private void linkWith(UserProfileDTO user)
    {
        if (user != null)
        {
            if (user.picture != null && imgMeHead != null)
            {
                picasso.load(user.picture).placeholder(R.drawable.superman_facebook).fit().error(R.drawable.superman_facebook)
                        .centerInside().into(imgMeHead);
            }
            tvMeName.setText(user.displayName);
            tvAllFans.setText(String.valueOf(user.allFollowerCount));
            tvAllHero.setText(String.valueOf(user.heroIds == null ? 0 : user.heroIds.size()));
        }
        linkWith(user.portfolio);
    }

    private void linkWith(PortfolioCompactDTO cached)
    {
        if (cached != null)
        {
            String valueString = String.format("%s %,.0f", cached.getNiceCurrency(), cached.totalValue);
            tvAllAmount.setText(valueString);
            if (cached.roiSinceInception != null)
            {
                THSignedNumber roi = THSignedPercentage.builder(cached.roiSinceInception * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                tvEarning.setText(roi.toString());
                tvEarning.setTextColor(getResources().getColor(roi.getColorResId()));
            }
        }
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    private void detachCurrentUserProfileCache()
    {
        userProfileCache.get().unregister(currentUserProfileCacheListener);
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.get().register(userBaseKey, userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(userBaseKey);
    }

    protected void fetchCurrentUserProfile()
    {
        detachCurrentUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), currentUserProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public void fetchTimeLine()
    {
        maxID = -1;
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineNew(userBaseKey, 10, -1, maxID, new TimeLineCallback());
    }

    public void fetchTimeLineMore()
    {
        detachTimeLineMiddleCallback();
        maxID = adapter.getMaxID();
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineNew(userBaseKey, 10, maxID, -1, new TimeLineCallback());
    }

    public class TimeLineCallback implements retrofit.Callback<TimelineDTO>
    {
        @Override
        public void success(TimelineDTO timelineDTO, Response response)
        {

            if (maxID == -1)//重新加载
            {
                adapter.setListData(timelineDTO);
                adapter.notifyDataSetChanged();
            }
            else
            {
                adapter.addItems(timelineDTO);
                adapter.notifyDataSetChanged();
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            onFinish();
        }

        public void onFinish()
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
            listTimeLine.onRefreshComplete();
        }
    }

    @OnClick({R.id.llItemAllAmount, R.id.llItemAllHero, R.id.llItemAllFans})
    public void onItemClicked(View view)
    {
        int id = view.getId();
        switch (id)
        {
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
        bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_USER_ID, userBaseKey.key);
        pushFragment(UserAccountPage.class, bundle);
    }

    public void enterFriendsListFragment(int type)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_USER_ID, userBaseKey.key);
        bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_FRIENDS_TYPE, type);
        pushFragment(UserFriendsListFragment.class, bundle);
    }
}
