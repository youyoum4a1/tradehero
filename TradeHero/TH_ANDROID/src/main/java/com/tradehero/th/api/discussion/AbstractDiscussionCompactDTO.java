package com.ayondo.academy.api.discussion;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ayondo.academy.api.ExtendedDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import java.util.Date;

abstract public class AbstractDiscussionCompactDTO<T extends AbstractDiscussionCompactDTO> extends ExtendedDTO
    implements Comparable<T>
{
    public int id;
    public Date createdAtUtc;
    public int upvoteCount;
    public int downvoteCount;
    public int voteDirection; //-1: down, 0: cancel, 1: up
    public int commentCount;
    @Nullable public String langCode;

    /**
     * Identifies the stub discussion that this discussion replaces.
     */
    @JsonIgnore
    @Nullable public DiscussionKey stubKey;

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


    @Override public int compareTo(T another)
    {
        if (another == null)
        {
            return 1;
        }
        return id - another.id;
    }
}
