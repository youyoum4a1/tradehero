package com.ayondo.academy.models.social.facebook;

import android.support.annotation.NonNull;
import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.tradehero.common.social.facebook.FacebookInvitableFriendGraphUser;
import com.ayondo.academy.api.social.UserFriendsDTOList;
import com.ayondo.academy.api.social.UserFriendsFacebookDTO;
import org.json.JSONException;
import timber.log.Timber;

public class UserFriendsFacebookUtil
{
    @NonNull public static UserFriendsDTOList convert(@NonNull Response response)
    {
        return UserFriendsFacebookUtil.convert(
                response.getGraphObjectAs(GraphObject.class)
                        .getPropertyAsList("data", GraphUser.class));
    }

    @NonNull public static UserFriendsDTOList convert(
            @NonNull GraphObjectList<GraphUser> facebookGraphUsers)
    {
        UserFriendsDTOList converted = new UserFriendsDTOList();
        for (GraphUser graphUser : facebookGraphUsers)
        {
            try
            {
                converted.add(create(new FacebookInvitableFriendGraphUser(graphUser)));
            } catch (JSONException e)
            {
                Timber.e(e, "Failed to parse a graphUser: " + graphUser);
            }
        }

        return converted;
    }

    @NonNull public static UserFriendsFacebookDTO create(@NonNull FacebookInvitableFriendGraphUser invitableFriendGraphUser)
    {
        return new UserFriendsFacebookDTO(
                invitableFriendGraphUser.graphUser.getId(),
                invitableFriendGraphUser.picture.url,
                invitableFriendGraphUser.graphUser.getName());
    }
}
