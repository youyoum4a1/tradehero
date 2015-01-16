package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.ButterKnife;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.WeiboUtils;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * Detail of News
 *
 * Created by palmer on 15/1/16.
 */
public class NewsDetailFragment extends DashboardFragment {

    @Inject Lazy<NewsServiceWrapper> newsServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialSharer> socialSharerLazy;

    private WebView newsWebView;

    private DisplayMetrics dm;

    private long newsId;
    private String newsTitle;

    public final static String KEY_BUNDLE_NEWS_ID = "key_bundle_news_id";
    public final static String KEY_BUNDLE_NEWS_TITLE = "key_bundle_news_title";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.news_detail, container, false);

        ButterKnife.inject(this, view);

        dm = new DisplayMetrics();
        dm = getActivity().getResources().getDisplayMetrics();

        newsWebView = (WebView)view.findViewById(R.id.webview_news_html_content);
        newsWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        newsWebView.getSettings().setBuiltInZoomControls(false);
        newsWebView.getSettings().setJavaScriptEnabled(true);
        newsWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        Bundle bundle = getArguments();
        newsId = bundle.getLong(KEY_BUNDLE_NEWS_ID);

        newsServiceWrapper.get().getSecurityNewsDetail(newsId, new Callback<NewsItemDTO>() {
            @Override
            public void success(NewsItemDTO newsItemDTO, Response response) {
                if(newsItemDTO!=null && newsItemDTO.text!=null && newsWebView!=null){
                    String htmlContent = StringUtils.convertToHtmlFormat(newsItemDTO.text, (int)(dm.widthPixels/dm.density-36));
                    newsWebView.loadData(htmlContent, "text/html; charset=UTF-8", null);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THToast.show(new THException(retrofitError).getMessage());
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        Bundle bundle = getArguments();
        newsTitle = bundle.getString(KEY_BUNDLE_NEWS_TITLE);
        setHeadViewMiddleMain(newsTitle);
        setHeadViewRight0(getActivity().getResources().getString(R.string.discovery_discuss_send_share));
    }

    @Override
    public void onClickHeadRight0()
    {
        shareToWechatMoment(newsTitle);
    }

    //Share to wechat moment and share to weibo on the background
    private void shareToWechatMoment(final String strShare)
    {
        if (TextUtils.isEmpty(strShare))
        {
            return;
        }
        String show = getUnParsedText(strShare);
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (updatedUserProfileDTO.wbLinked)
            {
                String downloadCNTradeHeroWeibo = getActivity().getResources().getString(R.string.download_tradehero_android_app_on_weibo);
                String outputStr = show;
                outputStr = WeiboUtils.getShareContentWeibo(outputStr, downloadCNTradeHeroWeibo);
                InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(outputStr);
                userServiceWrapper.get().inviteFriends(
                        currentUserId.toUserBaseKey(), inviteFormDTO, new Callback<Response>()
                        {
                            @Override
                            public void success(Response response, Response response2)
                            {

                            }

                            @Override
                            public void failure(RetrofitError retrofitError)
                            {

                            }
                        });
            }
        }
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = strShare;
        ((SocialSharerImpl) socialSharerLazy.get()).share(weChatDTO, getActivity());
    }
}
