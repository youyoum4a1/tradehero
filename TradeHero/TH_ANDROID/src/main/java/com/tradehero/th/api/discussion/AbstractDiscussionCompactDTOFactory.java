package com.ayondo.academy.api.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.api.news.NewsItemDTO;
import com.ayondo.academy.api.timeline.TimelineItemDTO;
import com.ayondo.academy.api.translation.TranslationResult;
import com.ayondo.academy.persistence.translation.TranslationKey;

public class AbstractDiscussionCompactDTOFactory
{
    //<editor-fold desc="Clone">
    @Nullable public static AbstractDiscussionCompactDTO clone(@Nullable AbstractDiscussionCompactDTO original)
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
    public static void populateTranslation(
            @Nullable AbstractDiscussionCompactDTO toPopulate,
            @NonNull TranslationKey translationKey,
            @NonNull TranslationResult translationResult)
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

    @Nullable protected static String getSameOrTranslated(
            @Nullable String original,
            @NonNull TranslationKey translationKey,
            @NonNull TranslationResult translationResult)
    {
        if (original == null || original.hashCode() != translationKey.textHashCode)
        {
            return original;
        }
        return translationResult.getContent();
    }
    //</editor-fold>
}
