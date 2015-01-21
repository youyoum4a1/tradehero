package com.tradehero.th.fragments.news;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.utils.StringUtils;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsItemCompactViewHolder<DiscussionType extends NewsItemCompactDTO>
        extends AbstractDiscussionCompactItemViewHolder<DiscussionType>
{
    @InjectView(R.id.news_title_description) @Optional protected TextView newsDescription;
    @InjectView(R.id.news_title_title) @Optional protected TextView newsTitle;
    @InjectView(R.id.news_source) @Optional protected TextView newsSource;
    @InjectView(R.id.news_item_placeholder) @Optional ImageView newsItemPlaceholder;

    //<editor-fold desc="Constructors">
    public NewsItemCompactViewHolder(Context context)
    {
        super(context);
    }
    //</editor-fold>

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displaySource();
        displayTitle();
        displayDescription();
    }

    protected void displaySource()
    {
        if (newsSource != null)
        {
            if (discussionDTO != null)
            {
                newsSource.setText(parseHost(discussionDTO.url));
            }
            else
            {
                newsSource.setText(R.string.na);
            }
        }
    }

    @Nullable public String parseHost(String url)
    {
        try
        {
            return new URL(url).getHost();
        }
        catch (MalformedURLException ignored)
        {
            return null;
        }
    }

    @Override public void displayTranslatableTexts()
    {
        super.displayTranslatableTexts();
        displayTitle();
        displayDescription();
    }

    protected void displayTitle()
    {
        if (newsTitle != null)
        {
            newsTitle.setText(getTitleText());
        }
    }

    @Nullable public String getTitleText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
            case FAILED:
                if (discussionDTO != null)
                {
                    return discussionDTO.title;
                }
                return null;

            case TRANSLATED:
                if (translatedDiscussionDTO != null)
                {
                    return translatedDiscussionDTO.title;
                }
                return null;
        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }

    protected void displayDescription()
    {
        if (newsDescription != null)
        {
            String descriptionText = getDescriptionText();
            if (descriptionText != null)
            {
                newsDescription.setText(StringUtils.removeImageSpanObjects(descriptionText));
            }
        }
    }

    public String getDescriptionText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
            case FAILED:
                if (discussionDTO != null)
                {
                    return discussionDTO.description;
                }
                return null;

            case TRANSLATED:
                if (translatedDiscussionDTO != null)
                {
                    return translatedDiscussionDTO.description;
                }
                return null;
        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }

    @Override public void setBackgroundResource(int resId)
    {
        if (this.newsItemPlaceholder != null)
        {
            this.newsItemPlaceholder.setBackgroundResource(resId);
        }
    }

    //</editor-fold>

    public static interface OnMenuClickedListener extends AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener
    {
        // Nothing for now
    }
}
