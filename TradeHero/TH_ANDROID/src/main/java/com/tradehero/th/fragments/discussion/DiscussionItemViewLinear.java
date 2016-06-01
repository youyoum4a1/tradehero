package com.ayondo.academy.fragments.discussion;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.squareup.picasso.Picasso;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class DiscussionItemViewLinear
        extends AbstractDiscussionCompactItemViewLinear
{
    @Inject protected Picasso picasso;

    //<editor-fold desc="Constructors">
    public DiscussionItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @NonNull @Override protected DiscussionItemViewHolder createViewHolder()
    {
        return new DiscussionItemViewHolder(picasso);
    }

    public static class Requisite extends AbstractDiscussionCompactItemViewLinear.Requisite
    {
        public Requisite(
                @NonNull Resources resources,
                @NonNull PrettyTime prettyTime,
                @NonNull DiscussionDTO discussionDTO,
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

        @NonNull @Override protected AbstractDiscussionCompactItemViewHolder.DTO createViewHolderDTO(
                @NonNull AbstractDiscussionCompactItemViewLinear.Requisite requisite)
        {
            return new DiscussionItemViewHolder.DTO(
                    new DiscussionItemViewHolder.Requisite(
                            requisite.resources,
                            requisite.prettyTime,
                            (DiscussionDTO) requisite.discussionDTO,
                            requisite.canTranslate,
                            requisite.isAutoTranslate));
        }
    }
}
