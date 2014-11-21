package com.tradehero.th.persistence.translation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

public class TranslationKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public TranslationKeyFactory()
    {
    }
    //</editor-fold>

    @Nullable public TranslationKeyList createFrom(
            @Nullable AbstractDiscussionCompactDTO fromDiscussion,
            @NonNull String toLanguage)
    {
        if (fromDiscussion == null || fromDiscussion.langCode == null)
        {
            return null;
        }
        return createKeys(
                fromDiscussion.langCode,
                toLanguage,
                getTranslatableTexts(fromDiscussion));
    }

    @Nullable
    protected List<String> getTranslatableTexts(@Nullable AbstractDiscussionCompactDTO fromDiscussion)
    {
        if (fromDiscussion == null)
        {
            return null;
        }
        List<String> texts = new ArrayList<>();
        if (fromDiscussion instanceof AbstractDiscussionDTO)
        {
            addTranslatableTexts(texts, (AbstractDiscussionDTO) fromDiscussion);
        }
        else if (fromDiscussion instanceof NewsItemCompactDTO)
        {
            addTranslatableTexts(texts, (NewsItemCompactDTO) fromDiscussion);
        }
        return texts;
    }

    protected void addTranslatableTexts(
            @NonNull List<String> texts, @NonNull AbstractDiscussionDTO abstractDiscussionDTO)
    {
        if (abstractDiscussionDTO.text != null)
        {
            texts.add(abstractDiscussionDTO.text);
        }
        if (abstractDiscussionDTO instanceof DiscussionDTO)
        {
            addTranslatableTexts(texts, (DiscussionDTO) abstractDiscussionDTO);
        }
        else if (abstractDiscussionDTO instanceof TimelineItemDTO)
        {
            addTranslatableTexts(texts, (TimelineItemDTO) abstractDiscussionDTO);
        }
    }

    protected void addTranslatableTexts(
            @NonNull List<String> texts,
            @NonNull DiscussionDTO discussionDTO)
    {
        if (discussionDTO instanceof PrivateDiscussionDTO)
        {
            addTranslatableTexts(texts, (PrivateDiscussionDTO) discussionDTO);
        }
    }

    protected void addTranslatableTexts(
            @NonNull List<String> texts,
            @NonNull PrivateDiscussionDTO privateDiscussionDTO)
    {
    }

    protected void addTranslatableTexts(
            @NonNull List<String> texts,
            @NonNull TimelineItemDTO timelineItemDTO)
    {
    }

    protected void addTranslatableTexts(
            @NonNull List<String> texts,
            @NonNull NewsItemCompactDTO newsItemCompactDTO)
    {
        if (newsItemCompactDTO.caption != null)
        {
            texts.add(newsItemCompactDTO.caption);
        }
        if (newsItemCompactDTO.title != null)
        {
            texts.add(newsItemCompactDTO.title);
        }
        if (newsItemCompactDTO.description != null)
        {
            texts.add(newsItemCompactDTO.description);
        }
        if (newsItemCompactDTO instanceof NewsItemDTO)
        {
            addTranslatableTexts(texts, (NewsItemDTO) newsItemCompactDTO);
        }
    }

    protected void addTranslatableTexts(
            @NonNull List<String> texts,
            @NonNull NewsItemDTO newsItemDTO)
    {
        if (newsItemDTO.text != null)
        {
            texts.add(newsItemDTO.text);
        }
        if (newsItemDTO.message != null)
        {
            texts.add(newsItemDTO.message);
        }
    }

    public TranslationKeyList createKeys(@NonNull String fromLang, @NonNull String toLang, @Nullable Collection<String> texts)
    {
        if (texts == null)
        {
            return null;
        }
        TranslationKeyList keys = new TranslationKeyList();
        for (String text: texts)
        {
            keys.add(new TranslationKey(fromLang, toLang, text));
        }
        return keys;
    }

    public boolean isValidLangCode(@Nullable String langCode)
    {
        return langCode != null && !TextUtils.isEmpty(langCode) && !langCode.equals("xxx");
    }
}
