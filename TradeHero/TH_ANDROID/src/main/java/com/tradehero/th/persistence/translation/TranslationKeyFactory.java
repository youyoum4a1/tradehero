package com.tradehero.th.persistence.translation;

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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TranslationKeyFactory
{
    @Inject public TranslationKeyFactory()
    {
    }

    @Nullable public TranslationKeyList createFrom(@Nullable AbstractDiscussionCompactDTO fromDiscussion, @NotNull String toLanguage)
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

    @Nullable protected List<String> getTranslatableTexts(@Nullable AbstractDiscussionCompactDTO fromDiscussion)
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
            @NotNull List<String> texts, @NotNull AbstractDiscussionDTO abstractDiscussionDTO)
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

    protected void addTranslatableTexts(List<String> texts, DiscussionDTO discussionDTO)
    {
        if (discussionDTO instanceof PrivateDiscussionDTO)
        {
            addTranslatableTexts(texts, (PrivateDiscussionDTO) discussionDTO);
        }
    }

    protected void addTranslatableTexts(List<String> texts, PrivateDiscussionDTO privateDiscussionDTO)
    {
    }

    protected void addTranslatableTexts(List<String> texts, TimelineItemDTO timelineItemDTO)
    {
    }

    protected void addTranslatableTexts(@NotNull List<String> texts, @NotNull NewsItemCompactDTO newsItemCompactDTO)
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

    protected void addTranslatableTexts(@NotNull List<String> texts, @NotNull NewsItemDTO newsItemDTO)
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

    @Contract("_, _, null -> null")
    public TranslationKeyList createKeys(@NotNull String fromLang, @NotNull String toLang, @Nullable Collection<String> texts)
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
