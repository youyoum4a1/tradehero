package com.tradehero.th.fragments.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;

public class NewsDialogLayout extends ShareDialogLayout
{
    public static final int FIRST_MENU_ID = 0;
    public static final int SHARE_MENU_ID = 1;

    @InjectView(R.id.news_action_share_title) protected TextView newsTitleView;
    @InjectView(R.id.news_action_share_subtitle) protected TextView newsSubTitleView;

    @InjectView(R.id.news_action_back) protected View backView;
    @InjectView(R.id.news_action_share_switcher) protected ViewSwitcher titleSwitcher;

    @InjectView(R.id.news_action_list_switcher) protected ViewSwitcher optionsViewSwitcher;
    @InjectView(R.id.news_action_list_sharing_translation) protected ListView listViewOptions;

    protected ProgressDialog dialog;

    //<editor-fold desc="Constructors">
    public NewsDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void fillData()
    {
        super.fillData();
        String[] dataForFirst = {getContext().getString(R.string.sharing),
                getContext().getString(R.string.translation)};
        MyListAdapter adapterForFirst =
                new MyListAdapter(getContext(), R.layout.common_dialog_item_layout, R.id.popup_text, dataForFirst);
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
            notifyTranslationClicked();
        }
    }

    private class MyListAdapter extends ArrayAdapter<String>
    {
        public MyListAdapter(Context context, int resource, int textViewResourceId,
                String[] objects)
        {
            super(context, resource, textViewResourceId, objects);
        }
    }

    @Override public void setWhatToShare(@NonNull DTO discussionToShare)
    {
        super.setWhatToShare(discussionToShare);
        if (!(discussionToShare instanceof AbstractDiscussionCompactDTO))
        {
            throw new IllegalArgumentException("Cannot share " + discussionToShare.getClass().getName());
        }
        setNewsTitle();
    }

    @Override public void setMenuClickedListener(
            @Nullable OnShareMenuClickedListener menuClickedListener)
    {
        if (menuClickedListener != null &&
                !(menuClickedListener instanceof OnMenuClickedListener))
        {
            throw new IllegalArgumentException("You can only set OnMenuClickedListener");
        }
        super.setMenuClickedListener(menuClickedListener);
    }

    public void setMenuClickedListener(OnMenuClickedListener menuClickedListener)
    {
        super.setMenuClickedListener(menuClickedListener);
    }

    protected void notifyTranslationClicked()
    {
        NewsDialogLayout.OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslationRequestedClicked((AbstractDiscussionCompactDTO) whatToShare);
        }
    }

    public static interface OnMenuClickedListener extends OnShareMenuClickedListener
    {
        void onTranslationRequestedClicked(AbstractDiscussionCompactDTO toTranslate);
    }
}
