package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractDiscussionCompactCutDTO implements DTO
{
    public int id;
    public Date createdAtUtc;
    public int upvoteCount;
    public int downvoteCount;
    public int voteDirection;
    public int commentCount;
    @Nullable public String langCode;
    public DiscussionKey stubKey;

    AbstractDiscussionCompactCutDTO(
            @NotNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        this.id = abstractDiscussionCompactDTO.id;
        this.createdAtUtc = abstractDiscussionCompactDTO.createdAtUtc;
        this.upvoteCount = abstractDiscussionCompactDTO.upvoteCount;
        this.downvoteCount = abstractDiscussionCompactDTO.downvoteCount;
        this.voteDirection = abstractDiscussionCompactDTO.voteDirection;
        this.commentCount = abstractDiscussionCompactDTO.commentCount;
        this.langCode = abstractDiscussionCompactDTO.langCode;
        this.stubKey = abstractDiscussionCompactDTO.stubKey;
    }

    final boolean populate(@NotNull AbstractDiscussionCompactDTO inflated)
    {
        inflated.id = this.id;
        inflated.createdAtUtc = this.createdAtUtc;
        inflated.upvoteCount = this.upvoteCount;
        inflated.downvoteCount = this.downvoteCount;
        inflated.voteDirection = this.voteDirection;
        inflated.commentCount = this.commentCount;
        inflated.langCode = this.langCode;
        inflated.stubKey = this.stubKey;
        return true;
    }
}
