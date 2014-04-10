package com.tradehero.th.fragments.social.message;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;

public class PrivateMessageBubbleViewHolder
{
    @InjectView(R.id.private_text_container) View textContainer;
    @InjectView(R.id.private_text) TextView text;

    private DiscussionDTO discussionDTO;

    public PrivateMessageBubbleViewHolder()
    {
        super();
    }

    public void initView(View view)
    {
        ButterKnife.inject(this, view);
    }

    public void linkWith(DiscussionDTO discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;
        if (andDisplay)
        {
            displayText();
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
    }
}
