package com.tradehero.th.api.discussion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.R;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.base.Application;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

abstract public class AbstractDiscussionCompactDTO extends ExtendedDTO
{
    public int id;
    public Date createdAtUtc;
    public int upvoteCount;
    public int downvoteCount;
    public int voteDirection; //-1: down, 0: cancel, 1: up
    public int commentCount;
    public int viewCount;
    @Nullable public String langCode;

    /**
     * Identifies the stub discussion that this discussion replaces.
     */
    @JsonIgnore
    public DiscussionKey stubKey;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO>
    AbstractDiscussionCompactDTO(ExtendedDTOType other,
            Class<? extends AbstractDiscussionCompactDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    public abstract DiscussionKey getDiscussionKey();

    public boolean isInProcess()
    {
        return stubKey != null && stubKey.id.equals(id);
    }

    public void populateVote(AbstractDiscussionCompactDTO target)
    {
        target.upvoteCount = upvoteCount;
        target.downvoteCount = downvoteCount;
        target.voteDirection = voteDirection;
    }

    @Override public String toString()
    {
        return "AbstractDiscussionCompactDTO{" +
                "id=" + id +
                ", createdAtUtc=" + createdAtUtc +
                ", upvoteCount=" + upvoteCount +
                ", downvoteCount=" + downvoteCount +
                ", voteDirection=" + voteDirection +
                ", commentCount=" + commentCount +
                ", langCode='" + langCode + '\'' +
                ", stubKey=" + stubKey +
                '}';
    }

    public String getVoteUpString()
    {
        String str = "" + upvoteCount;
        if(voteDirection == 1)
        {
            return Application.context().getString(R.string.html_string_blue,str);
        }
        else
        {
            return Application.context().getString(R.string.html_string_gray,str);
        }
    }

    public String getVoteDownString()
    {
        String str = "" + downvoteCount;
        if(voteDirection == -1)
        {
            return Application.context().getString(R.string.html_string_blue,str);
        }
        else
        {
            return Application.context().getString(R.string.html_string_gray,str);
        }
    }
}
