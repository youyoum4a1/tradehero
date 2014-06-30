package com.tradehero.th.fragments.news;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.tradehero.thm.R;
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

    @Override protected void fillData()
    {
        super.fillData();
        String[] dataForFirst = {getContext().getString(R.string.sharing),
                getContext().getString(R.string.translation)};
        MyListAdapter adapterForFirst =
                new MyListAdapter(getContext(), R.layout.common_dialog_item_layout, R.id.popup_text,
                        dataForFirst);
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
        if (discussionToShare instanceof NewsItemCompactDTO)
        {
            return ((NewsItemCompactDTO) discussionToShare).title;
        }
        else if (discussionToShare instanceof NewsItemDTO)
        {
            return ((NewsItemDTO) discussionToShare).title;
        }
        else if (discussionToShare instanceof DiscussionDTO)
        {
            return String.format("%s: %s",
                    ((DiscussionDTO) discussionToShare).user.displayName,
                    ((DiscussionDTO) discussionToShare).text);
        }
        else if (discussionToShare instanceof AbstractDiscussionDTO)
        {
            return ((AbstractDiscussionDTO) discussionToShare).text;
        }
        return getContext().getString(R.string.na);
    }

    protected String getDescriptionString()
    {
        if (discussionToShare instanceof NewsItemCompactDTO)
        {
            return ((NewsItemCompactDTO) discussionToShare).description;
        }
        else if (discussionToShare instanceof NewsItemDTO)
        {
            return ((NewsItemDTO) discussionToShare).description;
        }
        else if (discussionToShare instanceof AbstractDiscussionDTO)
        {
            return "";
        }
        return getContext().getString(R.string.na);
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

    @Override public void setDiscussionToShare(AbstractDiscussionCompactDTO discussionToShare)
    {
        super.setDiscussionToShare(discussionToShare);
        setNewsTitle();
    }

    @Override public void setMenuClickedListener(
            OnShareMenuClickedListener menuClickedListener)
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
            listenerCopy.onTranslationRequestedClicked(discussionToShare);
        }
    }

    public static interface OnMenuClickedListener extends OnShareMenuClickedListener
    {
        void onTranslationRequestedClicked(AbstractDiscussionCompactDTO toTranslate);
    }
}
