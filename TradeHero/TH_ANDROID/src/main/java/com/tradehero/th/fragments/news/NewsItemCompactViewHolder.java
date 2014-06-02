package com.tradehero.th.fragments.news;

import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsItemCompactViewHolder<DiscussionType extends NewsItemCompactDTO> extends AbstractDiscussionCompactItemViewHolder<DiscussionType>
{
    @InjectView(R.id.news_title_description) protected TextView newsDescription;
    @InjectView(R.id.news_title_title) protected TextView newsTitle;
    @InjectView(R.id.news_source) protected TextView newsSource;

    //<editor-fold desc="Constructors">
    public NewsItemCompactViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void linkWith(DiscussionType discussionDTO, boolean andDisplay)
    {
        super.linkWith(discussionDTO, andDisplay);
        if (andDisplay)
        {
        }
    }

    @Override public boolean isAutoTranslate()
    {
        return true;
    }

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
        if (discussionDTO != null)
        {
            newsSource.setText(parseHost(discussionDTO.url));
        }
        else
        {
            newsSource.setText(R.string.na);
        }
    }

    public String parseHost(String url)
    {
        try
        {
            return new URL(url).getHost();
        }
        catch (MalformedURLException e)
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
        newsTitle.setText(getTitleText());
    }

    public String getTitleText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
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
        newsDescription.setText(getDescriptionText());
    }

    public String getDescriptionText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
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
    //</editor-fold>
}
