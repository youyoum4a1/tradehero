package com.tradehero.th.fragments.discussion;

import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;

public class AbstractDiscussionItemViewHolder<DiscussionDTOType extends AbstractDiscussionDTO>
    extends AbstractDiscussionCompactItemViewHolder<DiscussionDTOType>
{
    @InjectView(R.id.private_text_container) @Optional protected View textContainer;
    @InjectView(R.id.discussion_content) protected TextView textContent;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionItemViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void linkWith(DiscussionDTOType discussionDTO, boolean andDisplay)
    {
        super.linkWith(discussionDTO, andDisplay);
        if (andDisplay)
        {
        }
    }

    @Override public void display()
    {
        super.display();
        displayText();
        displayStubText();
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

    protected void displayStubText()
    {
        if (stubContent != null)
        {
            stubContent.setText(getStubText());
        }
    }

    protected String getStubText()
    {
        if (discussionDTO != null)
        {
            return discussionDTO.text;
        }
        return null;
    }

    @Override protected void displayInProcess()
    {
        super.displayInProcess();
        if (textContainer != null)
        {
            textContainer.setVisibility(isInProcess() ? View.GONE : View.VISIBLE);
        }
    }
}
