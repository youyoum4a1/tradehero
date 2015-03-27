package com.tradehero.th.fragments.news;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemKnowledge;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsItemCompactViewHolder
        extends AbstractDiscussionCompactItemViewHolder
{
    @InjectView(R.id.news_title_title) @Optional protected TextView newsTitle;
    @InjectView(R.id.news_icon) ImageView newsIcon;
    @Inject Picasso picasso;

    //<editor-fold desc="Constructors">
    public NewsItemCompactViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void onDetachedFromWindow()
    {
        picasso.cancelRequest(newsIcon);
        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Display Methods">
    @Override public void display(@NonNull AbstractDiscussionCompactItemViewHolder.DTO parentViewDto)
    {
        super.display(parentViewDto);
        DTO dto = (DTO) parentViewDto;
        if (newsTitle != null)
        {
            newsTitle.setText(dto.getTitle());
        }
        if (dto.thumbnailUrl != null)
        {
            picasso.load(dto.thumbnailUrl)
                    .placeholder(dto.thumbnailPlaceHolderResId)
                    .into(newsIcon);
        }
        else
        {
            newsIcon.setImageResource(dto.thumbnailPlaceHolderResId);
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
                    && newsItem.source.id.equals(NewsItemKnowledge.SEEKING_ALPHA_SOURCE_ID))
            {
                thumbnailUrl = null;
                thumbnailPlaceHolderResId = R.drawable.seeking_alpha;
            }
            else if (newsItem.source != null
                    && newsItem.source.id != null
                    && newsItem.source.id.equals(NewsItemKnowledge.MOTLEY_FOOL_SOURCE_ID))
            {
                thumbnailUrl = null;
                thumbnailPlaceHolderResId = R.drawable.motley_fool;
            }
            else
            {
                if (newsItem.thumbnail != null)
                {
                    thumbnailUrl = newsItem.thumbnail;
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
