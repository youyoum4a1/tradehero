package com.tradehero.th.fragments.chinabuild.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.data.sp.THSharePreferenceManager;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.social.WeiboSocialLinkHelper;
import com.tradehero.th.fragments.social.friend.SocialFriendHandlerWeibo;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.WeiboUtils;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Provider;


public class ShareDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @InjectView(R.id.title) TextView mTitleText;
    @InjectView(R.id.btn_cancel) TextView mCancelBtn;
    @InjectView(R.id.btn_ok) TextView mOKBtn;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Provider<SocialFriendHandlerWeibo> weiboSocialFriendHandlerProvider;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;

    private static String mTitle;
    private static String mShareContent;
    private static String mType;
    private static int mUserId = -1;

    private static boolean isDialogShowing = false;

    public static ShareDialogFragment showDialog(FragmentManager fragmentManager, String title) {
        if(isDialogShowing){
            return null;
        }
        mTitle = title;
        mShareContent = "";
        mType = "";
        mUserId = -1;
        ShareDialogFragment dialogFragment = new ShareDialogFragment();
        try {
            dialogFragment.show(fragmentManager, ShareDialogFragment.class.getName());
            isDialogShowing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialogFragment;
    }

    /**
     * Share to WeChat moment and share to WeiBo on the background
     *
     * @param fragmentManager
     * @param title
     * @param shareContent
     * @return
     */
    public static ShareDialogFragment showDialog(FragmentManager fragmentManager, String title, String shareContent) {
        if(isDialogShowing){
            return null;
        }
        ShareDialogFragment dialogFragment = new ShareDialogFragment();
        mTitle = title;
        mShareContent = shareContent;
        mType = "";
        mUserId = -1;
        try {
            dialogFragment.show(fragmentManager, ShareDialogFragment.class.getName());
            isDialogShowing = true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return dialogFragment;
    }

    /**
     * Share to WeChat moment and share to WeiBo on the background
     *
     * @param fragmentManager
     * @param title
     * @param shareContent
     * @return
     */
    public static ShareDialogFragment showDialog(FragmentManager fragmentManager, String title, String shareContent, String type, int userId) {
        if(isDialogShowing){
            return null;
        }
        ShareDialogFragment dialogFragment = new ShareDialogFragment();
        mTitle = title;
        mShareContent = shareContent;
        mType = type;
        mUserId = userId;
        try {
            dialogFragment.show(fragmentManager, ShareDialogFragment.class.getName());
            isDialogShowing = true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_dialog_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleText.setText(mTitle);
        mCancelBtn.setOnClickListener(this);
        mOKBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                isDialogShowing = false;
                recordCancel();
                dismiss();
                break;
            case R.id.btn_ok:
                isDialogShowing = false;
                dismiss();
                recordConfirm();
                shareToWeChatMoment();
                break;
        }
    }

    private void recordCancel() {
        if (getActivity() == null) {
            return;
        }
        if (mUserId == -1) {
            return;
        }
        if (TextUtils.isEmpty(mType)) {
            return;
        }
        if (mType.equals(THSharePreferenceManager.PROPERTY_MORE_THAN_FIFTEEN)) {
            THSharePreferenceManager.recordShareDialogMoreThanFifteen(mUserId, false, getActivity());
        }
        if (mType.equals(THSharePreferenceManager.PROPERTY_MORE_THAN_TWENTY_FIVE)) {
            THSharePreferenceManager.recordShareDialogMoreThanTwentyFive(mUserId, false, getActivity());
        }
        if (mType.equals(THSharePreferenceManager.FANS_MORE_THAN_NINE)){
            THSharePreferenceManager.recordShareDialogFANSMoreThanNine(mUserId, false, getActivity());
        }
        if (mType.equals(THSharePreferenceManager.LOGIN_CONTINUALLY)){
            THSharePreferenceManager.recordShareDialogLoginContinually(mUserId, false, getActivity());
        }
    }

    private void recordConfirm() {
        if (getActivity() == null) {
            return;
        }
        if (mUserId == -1) {
            return;
        }
        if (TextUtils.isEmpty(mType)) {
            return;
        }
        if (mType.equals(THSharePreferenceManager.PROPERTY_MORE_THAN_FIFTEEN)) {
            THSharePreferenceManager.recordShareDialogMoreThanFifteen(mUserId, true, getActivity());
        }
        if (mType.equals(THSharePreferenceManager.PROPERTY_MORE_THAN_TWENTY_FIVE)) {
            THSharePreferenceManager.recordShareDialogMoreThanTwentyFive(mUserId, true, getActivity());
        }
        if (mType.equals(THSharePreferenceManager.FANS_MORE_THAN_NINE)){
            THSharePreferenceManager.recordShareDialogFANSMoreThanNine(mUserId, true, getActivity());
        }
        if (mType.equals(THSharePreferenceManager.LOGIN_CONTINUALLY)){
            THSharePreferenceManager.recordShareDialogLoginContinually(mUserId, true, getActivity());
        }
    }

    //Share to WeChat moment and share to WeiBo on the background
    private void shareToWeChatMoment() {
        if (TextUtils.isEmpty(mShareContent)) {
            return;
        }
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null) {
            if (updatedUserProfileDTO.wbLinked) {
                String dowloadCNTradeHeroWeiboURL = getActivity().getResources().getString(R.string.download_tradehero_android_app_on_weibo);
                String outputStr = WeiboUtils.getShareContentWeibo(mShareContent, dowloadCNTradeHeroWeiboURL);
                InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(outputStr);
                userServiceWrapper.get().inviteFriends(
                        currentUserId.toUserBaseKey(), inviteFormDTO, new RequestCallback());
            }
        }
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = mShareContent;
        ((SocialSharerImpl) socialSharerLazy.get()).share(weChatDTO, getActivity());

    }

    private class RequestCallback implements Callback {

        @Override
        public void success(Object o, Response response) {

        }

        @Override
        public void failure(RetrofitError retrofitError) {

        }
    }

}
