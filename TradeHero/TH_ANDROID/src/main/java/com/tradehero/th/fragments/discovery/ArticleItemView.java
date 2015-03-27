package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import org.ocpsoft.prettytime.PrettyTime;

public class ArticleItemView extends AbstractDiscussionCompactItemViewLinear
{
    public ArticleItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @NonNull @Override protected ArticleItemViewHolder createViewHolder()
    {
        return new ArticleItemViewHolder();
    }

    public static class Requisite extends AbstractDiscussionCompactItemViewLinear.Requisite
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

    public static class DTO extends AbstractDiscussionCompactItemViewLinear.DTO
    {
        public DTO(@NonNull Requisite requisite)
        {
            super(requisite);
        }

        @NonNull @Override
        protected AbstractDiscussionCompactItemViewHolder.DTO createViewHolderDTO(
                @NonNull AbstractDiscussionCompactItemViewLinear.Requisite requisite)
        {
            return new ArticleItemViewHolder.DTO(
                    new ArticleItemViewHolder.Requisite(
                            requisite.resources,
                            requisite.prettyTime,
                            (ArticleInfoDTO) requisite.discussionDTO,
                            requisite.canTranslate,
                            requisite.isAutoTranslate));
        }
    }
}
