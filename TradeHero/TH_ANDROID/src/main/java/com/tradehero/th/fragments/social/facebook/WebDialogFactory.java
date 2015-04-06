package com.tradehero.th.fragments.social.facebook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.social.facebook.WebDialogConstants;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.network.service.SocialServiceWrapper;
import dagger.Lazy;
import java.util.Arrays;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WebDialogFactory
{
    @NonNull private final Context context;
    @NonNull private final Lazy<FacebookAuthenticationProvider> facebookAuthenticationProvider;
    @NonNull private final Lazy<SocialServiceWrapper> socialServiceWrapperLazy;
    @NonNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public WebDialogFactory(
            @NonNull Context context,
            @NonNull Lazy<FacebookAuthenticationProvider> facebookAuthenticationProvider,
            @NonNull Lazy<SocialServiceWrapper> socialServiceWrapperLazy,
            @NonNull CurrentUserId currentUserId)
    {
        this.context = context;
        this.facebookAuthenticationProvider = facebookAuthenticationProvider;
        this.socialServiceWrapperLazy = socialServiceWrapperLazy;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    public void addTo(@NonNull Bundle bundle, @NonNull UserFriendsFacebookDTO userFriendsFacebookDTO)
    {
        addTo(bundle, Arrays.asList(userFriendsFacebookDTO));
    }

    public void addTo(@NonNull Bundle bundle, @NonNull Iterable<? extends UserFriendsFacebookDTO> userFriendsFacebookDTOs)
    {
        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (UserFriendsFacebookDTO userFriendsFacebookDTO : userFriendsFacebookDTOs)
        {
            sb.append(separator).append(userFriendsFacebookDTO.fbId);
            separator = ",";
        }
        bundle.putString(WebDialogConstants.REQUEST_BUNDLE_KEY_TO, sb.toString());
    }

    public void addInvitation(@NonNull Bundle bundle, @NonNull UserProfileDTO userProfileDTO)
    {
        bundle.putString(
                WebDialogConstants.REQUEST_BUNDLE_KEY_MESSAGE,
                context.getString(
                        R.string.invite_friend_facebook_tradehero_refer_friend_message,
                        userProfileDTO.referralCode));
    }

    public Observable<UserProfileDTO> authenticateWithPermission(@NonNull final Activity activity)
    {
        return facebookAuthenticationProvider.get()
                .logIn(activity)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(socialServiceWrapperLazy.get().connectFunc1(currentUserId.toUserBaseKey()));
    }
}
