package com.androidth.general.common.facebook;

import com.facebook.FacebookPermissionsConstants;

public class FacebookConstants
{
    public static final String PERMISSION_FRIENDS = FacebookPermissionsConstants.USER_FRIENDS;
    public static final String API_INVITABLE_FRIENDS = "/me/invitable_friends";

    // Example "/me/invitable_friends?fields=name,picture.width(300)"
    public static final String QUERY_KEY_FIELDS = "fields";
    public static final String QUERY_KEY_FIELDS_SEPARATOR = ",";
    public static final String FIELDS_VALUE_USER_NAME = "name";
    public static final String FIELDS_VALUE_USER_PICTURE = "picture";
    public static final String FIELDS_VALUE_USER_PICTURE_WIDTH = ".width(%d)";

    public static final String API_PLAYING_FRIENDS = "/me/friends";
}
