package com.androidth.general.fragments.news;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.squareup.picasso.Picasso;
import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsViewLinear extends AbstractDiscussionCompactItemViewLinear
{
    @Inject protected Picasso picasso;
    //<editor-fold desc="Constructors">
    public NewsViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @NonNull @Override protected NewsItemViewHolder createViewHolder()
    {
        return new NewsItemViewHolder(getContext(), picasso);
    }

    public void setTitleBackground(int resId)
    {
        viewHolder.setBackgroundResource(resId);
    }

    public static class Requisite extends AbstractDiscussionCompactItemViewLinear.Requisite
    {
        @NonNull public final List<SecurityCompactDTO> securityCompactDTOs;

        public Requisite(
                @NonNull Resources resources,
                @NonNull PrettyTime prettyTime,
                @NonNull NewsItemCompactDTO discussionDTO,
                boolean canTranslate,
                boolean isAutoTranslate,
                @NonNull List<SecurityCompactDTO> securityCompactDTOs)
        {
            super(resources, prettyTime, discussionDTO, canTranslate, isAutoTranslate);
            this.securityCompactDTOs = securityCompactDTOs;
        }
    }

    public static class DTO extends AbstractDiscussionCompactItemViewLinear.DTO
    {
        public DTO(@NonNull Requisite requisite)
        {
            super(requisite);
        }

        @NonNull @Override
        protected NewsItemViewHolder.DTO createViewHolderDTO(
                @NonNull AbstractDiscussionCompactItemViewLinear.Requisite requisite)
        {
            return new NewsItemViewHolder.DTO(
                    new NewsItemViewHolder.Requisite(
                            requisite.resources,
                            requisite.prettyTime,
                            (NewsItemCompactDTO) requisite.discussionDTO,
                            requisite.canTranslate,
                            requisite.isAutoTranslate,
                            ((Requisite) requisite).securityCompactDTOs)
            );
        }
    }
}
