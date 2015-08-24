package com.tradehero.th.fragments.timeline;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.Bind;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.activities.PrivateDiscussionActivity;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.OnMovableBottomTranslateListener;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@Routable({
        "user/id/:" + PushableTimelineFragment.ROUTER_USER_ID,
        "user/id/:" + PushableTimelineFragment.ROUTER_HERO_ID_FREE + "/follow/free",
        "user/id/:" + PushableTimelineFragment.ROUTER_HERO_ID_PREMIUM + "/follow/premium",
})
public class PushableTimelineFragment extends TimelineFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @RouteProperty(ROUTER_USER_ID) Integer userId;
    @RouteProperty(ROUTER_HERO_ID_FREE) Integer freeFollowHeroId;
    @RouteProperty(ROUTER_HERO_ID_PREMIUM) Integer premiumFollowHeroId;

    public static final String ROUTER_USER_ID = "userId";
    public static final String ROUTER_HERO_ID_FREE = "heroIdFree";
    public static final String ROUTER_HERO_ID_PREMIUM = "heroIdPremium";

    @Inject protected FollowerSummaryCacheRx followerSummaryCache;
    @Inject protected THBillingInteractorRx userInteractorRx;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject MessageThreadHeaderCacheRx messageThreadHeaderCache;

    @Bind(R.id.follow_button) Button mFollowButton;
    @Bind(R.id.message_button) Button mSendMsgButton;
    @Bind(R.id.follow_message_container) ViewGroup btnContainer;

    protected int mFollowType;//0 not follow, 1 free follow, 2 premium follow
    protected boolean mIsHero = false;//whether the showUser follow the user
    protected MessageHeaderDTO messageThreadHeaderDTO;

    public static String getUserPath(@NonNull UserBaseKey userId)
    {
        return "user/id/" + userId.key;
    }

    @Nullable @Override protected UserBaseKey getShownUserBaseKey()
    {
        if (userId != null)
        {
            return new UserBaseKey(userId);
        }
        else if (freeFollowHeroId != null)
        {
            return new UserBaseKey(freeFollowHeroId);
        }
        else if (premiumFollowHeroId != null)
        {
            return new UserBaseKey(premiumFollowHeroId);
        }
        return super.getShownUserBaseKey();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mIsOtherProfile = true;
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        updateBottomButton();
        super.onPrepareOptionsMenu(menu);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchMessageThreadHeader();
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this, followerSummaryCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FollowerSummaryObserver()));
    }

    @Override public void onResume()
    {
        super.onResume();

        if (freeFollowHeroId != null)
        {
            freeFollow(shownUserBaseKey);
            freeFollowHeroId = null;
        }
        else if (premiumFollowHeroId != null)
        {
            handleFollowRequested();
            premiumFollowHeroId = null;
        }
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                btnContainer.setTranslationY(y);
            }
        });
    }

    @Override protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        super.linkWith(userProfileDTO);
        mFollowButton.setEnabled(true);
        mSendMsgButton.setEnabled(true);
        displayActionBarTitle();
    }

    private class FollowerSummaryObserver implements Observer<Pair<UserBaseKey, FollowerSummaryDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, FollowerSummaryDTO> pair)
        {
            updateHeroType(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(e.getMessage());
        }
    }

    private void updateHeroType(FollowerSummaryDTO value)
    {
        if (value != null && value.userFollowers.size() > 0)
        {
            for (UserFollowerDTO userFollowerDTO : value.userFollowers)
            {
                if (userFollowerDTO.id == shownUserBaseKey.key)
                {
                    mIsHero = true;
                    return;
                }
            }
        }
        mIsHero = false;
    }

    protected void updateBottomButton()
    {
        if (!mIsOtherProfile)
        {
            return;
        }
        mFollowType = getFollowType();
        if (mFollowType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
        {
            mFollowButton.setText(R.string.upgrade_to_premium);
        }
        else if (mFollowType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
        {
            mFollowButton.setText(R.string.following_premium);
        }
        mFollowButton.setVisibility(View.VISIBLE);
        mSendMsgButton.setVisibility(View.VISIBLE);
        mSendMsgButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (!mIsHero && (mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER
                        || mFollowType == UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG))
                {
                    //onStopSubscriptions.add(HeroAlertDialogRxUtil.showFollowDialog(
                    //        getActivity(),
                    //        shownProfile,
                    //        UserProfileDTOUtil.IS_NOT_FOLLOWER_WANT_MSG)
                    //        .flatMap(new Func1<FollowRequest, Observable<? extends UserProfileDTO>>()
                    //        {
                    //            @Override public Observable<? extends UserProfileDTO> call(FollowRequest request)
                    //            {
                    //                return handleFollowRequest(request);
                    //            }
                    //        })
                    //        .subscribe(
                    //                new EmptyAction1<UserProfileDTO>(),
                    //                new EmptyAction1<Throwable>()));
                }
                else
                {
                    pushPrivateMessageFragment();
                }
            }
        });
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.follow_button)
    protected void handleFollowRequested()
    {
        //onStopSubscriptions.add(userProfileCache.get().getOne(shownUserBaseKey)
        //        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
        //        .observeOn(AndroidSchedulers.mainThread())
        //        .flatMap(new Func1<UserProfileDTO, Observable<Pair<FollowRequest, UserProfileDTO>>>()
        //        {
        //            @Override public Observable<Pair<FollowRequest, UserProfileDTO>> call(UserProfileDTO shownProfile)
        //            {
        //                return new ChoiceFollowUserAssistantWithDialog(
        //                        getActivity(),
        //                        shownProfile)
        //                        .launchChoiceRx();
        //            }
        //        })
        //        .subscribe(
        //                new Action1<Pair<FollowRequest, UserProfileDTO>>()
        //                {
        //                    @Override public void call(Pair<FollowRequest, UserProfileDTO> pair)
        //                    {
        //                        if (!mIsOtherProfile)
        //                        {
        //                            linkWith(pair.second);
        //                        }
        //                        updateBottomButton();
        //                        String actionName = pair.first.isPremium
        //                                ? AnalyticsConstants.PremiumFollow_Success
        //                                : AnalyticsConstants.FreeFollow_Success;
        //                        analytics.addEvent(new ScreenFlowEvent(actionName, AnalyticsConstants.Profile));
        //                    }
        //                },
        //                new ToastOnErrorAction1()
        //        ));
    }

    protected Observable<UserProfileDTO> freeFollow(@NonNull UserBaseKey heroId)
    {
        ProgressDialog progressDialog = ProgressDialogUtil.create(getActivity(), getString(R.string.following_this_hero));
        return userServiceWrapperLazy.get().freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog));
    }

    protected void pushPrivateMessageFragment()
    {
        if (navigator == null)
        {
            return;
        }

        Bundle args = new Bundle();
        PrivateDiscussionActivity.putCorrespondentUserBaseKey(args, shownUserBaseKey);
        if (messageThreadHeaderDTO != null)
        {
            PrivateDiscussionActivity.putDiscussionKey(args, DiscussionKeyFactory.create(messageThreadHeaderDTO));
        }
        navigator.get().launchActivity(PrivateDiscussionActivity.class, args);
    }

    protected void fetchMessageThreadHeader()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                messageThreadHeaderCache.get(shownUserBaseKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TimelineMessageThreadHeaderCacheObserver()));
    }

    protected class TimelineMessageThreadHeaderCacheObserver implements Observer<Pair<UserBaseKey, MessageHeaderDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, MessageHeaderDTO> pair)
        {
            linkWithMessageThread(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (!(e instanceof RetrofitError) ||
                    (((RetrofitError) e).getResponse() != null &&
                            ((RetrofitError) e).getResponse().getStatus() != 404))
            {
                THToast.show(R.string.error_fetch_message_thread_header);
                Timber.e(e, "Error while getting message thread");
            }
        }
    }

    protected void linkWithMessageThread(MessageHeaderDTO messageHeaderDTO)
    {
        this.messageThreadHeaderDTO = messageHeaderDTO;
    }

    @Override public <T extends Fragment> boolean allowNavigateTo(@NonNull Class<T> fragmentClass, Bundle args)
    {
        return !(
                ((Object) this).getClass().isAssignableFrom(fragmentClass) &&
                    shownUserBaseKey != null &&
                    shownUserBaseKey.equals(getUserBaseKey(args)))
                && super.allowNavigateTo(fragmentClass, args);
    }
}
