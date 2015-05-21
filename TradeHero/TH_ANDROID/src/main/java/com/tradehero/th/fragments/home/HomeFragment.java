package com.tradehero.th.fragments.home;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.webkit.WebView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.RequestObserver;
import com.tradehero.th.fragments.social.friend.SocialFriendHandler;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.android.schedulers.AndroidSchedulers;

@Routable("home")
public final class HomeFragment extends BaseWebViewFragment
{
    @InjectView(R.id.main_content_wrapper) BetterViewAnimator mainContentWrapper;

    @Inject Lazy<UserProfileCacheRx> userProfileCacheLazy;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;
    @Inject THRouter thRouter;

    protected SocialFriendHandler socialFriendHandler;

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

    @Override public void onResume()
    {
        super.onResume();
        thRouter.inject(this);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    //<editor-fold desc="Windy's stuff, to be refactored">
    public void follow(UserFriendsDTO userFriendsDTO)
    {
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userFriendsDTO);
        handleFollowUsers(usersToFollow);
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
