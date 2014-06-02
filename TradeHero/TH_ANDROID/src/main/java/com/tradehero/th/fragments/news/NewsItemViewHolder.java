package com.tradehero.th.fragments.news;

import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;

public class NewsItemViewHolder<DiscussionType extends NewsItemDTO> extends
        NewsItemCompactViewHolder<DiscussionType>
{
    @InjectView(R.id.discussion_content) protected TextView textContent;

    //<editor-fold desc="Constructors">
    public NewsItemViewHolder()
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
        return false;
    }

    //<editor-fold desc="Display Methods">
    @Override public void displayTranslatableTexts()
    {
        super.displayTranslatableTexts();
        displayText();
    }

    protected void displayText()
    {
        if (textContent != null)
        {
            textContent.setText(getText());
        }
    }

    protected String getText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
                if (discussionDTO != null)
                {
                    return discussionDTO.text;
                }
                return null;

            case TRANSLATING:
            case TRANSLATED:
                if (translatedDiscussionDTO != null)
                {
                    return translatedDiscussionDTO.text;
                }
                return null;

        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }
    //</editor-fold>
}
