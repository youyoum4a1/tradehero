package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.VotePair;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import timber.log.Timber;

public class AbstractDiscussionCompactItemViewHolder
{
    @InjectView(R.id.private_text_container) @Optional protected View textContainer;
    @InjectView(R.id.discussion_content) protected TextView content;
    @InjectView(R.id.private_text_stub_container) @Optional protected  View stubTextContainer;
    @InjectView(R.id.discussion_stub_content) @Optional protected  TextView stubContent;
    @InjectView(R.id.vote_pair) @Optional VotePair votePair;
    @InjectView(R.id.discussion_time) TextView time;
    @InjectView(R.id.discussion_action_button_comment_count) @Optional CompoundButton commentCountView;

    @Inject protected PrettyTime prettyTime;
    @Inject protected Context context;
    @Inject protected SocialShareTranslationHelper socialShareHelper;

    protected AbstractDiscussionCompactDTO abstractDiscussionCompactDTO;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewHolder()
    {
        super();
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    public void initView(View view)
    {
        ButterKnife.inject(this, view);
    }

    public void linkWith(AbstractDiscussionCompactDTO discussionDTO, boolean andDisplay)
    {
        this.abstractDiscussionCompactDTO = discussionDTO;
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
                commentCountView.setChecked(discussionDTO.commentCount > 0);
            }
        }
    }

    protected void displayText()
    {
        if (content != null)
        {
            if (this.abstractDiscussionCompactDTO instanceof AbstractDiscussionDTO)
            {
                content.setText(((AbstractDiscussionDTO) this.abstractDiscussionCompactDTO).text);
            }
            else
            {
                content.setText(R.string.na);
            }
        }
        if (stubContent != null)
        {
            if (this.abstractDiscussionCompactDTO instanceof AbstractDiscussionDTO)
            {
                stubContent.setText(((AbstractDiscussionDTO) this.abstractDiscussionCompactDTO).text);
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
        return abstractDiscussionCompactDTO != null && abstractDiscussionCompactDTO.isInProcess();
    }

    private void displayTime()
    {
        if (time != null)
        {
            if (abstractDiscussionCompactDTO.createdAtUtc != null)
            {
                time.setText(prettyTime.formatUnrounded(abstractDiscussionCompactDTO.createdAtUtc));
            }
        }
    }
}
