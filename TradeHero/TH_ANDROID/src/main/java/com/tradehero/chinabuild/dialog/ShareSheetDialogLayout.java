package com.tradehero.chinabuild.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.WeiboSocialLinkHelper;
import com.tradehero.th.fragments.social.friend.SocialFriendHandler;
import com.tradehero.th.fragments.social.friend.SocialFriendHandlerWeibo;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShareSheetDialogLayout extends LinearLayout implements View.OnClickListener
{
    @InjectView(R.id.share_local) @Optional ImageView localBtn;
    @InjectView(R.id.share_wechat) ImageView wechatBtn;
    @InjectView(R.id.share_pengyou) ImageView wechatTimelineBtn;
    @InjectView(R.id.share_weibo) ImageView weiboBtn;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Provider<SocialFriendHandlerWeibo> weiboSocialFriendHandlerProvider;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;
    private OnLocalSocialClickedListener mLocalSocialClickedListener;

    //<editor-fold desc="Constructors">
    public ShareSheetDialogLayout(Context context)
    {
        super(context);
    }

    public ShareSheetDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ShareSheetDialogLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        if (localBtn != null)
        {
            localBtn.setOnClickListener(this);
        }
        wechatBtn.setOnClickListener(this);
        wechatTimelineBtn.setOnClickListener(this);
        weiboBtn.setOnClickListener(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.share_local:
                mLocalSocialClickedListener.onShareRequestedClicked();
                break;
            case R.id.share_wechat:
                shareWeChat();
                break;
            case R.id.share_pengyou:
                shareWeChatTimeline();
                break;
            case R.id.share_weibo:
                shareWeibo();
                break;
        }
    }

    @OnClick(R.id.share_wechat)
    public void shareWeChat()
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSell;
        weChatDTO.title = mShareSheetTitleCache.get();
        socialSharerLazy.get().share(weChatDTO);
    }

    @OnClick(R.id.share_pengyou)
    public void shareWeChatTimeline()
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = mShareSheetTitleCache.get();
        socialSharerLazy.get().share(weChatDTO);
    }

    @OnClick(R.id.share_weibo)
    public void shareWeibo()
    {
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (updatedUserProfileDTO.wbLinked)
            {
                weiboSocialFriendHandlerProvider.get().inviteWeiboFriends(
                        mShareSheetTitleCache.get(),
                        currentUserId.toUserBaseKey(), new InviteFriendCallback());
            }
            else
            {
                weiboSocialLinkHelperProvider.get().link();
            }

        }
    }

    class InviteFriendCallback extends SocialFriendHandler.RequestCallback<Response>
    {
        private InviteFriendCallback()
        {
            super(getContext());
        }

        @Override
        public void success(Response data, @NotNull Response response)
        {
            super.success(data, response);
            if (response.getStatus() == 200 || response.getStatus() == 204)
            {
                THToast.show(R.string.share_success);
                return;
            }
            THToast.show(R.string.share_fail);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            super.failure(retrofitError);
            THToast.show(R.string.share_fail);
        }
    }

    public void setLocalSocialClickedListener(@Nullable OnLocalSocialClickedListener localSocialClickedListener)
    {
        mLocalSocialClickedListener = localSocialClickedListener;
    }

    public static interface OnLocalSocialClickedListener
    {
        void onShareRequestedClicked();
    }
}
