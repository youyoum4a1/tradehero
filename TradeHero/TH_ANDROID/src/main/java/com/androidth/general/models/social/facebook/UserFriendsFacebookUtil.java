//package com.androidth.general.models.social.facebook;
//Jeff removed this class
//import android.support.annotation.NonNull;
//import com.androidth.general.common.facebook.FacebookInvitableFriendGraphUser;
//import com.androidth.general.api.social.UserFriendsDTOList;
//import com.androidth.general.api.social.UserFriendsFacebookDTO;
//import org.json.JSONException;
//import timber.log.Timber;
//
//public class UserFriendsFacebookUtil
//{
////    @NonNull public static UserFriendsDTOList convert(@NonNull Response response)
////    {
////        return UserFriendsFacebookUtil.convert(
////                response.getGraphObjectAs(GraphObject.class)
////                        .getPropertyAsList("data", GraphUser.class));
////    }
////
////    @NonNull public static UserFriendsDTOList convert(
////            @NonNull GraphObjectList<GraphUser> facebookGraphUsers)
////    {
////        UserFriendsDTOList converted = new UserFriendsDTOList();
////        for (GraphUser graphUser : facebookGraphUsers)
////        {
////            try
////            {
////                converted.add(create(new FacebookInvitableFriendGraphUser(graphUser)));
////            } catch (JSONException e)
////            {
////                Timber.e(e, "Failed to parse a graphUser: " + graphUser);
////            }
////        }
////
////        return converted;
////    }
////
////    @NonNull public static UserFriendsFacebookDTO create(@NonNull FacebookInvitableFriendGraphUser invitableFriendGraphUser)
////    {
////        return new UserFriendsFacebookDTO(
////                invitableFriendGraphUser.graphUser.getId(),
////                invitableFriendGraphUser.picture.url,
////                invitableFriendGraphUser.graphUser.getName());
////    }
//}
