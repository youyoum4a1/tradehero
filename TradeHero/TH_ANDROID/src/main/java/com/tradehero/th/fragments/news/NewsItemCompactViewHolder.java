package com.tradehero.th.fragments.news;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemKnowledge;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsItemCompactViewHolder
        extends AbstractDiscussionCompactItemViewHolder
{
    @NonNull protected final Picasso picasso;
    @Bind(R.id.news_title_title) @Nullable protected TextView newsTitle;
    @Bind(R.id.news_icon) @Nullable ImageView newsIcon;

    //<editor-fold desc="Constructors">
    public NewsItemCompactViewHolder(@NonNull Picasso picasso)
    {
        super();
        this.picasso = picasso;
    }
    //</editor-fold>

    //<editor-fold desc="Display Methods">
    @Override public void display(@NonNull AbstractDiscussionCompactItemViewHolder.DTO parentViewDto)
    {
        super.display(parentViewDto);
        DTO dto = (DTO) parentViewDto;
        if (newsTitle != null)
        {
            newsTitle.setText(dto.getTitle());
        }
        if (newsIcon != null)
        {
            picasso.load(dto.thumbnailUrl)
                    .placeholder(dto.thumbnailPlaceHolderResId)
                    .error(dto.thumbnailPlaceHolderResId)
                    .into(newsIcon);
        }
    }
    //</editor-fold>

    public static class Requisite extends AbstractDiscussionCompactItemViewHolder.Requisite
    {
        public Requisite(
                @NonNull Resources resources,
                @NonNull PrettyTime prettyTime,
                @NonNull NewsItemCompactDTO discussionDTO,
                boolean canTranslate,
                boolean isAutoTranslate)
        {
            super(resources, prettyTime, discussionDTO, canTranslate, isAutoTranslate);
        }
    }

    public static class DTO extends AbstractDiscussionCompactItemViewHolder.DTO
    {
        @Nullable private String title;
        @Nullable public final String thumbnailUrl;
        @DrawableRes public final int thumbnailPlaceHolderResId;

        public DTO(@NonNull Requisite requisite)
        {
            super(requisite);

            NewsItemCompactDTO newsItem = (NewsItemCompactDTO) requisite.discussionDTO;
            this.title = newsItem.title;

            //<editor-fold desc="Thumbnail Url & PlaceHolder">
            if (newsItem.source != null
                    && newsItem.source.id != null
                    && NewsItemKnowledge.NEWS_PLACEHOLDER_MAP.containsKey(newsItem.source.id))
            {
                thumbnailUrl = null;
                thumbnailPlaceHolderResId = NewsItemKnowledge.NEWS_PLACEHOLDER_MAP.get(newsItem.source.id);
            }
            else
            {
                if (newsItem.imageUrl != null)
                {
                    thumbnailUrl = newsItem.imageUrl;
                }
                else if (newsItem.source != null && newsItem.source.imageUrl != null)
                {
                    thumbnailUrl = newsItem.source.imageUrl;
                }
                else
                {
                    thumbnailUrl = null;
                }
                thumbnailPlaceHolderResId = R.drawable.card_item_top_bg;
            }
            //</editor-fold>
        }

        @Override public void setCurrentTranslationStatus(@NonNull TranslationStatus currentTranslationStatus)
        {
            super.setCurrentTranslationStatus(currentTranslationStatus);
            this.title = createTitleText();
        }
        //</editor-fold>

        @Nullable protected String createTitleText()
        {
            switch (getCurrentTranslationStatus())
            {
                case ORIGINAL:
                case TRANSLATING:
                case FAILED:
                    return ((NewsItemCompactDTO) discussionDTO).title;

                case TRANSLATED:
                    if (translatedDiscussionDTO != null)
                    {
                        return ((NewsItemCompactDTO) translatedDiscussionDTO).title;
                    }
                    return null;
            }
            throw new IllegalStateException("Unhandled state " + getCurrentTranslationStatus());
        }

        @Nullable public String getTitle()
        {
            return title;
        }
    }
}
