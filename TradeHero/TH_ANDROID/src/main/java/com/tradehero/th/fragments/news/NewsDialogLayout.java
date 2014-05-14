package com.tradehero.th.fragments.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
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

public class NewsDialogLayout extends LinearLayout implements View.OnClickListener,
        AdapterView.OnItemClickListener, THDialog.DialogCallback
{
    @InjectView(R.id.news_action_share_title) protected TextView newsTitleView;
    @InjectView(R.id.news_action_share_subtitle) protected TextView newsSubTitleView;
    @InjectView(R.id.news_action_share_title2) protected TextView shareTitleView;

    @InjectView(R.id.news_action_back) protected View backView;
    @InjectView(R.id.news_action_share_cancel) protected View cancelView;
    @InjectView(R.id.news_action_list_switcher) protected ViewSwitcher viewSwitcher;
    @InjectView(R.id.news_action_share_switcher) protected ViewSwitcher titleSwitcher;

    @InjectView(R.id.news_action_list_sharing_translation) protected ListView listViewFirst;
    @InjectView(R.id.news_action_list_sharing_items) protected ListView listViewSecond;

    private THDialog.DialogInterface dialogCallback;
    protected ProgressDialog dialog;

    private int id;
    private String title;
    private String description;

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapperLazy;
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
        findView();
        fillData();
        registerListener();
    }

    @Override protected void onAttachedToWindow()
    {
        ButterKnife.inject(this);
        super.onAttachedToWindow();
    }

    private void findView()
    {
        this.viewSwitcher.setOutAnimation(getContext(), R.anim.slide_right_out);
        this.viewSwitcher.setInAnimation(getContext(), R.anim.slide_left_in);

        this.titleSwitcher.setOutAnimation(
                AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        this.titleSwitcher.setInAnimation(
                AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
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
        listViewFirst.setAdapter(adapterForFirst);
        listViewSecond.setAdapter(adapterForSecond);
        listViewFirst.setDividerHeight(1);
        listViewSecond.setDividerHeight(1);
        setNewsTitle();
        setShareTitle();
    }

    private void registerListener()
    {
        backView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        listViewFirst.setOnItemClickListener(this);
        listViewSecond.setOnItemClickListener(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void setNewsTitle()
    {
        //if (newsItemDTO != null)
        //{
        newsTitleView.setText(title);
        //newsTitleView.setText(newsItemDTO.title);
        if (!TextUtils.isEmpty(description))
        {
            newsSubTitleView.setText(description);
            //subTitleView.setVisibility(View.VISIBLE);
        }
        else
        {
            newsSubTitleView.setVisibility(View.GONE);
        }
        //}
    }

    private void setShareTitle()
    {
        shareTitleView.setText(R.string.share_to);
    }

    private void showFirstChild()
    {
        this.backView.setVisibility(View.INVISIBLE);
        this.viewSwitcher.setDisplayedChild(0);
        //setNewsTitle();
        titleSwitcher.setDisplayedChild(0);
    }

    private void showSecondChild()
    {

        this.backView.setVisibility(View.VISIBLE);
        this.viewSwitcher.setDisplayedChild(1);
        //setShareTitle();

        titleSwitcher.setDisplayedChild(1);
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
        discussionServiceWrapperLazy.get().share(key,
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

    private void dismissDialog()
    {
        if (dialogCallback != null)
        {
            dialogCallback.onDialogDismiss();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.news_action_back:
                showFirstChild();
                break;
            case R.id.news_action_share_cancel:
                dismissDialog();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (parent == listViewFirst)
        {
            if (position == 0)
            {
                //share
                showSecondChild();
            }
            else if (position == 1)
            {
                notifyTranslationClicked();
                dismissDialog();
            }
        }
        else
        {
            notifyShareClicked();
            handleShareAction(position);
            dismissDialog();
        }
    }

    @Override
    public void setOnDismissCallback(THDialog.DialogInterface listener)
    {
        this.dialogCallback = listener;
    }

    public void setNewsData(NewsItemDTO newsItemDTO,
            int shareType)
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
