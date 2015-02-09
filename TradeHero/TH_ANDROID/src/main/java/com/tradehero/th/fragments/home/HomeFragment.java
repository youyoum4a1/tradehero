package com.tradehero.th.fragments.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.facebook.FacebookOperationCanceledException;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsContactEntryDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTOFactory;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.social.UserFriendsLinkedinDTO;
import com.tradehero.th.api.social.UserFriendsTwitterDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.RequestObserver;
import com.tradehero.th.fragments.social.friend.SocialFriendHandler;
import com.tradehero.th.fragments.social.friend.SocialFriendHandlerFacebook;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@Routable({
        "refer-friend/:socialId/:socialUserId",
        "user/:userId/follow/free"
})
public final class HomeFragment extends BaseWebViewFragment
{
    @InjectView(android.R.id.progress) View progressBar;
    @InjectView(R.id.main_content_wrapper) BetterViewAnimator mainContentWrapper;

    @Inject Provider<Activity> activityProvider;
    @Inject Lazy<UserProfileCacheRx> userProfileCacheLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;
    @Inject Provider<SocialFriendHandlerFacebook> socialFriendHandlerFacebookProvider;
    @Inject CurrentUserId currentUserId;
    @Inject HomeContentCacheRx homeContentCache;
    @Inject THRouter thRouter;

    @RouteProperty(ROUTER_SOCIALID) String socialId;
    @RouteProperty(ROUTER_SOCIALUSERID) String socialUserId;
    @RouteProperty(ROUTER_USERID) Integer userId;

    public static final String ROUTER_SOCIALID = "socialId";
    public static final String ROUTER_SOCIALUSERID = "socialUserId";
    public static final String ROUTER_USERID = "userId";

    protected SocialFriendHandler socialFriendHandler;
    private UserFriendsDTO userFriendsDTO;
    @Nullable private Subscription inviteSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @LayoutRes @Override protected int getLayoutResId()
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

    @Override protected void onProgressChanged(WebView view, int newProgress)
    {
        super.onProgressChanged(view, newProgress);

        if (mainContentWrapper != null && newProgress > 50)
        {
            mainContentWrapper.setDisplayedChildByLayoutId(R.id.webview);
        }
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        webView.reload();
    }

    @Override public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_home);
        inflater.inflate(R.menu.menu_refresh_button, menu);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_fresh:
                webView.reload();
                userProfileCacheLazy.get().get(currentUserId.toUserBaseKey());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        homeContentCache.get(currentUserId.toUserBaseKey());
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();
        thRouter.inject(this);

        if (socialId != null && socialUserId != null)
        {
            createInviteInHomePage();
        }
        else if (userId != null)
        {
            createFollowInHomePage();
        }
    }

    @Override public void onPause()
    {
        super.onPause();
        resetRoutingData();
    }

    private void resetRoutingData()
    {
        // TODO Routing library should have a way to clear injected data, proposing: THRouter.reset(this)
        Bundle args = getArguments();
        if (args != null)
        {
            args.remove(ROUTER_SOCIALID);
            args.remove(ROUTER_SOCIALUSERID);
            args.remove(ROUTER_USERID);
        }
        socialId = null;
        socialUserId = null;
        userId = null;
    }

    //<editor-fold desc="Windy's stuff, to be refactored">
    private void createInviteInHomePage()
    {
        userFriendsDTO = UserFriendsDTOFactory.createFrom(socialId, socialUserId);
        invite();
    }

    public void createFollowInHomePage()
    {
        UserFriendsDTO user = new UserFriendsContactEntryDTO();
        user.thUserId = userId;
        follow(user);
    }

    public void follow(UserFriendsDTO userFriendsDTO)
    {
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userFriendsDTO);
        handleFollowUsers(usersToFollow);
    }

    private void invite()
    {
        if (userFriendsDTO instanceof UserFriendsLinkedinDTO || userFriendsDTO instanceof UserFriendsTwitterDTO)
        {
            InviteFormUserDTO inviteFriendForm = new InviteFormUserDTO();
            inviteFriendForm.add(userFriendsDTO);
            ProgressDialog progressDialog = getProgressDialog();
            unsubscribe(inviteSubscription);
            inviteSubscription = AppObservable.bindFragment(
                    this,
                    userServiceWrapperLazy.get()
                            .inviteFriendsRx(currentUserId.toUserBaseKey(), inviteFriendForm))
                    .finallyDo(progressDialog::dismiss)
                    .subscribe(
                            this::onFriendInvited,
                            new ToastOnErrorAction());
        }
        else if (userFriendsDTO instanceof UserFriendsFacebookDTO)
        {
            socialFriendHandlerFacebookProvider.get()
                    .createShareRequestObservable(Arrays.asList((UserFriendsFacebookDTO) userFriendsDTO), null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bundle>()
                    {
                        @Override public void onCompleted()
                        {
                            Timber.d("completed");
                        }

                        @Override public void onError(Throwable e)
                        {
                            if (e instanceof FacebookOperationCanceledException)
                            {
                                THToast.show(R.string.invite_friend_request_cancelled);
                            }
                            Timber.e(e, "error");
                        }

                        @Override public void onNext(Bundle bundle)
                        {
                            final String requestId = bundle.getString("request");
                            if (requestId != null)
                            {
                                THToast.show(R.string.invite_friend_request_sent);
                                invite(userFriendsDTO);
                            }
                            else
                            {
                                THToast.show(R.string.invite_friend_request_cancelled);
                            }

                            Timber.d("next %s", bundle);
                        }
                    });
        }
    }

    private void invite(UserFriendsDTO userDto)
    {
        InviteFormUserDTO inviteFriendForm = new InviteFormUserDTO();
        inviteFriendForm.add(userDto);
        ProgressDialog progressDialog = getProgressDialog();
        unsubscribe(inviteSubscription);
        inviteSubscription = AppObservable.bindFragment(
                this,
                userServiceWrapperLazy.get()
                        .inviteFriendsRx(currentUserId.toUserBaseKey(), inviteFriendForm))
                .finallyDo(progressDialog::dismiss)
                .subscribe(
                        this::onFriendInvited,
                        new ToastOnErrorAction());
    }

    public void onFriendInvited(BaseResponseDTO args)
    {
        THToast.show(R.string.invite_friend_success);
    }

    private ProgressDialog getProgressDialog()
    {
        return ProgressDialog.show(
                activityProvider.get(),
                activityProvider.get().getString(R.string.loading_loading),
                activityProvider.get().getString(R.string.alert_dialog_please_wait),
                true);
    }

    protected void handleFollowUsers(List<UserFriendsDTO> usersToFollow)
    {
        createFriendHandler();
        RequestObserver<UserProfileDTO> observer = new FollowFriendObserver();
        observer.onRequestStart();
        socialFriendHandler.followFriends(usersToFollow)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    protected void createFriendHandler()
    {
        if (socialFriendHandler == null)
        {
            socialFriendHandler = socialFriendHandlerProvider.get();
        }
    }

    class FollowFriendObserver extends RequestObserver<UserProfileDTO>
    {
        private FollowFriendObserver()
        {
            super(getActivity());
        }

        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            super.onNext(userProfileDTO);
            // TODO
            handleFollowSuccess();
            userProfileCacheLazy.get().onNext(userProfileDTO.getBaseKey(), userProfileDTO);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            handleFollowError();
        }
    }

    private void handleFollowSuccess()
    {
        THToast.show("Follow success");
    }

    protected void handleFollowError()
    {
        // TODO
        THToast.show(R.string.follow_friend_request_error);
    }
    //</editor-fold>
}
