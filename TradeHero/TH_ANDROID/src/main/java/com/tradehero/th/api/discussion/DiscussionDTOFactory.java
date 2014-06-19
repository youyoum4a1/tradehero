package com.tradehero.th.api.discussion;

import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import java.util.Date;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiscussionDTOFactory
{
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionDTOFactory(@NotNull CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Contract("null -> null; !null -> !null") @Nullable
    public DiscussionDTO createChildClass(@Nullable DiscussionDTO unidentified)
    {
        if (unidentified == null)
        {
            return null;
        }

        // TODO remove this temporary HACK
        {
            if (unidentified.type.equals(DiscussionType.COMMENT))
            {
                unidentified.type = DiscussionType.PRIVATE_MESSAGE;
            }
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

    public DiscussionDTO createEmptyChild(DiscussionType discussionType)
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
        DiscussionDTO created = createEmptyChild(fromForm.getInReplyToType());
        if (fromForm.stubKey != null)
        {
            created.id = fromForm.stubKey.id;
        }
        created.stubKey = fromForm.stubKey;
        created.inReplyToType = DiscussionType.PRIVATE_MESSAGE;
        created.type = DiscussionType.PRIVATE_MESSAGE;
        created.inReplyToId = fromForm.inReplyToId;
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
