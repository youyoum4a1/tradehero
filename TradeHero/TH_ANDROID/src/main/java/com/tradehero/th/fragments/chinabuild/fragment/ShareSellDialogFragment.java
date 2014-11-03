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
import com.tradehero.th.R;
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
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.touch.MoneyUtils;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Provider;

public class ShareSellDialogFragment extends BaseDialogFragment
{
    @InjectView(R.id.get_money_text) TextView mGetMoneyText;
    @InjectView(R.id.textview_share_sellsecurity_contenta) TextView shareContentA;
    @InjectView(R.id.textview_share_sellsecurity_contentb) TextView shareContentB;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Provider<SocialFriendHandlerWeibo> weiboSocialFriendHandlerProvider;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;

    private static String mStockName;
    private static String mStockCode;
    private static String mStockUp;
    private static String mStockNum;
    private static String mGetMoney = "1";
    private static String mUserId;
    private static String mPositionId;
    private String loseMoneyContentA;
    private String loseMoneyContentB;
    private int loseMoneyColor;
    private static String mTradeId;
    private static Double mProfit;

    public static ShareSellDialogFragment showSellDialog(FragmentManager fragmentManager,
            String stockName, String stockCode, String stockUp, String stockNum, String getMoney,
            String userId, String positionId, String tradeId, Double profit)
    {
        ShareSellDialogFragment dialogFragment = new ShareSellDialogFragment();
        try{
            dialogFragment.show(fragmentManager, ShareSellDialogFragment.class.getName());
        }catch (Exception e){
            e.printStackTrace();
            return dialogFragment;
        }
        mStockName = stockName;
        mStockCode = stockCode;
        mStockUp = stockUp;
        mStockNum = stockNum;
        mGetMoney = getMoney;
        mUserId = userId;
        mPositionId = positionId;
        mTradeId = tradeId;
        mProfit = profit;
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
        if(getActivity()==null){
            return;
        }
        loseMoneyContentA = getActivity().getResources().getString(R.string.share_sell_title11);
        loseMoneyContentB = getActivity().getResources().getString(R.string.share_sell_title12);
        loseMoneyColor = getActivity().getResources().getColor(R.color.share_sellsecurity_losemoney);
        if(mProfit != null){
            String currency = SecurityUtils.getDefaultCurrency();
            mGetMoney = MoneyUtils.convertMoneyStr(mProfit, getActivity(), currency);
        }
        if(mGetMoney.startsWith("-")){
            shareContentA.setText(loseMoneyContentA);
            shareContentB.setText(loseMoneyContentB);
            mGetMoneyText.setTextColor(loseMoneyColor);
        }
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
        if(!mGetMoney.startsWith("-")){
            weChatDTO.title = getString(R.string.share_sell_to_wechat_title,/* mStockNum, mStockName, mStockCode, mStockUp, mGetMoney,*/ mUserId, mPositionId, mTradeId);
        }else{
            weChatDTO.title = getString(R.string.share_sell_to_wechat_title_losemoney, mUserId, mPositionId, mTradeId);
        }
        socialSharerLazy.get().share(weChatDTO);
    }

    @OnClick(R.id.share_pengyou)
    public void shareWeChatTimeline()
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        if(!mGetMoney.startsWith("-")){
            weChatDTO.title = getString(R.string.share_sell_to_wechat_title, /*mStockNum, mStockName, mStockCode, mStockUp, mGetMoney,*/ mUserId, mPositionId, mTradeId);
        }else{
            weChatDTO.title = getString(R.string.share_sell_to_wechat_title_losemoney, mUserId, mPositionId, mTradeId);
        }
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
                if(!mGetMoney.startsWith("-")){
                    weiboSocialFriendHandlerProvider.get().inviteWeiboFriends(
                        getString(R.string.share_sell_to_wechat_title, /*mStockNum, mStockName, mStockCode, mStockUp, mGetMoney,*/ mUserId, mPositionId, mTradeId),
                        currentUserId.toUserBaseKey(), new InviteFriendCallback());
                }else{
                    weiboSocialFriendHandlerProvider.get().inviteWeiboFriends(
                            getString(R.string.share_sell_to_wechat_title_losemoney, mUserId, mPositionId, mTradeId),
                            currentUserId.toUserBaseKey(), new InviteFriendCallback());
                }
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
