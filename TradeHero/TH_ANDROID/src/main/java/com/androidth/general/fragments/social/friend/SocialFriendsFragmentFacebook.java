package com.androidth.general.fragments.social.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.facebook.FacebookConstants;
import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTOList;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

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

    //Jeff- should not use this to invite from Facebook
//    @NonNull @Override protected Observable<UserFriendsDTOList> getFetchAllFriendsObservable()
//    {
//        return facebookSocialFriendHandlerProvider.get().getFetchFacebookInvitableObservable(getFriendsFields())
//                .flatMap(new Func1<UserFriendsDTOList, Observable<UserFriendsDTOList>>()
//                {
//                    @Override public Observable<UserFriendsDTOList> call(final UserFriendsDTOList fbInvitable)
//                    {
//                        // We are doing it sequentially because the access token may have changed and we give a chance to the server
//                        // to fetch again on its side
//                        return SocialFriendsFragmentFacebook.super.getFetchAllFriendsObservable()
//                                .map(new Func1<UserFriendsDTOList, UserFriendsDTOList>()
//                                {
//                                    @Override public UserFriendsDTOList call(
//                                            UserFriendsDTOList thUsers)
//                                    {
//                                        UserFriendsDTOList all = new UserFriendsDTOList();
//                                        all.addAll(thUsers.getTradeHeroUsers());
//                                        all.addAll(fbInvitable);
//                                        return all;
//                                    }
//                                });
//                    }
//                });
//    }

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
