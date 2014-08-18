package com.tradehero.th.fragments.social.friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@Singleton public class SocialFriendHandler
{
    @NotNull Lazy<UserServiceWrapper> userService;

    //<editor-fold desc="Constructors">
    @Inject public SocialFriendHandler(@NotNull Lazy<UserServiceWrapper> userService)
    {
        this.userService = userService;
    }
    //</editor-fold>

    public static class RequestCallback<T> implements Callback<T>
    {
        @Nullable private ProgressDialog dialog;
        @NotNull private Context context;

        public RequestCallback(@NotNull Context context)
        {
            this.context = context;
        }

        private void showDialog(@NotNull Context context)
        {
            if (dialog == null)
            {
                dialog = new ProgressDialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            dialog.show();
        }

        private void dismissDialog()
        {
            if (dialog != null && dialog.isShowing())
            {
                dialog.dismiss();
            }
        }

        public void onRequestStart()
        {
            showDialog(context);
        }

        @Override
        public void success(T data, Response response)
        {
            dismissDialog();
        }

        public void success()
        {
            dismissDialog();
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            dismissDialog();
        }
    }

    public MiddleCallback<UserProfileDTO> followFriends(@NotNull List<UserFriendsDTO> users, @Nullable RequestCallback<UserProfileDTO> callback)
    {
        if (callback != null)
        {
            callback.onRequestStart();
        }
        return userService.get().followBatchFree(new BatchFollowFormDTO(users), callback);
    }

    // TODO
    public MiddleCallback<Response> inviteFriends(
            @NotNull UserBaseKey userKey,
            @NotNull List<UserFriendsDTO> users,
            @Nullable RequestCallback<Response> callback)
    {

        InviteFormUserDTO inviteFormDTO = new InviteFormUserDTO();
        inviteFormDTO.addAll(users);
        return inviteFriends(userKey, inviteFormDTO, callback);
    }

    public MiddleCallback<Response> inviteFriends(
            @NotNull UserBaseKey userKey,
            @NotNull InviteFormDTO inviteFormDTO,
            @Nullable RequestCallback<Response> callback)
    {
        if (callback != null)
        {
            callback.onRequestStart();
        }
        return userService.get().inviteFriends(userKey, inviteFormDTO, callback);
    }
}
