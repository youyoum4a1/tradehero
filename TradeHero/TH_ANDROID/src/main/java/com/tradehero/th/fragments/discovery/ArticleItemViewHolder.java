package com.tradehero.th.fragments.discovery;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class ArticleItemViewHolder extends AbstractDiscussionCompactItemViewHolder
{
    @InjectView(R.id.article_title) TextView articleTitle;
    @InjectView(R.id.article_description) TextView articleDescriptionView;
    @InjectView(R.id.article_image) ImageView imageView;

    @Override public void display(@NonNull AbstractDiscussionCompactItemViewHolder.DTO parentDto)
    {
        super.display(parentDto);
        DTO dto = (DTO) parentDto;
        this.viewDTO = dto;

        if (imageView != null)
        {
            ImageLoader.getInstance()
                    .displayImage(
                            ((ArticleInfoDTO) dto.discussionDTO).image,
                            imageView,
                            getArticleImageLoaderOptions());
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

    public static DisplayImageOptions getArticleImageLoaderOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.card_item_top_bg)
                .showImageForEmptyUri(R.drawable.card_item_top_bg)
                .showImageOnFail(R.drawable.card_item_top_bg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        return options;
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
