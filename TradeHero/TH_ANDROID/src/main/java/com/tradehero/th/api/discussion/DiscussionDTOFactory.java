package com.tradehero.th.api.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.ReplyDiscussionFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import java.util.Date;
import javax.inject.Inject;

public class DiscussionDTOFactory
{
    @NonNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionDTOFactory(@NonNull CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Nullable
    public DiscussionDTO createChildClass(@Nullable DiscussionDTO unidentified)
    {
        if (unidentified == null)
        {
            return null;
        }
        return createChildClass(unidentified.type, unidentified);
    }

    @Nullable
    public DiscussionDTO createChildClass(@Nullable DiscussionType discussionType, @Nullable DiscussionDTO discussionDTO)
    {
        DiscussionDTO created = discussionDTO;
        if (discussionType != null)
        {
            switch (discussionType)
            {
                case PRIVATE_MESSAGE:
                    created = new PrivateDiscussionDTO();
                    if (discussionDTO != null)
                    {
                        ((PrivateDiscussionDTO) created).putAll(discussionDTO, PrivateDiscussionDTO.class);
                    }
                    break;
            }
        }
        return created;
    }

    public DiscussionDTO createEmptyChild(@Nullable DiscussionType discussionType)
    {
        DiscussionDTO created = null;
        if (discussionType != null)
        {
            switch (discussionType)
            {
                case PRIVATE_MESSAGE:
                    created = new PrivateDiscussionDTO();
                    break;

                default:
                    created = new DiscussionDTO();
                    break;
            }
        }
        return created;
    }

    public DiscussionDTO createStub(DiscussionFormDTO fromForm)
    {
        DiscussionDTO created = null;
        if (fromForm != null)
        {
            created = createStubStrict(fromForm);
        }
        return created;
    }

    private DiscussionDTO createStubStrict(DiscussionFormDTO fromForm)
    {
        DiscussionDTO created;
        if (fromForm instanceof ReplyDiscussionFormDTO)
        {
            created = createEmptyChild(((ReplyDiscussionFormDTO) fromForm).getInReplyToType());
            created.inReplyToId = ((ReplyDiscussionFormDTO) fromForm).inReplyToId;
        }
        else
        {
            created = createEmptyChild(null);
        }

        if (fromForm.stubKey != null)
        {
            created.id = fromForm.stubKey.id;
        }
        created.stubKey = fromForm.stubKey;
        created.inReplyToType = DiscussionType.PRIVATE_MESSAGE;
        created.type = DiscussionType.PRIVATE_MESSAGE;
        created.text = fromForm.text;
        created.langCode = fromForm.langCode;
        created.userId = currentUserId.toUserBaseKey().key;
        created.createdAtUtc = new Date();
        created.url = fromForm.url;
        created.geo_alt = fromForm.geo_alt;
        created.geo_lat = fromForm.geo_lat;
        created.geo_long = fromForm.geo_long;
        created.publishToFb = fromForm.publishToFb;
        created.publishToLi = fromForm.publishToLi;
        created.publishToTw = fromForm.publishToTw;
        created.publishToWb = fromForm.publishToWb;
        created.isPublic = fromForm.isPublic;
        return created;
    }
}
