package com.tradehero.th.fragments.social.friend;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import com.tradehero.th.api.social.InviteDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@Singleton
public class SocialFriendHandler {

    @Inject Lazy<UserServiceWrapper> userService;

    @Inject
    public SocialFriendHandler() {
        DaggerUtils.inject(this);
    }

    public static class RequestCallback<T> implements Callback<T>{

        private ProgressDialog dialog;
        private Context context;

        public RequestCallback(Context context)
        {
            this.context = context;
        }

        private void showDialog(Context context) {
            if (dialog == null) {
                dialog = new ProgressDialog(context);
            }
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        }

        private void dismissDialog() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }


        public void onRequestStart()
        {
            showDialog(context);
        }

        @Override
        public void success(T data, Response response) {
            dismissDialog();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            dismissDialog();
        }
    }

    public MiddleCallback<UserProfileDTO> followFriends(List<UserFriendsDTO> users,RequestCallback<UserProfileDTO> callback) {

        if (callback != null)
        {
            callback.onRequestStart();
        }
        MiddleCallback<UserProfileDTO> middleCallback = null;
        if(users.size() > 1)
        {
            FollowFriendsForm followFriendsForm = new FollowFriendsForm();
            followFriendsForm.userIds = new ArrayList<Integer>();
            for (int i=0,j=users.size();i<j;i++)
            {
                followFriendsForm.userIds.add(users.get(i).thUserId);
            }
            middleCallback = userService.get().followBatchFree(followFriendsForm, callback);
        }
        else if (users.size() == 1)
        {
            UserFriendsDTO userFriendsDTO = users.get(0);
            middleCallback = userService.get().freeFollow(new UserBaseKey(userFriendsDTO.thUserId), callback);
        }
        return middleCallback;
    }

    // TODO
    public MiddleCallback<Response> inviteFriends(UserBaseKey userKey, List<UserFriendsDTO> users,RequestCallback<Response> callback) {

        InviteFormDTO inviteFormDTO = new InviteFormDTO();
        List<InviteDTO> usersToFollow = new ArrayList<>(users.size());
        for (int i=0;i<users.size();i++)
        {
            InviteDTO inviteDTO = new InviteDTO();
            UserFriendsDTO userFriendsDTO = users.get(i);
            inviteDTO.email = userFriendsDTO.getEmail();
            inviteDTO.fbId = userFriendsDTO.fbId;
            inviteDTO.liId = userFriendsDTO.liId;
            inviteDTO.twId = userFriendsDTO.twId;

            usersToFollow.add(inviteDTO);
        }
        inviteFormDTO.users = usersToFollow;
        if (callback != null)
        {
            callback.onRequestStart();
        }
        MiddleCallback<Response> middleCallback = userService.get().inviteFriends(userKey,inviteFormDTO, callback);
       return middleCallback;
    }
}
