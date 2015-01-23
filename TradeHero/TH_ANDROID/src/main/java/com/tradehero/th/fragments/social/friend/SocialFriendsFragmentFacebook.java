package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.HttpMethod;
import com.facebook.Response;
import com.tradehero.common.social.facebook.FacebookRequestException;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.common.social.facebook.FacebookRequestOperator;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class SocialFriendsFragmentFacebook extends SocialFriendsFragment
{
    private static final String API_INVITABLE_FRIENDS = "/me/invitable_friends";

    @Inject Provider<SocialFriendHandlerFacebook> facebookSocialFriendHandlerProvider;

    @Nullable Subscription invitableFriendsSubscription;

    @Override public void onDestroyView()
    {
        unsubscribe(invitableFriendsSubscription);
        invitableFriendsSubscription = null;
        super.onDestroyView();
    }

    @Override
    protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.FB;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.invite_social_friend, getString(R.string.facebook));
    }

    @Override @NonNull
    protected SocialFriendHandlerFacebook createFriendHandler()
    {
        return facebookSocialFriendHandlerProvider.get();
    }

    @Override protected void linkWith(@NonNull UserFriendsDTOList value)
    {
        super.linkWith(value);
        if (this.invitableFriends.isEmpty())
        {
            fetchInvitableFriends();
        }
    }

    protected void fetchInvitableFriends()
    {
        unsubscribe(invitableFriendsSubscription);
        invitableFriendsSubscription = AndroidObservable.bindFragment(
                this,
                facebookSocialFriendHandlerProvider.get().createProfileSessionObservable()
                        .take(1)
                        .flatMap(pair -> Observable.create(
                                FacebookRequestOperator
                                        .builder(pair.second, API_INVITABLE_FRIENDS)
                                        .setHttpMethod(HttpMethod.GET)
                                        .build())))
                .subscribe(
                        this::onInvitableFriendsReceived,
                        this::onInvitableFriendsFailed);
    }

    protected void onInvitableFriendsReceived(Response response)
    {
        Timber.d("InvitableFriends response %s", response);
        if (BuildConfig.DEBUG)
        {
            THToast.show("Parse invitable friends");
        }
    }

    protected void onInvitableFriendsFailed(Throwable e)
    {
        Timber.e(e, "InvitableFriends error");
        if (e instanceof FacebookRequestException)
        {
            Timber.e(new Exception(), ((FacebookRequestException) e).facebookCause.getErrorMessage());
        }
    }
}
