package com.androidth.general.fragments.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.discussion.AbstractDiscussionDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.news.NewsItemDTO;
import rx.Observable;

public class NewsDialogLayout extends ShareDialogLayout
{
    public static final int FIRST_MENU_ID = 0;
    public static final int SHARE_MENU_ID = 1;

    @BindView(R.id.news_action_share_title) protected TextView newsTitleView;
    @BindView(R.id.news_action_share_subtitle) protected TextView newsSubTitleView;

    @BindView(R.id.news_action_back) protected View backView;
    @BindView(R.id.news_action_share_switcher) protected ViewSwitcher titleSwitcher;

    @BindView(R.id.news_action_list_switcher) protected ViewSwitcher optionsViewSwitcher;
    @BindView(R.id.news_action_list_sharing_translation) protected ListView listViewOptions;

    protected ProgressDialog dialog;

    //<editor-fold desc="Constructors">
    public NewsDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        String[] dataForFirst = {getContext().getString(R.string.sharing),
                getContext().getString(R.string.translation)};
        ArrayAdapter<String> adapterForFirst =
                new ArrayAdapter<>(getContext(), R.layout.common_dialog_item_layout, R.id.popup_text, dataForFirst);
        listViewOptions.setAdapter(adapterForFirst);
        listViewOptions.setDividerHeight(1);
        setNewsTitle();
    }

    private void setNewsTitle()
    {
        newsTitleView.setText(getTitleString());
        String description = getDescriptionString();
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

    protected String getTitleString()
    {
        if (whatToShare instanceof NewsItemDTO)
        {
            return ((NewsItemDTO) whatToShare).title;
        }
        else if (whatToShare instanceof NewsItemCompactDTO)
        {
            return ((NewsItemCompactDTO) whatToShare).title;
        }
        else if (whatToShare instanceof DiscussionDTO)
        {
            return String.format("%s: %s",
                    ((DiscussionDTO) whatToShare).user.displayName,
                    ((DiscussionDTO) whatToShare).text);
        }
        else if (whatToShare instanceof AbstractDiscussionDTO)
        {
            return ((AbstractDiscussionDTO) whatToShare).text;
        }
        return getContext().getString(R.string.na);
    }

    protected String getDescriptionString()
    {
        if (whatToShare instanceof NewsItemDTO)
        {
            return ((NewsItemDTO) whatToShare).description;
        }
        else if (whatToShare instanceof NewsItemCompactDTO)
        {
            return ((NewsItemCompactDTO) whatToShare).description;
        }
        else if (whatToShare instanceof AbstractDiscussionDTO)
        {
            return "";
        }
        return getContext().getString(R.string.na);
    }

    @SuppressWarnings("UnusedDeclaration")
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

    @SuppressWarnings("UnusedDeclaration")
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
            shareActionBehavior.onNext(new TranslateUserAction());
            shareActionBehavior.onCompleted();
        }
    }

    @NonNull @Override public Observable<UserAction> show(@SuppressWarnings("NullableProblems") @NonNull DTO discussionToShare)
    {
        Observable<UserAction> observable = super.show(discussionToShare);
        if (!(discussionToShare instanceof AbstractDiscussionCompactDTO))
        {
            throw new IllegalArgumentException("Cannot share " + discussionToShare.getClass().getName());
        }
        setNewsTitle();
        return observable;
    }

    public static class TranslateUserAction implements UserAction
    {
    }
}
