package com.tradehero.th.fragments.discussion;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.VotePair;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import timber.log.Timber;

public class AbstractDiscussionItemViewHolder
{
    @InjectView(R.id.private_text_container) @Optional protected View textContainer;
    @InjectView(R.id.discussion_content) protected TextView content;
    @InjectView(R.id.private_text_stub_container) @Optional protected  View stubTextContainer;
    @InjectView(R.id.discussion_stub_content) @Optional protected  TextView stubContent;
    @InjectView(R.id.vote_pair) @Optional VotePair votePair;
    @InjectView(R.id.discussion_time) TextView time;
    @InjectView(R.id.discussion_action_button_comment_count) @Optional TextView commentCountView;

    @Inject protected PrettyTime prettyTime;

    protected AbstractDiscussionDTO discussionDTO;

    public AbstractDiscussionItemViewHolder()
    {
        super();
        DaggerUtils.inject(this);
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
            // timeline time
            displayTime();

            if (votePair != null)
            {
                votePair.display(discussionDTO);
            }
            if (commentCountView != null)
            {
                Timber.d("commentCountView %d",discussionDTO.commentCount);
                commentCountView.setText(String.valueOf(discussionDTO.commentCount));
            }
        }
    }

    protected void displayText()
    {
        if (content != null)
        {
            if (this.discussionDTO != null)
            {
                content.setText(this.discussionDTO.text);
            }
            else
            {
                content.setText(R.string.na);
            }
        }
        if (stubContent != null)
        {
            if (this.discussionDTO != null)
            {
                stubContent.setText(this.discussionDTO.text);
            }
            else
            {
                stubContent.setText(R.string.na);
            }
        }
    }

    protected void displayInProcess()
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
        return discussionDTO != null && discussionDTO.isInProcess();
    }

    private void displayTime()
    {
        if (time != null)
        {
            if (discussionDTO.createdAtUtc != null && time != null)
            {
                time.setText(prettyTime.formatUnrounded(discussionDTO.createdAtUtc));
            }
        }
    }
}
