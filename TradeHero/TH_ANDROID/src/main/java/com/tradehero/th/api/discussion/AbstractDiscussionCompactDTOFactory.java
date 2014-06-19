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
            return clone((NewsItemCompactDTO) original);
        }
        if (original instanceof AbstractDiscussionDTO)
        {
            return clone((AbstractDiscussionDTO) original);
        }

        throw new IllegalArgumentException("Unhandled type " + original.getClass());
    }

    protected NewsItemCompactDTO clone(NewsItemCompactDTO original)
    {
        if (original instanceof NewsItemDTO)
        {
            return new NewsItemDTO(original, NewsItemDTO.class);
        }
        return new NewsItemCompactDTO(original, NewsItemCompactDTO.class);
    }

    protected AbstractDiscussionDTO clone(AbstractDiscussionDTO original)
    {
        if (original instanceof TimelineItemDTO)
        {
            return new TimelineItemDTO(original, TimelineItemDTO.class);
        }
        if (original instanceof DiscussionDTO)
        {
            return clone((DiscussionDTO) original);
        }
        throw new IllegalArgumentException("Unhandled type " + original.getClass());
    }

    protected DiscussionDTO clone(DiscussionDTO original)
    {
        if (original instanceof PrivateDiscussionDTO)
        {
            return new PrivateDiscussionDTO(original, PrivateDiscussionDTO.class);
        }
        return new DiscussionDTO(original, DiscussionDTO.class);
    }
    //</editor-fold>

    //<editor-fold desc="Populate Translations">
    public void populateTranslation(AbstractDiscussionCompactDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
        if (toPopulate instanceof AbstractDiscussionDTO)
        {
            populateTranslation((AbstractDiscussionDTO) toPopulate, translationKey, translationResult);
        }
        else if (toPopulate instanceof NewsItemCompactDTO)
        {
            populateTranslation((NewsItemCompactDTO) toPopulate, translationKey, translationResult);
        }
    }

    protected void populateTranslation(AbstractDiscussionDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
        toPopulate.text = getSameOrTranslated(toPopulate.text, translationKey, translationResult);
        if (toPopulate instanceof TimelineItemDTO)
        {
            populateTranslation((TimelineItemDTO) toPopulate, translationKey, translationResult);
        }
        else if (toPopulate instanceof DiscussionDTO)
        {
            populateTranslation((DiscussionDTO) toPopulate, translationKey, translationResult);
        }
    }

    protected void populateTranslation(TimelineItemDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
    }

    protected void populateTranslation(DiscussionDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
        if (toPopulate instanceof PrivateDiscussionDTO)
        {
            populateTranslation((PrivateDiscussionDTO) toPopulate, translationKey, translationResult);
        }
    }

    protected void populateTranslation(PrivateDiscussionDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
    }

    protected void populateTranslation(NewsItemCompactDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
        toPopulate.caption = getSameOrTranslated(toPopulate.caption, translationKey, translationResult);
        toPopulate.description = getSameOrTranslated(toPopulate.description, translationKey, translationResult);
        toPopulate.title = getSameOrTranslated(toPopulate.title, translationKey, translationResult);
        if (toPopulate instanceof NewsItemDTO)
        {
            populateTranslation((NewsItemDTO) toPopulate, translationKey, translationResult);
        }
    }

    protected void populateTranslation(NewsItemDTO toPopulate, TranslationKey translationKey, TranslationResult translationResult)
    {
        toPopulate.text = getSameOrTranslated(toPopulate.text, translationKey, translationResult);
        toPopulate.message = getSameOrTranslated(toPopulate.message, translationKey, translationResult);
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
