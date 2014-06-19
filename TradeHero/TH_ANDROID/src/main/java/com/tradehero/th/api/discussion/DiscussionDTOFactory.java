package com.tradehero.th.api.discussion;

import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import java.util.Date;
import javax.inject.Inject;

public class DiscussionDTOFactory
{
    private final CurrentUserId currentUserId;

    @Inject public DiscussionDTOFactory(CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }

    public DiscussionDTO createChildClass(DiscussionDTO unidentified)
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

    public DiscussionDTO createChildClass(DiscussionType discussionType, DiscussionDTO discussionDTO)
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
