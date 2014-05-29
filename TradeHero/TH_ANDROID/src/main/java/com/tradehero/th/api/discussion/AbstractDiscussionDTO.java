package com.tradehero.th.api.discussion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.translation.TranslationKey;
import java.util.Date;

public abstract class AbstractDiscussionDTO extends AbstractDiscussionCompactDTO
{
    public int userId;
    public String text;

    /**
     * Identifies the stub discussion that this discussion replaces.
     */
    @JsonIgnore
    public DiscussionKey stubKey;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> AbstractDiscussionDTO(ExtendedDTOType other, Class<? extends AbstractDiscussionDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    public void populateVote(AbstractDiscussionDTO target)
    {
        target.upvoteCount = upvoteCount;
        target.downvoteCount = downvoteCount;
        target.voteDirection = voteDirection;
    }

    public UserBaseKey getSenderKey()
    {
        return new UserBaseKey(userId);
    }

    public abstract DiscussionKey getDiscussionKey();

    public boolean isInProcess()
    {
        return stubKey != null && stubKey.id.equals(id);
    }

    public TranslationKey createTranslationKey(String toLanguage)
    {
        return new TranslationKey(langCode, toLanguage, text);
    }

    @Override public String toString()
    {
        return "AbstractDiscussionDTO{" +
                "id=" + id +
                ", createdAtUtc=" + createdAtUtc +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                ", upvoteCount=" + upvoteCount +
                ", downvoteCount=" + downvoteCount +
                ", voteDirection=" + voteDirection +
                ", commentCount=" + commentCount +
                ", langCode='" + langCode + '\'' +
                ", stubKey=" + stubKey +
                '}';
    }
}
