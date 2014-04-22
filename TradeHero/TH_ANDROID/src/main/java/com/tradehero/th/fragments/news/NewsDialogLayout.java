package com.tradehero.th.fragments.news;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.translation.TranslationResult;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.TranslationServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ForWeChat;
import com.tradehero.th.utils.SocialSharer;
import dagger.Lazy;
import retrofit.Callback;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * Created by tradehero on 14-3-7.
 */
public class NewsDialogLayout extends LinearLayout implements View.OnClickListener,
        AdapterView.OnItemClickListener, THDialog.DialogCallback
{
    private TextView newsTitleView;
    private TextView newsSubTitleView;
    private TextView shareTitleView;

    private View backView;
    private View cancelView;
    private ViewSwitcher viewSwitcher;
    private ViewSwitcher titleSwitcher;

    private ListView listViewFirst;
    private ListView listViewSecond;

    private THDialog.DialogInterface dialogCallback;

    //private NewsItemDTO newsItemDTO;
    private boolean mIsTranslateTitle;

    private int id;
    private String title;
    private String description;
    private String langCode;
    private String text;
    private DiscussionKey discussionKey;

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapperLazy;
    @Inject Lazy<TranslationServiceWrapper> translationServiceWrapperLazy;

    @Inject @ForWeChat SocialSharer wechatSharer;

    public NewsDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        DaggerUtils.inject(this);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        findView();
        fillData();
        registerListener();
    }

    private void findView()
    {
        this.titleSwitcher = (ViewSwitcher) findViewById(R.id.news_action_share_switcher);
        this.newsTitleView = (TextView) titleSwitcher.findViewById(R.id.news_action_share_title);
        this.newsSubTitleView =
                (TextView) titleSwitcher.findViewById(R.id.news_action_share_subtitle);
        this.shareTitleView = (TextView) titleSwitcher.findViewById(R.id.news_action_share_title2);

        this.backView = findViewById(R.id.news_action_back);
        this.cancelView = findViewById(R.id.news_action_share_cancel);
        this.viewSwitcher = (ViewSwitcher) findViewById(R.id.news_action_list_switcher);

        this.listViewFirst = (android.widget.ListView) this.viewSwitcher.findViewById(
                R.id.news_action_list_sharing_translation);
        this.listViewSecond = (android.widget.ListView) this.viewSwitcher.findViewById(
                R.id.news_action_list_sharing_items);

        this.viewSwitcher.setOutAnimation(getContext(), R.anim.slide_right_out);
        this.viewSwitcher.setInAnimation(getContext(), R.anim.slide_left_in);

        this.titleSwitcher.setOutAnimation(
                AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        this.titleSwitcher.setInAnimation(
                AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
    }

    private void registerListener()
    {
        backView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        listViewFirst.setOnItemClickListener(this);
        listViewSecond.setOnItemClickListener(this);
    }

    private void fillData()
    {
        String[] dataForFirst = {getContext().getString(R.string.sharing), getContext().getString(R.string.translation)};
        String[] dataForSecond = {"Facebook", "Twitter", "LinkedIn", "WeChat"};
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
                socialNetworkEnum = SocialNetworkEnum.FB;
                break;
            case 1:
                socialNetworkEnum = SocialNetworkEnum.TW;
                break;
            case 2:
                socialNetworkEnum = SocialNetworkEnum.LN;
                break;
            case 3:
                shareNewsToWeChat();
                return;
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
        wechatSharer.share(getContext(), discussionKey);
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
                //THToast.show(ex);
            }
        };
    }

    private void handleTranslation()
    {

        new AsyncTask<Void, Void, TranslationResult>()
        {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                dialog = new ProgressDialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setMessage(getContext().getString(R.string.translating));
                dialog.show();
            }

            @Override
            protected TranslationResult doInBackground(Void... params)
            {
                try
                {
                    TranslationServiceWrapper serviceWrapper = translationServiceWrapperLazy.get();
                    Timber.d("serviceWrapper " + serviceWrapper);
                    //TODO zh enough ?
                    return translationServiceWrapperLazy.get()
                            .translate(langCode, "zh",
                                    mIsTranslateTitle ? title : text);
                } catch (Exception e)
                {
                    Timber.e(e, "Translation Error");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(TranslationResult s)
            {
                super.onPostExecute(s);
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                //TODO
                if (s != null && s.getContent() != null)
                {
                    THToast.show("Success");
                    showTranslationResult(s.getContent());
                }
                else
                {
                    THToast.show("error");
                }
            }
        }.execute();
    }

    private void showTranslationResult(String text)
    {
        THDialog.showCenterDialog(getContext(), getContext().getString(R.string.translation_result), text, null,
                getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
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
                handleTranslation();
                dismissDialog();
            }
        }
        else
        {
            handleShareAction(position);
            dismissDialog();
        }
    }

    @Override
    public void setOnDismissCallback(THDialog.DialogInterface listener)
    {
        this.dialogCallback = listener;
    }

    //public void setNewsData(NewsItemDTO data, boolean isTranslateTitle)
    //{
    //    this.newsItemDTO = data;
    //    setNewsTitle();
    //    mIsTranslateTitle = isTranslateTitle;
    //}

    public void setNewsData(String title, String description, String langCode, int id, String text,
            DiscussionKey discussionKey, boolean isTranslateTitle)
    {
        this.title = title;
        this.description = description;
        this.langCode = langCode;
        this.id = id;
        this.text = text;
        this.discussionKey = discussionKey;
        setNewsTitle();
        mIsTranslateTitle = isTranslateTitle;
    }

    private class MyListAdapter extends ArrayAdapter<String>
    {

        public MyListAdapter(Context context, int resource, int textViewResourceId,
                String[] objects)
        {
            super(context, resource, textViewResourceId, objects);
        }
    }
}
