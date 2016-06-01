package com.ayondo.academy.api.discussion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.discussion.form.DiscussionFormDTO;
import com.ayondo.academy.api.discussion.form.ReplyDiscussionFormDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import java.util.Date;

public class DiscussionDTOFactory
{
    @Nullable
    public static DiscussionDTO createChildClass(@Nullable DiscussionDTO unidentified)
    {
        if (unidentified == null)
        {
            return null;
        }
        return createChildClass(unidentified.type, unidentified);
    }

    @Nullable
    public static DiscussionDTO createChildClass(@Nullable DiscussionType discussionType, @Nullable DiscussionDTO discussionDTO)
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

    @Nullable public static DiscussionDTO createEmptyChild(@Nullable DiscussionType discussionType)
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

    @Nullable public static DiscussionDTO createStub(@Nullable DiscussionFormDTO fromForm,
            @NonNull CurrentUserId currentUserId)
    {
        DiscussionDTO created = null;
        if (fromForm != null)
        {
            created = createStubStrict(fromForm, currentUserId);
        }
        return created;
    }

    @Nullable private static DiscussionDTO createStubStrict(@NonNull DiscussionFormDTO fromForm,
            @NonNull CurrentUserId currentUserId)
    {
        DiscussionDTO created;
        if (fromForm instanceof ReplyDiscussionFormDTO)
        {
            created = createEmptyChild(((ReplyDiscussionFormDTO) fromForm).getInReplyToType());
            if (created != null)
            {
                created.inReplyToId = ((ReplyDiscussionFormDTO) fromForm).inReplyToId;
            }
        }
        else
        {
            created = createEmptyChild(null);
        }

        if (created != null)
        {
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
        }
        return created;
    }
}
