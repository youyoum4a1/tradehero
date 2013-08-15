package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.th.application.App;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import java.util.Collection;
import java.util.Collections;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:07 PM Copyright (c) TradeHero */

public class FacebookUtils
{
    private static FacebookAuthenticationProvider provider;
    private static boolean isInitialized = false;

    public static void initialize(String appId)
    {
        provider = new FacebookAuthenticationProvider(App.context(), appId);
        THUser.registerAuthenticationProvider(provider);
        isInitialized = true;
    }

    public static void logIn(Activity activity, LogInCallback callback)
    {
        logIn(null, activity, 32655, callback);
    }

    public static void logIn(Collection<String> permissions, Activity activity, int activityCode,
            LogInCallback callback)
    {
        checkInitialization();
        provider.setActivity(activity);
        provider.setActivityCode(activityCode);
        if (permissions == null)
        {
            permissions = Collections.emptyList();
        }
        provider.setPermissions(permissions);
        THUser.logInWithAsync(provider.getAuthType(), callback);
    }

    private static void checkInitialization()
    {
        if (!isInitialized)
        {
            throw new IllegalStateException(
                    "You must call THFacebookUtils.initialize() before using THFacebookUtils");
        }
    }

    public static void finishAuthentication(int requestCode, int resultCode, Intent data)
    {
        if (provider != null)
            provider.onActivityResult(requestCode, resultCode, data);
    }

    public static final class Permissions
    {
        public static final class Extended
        {
            public static final String READ_FRIEND_LISTS = "read_friendlists";
            public static final String READ_INSIGHTS = "read_insights";
            public static final String READ_MAILBOX = "read_mailbox";
            public static final String READ_REQUESTS = "read_requests";
            public static final String READ_STREAM = "read_stream";
            public static final String XMPP_LOGIN = "xmpp_login";
            public static final String ADS_MANAGEMENT = "ads_management";
            public static final String CREATE_EVENT = "create_event";
            public static final String MANAGE_FRIEND_LISTS = "manage_friendlists";
            public static final String MANAGE_NOTIFICATIONS = "manage_notifications";
            public static final String OFFLINE_ACCESS = "offline_access";
            public static final String PUBLISH_CHECKINS = "publish_checkins";
            public static final String PUBLISH_STREAM = "publish_stream";
            public static final String RSVP_EVENT = "rsvp_event";
            public static final String PUBLISH_ACTIONS = "publish_actions";
        }

        public static final class Friends
        {
            public static final String ABOUT_ME = "friends_about_me";
            public static final String ACTIVITIES = "friends_activities";
            public static final String BIRTHDAY = "friends_birthday";
            public static final String CHECKINS = "friends_checkins";
            public static final String EDUCATION_HISTORY = "friends_education_history";
            public static final String EVENTS = "friends_events";
            public static final String GROUPS = "friends_groups";
            public static final String HOMETOWN = "friends_hometown";
            public static final String INTERESTS = "friends_interests";
            public static final String LIKES = "friends_likes";
            public static final String LOCATION = "friends_location";
            public static final String NOTES = "friends_notes";
            public static final String ONLINE_PRESENCE = "friends_online_presence";
            public static final String PHOTOS = "friends_photos";
            public static final String QUESTIONS = "friends_questions";
            public static final String RELATIONSHIPS = "friends_relationships";
            public static final String RELATIONSHIP_DETAILS = "friends_relationship_details";
            public static final String RELIGION_POLITICS = "friends_religion_politics";
            public static final String STATUS = "friends_status";
            public static final String VIDEOS = "friends_videos";
            public static final String WEBSITE = "friends_website";
            public static final String WORK_HISTORY = "friends_work_history";
        }

        public static final class Page
        {
            public static final String MANAGE_PAGES = "manage_pages";
        }

        public static final class User
        {
            public static final String ABOUT_ME = "user_about_me";
            public static final String ACTIVITIES = "user_activities";
            public static final String BIRTHDAY = "user_birthday";
            public static final String CHECKINS = "user_checkins";
            public static final String EDUCATION_HISTORY = "user_education_history";
            public static final String EVENTS = "user_events";
            public static final String GROUPS = "user_groups";
            public static final String HOMETOWN = "user_hometown";
            public static final String INTERESTS = "user_interests";
            public static final String LIKES = "user_likes";
            public static final String LOCATION = "user_location";
            public static final String NOTES = "user_notes";
            public static final String ONLINE_PRESENCE = "user_online_presence";
            public static final String PHOTOS = "user_photos";
            public static final String QUESTIONS = "user_questions";
            public static final String RELATIONSHIPS = "user_relationships";
            public static final String RELATIONSHIP_DETAILS = "user_relationship_details";
            public static final String RELIGION_POLITICS = "user_religion_politics";
            public static final String STATUS = "user_status";
            public static final String VIDEOS = "user_videos";
            public static final String WEBSITE = "user_website";
            public static final String WORK_HISTORY = "user_work_history";
            public static final String EMAIL = "email";
        }
    }
}
