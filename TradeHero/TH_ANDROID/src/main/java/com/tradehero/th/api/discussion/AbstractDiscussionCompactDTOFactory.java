package com.tradehero.th.api.discussion;

import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.persistence.translation.TranslationKey;
import javax.inject.Inject;

public class AbstractDiscussionCompactDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public AbstractDiscussionCompactDTOFactory()
    {
    }
    //</editor-fold>

    //<editor-fold desc="Clone">
    public AbstractDiscussionCompactDTO clone(AbstractDiscussionCompactDTO original)
    {
        if (original == null)
        {
            return null;
        }
        if (original instanceof NewsItemCompactDTO)
        {
            NewsItemCompactDTO newsCompact = (NewsItemCompactDTO) original;
            if (newsCompact instanceof NewsItemDTO)
            {
                return new NewsItemDTO(newsCompact, NewsItemDTO.class);
            }
            return new NewsItemCompactDTO(newsCompact, NewsItemCompactDTO.class);
        }
        if (original instanceof AbstractDiscussionDTO)
        {
            AbstractDiscussionDTO abstractDiscussion = (AbstractDiscussionDTO) original;
            if (abstractDiscussion instanceof TimelineItemDTO)
            {
                return new TimelineItemDTO(abstractDiscussion, TimelineItemDTO.class);
            }
            if (abstractDiscussion instanceof DiscussionDTO)
            {
                DiscussionDTO originalDiscussion = (DiscussionDTO) abstractDiscussion;
                if (originalDiscussion instanceof PrivateDiscussionDTO)
                {
                    return new PrivateDiscussionDTO(originalDiscussion, PrivateDiscussionDTO.class);
                }
                return new DiscussionDTO(originalDiscussion, DiscussionDTO.class);
            }
        }
        throw new IllegalArgumentException("Unhandled type " + original.getClass());
    }

    //</editor-fold>

    //<editor-fold desc="Populate Translations">
    public void populateTranslation(AbstractDiscussionCompactDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
        if (toPopulate instanceof AbstractDiscussionDTO)
        {
            AbstractDiscussionDTO casted = (AbstractDiscussionDTO) toPopulate;
            casted.text = getSameOrTranslated(casted.text, translationKey, translationResult);
        }
        else if (toPopulate instanceof NewsItemCompactDTO)
        {
            NewsItemCompactDTO newsCompact = (NewsItemCompactDTO) toPopulate;
            newsCompact.caption = getSameOrTranslated(newsCompact.caption, translationKey, translationResult);
            newsCompact.description = getSameOrTranslated(newsCompact.description, translationKey, translationResult);
            newsCompact.title = getSameOrTranslated(newsCompact.title, translationKey, translationResult);
            if (newsCompact instanceof NewsItemDTO)
            {
                NewsItemDTO news = (NewsItemDTO) newsCompact;
                news.text = getSameOrTranslated(news.text, translationKey, translationResult);
                news.message = getSameOrTranslated(news.message, translationKey, translationResult);
            }
        }
    }

    protected String getSameOrTranslated(String original, TranslationKey translationKey, TranslationResult translationResult)
    {
        if (original == null || original.hashCode() != translationKey.textHashCode)
        {
            return original;
        }
        return translationResult.getContent();
    }
    //</editor-fold>
}
