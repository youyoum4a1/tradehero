package com.tradehero.th.fragments.social.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.HttpMethod;
import com.tradehero.common.social.facebook.FacebookConstants;
import com.tradehero.common.social.facebook.FacebookRequestOperator;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.models.social.facebook.UserFriendsFacebookUtil;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;

public class SocialFriendsFragmentFacebook extends SocialFriendsFragment
{
    @Inject Provider<SocialFriendHandlerFacebook> facebookSocialFriendHandlerProvider;

    @Nullable Subscription invitableFriendsSubscription;

    @Override public void onDestroyView()
    {
        unsubscribe(invitableFriendsSubscription);
        invitableFriendsSubscription = null;
        super.onDestroyView();
    }

    @Override protected SocialNetworkEnum getSocialNetwork()
    {
        return SocialNetworkEnum.FB;
    }

    @Override protected String getTitle()
    {
        return getString(R.string.invite_social_friend, getString(R.string.facebook));
    }

    @Override @NonNull
    protected SocialFriendHandlerFacebook createFriendHandler()
    {
        return facebookSocialFriendHandlerProvider.get();
    }

    @NonNull @Override protected Observable<UserFriendsDTOList> getFetchAllFriendsObservable()
    {
        return Observable.zip(
                super.getFetchAllFriendsObservable(),
                getFetchFacebookInvitableObservable(),
                new Func2<UserFriendsDTOList, UserFriendsDTOList, UserFriendsDTOList>()
                {
                    @Override public UserFriendsDTOList call(
                            UserFriendsDTOList thUsers, UserFriendsDTOList fbInvitable)
                    {
                        UserFriendsDTOList all = new UserFriendsDTOList();
                        all.addAll(thUsers.getTradeHeroUsers());
                        all.addAll(fbInvitable);
                        return all;
                    }
                });
    }

    @NonNull protected Observable<UserFriendsDTOList> getFetchFacebookInvitableObservable()
    {

        return facebookSocialFriendHandlerProvider.get().createProfileSessionObservable()
                .take(1)
                .flatMap(pair -> Observable.create(
                        FacebookRequestOperator
                                .builder(pair.second, FacebookConstants.API_INVITABLE_FRIENDS)
                                .setParameters(getFriendsFields())
                                .setHttpMethod(HttpMethod.GET)
                                .build()))
                .map(UserFriendsFacebookUtil::convert);
    }

    @NonNull protected Bundle getFriendsFields()
    {
        Bundle params = new Bundle();
        params.putString(FacebookConstants.QUERY_KEY_FIELDS,
                String.format(
                        FacebookConstants.FIELDS_VALUE_USER_NAME + FacebookConstants.QUERY_KEY_FIELDS_SEPARATOR +
                                FacebookConstants.FIELDS_VALUE_USER_PICTURE + FacebookConstants.FIELDS_VALUE_USER_PICTURE_WIDTH,
                        (int) getResources().getDimension(R.dimen.medium_image_w_h)));
        return params;
    }
}
