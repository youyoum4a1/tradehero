package com.tradehero.th.fragments.social.friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.translation.TranslationCache;
import com.tradehero.th.persistence.translation.TranslationKey;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by tradehero on 14-5-27.
 */
@Singleton
public class SocialFriendHandler {

    private ProgressDialog dialog;

    Lazy<UserServiceWrapper> userService;

    @Inject
    public SocialFriendHandler(){

    }

    // TODO
    public void inviteFriends(Context context, List<UserFriendsDTO> users)
    {

    }


    public MiddleCallback<Response> followFriends(Context context, List<UserFriendsDTO> users)
    {
        showDialog(context);

        Callback<Response> callback = new Callback<Response>() {
            @Override
            public void success(Response userProfileDTO, Response response) {
                dismissDialog();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                dismissDialog();
            }
        };

        FollowFriendsForm followFriendsForm = new FollowFriendsForm();
        followFriendsForm.userFriendsDTOs = users;
        MiddleCallback<Response> middleCallback = userService.get().followBatchFree(followFriendsForm, callback);
        return middleCallback;
    }

    private void showDialog(Context context)
    {
        if (dialog == null)
        {
            dialog = new ProgressDialog(context);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setMessage(context.getString(R.string.translating));
        dialog.show();
    }

    private void dismissDialog()
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }
}
