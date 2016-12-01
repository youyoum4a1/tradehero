package com.androidth.general.fragments.timeline;

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
import com.androidth.general.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.activities.PrivateDiscussionActivity;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.discussion.key.DiscussionKeyFactory;
import com.androidth.general.api.social.FollowerSummaryDTO;
import com.androidth.general.api.social.UserFollowerDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.billing.THBillingInteractorRx;
import com.androidth.general.fragments.OnMovableBottomTranslateListener;
import com.androidth.general.models.user.follow.FollowUserAssistant;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.persistence.message.MessageThreadHeaderCacheRx;
import com.androidth.general.persistence.social.FollowerSummaryCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.ReplaceWithFunc1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.rx.view.DismissDialogAction0;
import com.androidth.general.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
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
        updateBottomButton();
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
        boolean isFollowing = isFollowing();
        mSendMsgButton.setVisibility(View.VISIBLE);
        FollowUserAssistant.updateFollowButton(mFollowButton, isFollowing, shownUserBaseKey);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.follow_button)
    protected void handleFollowRequested()
    {
        FollowUserAssistant assistant = new FollowUserAssistant(getActivity(), shownUserBaseKey);
        if (isFollowing())
        {
            onStopSubscriptions.add(assistant.showUnFollowConfirmation(shownProfile.displayName)
                    .map(new ReplaceWithFunc1<OnDialogClickEvent, FollowUserAssistant>(assistant))
                    .flatMap(new Func1<FollowUserAssistant, Observable<FollowUserAssistant>>()
                    {
                        @Override public Observable<FollowUserAssistant> call(FollowUserAssistant followUserAssistant)
                        {
                            return followUserAssistant.ensureCacheValue();
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<FollowUserAssistant>()
                    {
                        @Override public void call(FollowUserAssistant followUserAssistant)
                        {
                            followUserAssistant.unFollowFromCache();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<FollowUserAssistant>()
                    {
                        @Override public void call(FollowUserAssistant followUserAssistant)
                        {
                            updateBottomButton();
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                        {
                            return followUserAssistant.unFollowFromServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to unfollow user")));
        }
        else
        {
            onStopSubscriptions.add(assistant.ensureCacheValue()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<FollowUserAssistant>()
                    {
                        @Override public void call(FollowUserAssistant followUserAssistant)
                        {
                            followUserAssistant.followingInCache();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<FollowUserAssistant>()
                    {
                        @Override public void call(FollowUserAssistant followUserAssistant)
                        {
                            updateBottomButton();
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                        {
                            return followUserAssistant.followingInServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to follow user")));
        }
    }

    protected Observable<UserProfileDTO> freeFollow(@NonNull UserBaseKey heroId)
    {
        ProgressDialog progressDialog = ProgressDialogUtil.create(getActivity(), getString(R.string.following_this_hero));
        return userServiceWrapperLazy.get().freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog));
    }

    @OnClick(R.id.message_button)
    protected void handleMessageRequested()
    {
        if (isFollowing())
        {
            pushPrivateMessageFragment();
        }
        else
        {
            final FollowUserAssistant assistant = new FollowUserAssistant(getActivity(), shownUserBaseKey);
            onDestroyViewSubscriptions.add(assistant.showFollowForMessageConfirmation(shownProfile.displayName)
                    .map(new ReplaceWithFunc1<OnDialogClickEvent, FollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
                    .doOnNext(new Action1<FollowUserAssistant>()
                    {
                        @Override public void call(FollowUserAssistant followUserAssistant)
                        {
                            followUserAssistant.followingInCache();
                        }
                    })
                    .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                        {
                            return assistant.followingInServer();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<UserProfileDTO>()
                    {
                        @Override public void call(UserProfileDTO userProfileDTO)
                        {
                            pushPrivateMessageFragment();
                        }
                    }, new TimberOnErrorAction1("Failed to follow user for sending private message")));
        }
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
//            if (!(e instanceof RetrofitError) ||
//                    (((RetrofitError) e).getResponse() != null &&
//                            ((RetrofitError) e).getResponse().getStatus() != 404))
//            {
//                THToast.show(R.string.error_fetch_message_thread_header);
//                Timber.e(e, "Error while getting message thread");
//            }

            //Retrofit 2 way
            if (!(e instanceof HttpException) ||
                    (((HttpException) e).getMessage() != null &&
                            ((HttpException) e).code() != 404))
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
