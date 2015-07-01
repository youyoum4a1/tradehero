package com.tradehero.th.fragments.discussion;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.api.discussion.DiscussionDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class DiscussionItemViewHolder
        extends AbstractDiscussionItemViewHolder
{
    //<editor-fold desc="Constructors">
    public DiscussionItemViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void display(@NonNull AbstractDiscussionCompactItemViewHolder.DTO parentViewDto)
    {
        super.display(parentViewDto);
        DTO dto = (DTO) parentViewDto;
    }

    //<editor-fold desc="Display Methods">
    @NonNull @Override protected String getUserAvatarURL()
    {
        if (viewDTO != null
                && ((DiscussionDTO) viewDTO.discussionDTO).user != null
                && ((DiscussionDTO) viewDTO.discussionDTO).user.picture != null)
        {
            return ((DiscussionDTO) viewDTO.discussionDTO).user.picture;
        }
        return null;
    }
    //</editor-fold>

    public static class Requisite extends AbstractDiscussionItemViewHolder.Requisite
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

    public static class DTO extends AbstractDiscussionItemViewHolder.DTO
    {
        public DTO(@NonNull Requisite requisite)
        {
            super(requisite);
        }

        @NonNull @Override protected String createUserDisplayName()
        {
            if (((DiscussionDTO) discussionDTO).user != null)
            {
                return ((DiscussionDTO) discussionDTO).user.displayName;
            }
            return super.createUserDisplayName();
        }
    }
}
