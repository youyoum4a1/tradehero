package com.tradehero.chinabuild.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.WeiboSocialLinkHelper;
import com.tradehero.th.fragments.social.friend.SocialFriendsFragmentWeibo;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class InviteFriendsFragment extends DashboardFragment implements View.OnClickListener
{
    @InjectView(R.id.title) TextView mTitle;
    @InjectView(R.id.invite_friend_input_layout) RelativeLayout mInputLayout;
    @InjectView(R.id.my_invite_code) TextView mMyInviteCode;
    @InjectView(R.id.copy) TextView mCopyButton;
    @InjectView(R.id.invite_friends_weChat) RelativeLayout mInviteQQFriendsLayout;
    @InjectView(R.id.invite_friends_weibo) RelativeLayout mInviteWeiboFriendsLayout;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;
    @Nullable private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = new SettingsReferralUserProfileListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.invite_friends);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.invite_friends_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        mInputLayout.setOnClickListener(this);
        mCopyButton.setOnClickListener(this);
        SpannableStringBuilder style = new SpannableStringBuilder(mTitle.getText());
        style.setSpan(new ForegroundColorSpan(Color.rgb(255, 174, 0)), 27, 32,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTitle.setText(style);
        mInviteQQFriendsLayout.setOnClickListener(this);
        mInviteWeiboFriendsLayout.setOnClickListener(this);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchProfile();
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.invite_friend_input_layout:
                goToFragment(InputInviteCodeFragment.class);
                break;
            case R.id.copy:
                THToast.show(R.string.copy_my_invite_code_success);
                ClipboardManager clipboard = (ClipboardManager)getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", mMyInviteCode.getText());
                clipboard.setPrimaryClip(clip);
                break;
            case R.id.invite_friends_weChat:
                UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
                if (userProfileDTO != null)
                {
                    WeChatDTO weChatDTO = new WeChatDTO();
                    weChatDTO.id = 0;
                    weChatDTO.type = WeChatMessageType.Invite;
                    weChatDTO.title = getString(WeChatMessageType.Invite.getTitleResId(), userProfileDTO.referralCode);
                    socialSharerLazy.get().share(weChatDTO); // TODO proper callback?
                }
                break;
            case R.id.invite_friends_weibo:
                UserProfileDTO updatedUserProfileDTO =
                        userProfileCache.get(currentUserId.toUserBaseKey());
                if (updatedUserProfileDTO != null)
                {
                    boolean linked = updatedUserProfileDTO.wbLinked;
                    if (linked)
                    {
                        goToFragment(SocialFriendsFragmentWeibo.class);
                    }
                    else
                    {
                        weiboSocialLinkHelperProvider.get().link();
                    }
                }
                break;
        }
    }

    @Override public void onStop()
    {
        detachProfileCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }
    protected void fetchProfile()
    {
        detachProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void detachProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected class SettingsReferralUserProfileListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            mMyInviteCode.setText(value.referralCode);
            if (value.inviteCode != null && !value.inviteCode.isEmpty())
            {
                mInputLayout.setVisibility(View.GONE);
            }
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Failed to fetch profile info", error);
        }
    }
}
