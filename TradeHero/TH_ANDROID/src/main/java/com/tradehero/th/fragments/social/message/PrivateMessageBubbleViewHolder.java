package com.tradehero.th.fragments.social.message;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;

public class PrivateMessageBubbleViewHolder
{
    @InjectView(R.id.private_text_container) View textContainer;
    //@InjectView(R.id.private_text)
    TextView text;
    @InjectView(R.id.private_text_stub_container) @Optional View stubTextContainer;
    @InjectView(R.id.discussion_stub_content) @Optional TextView stubText;

    private AbstractDiscussionDTO discussionDTO;

    public PrivateMessageBubbleViewHolder()
    {
        super();
    }

    public void initView(View view)
    {
        ButterKnife.inject(this, view);
    }

    public void linkWith(AbstractDiscussionDTO discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;
        if (andDisplay)
        {
            displayText();
            displayInProcess();
        }
    }

    public void displayText()
    {
        if (text != null)
        {
            if (this.discussionDTO != null)
            {
                text.setText(this.discussionDTO.text);
            }
            else
            {
                text.setText(R.string.na);
            }
        }
        if (stubText != null)
        {
            if (this.discussionDTO != null)
            {
                stubText.setText(this.discussionDTO.text);
            }
            else
            {
                stubText.setText(R.string.na);
            }
        }
    }

    public void displayInProcess()
    {
        boolean isInProcess = isInProcess();
        if (stubTextContainer != null)
        {
            stubTextContainer.setVisibility(isInProcess ? View.VISIBLE : View.GONE);
        }
        if (textContainer != null)
        {
            textContainer.setVisibility(isInProcess ? View.GONE : View.VISIBLE);
        }
    }

    public boolean isInProcess()
    {
        return discussionDTO != null && discussionDTO.getDiscussionKey().equals(discussionDTO.stubKey);
    }
}
