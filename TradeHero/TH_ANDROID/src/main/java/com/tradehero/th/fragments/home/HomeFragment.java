package com.tradehero.th.fragments.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.social.UserFriendsLinkedinDTO;
import com.tradehero.th.api.social.UserFriendsTwitterDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.VersionUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@Routable("refer-friend/:SocialID/:UserID")
public class HomeFragment extends BaseWebViewFragment
{
    @InjectView(android.R.id.progress) View progressBar;
    @InjectView(R.id.main_content_wrapper) BetterViewAnimator mainContentWrapper;

    @Inject MainCredentialsPreference mainCredentialsPreference;
    @Inject @LanguageCode String languageCode;
    @Inject CurrentUserId currentUserId;
    @Inject HomeContentCache homeContentCache;

    @Inject AlertDialogUtil alertDialogUtil;
    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<CurrentActivityHolder> currentActivityHolderLazy;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;
    @Inject Lazy<SocialServiceWrapper> socialServiceWrapperLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject SocialServiceWrapper socialServiceWrapper;

    private ProgressDialog progressDialog;
    private UserFriendsDTO userFriendsDTO;
    private MiddleCallback<UserProfileDTO> middleCallbackConnect;
    private MiddleCallback<Response> middleCallbackInvite;

    private DTOCacheNew.Listener<UserBaseKey, HomeContentDTO> homeContentCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        homeContentCacheListener = createHomeContentCacheListener();
    }

    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_home_webview;
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        ButterKnife.inject(this, view);

        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
    }

    @Override public void onStart()
    {
        super.onStart();
        homeContentCache.register(currentUserId.toUserBaseKey(), homeContentCacheListener);
        homeContentCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        reloadWebView();
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(R.string.dashboard_home);
        inflater.inflate(R.menu.menu_refresh_button, menu);
    }

    @Override public boolean onOptionsItemSelected(@NotNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_fresh:
                homeContentCache.invalidate(currentUserId.toUserBaseKey());
                reloadWebView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop()
    {
        detachHomeCacheListener();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        homeContentCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        homeContentCacheListener = null;
        super.onDestroy();
    }

    private void detachHomeCacheListener()
    {
        homeContentCache.unregister(currentUserId.toUserBaseKey(), homeContentCacheListener);
    }

    private void reloadWebView()
    {
        reloadWebView(homeContentCache.get(currentUserId.toUserBaseKey()));
    }

    private void reloadWebView(@Nullable HomeContentDTO homeContentDTO)
    {
        String appHomeLink = String.format("%s/%d", Constants.APP_HOME, currentUserId.get());

        if (homeContentDTO != null)
        {
            Timber.d("Getting home app data from cache!");
            webView.loadDataWithBaseURL(Constants.BASE_STATIC_CONTENT_URL, homeContentDTO.content, "text/html", "", appHomeLink);
        }
        else
        {
            Map<String, String> additionalHeaders = new HashMap<>();
            additionalHeaders.put(Constants.AUTHORIZATION, createTypedAuthParameters(mainCredentialsPreference.getCredentials()));
            additionalHeaders.put(Constants.TH_CLIENT_VERSION, VersionUtils.getVersionId(getActivity()));
            additionalHeaders.put(Constants.TH_LANGUAGE_CODE, languageCode);

            loadUrl(appHomeLink, additionalHeaders);
        }
    }

    public String createTypedAuthParameters(@NotNull CredentialsDTO credentialsDTO)
    {
        return String.format("%1$s %2$s", credentialsDTO.getAuthType(), credentialsDTO.getAuthHeaderParameter());
    }

    @Override protected void onProgressChanged(WebView view, int newProgress)
    {
        super.onProgressChanged(view, newProgress);
        Activity activity = getActivity();
        if (activity != null)
        {
            activity.setProgress(newProgress * 100);
        }

        if (mainContentWrapper != null && newProgress > 50)
        {
            mainContentWrapper.setDisplayedChildByLayoutId(R.id.webview);
        }
    }

    //<editor-fold desc="Listeners">
    @NotNull private DTOCacheNew.Listener<UserBaseKey, HomeContentDTO> createHomeContentCacheListener()
    {
        return new HomeContentCacheListener();
    }

    private class HomeContentCacheListener implements DTOCacheNew.HurriedListener<UserBaseKey, HomeContentDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull UserBaseKey key, @NotNull HomeContentDTO value)
        {
            reloadWebView(value);
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull HomeContentDTO value)
        {
            reloadWebView(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            // do nothing
        }
    }
    //</editor-fold>

    public void createInviteInHomePage(String social, String userid)
    {
        if (social == null) return;
        if (social.equals("fb"))
        {
            userFriendsDTO = new UserFriendsFacebookDTO();
            ((UserFriendsFacebookDTO) userFriendsDTO).fbId = userid;
        }
        else if (social.equals("li"))
        {
            userFriendsDTO = new UserFriendsLinkedinDTO();
            ((UserFriendsLinkedinDTO) userFriendsDTO).liId = userid;
        }
        else if (social.equals("tw"))
        {
            userFriendsDTO = new UserFriendsTwitterDTO();
            ((UserFriendsTwitterDTO) userFriendsDTO).twId = userid;
        }
        invite();
    }

    private void invite()
    {
        Timber.d("windy: invite()");
        if (userFriendsDTO instanceof UserFriendsLinkedinDTO || userFriendsDTO instanceof UserFriendsTwitterDTO)
        {
            InviteFormDTO inviteFriendForm = new InviteFormDTO();
            inviteFriendForm.users = new ArrayList<>();
            inviteFriendForm.users.add(userFriendsDTO.createInvite());
            getProgressDialog().show();
            detachMiddleCallbackInvite();
            middleCallbackInvite = userServiceWrapperLazy.get()
                    .inviteFriends(currentUserId.toUserBaseKey(), inviteFriendForm,
                            new TrackShareCallback());
        }
        else if (userFriendsDTO instanceof UserFriendsFacebookDTO)
        {
            if (Session.getActiveSession() == null)
            {
                Timber.d("windy: Session.getActiveSession() = " + Session.getActiveSession());
                Timber.d("windy: facebookUtils.get.login()...");
                facebookUtils.get().logIn(currentActivityHolderLazy.get().getCurrentActivity(),
                        new TrackFacebookCallback());
            }
            else
            {
                sendRequestDialogFacebook();
            }
        }
    }

    private void sendRequestDialogFacebook()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(((UserFriendsFacebookDTO) userFriendsDTO).fbId);

        Bundle params = new Bundle();
        String messageToFacebookFriends = getActivity().getString(
                R.string.invite_friend_facebook_tradehero_refer_friend_message);
        if (messageToFacebookFriends.length() > 60)
        {
            messageToFacebookFriends = messageToFacebookFriends.substring(0, 60);
        }

        params.putString("message", messageToFacebookFriends);
        params.putString("to", stringBuilder.toString());

        WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
                currentActivityHolderLazy.get().getCurrentActivity(), Session.getActiveSession(),
                params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener()
                {
                    @Override
                    public void onComplete(Bundle values, FacebookException error)
                    {
                        if (error != null)
                        {
                            if (error instanceof FacebookOperationCanceledException)
                            {
                                THToast.show(R.string.invite_friend_request_canceled);
                            }
                        }
                        else
                        {
                            final String requestId = values.getString("request");
                            if (requestId != null)
                            {
                                THToast.show(R.string.invite_friend_request_sent);
                            }
                            else
                            {
                                THToast.show(R.string.invite_friend_request_canceled);
                            }
                        }
                    }
                })
                .build();
        requestsDialog.show();
    }

    private void detachMiddleCallbackInvite()
    {
        if (middleCallbackInvite != null)
        {
            middleCallbackInvite.setPrimaryCallback(null);
        }
        middleCallbackInvite = null;
    }

    private class TrackShareCallback implements retrofit.Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            THToast.show(R.string.invite_friend_success);
            getProgressDialog().hide();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            getProgressDialog().hide();
        }
    }

    private class TrackFacebookCallback extends LogInCallback
    {
        @Override public void done(UserLoginDTO user, THException ex)
        {
            getProgressDialog().dismiss();
        }

        @Override public void onStart()
        {
            getProgressDialog().show();
        }

        @Override public boolean onSocialAuthDone(JSONCredentials json)
        {
            detachMiddleCallbackConnect();
            middleCallbackConnect = socialServiceWrapperLazy.get().connect(
                    currentUserId.toUserBaseKey(), UserFormFactory.create(json),
                    new SocialLinkingCallback());
            progressDialog.setMessage(getActivity().getString(
                    R.string.authentication_connecting_tradehero,
                    "Facebook"));
            return true;
        }
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = progressDialogUtilLazy.get().show(
                currentActivityHolderLazy.get().getCurrentContext(),
                R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }

    protected void detachMiddleCallbackConnect()
    {
        if (middleCallbackConnect != null)
        {
            middleCallbackConnect.setPrimaryCallback(null);
        }
        middleCallbackConnect = null;
    }

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            userProfileCacheLazy.get().put(currentUserId.toUserBaseKey(), userProfileDTO);
            invite();
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            getProgressDialog().dismiss();
        }
    }
}
