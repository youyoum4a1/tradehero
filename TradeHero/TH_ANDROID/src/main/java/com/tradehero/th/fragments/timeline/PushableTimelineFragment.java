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
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.OnMovableBottomTranslateListener;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.message.MessageThreadHeaderCacheRx;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ReplaceWithFunc1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
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
        mFollowButton.setVisibility(View.VISIBLE);
        SimpleFollowUserAssistant.updateFollowButton(mFollowButton, isFollowing);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.follow_button)
    protected void handleFollowRequested()
    {
        if (isFollowing())
        {
            SimpleFollowUserAssistant assistant = new SimpleFollowUserAssistant(getActivity(), shownUserBaseKey);
            onStopSubscriptions.add(assistant.showUnFollowConfirmation(shownProfile.displayName)
                    .flatMap(new Func1<OnDialogClickEvent, Observable<Pair<UserBaseKey, UserProfileDTO>>>()
                    {
                        @Override public Observable<Pair<UserBaseKey, UserProfileDTO>> call(OnDialogClickEvent onDialogClickEvent)
                        {
                            return userProfileCache.get().getOne(currentUserId.toUserBaseKey());
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .map(new ReplaceWithFunc1<Pair<UserBaseKey, UserProfileDTO>, SimpleFollowUserAssistant>(assistant))
                    .doOnNext(new Action1<SimpleFollowUserAssistant>()
                    {
                        @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            simpleFollowUserAssistant.unFollowFromCache();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<SimpleFollowUserAssistant>()
                    {
                        @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            updateBottomButton();
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            return simpleFollowUserAssistant.unFollowFromServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to unfollow user")));
        }
        else
        {
            onStopSubscriptions.add(userProfileCache.get().getOne(currentUserId.toUserBaseKey())
                    .subscribeOn(Schedulers.io())
                    .map(new ReplaceWithFunc1<Pair<UserBaseKey, UserProfileDTO>, SimpleFollowUserAssistant>(
                            new SimpleFollowUserAssistant(getActivity(), shownUserBaseKey)))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<SimpleFollowUserAssistant>()
                    {
                        @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            simpleFollowUserAssistant.followingInCache();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<SimpleFollowUserAssistant>()
                    {
                        @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            updateBottomButton();
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            return simpleFollowUserAssistant.followingInServer();
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
            final SimpleFollowUserAssistant assistant = new SimpleFollowUserAssistant(getActivity(), shownUserBaseKey);
            onDestroyViewSubscriptions.add(assistant.showFollowForMessageConfirmation(shownProfile.displayName)
                    .map(new ReplaceWithFunc1<OnDialogClickEvent, SimpleFollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
                    .doOnNext(new Action1<SimpleFollowUserAssistant>()
                    {
                        @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            simpleFollowUserAssistant.followingInCache();
                        }
                    })
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
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
