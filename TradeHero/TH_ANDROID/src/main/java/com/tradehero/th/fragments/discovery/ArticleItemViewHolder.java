package com.tradehero.th.fragments.discovery;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class ArticleItemViewHolder extends AbstractDiscussionCompactItemViewHolder
{
    @Inject Picasso picasso;

    @InjectView(R.id.article_title) TextView articleTitle;
    @InjectView(R.id.article_description) TextView articleDescriptionView;
    @InjectView(R.id.article_image) ImageView imageView;

    @Override public void onDetachedFromWindow()
    {
        picasso.cancelRequest(imageView);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull AbstractDiscussionCompactItemViewHolder.DTO parentDto)
    {
        super.display(parentDto);
        DTO dto = (DTO) parentDto;
        this.viewDTO = dto;

        if (imageView != null)
        {
            picasso.cancelRequest(imageView);
            if (((ArticleInfoDTO) dto.discussionDTO).image != null)
            {
                picasso.load(((ArticleInfoDTO) dto.discussionDTO).image)
                        // TODO better placeholder images showing that image is still being loaded
                        .placeholder(R.drawable.card_item_top_bg)
                        .into(imageView);
            }
            else
            {
                picasso.load(R.drawable.card_item_top_bg)
                        .into(imageView);
            }
        }
        if (articleDescriptionView != null)
        {
            articleDescriptionView.setText(dto.description);
        }
        if (articleTitle != null)
        {
            articleTitle.setText(dto.title);
        }
    }

    public static class Requisite extends AbstractDiscussionCompactItemViewHolder.Requisite
    {
        public Requisite(
                @NonNull Resources resources,
                @NonNull PrettyTime prettyTime,
                @NonNull ArticleInfoDTO discussionDTO,
                boolean canTranslate,
                boolean isAutoTranslate)
        {
            super(resources, prettyTime, discussionDTO, canTranslate, isAutoTranslate);
        }
    }

    public static class DTO extends AbstractDiscussionCompactItemViewHolder.DTO
    {
        @NonNull public final String title;
        @NonNull public final String description;

        public DTO(@NonNull Requisite requisite)
        {
            super(requisite);

            this.title = ((ArticleInfoDTO) requisite.discussionDTO).headline;
            this.description = ((ArticleInfoDTO) requisite.discussionDTO).previewText;
        }
    }
}
