package com.tradehero.th.fragments.chinabuild.fragment;

//import android.app.FragmentManager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.social.WeiboSocialLinkHelper;
import com.tradehero.th.fragments.social.friend.SocialFriendHandler;
import com.tradehero.th.fragments.social.friend.SocialFriendHandlerWeibo;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th2.R;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import com.tradehero.th.activities.MarketUtil;
//import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
//import com.tradehero.th.persistence.timing.TimingIntervalPreference;

public class ShareSellDialogFragment extends BaseDialogFragment
{
    @InjectView(R.id.get_money_text) TextView mGetMoneyText;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Provider<SocialFriendHandlerWeibo> weiboSocialFriendHandlerProvider;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;

    private static String mGetMoney;

    public static ShareSellDialogFragment showReviewDialog(FragmentManager fragmentManager, String getMoney)
    {
        ShareSellDialogFragment dialogFragment = new ShareSellDialogFragment();
        dialogFragment.show(fragmentManager, ShareSellDialogFragment.class.getName());
        mGetMoney = getMoney;
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        setCancelable(false);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.share_sell_dialog_layout, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mGetMoneyText.setText(getString(R.string.share_sell_title_money, mGetMoney));
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel()
    {
        dismiss();
    }

    @OnClick(R.id.share_wechat)
    public void shareWeChat()
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSell;
        weChatDTO.title = getString(R.string.share_sell_to_wechat_title, mGetMoney);
        socialSharerLazy.get().share(weChatDTO);
    }

    @OnClick(R.id.share_pengyou)
    public void shareWeChatTimeline()
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = getString(R.string.share_sell_to_wechat_title, mGetMoney);
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
                        getString(R.string.share_sell_to_wechat_title, mGetMoney),
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
            super(getActivity());
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
}
