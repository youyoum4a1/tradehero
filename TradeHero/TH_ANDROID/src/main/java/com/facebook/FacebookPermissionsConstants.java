package com.facebook;

import java.util.Arrays;
import java.util.List;

public class FacebookPermissionsConstants
{
    // https://developers.facebook.com/docs/facebook-login/permissions/v2.2#reference
    public static final String PUBLIC_PROFILE = "public_profile";

    public static final String PUBLISH_STREAM = "publish_stream";
    public static final String PUBLISH_ACTIONS = "publish_actions";

    /**
     * To ask for the friends that use the app, with /{user-id}/friends
     */
    public static final String USER_FRIENDS = "user_friends";

    //TODO use these: user_likes, birthday, email, user_friends, publish_actions

    /**
     * Provides access to the names of custom lists a person has created to organize their friends.
     * This is useful for rendering an audience selector when someone is publishing stories to Facebook from your app.
     */
    public static final String READ_FRIEND_LISTS = "read_friendlists";

    public static final String EMAIL = "email";
    public static final String PUBLISH_WALL_FRIEND = "publish_actions";

    public static List<String> permissions = Arrays.asList(PUBLIC_PROFILE, PUBLISH_ACTIONS);
}
