package com.tradehero.th.fragments.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ForWeChat;
import com.tradehero.th.utils.SocialSharer;
import com.tradehero.th.wxapi.WeChatDTO;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import timber.log.Timber;

public class NewsDialogLayout extends LinearLayout implements THDialog.DialogCallback
{
    public static final int FIRST_MENU_ID = 0;
    public static final int SHARE_MENU_ID = 1;

    @InjectView(R.id.news_action_share_title) protected TextView newsTitleView;
    @InjectView(R.id.news_action_share_subtitle) protected TextView newsSubTitleView;
    @InjectView(R.id.news_action_share_title2) protected TextView shareTitleView;

    @InjectView(R.id.news_action_back) protected View backView;
    @InjectView(R.id.news_action_share_cancel) protected View cancelView;
    @InjectView(R.id.news_action_share_switcher) protected ViewSwitcher titleSwitcher;

    @InjectView(R.id.news_action_list_switcher) protected ViewSwitcher optionsViewSwitcher;
    @InjectView(R.id.news_action_list_sharing_translation) protected ListView listViewOptions;
    @InjectView(R.id.news_action_list_sharing_items) protected ListView listViewSharingOptions;

    private THDialog.DialogInterface dialogCallback;
    protected ProgressDialog dialog;

    private int id;
    private String title;
    private String description;

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    @Inject @ForWeChat SocialSharer wechatSharer;

    private int mShareType;
    private OnMenuClickedListener menuClickedListener;

    //<editor-fold desc="Constructors">
    public NewsDialogLayout(Context context)
    {
        super(context);
    }

    public NewsDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsDialogLayout(Context context, AttributeSet attrs, int defStyle)
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
        fillData();
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void fillData()
    {
        String[] dataForFirst = {getContext().getString(R.string.sharing),
                getContext().getString(R.string.translation)};
        String[] dataForSecond = getContext().getResources().getStringArray(R.array.share_to);
        MyListAdapter adapterForFirst =
                new MyListAdapter(getContext(), R.layout.common_dialog_item_layout, R.id.popup_text,
                        dataForFirst);
        MyListAdapter adapterForSecond =
                new MyListAdapter(getContext(), R.layout.common_dialog_item_layout, R.id.popup_text,
                        dataForSecond);
        listViewOptions.setAdapter(adapterForFirst);
        listViewSharingOptions.setAdapter(adapterForSecond);
        listViewOptions.setDividerHeight(1);
        listViewSharingOptions.setDividerHeight(1);
        setNewsTitle();
    }

    private void setNewsTitle()
    {
        newsTitleView.setText(title);
        if (!TextUtils.isEmpty(description))
        {
            newsSubTitleView.setVisibility(View.VISIBLE);
            newsSubTitleView.setText(description);
        }
        else
        {
            newsSubTitleView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.news_action_back)
    protected void showFirstOptions()
    {
        this.backView.setVisibility(View.INVISIBLE);
        this.optionsViewSwitcher.setDisplayedChild(FIRST_MENU_ID);
        titleSwitcher.setDisplayedChild(FIRST_MENU_ID);
    }

    private void showShareToOptions()
    {
        this.backView.setVisibility(View.VISIBLE);
        this.optionsViewSwitcher.setDisplayedChild(SHARE_MENU_ID);
        titleSwitcher.setDisplayedChild(SHARE_MENU_ID);
    }

    private void handleShareAction(int position)
    {
        SocialNetworkEnum socialNetworkEnum = null;
        switch (position)
        {
            case 0:
                shareNewsToWeChat();
                return;
            case 1:
                socialNetworkEnum = SocialNetworkEnum.LN;
                break;
            case 2:
                socialNetworkEnum = SocialNetworkEnum.FB;
                break;
            case 3:
                socialNetworkEnum = SocialNetworkEnum.TW;
                break;
            default:
                break;
        }
        DiscussionListKey key = new DiscussionListKey(DiscussionType.NEWS, id);
        discussionServiceWrapper.get().share(key,
                new TimelineItemShareRequestDTO(socialNetworkEnum),
                createShareRequestCallback(socialNetworkEnum));
    }

    private void shareNewsToWeChat()
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = id;
        weChatDTO.type = mShareType;
        weChatDTO.title = title;
        wechatSharer.share(getContext(), weChatDTO);
    }

    private Callback<DiscussionDTO> createShareRequestCallback(
            final SocialNetworkEnum socialNetworkEnum)
    {
        return new THCallback<DiscussionDTO>()
        {
            @Override protected void success(DiscussionDTO response, THResponse thResponse)
            {
                THToast.show(String.format(
                        getContext().getString(R.string.timeline_post_to_social_network),
                        socialNetworkEnum.getName()));
            }

            @Override protected void failure(THException ex)
            {
                THToast.show("Share error " + socialNetworkEnum.getName());
                Timber.e(ex, "Share error");
            }
        };
    }

    @OnClick(R.id.news_action_share_cancel)
    protected void dismissDialog()
    {
        if (dialogCallback != null)
        {
            dialogCallback.onDialogDismiss();
        }
    }

    @OnItemClick(R.id.news_action_list_sharing_translation)
    protected void onOptionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (position == 0)
        {
            //share
            showShareToOptions();
        }
        else if (position == 1)
        {
            notifyTranslationClicked();
            dismissDialog();
        }
    }

    @OnItemClick(R.id.news_action_list_sharing_items)
    protected void onShareOptionsItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        handleShareAction(position);
        dismissDialog();
    }

    @Override
    public void setOnDismissCallback(THDialog.DialogInterface listener)
    {
        this.dialogCallback = listener;
    }

    public void setNewsData(NewsItemDTO newsItemDTO, int shareType)
    {
        this.description = newsItemDTO.description;
        setNewsData((AbstractDiscussionDTO) newsItemDTO, shareType);
    }

    public void setNewsData(AbstractDiscussionDTO abstractDiscussionDTO, int shareType)
    {
        this.title = abstractDiscussionDTO.text;
        this.id = abstractDiscussionDTO.id;
        setNewsTitle();
        mShareType = shareType;
    }

    private class MyListAdapter extends ArrayAdapter<String>
    {

        public MyListAdapter(Context context, int resource, int textViewResourceId,
                String[] objects)
        {
            super(context, resource, textViewResourceId, objects);
        }
    }

    public void setMenuClickedListener(OnMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    protected void notifyTranslationClicked()
    {
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslationRequestedClicked();
        }
    }

    protected void notifyShareClicked()
    {
        OnMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareRequestedClicked();
        }
    }

    public static interface OnMenuClickedListener
    {
        void onTranslationRequestedClicked();
        void onShareRequestedClicked();
    }
}
