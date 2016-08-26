package com.androidth.general.fragments.social.friend;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;

import com.androidth.general.GooglePlayMarketUtilBase;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.referral.MyProviderReferralDTO;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.network.service.ProviderServiceRx;
import com.androidth.general.persistence.competition.MyProviderReferralCacheRx;
import com.androidth.general.utils.route.THRouter;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.share.wechat.WeChatDTO;
import com.androidth.general.api.share.wechat.WeChatMessageType;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.social.UserFriendsDTOFactory;
import com.androidth.general.api.social.UserFriendsDTOList;
import com.androidth.general.api.social.UserFriendsFacebookDTO;
import com.androidth.general.api.social.UserFriendsLinkedinDTO;
import com.androidth.general.api.social.UserFriendsTwitterDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.UserProfileDTOUtil;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.models.share.SocialShareHelper;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.network.share.SocialSharer;
import com.androidth.general.network.share.dto.SocialShareResult;
import com.androidth.general.persistence.prefs.ShowAskForInviteDialog;
import com.androidth.general.persistence.timing.TimingIntervalPreference;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.EmptyAction1;
import dagger.Lazy;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable({
        "refer-friends",
        "refer-friend/:socialId/:socialUserId",
        "provider-refer-friend/:providerId",
})
public class FriendsInvitationFragment extends BaseFragment
        implements SocialFriendUserView.OnElementClickListener
{
    @Bind(R.id.redeem_referral_code_card_view) CardView redeemReferralCodeContainer;
    @Bind(R.id.redeem_referral_edit_text) EditText redeemReferralCodeEditText;
    @Bind(R.id.redeem_referral_code_button) Button redeemReferralCodeButton;
    @Bind(R.id.social_friend_type_list) ListView socialListView;
    @Bind(R.id.social_friends_list) ListView friendsListView;
    @Bind(R.id.social_search_friends_progressbar) ProgressBar searchProgressBar;
    @Bind(R.id.social_search_friends_none) TextView friendsListEmptyView;
    @Bind(R.id.redeem_referral_code_loading_view) LinearLayout loadingLayoutView;
    @Bind(R.id.redeem_referral_code_input_view) LinearLayout inputLayoutView;
    @Bind(R.id.redeem_referral_code_message_view) LinearLayout messageLayoutView;
    @Bind(R.id.message_view_icon) ImageView messageViewIconView;
    @Bind(R.id.message_view_text) TextView messageViewTextView;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject CurrentUserId currentUserId;
    SocialFriendHandler socialFriendHandler;
    SocialFriendHandlerFacebook socialFriendHandlerFacebook;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;
    @Inject Provider<SocialFriendHandlerFacebook> facebookSocialFriendHandlerProvider;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject SocialShareHelper socialShareHelper;
    @Inject ProviderServiceRx serviceRx;
    @Inject protected THRouter thRouter;
    @Inject MyProviderReferralCacheRx myProviderReferralCacheRx;
    @Inject LiveServiceWrapper liveServiceWrapper;

    @NonNull private UserFriendsDTOList userFriendsDTOs = new UserFriendsDTOList();
    private SocialFriendListItemDTOList socialFriendListItemDTOs;
    private Runnable searchTask;

    private static final String KEY_BUNDLE = "key_bundle";
    private static final String KEY_LIST_TYPE = "key_list_type";
    private static final int LIST_TYPE_SOCIAL_LIST = 1;
    private static final int LIST_TYPE_FRIEND_LIST = 2;

    public static final String ROUTER_SOCIAL_ID = "socialId";
    public static final String ROUTER_SOCIAL_USER_ID = "socialUserId";
    public static final String ROUTER_PROVIDER_ID = "providerId";

    @RouteProperty(ROUTER_SOCIAL_ID) String socialId;
    @RouteProperty(ROUTER_SOCIAL_USER_ID) String socialUserId;
    @RouteProperty(ROUTER_PROVIDER_ID) Integer providerId;

    private Bundle savedState;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String referralCode;

    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final String YOUR_APP_ID = "[YOUR_FACEBOOK_APP_ID]";
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        socialFriendHandler = socialFriendHandlerProvider.get();
        socialFriendHandlerFacebook = facebookSocialFriendHandlerProvider.get();
        mShowAskForInviteDialogPreference.pushInFuture(TimingIntervalPreference.YEAR);
        thRouter.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(getString(R.string.action_invite));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_invite_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        restoreSavedData(savedInstanceState);

        UserProfileDTO userProfileDTO = userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            referralCode = userProfileDTO.referralCode;
        }

        if (providerId != null) {
            fetchReferralStatus();
        } else {
            redeemReferralCodeContainer.setVisibility(View.GONE);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        if (socialId != null && socialUserId != null)
        {
            createInviteInHomePage();
        }
    }

    @Override public void onPause()
    {
        resetRoutingData();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putBundle(KEY_BUNDLE, savedState != null ? savedState : saveState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView()
    {
        savedState = saveState();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void restoreSavedData(Bundle savedInstanceState)
    {
        if (savedInstanceState != null && savedState == null)
        {
            savedState = savedInstanceState.getBundle(KEY_BUNDLE);
        }
        int listType = LIST_TYPE_SOCIAL_LIST;
        if (savedState != null)
        {
            listType = savedState.getInt(KEY_LIST_TYPE);
        }
        savedState = null;
        if (listType == LIST_TYPE_SOCIAL_LIST)
        {
            bindSocialTypeData();
        }
        else
        {
            bindSearchData();
        }
    }

    private Bundle saveState()
    {
        Bundle state = new Bundle();
        if (friendsListView != null)
        {
            state.putInt(KEY_LIST_TYPE, (friendsListView.getVisibility() == View.VISIBLE) ? LIST_TYPE_FRIEND_LIST : LIST_TYPE_SOCIAL_LIST);
        }
        return state;
    }

    private void resetRoutingData()
    {
        // TODO Routing library should have a way to clear injected data, proposing: THRouter.reset(this)
        Bundle args = getArguments();
        if (args != null)
        {
            args.remove(ROUTER_SOCIAL_ID);
            args.remove(ROUTER_SOCIAL_USER_ID);
        }
        socialId = null;
        socialUserId = null;
    }

    private void bindSocialTypeData()
    {
        List<SocialTypeItem> socialTypeItemList = SocialTypeItemFactory.getSocialTypeList();
        SocialTypeListAdapter adapter = new SocialTypeListAdapter(getActivity(), 0, socialTypeItemList);
        socialListView.setAdapter(adapter);
        showSocialTypeList();
    }

    private void bindSearchData()
    {
        socialFriendListItemDTOs = new SocialFriendListItemDTOList(userFriendsDTOs, null);
        if (friendsListView.getAdapter() == null)
        {
            SocialFriendsAdapter socialFriendsListAdapter =
                    new SocialFriendsAdapter(
                            getActivity(),
                            socialFriendListItemDTOs,
                            R.layout.social_friends_item,
                            R.layout.social_friends_item_header);
            socialFriendsListAdapter.setOnElementClickedListener(this);
            friendsListView.setAdapter(socialFriendsListAdapter);
            friendsListView.setEmptyView(friendsListEmptyView);
        }
        else
        {
            SocialFriendsAdapter socialFriendsListAdapter = (SocialFriendsAdapter) friendsListView.getAdapter();
            socialFriendsListAdapter.clear();
            socialFriendsListAdapter.addAll(socialFriendListItemDTOs);
        }
        showSearchList();
    }

    private void fetchReferralStatus()
    {
        onDestroyViewSubscriptions.add(
            myProviderReferralCacheRx.get(new ProviderId(providerId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<ProviderId, MyProviderReferralDTO>>()
                {
                    @Override public void call(Pair<ProviderId, MyProviderReferralDTO> providerIdMyProviderReferralDTOPair)
                    {
                        MyProviderReferralDTO myProviderReferralDTO = providerIdMyProviderReferralDTOPair.second;
                        referralCode = myProviderReferralDTO.myReferralCode;

                        if (myProviderReferralDTO.haveAlreadyRedeemed)
                        {
                            redeemReferralCodeContainer.setVisibility(View.GONE);
                        } else {
                            observeRedeemView();
                        }
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        Timber.e(throwable, throwable.getMessage());
                    }
                })
        );
    }

    private void observeRedeemView()
    {
        redeemReferralCodeButton.setEnabled(false);
        redeemReferralCodeButton.setAlpha((float)0.5);

        onDestroyViewSubscriptions.add(
            WidgetObservable.text(redeemReferralCodeEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OnTextChangeEvent>()
                {
                    @Override public void call(OnTextChangeEvent onTextChangeEvent)
                    {
                        redeemReferralCodeButton.setEnabled(onTextChangeEvent.text().length() > 0);
                        redeemReferralCodeButton.setAlpha(onTextChangeEvent.text().length() > 0 ? 1 :(float)0.5);
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        Timber.e(throwable, throwable.getMessage());
                    }
                })
        );
    }

    @OnClick(R.id.redeem_referral_code_button)
    public void onClickRedeemReferralCodeButton()
    {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(redeemReferralCodeEditText.getWindowToken(), 0);

        // configure UI to display loading
        redeemReferralCodeButton.setEnabled(false);
        redeemReferralCodeEditText.setEnabled(false);
        loadingLayoutView.bringToFront();

        onDestroyViewSubscriptions.add(
                liveServiceWrapper.redeemReferralCode(redeemReferralCodeEditText.getText().toString(), new ProviderId(providerId))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>()
                        {
                            @Override public void call(String s)
                            {
                                AsyncTask.execute(new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        myProviderReferralCacheRx.fetch(new ProviderId(providerId)).subscribe();
                                    }
                                });

                                messageViewIconView.setImageResource(R.drawable.green_tick);
                                messageViewTextView.setText(s);
                                messageLayoutView.bringToFront();

                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        redeemReferralCodeContainer.setVisibility(View.GONE);
                                    }
                                }, 3000);
                            }
                        }, new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                String errorMessage = throwable.getMessage();

                                if (throwable instanceof RetrofitError) {
                                    RetrofitError error = (RetrofitError) throwable;
                                    errorMessage = new String(((TypedByteArray)error.getResponse().getBody()).getBytes()).replace("\"", "");
                                }

                                // configure UI display error
                                messageViewIconView.setImageResource(R.drawable.red_alert);
                                messageViewTextView.setText(errorMessage);
                                messageLayoutView.bringToFront();

                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        // configure UI display input view
                                        if (redeemReferralCodeContainer != null) {
                                            redeemReferralCodeEditText.setEnabled(true);
                                            redeemReferralCodeButton.setEnabled(true);
                                            inputLayoutView.bringToFront();

                                            // auto focus edit text and bring keyboard up
                                            redeemReferralCodeEditText.requestFocus();
                                            imm.showSoftInput(redeemReferralCodeEditText, InputMethodManager.SHOW_IMPLICIT);
                                        }
                                    }
                                }, 3000);
                            }
                        })
        );

    }

    @OnItemClick(R.id.social_friend_type_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        final SocialTypeItem item = (SocialTypeItem) parent.getItemAtPosition(position);
        final int stringId = getContext().getApplicationInfo().labelRes;
        final UserProfileDTO userProfileDTO = userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        boolean linked = false;
        Intent intent;

        switch (item.socialNetwork){
            case WECHAT:
//                inviteWeChatFriends();
                intent = new Intent(Intent.ACTION_SEND);
                intent.setPackage("com.tencent.mm");
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_email_text, getString(R.string.app_name),
                        referralCode, GooglePlayMarketUtilBase.getAppMarketUrl()));
                try{
                    getActivity().startActivity(intent);
                }catch (Exception e){
                    if(e instanceof android.content.ActivityNotFoundException){
                        THToast.show("WeChat is not installed in this device");
                    }else{
                        THToast.show(e.getMessage());
                    }
                }
                break;
            case FB://must change after FB sdk update
                linked = checkLinkedStatus(item.socialNetwork);
                if (linked)
                {
//                    pushSocialInvitationFragment(item.socialNetwork);
                    pushFacebookShareContent(userProfileDTO);
                }
                else
                {
                    onDestroySubscriptions.add(
                            socialShareHelper.offerToConnect(item.socialNetwork)
                            .observeOn(Schedulers.io())
                            .subscribe(
                                    new Action1<UserProfileDTO>() {
                                        @Override
                                        public void call(UserProfileDTO userProfileDTO) {
//                                            pushSocialInvitationFragment(item.socialNetwork);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pushFacebookShareContent(userProfileDTO);
                                                }
                                            });
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                        Timber.d("Offer to connect error "+throwable.getMessage());
                                        }
                                    })
                    );
                }
                break;
            case FB_MSNGR://must change after FB sdk update
                linked = checkLinkedStatus(SocialNetworkEnum.FB);
                if (linked)
                {
//                    pushSocialInvitationFragment(item.socialNetwork);
                    pushFBMessenger(userProfileDTO);
                }
                else
                {
                    onStopSubscriptions.add(socialShareHelper.offerToConnect(item.socialNetwork)
                            .subscribe(
                                    new Action1<UserProfileDTO>()
                                    {
                                        @Override public void call(UserProfileDTO userProfileDTO)
                                        {
//                                            pushSocialInvitationFragment(item.socialNetwork);
                                            pushFBMessenger(userProfileDTO);
                                        }
                                    },
                                    new EmptyAction1<Throwable>()));
                }
                break;
            case EMAIL:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invite_email_subject, getString(R.string.app_name)));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_email_text, getString(R.string.app_name), referralCode, GooglePlayMarketUtilBase.getAppMarketUrl()));
                try{
                    getActivity().startActivity(intent);
                }catch (Exception e){
                    THToast.show(e.getMessage());
                }
                break;
            case SMS:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:"));
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_email_text, getString(R.string.app_name), referralCode, GooglePlayMarketUtilBase.getAppMarketUrl()));
                intent.setData(Uri.parse("sms:"));
                try{
                    getActivity().startActivity(intent);
                }catch (Exception e){
                    THToast.show(e.getMessage());
                }
                break;
            case WHATSAPP:
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_email_text, getString(R.string.app_name), referralCode, GooglePlayMarketUtilBase.getAppMarketUrl()));
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                try{
                    getActivity().startActivity(intent);
                }catch (Exception e){
                    if(e instanceof android.content.ActivityNotFoundException){
                        THToast.show("WhatsApp is not installed in this device");
                    }else{
                        THToast.show(e.getMessage());
                    }
                }
            default:
                break;
        }
    }

    private void inviteWeChatFriends()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            WeChatDTO weChatDTO = new WeChatDTO();
            weChatDTO.id = 0;
            weChatDTO.type = WeChatMessageType.Invite;
            weChatDTO.title = getString(WeChatMessageType.Invite.getTitleResId(), referralCode);
            socialSharerLazy.get().share(weChatDTO)
                    .subscribe(
                            new EmptyAction1<SocialShareResult>(),
                            new EmptyAction1<Throwable>()); // TODO proper callback?
        }
    }

    private void cancelPendingSearchTask()
    {
        View view = getView();
        if (view != null && searchTask != null)
        {
            view.removeCallbacks(searchTask);
        }
    }

    private void showSearchList()
    {
        socialListView.setVisibility(View.GONE);
        friendsListView.setVisibility(View.VISIBLE);
        searchProgressBar.setVisibility(View.GONE);
        //friendsListEmptyView.setVisibility(View.GONE);
    }

    private void pushSocialInvitationFragment(SocialNetworkEnum socialNetwork)
    {
        Class<? extends SocialFriendsFragment> target = SocialNetworkFactory.findProperTargetFragment(socialNetwork);
        Bundle bundle = new Bundle();
        if (navigator != null)
        {
            navigator.get().pushFragment(target, bundle);
        }
    }

    private boolean checkLinkedStatus(SocialNetworkEnum socialNetwork)
    {
        UserProfileDTO updatedUserProfileDTO =
                userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        return updatedUserProfileDTO != null &&
                UserProfileDTOUtil.checkLinkedStatus(updatedUserProfileDTO, socialNetwork);
    }

    @Override
    public void onFollowButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        handleFollowUsers(userFriendsDTO);
    }

    @Override
    public void onInviteButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        handleInviteUsers(userFriendsDTO);
    }

    public void onCheckBoxClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onCheckBoxClicked " + userFriendsDTO);
    }

    protected void handleFollowUsers(UserFriendsDTO userToFollow)
    {
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userToFollow);
        RequestObserver<UserProfileDTO> observer = new FollowFriendObserver(usersToFollow);
        observer.onRequestStart();
        socialFriendHandler.followFriends(usersToFollow)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void createInviteInHomePage()
    {
        handleInviteUsers(UserFriendsDTOFactory.createFrom(socialId, socialUserId));
    }

    // TODO via which social network to invite user?
    protected void handleInviteUsers(UserFriendsDTO userToInvite)
    {
        List<UserFriendsDTO> usersToInvite = Arrays.asList(userToInvite);
        RequestObserver<BaseResponseDTO> observer = new InviteFriendObserver(usersToInvite);
        if (userToInvite instanceof UserFriendsLinkedinDTO || userToInvite instanceof UserFriendsTwitterDTO)
        {
            observer.onRequestStart();
            socialFriendHandler.inviteFriends(
                    currentUserId.toUserBaseKey(),
                    usersToInvite)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        else if (userToInvite instanceof UserFriendsFacebookDTO)
        {
            //TODO do invite on the client side.
            observer.onRequestStart();
            socialFriendHandlerFacebook.inviteFriends(
                    currentUserId.toUserBaseKey(),
                    usersToInvite)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        else
        {
            //if all ids are empty or only wbId is not empty, how to do?
        }
    }

    private void handleInviteSuccess(List<UserFriendsDTO> usersToInvite)
    {
        //Invite Success will not disappear the friend in Invite
        THToast.show(R.string.invite_friend_request_sent);
    }

    private void handleFollowSuccess(List<UserFriendsDTO> usersToFollow)
    {
        if (usersToFollow != null)
        {
            for (UserFriendsDTO userFriendsDTO : usersToFollow)
            {
                userFriendsDTOs.remove(userFriendsDTO);
            }
        }
        SocialFriendsAdapter socialFriendsAdapter = (SocialFriendsAdapter) friendsListView.getAdapter();

        socialFriendListItemDTOs = new SocialFriendListItemDTOList(userFriendsDTOs, null);

        socialFriendsAdapter.clear();
        socialFriendsAdapter.addAll(socialFriendListItemDTOs);
    }

    class FollowFriendObserver extends RequestObserver<UserProfileDTO>
    {
        final List<UserFriendsDTO> usersToFollow;

        //<editor-fold desc="Constructors">
        private FollowFriendObserver(List<UserFriendsDTO> usersToFollow)
        {
            super(getActivity());
            this.usersToFollow = usersToFollow;
        }
        //</editor-fold>

        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            super.onNext(userProfileDTO);
            handleFollowSuccess(usersToFollow);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            THToast.show(R.string.follow_friend_request_error);
        }
    }

    class InviteFriendObserver extends RequestObserver<BaseResponseDTO>
    {
        final List<UserFriendsDTO> usersToInvite;

        //<editor-fold desc="Constructors">
        private InviteFriendObserver(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
        }
        //</editor-fold>

        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            super.onNext(baseResponseDTO);
            handleInviteSuccess(usersToInvite);
        }

        @Override
        public void success()
        {
            handleInviteSuccess(usersToInvite);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            // TODO
            THToast.show(R.string.invite_friend_request_error);
        }
    }

    @NonNull private UserFriendsDTOList filterTheDuplicated(@NonNull List<UserFriendsDTO> friendDTOList)
    {
        TreeSet<UserFriendsDTO> hashSet = new TreeSet<>();
        hashSet.addAll(friendDTOList);
        return new UserFriendsDTOList(hashSet);
    }

    class SearchFriendsObserver implements Observer<UserFriendsDTOList>
    {
        @Override public void onNext(UserFriendsDTOList args)
        {
            FriendsInvitationFragment.this.userFriendsDTOs = filterTheDuplicated(userFriendsDTOs);
            bindSearchData();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "SearchFriendsCallback error");
            // TODO need to tell user.
            showSocialTypeList();
        }
    }

    private void scheduleSearch(final String query)
    {
        View view = getView();
        if (view != null)
        {
            if (searchTask != null)
            {
                view.removeCallbacks(searchTask);
            }
            searchTask = new Runnable()
            {
                @Override public void run()
                {
                    if (getView() != null)
                    {
                        showSocialTypeListWithProgress();
                        searchSocialFriends(query);
                    }
                }
            };
            view.postDelayed(searchTask, 500L);
        }
    }

    private void showSocialTypeListWithProgress()
    {
        socialListView.setVisibility(View.VISIBLE);
        friendsListView.setVisibility(View.GONE);
        searchProgressBar.setVisibility(View.VISIBLE);
        friendsListEmptyView.setVisibility(View.GONE);
    }

    private void searchSocialFriends(String query)
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userServiceWrapper.searchSocialFriendsRx(
                        currentUserId.toUserBaseKey(), null, query))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SearchFriendsObserver()));
    }

    private void showSocialTypeList()
    {
        socialListView.setVisibility(View.VISIBLE);
        friendsListView.setVisibility(View.GONE);
        searchProgressBar.setVisibility(View.GONE);
        friendsListEmptyView.setVisibility(View.GONE);
    }

    private void pushFacebookShareContent(UserProfileDTO userProfileDTO){
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Join me!", getString(R.string.invite_email_text,
                getString(R.string.app_name), referralCode, GooglePlayMarketUtilBase.getAppMarketUrl()));
        clipboard.setPrimaryClip(clip);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(getString(R.string.invite_text_from_clipboard));
        dialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog(getActivity());

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(GooglePlayMarketUtilBase.getAppMarketUrl()))
                            .build();
                    shareDialog.show(content);
                }else{
                    Toast.makeText(getActivity(), "Error in sharing link to FB", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    private void pushFBMessenger(UserProfileDTO userProfileDTO){
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Join me!", getString(R.string.invite_email_text,
                getString(R.string.app_name), referralCode, GooglePlayMarketUtilBase.getAppMarketUrl()));
        clipboard.setPrimaryClip(clip);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage(getString(R.string.invite_text_from_clipboard));
        dialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                callbackManager = CallbackManager.Factory.create();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setPackage("com.facebook.orca");
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION);
                intent.putExtra(EXTRA_APP_ID, YOUR_APP_ID);

                try{
                    getActivity().startActivityForResult(intent, SHARE_TO_MESSENGER_REQUEST_CODE);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "FB Messenger is not installed in this device", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
