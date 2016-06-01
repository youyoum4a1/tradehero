package com.ayondo.academy.utils.broadcast;

import android.content.IntentFilter;

public final class BroadcastConstants
{
    public static final String KEY_USER_ACHIEVEMENT_ID = BroadcastConstants.class.getName() + ".achievementId";
    public static final String KEY_ACHIEVEMENT_NODE = "achievements";
    public static final String ACHIEVEMENT_INTENT_ACTION_NAME = "com.ayondo.academy.achievement.ALERT";
    public static final IntentFilter ACHIEVEMENT_INTENT_FILTER = new IntentFilter(ACHIEVEMENT_INTENT_ACTION_NAME);

    public static final String XP_INTENT_ACTION_NAME = "com.ayondo.academy.level.xp.ALERT";
    public static final String KEY_XP_BROADCAST = BroadcastConstants.class.getName()+".xpBroadcast";
    public static final String KEY_XP_NODE = "xpEarned";
    public static final IntentFilter XP_INTENT_FILTER = new IntentFilter(XP_INTENT_ACTION_NAME);

    public static final String ON_BOARD_INTENT_ACTION_NAME = "com.ayondo.academy.onboard.ALERT";
    public static final String KEY_ON_BOARD_BROADCAST = BroadcastConstants.class.getName()+".onboardBroadcast";
    public static final IntentFilter ONBOARD_INTENT_FILTER = new IntentFilter(ON_BOARD_INTENT_ACTION_NAME);

    public static final String ENROLLMENT_INTENT_ACTION_NAME = "com.ayondo.academy.enrollment.ALERT";
    public static final String KEY_ENROLLMENT_BROADCAST = BroadcastConstants.class.getName()+".enrollmentBroadcast";
    public static final IntentFilter ENROLLMENT_INTENT_FILTER = new IntentFilter(ENROLLMENT_INTENT_ACTION_NAME);

    public static final String SEND_LOVE_INTENT_ACTION_NAME = "com.ayondo.academy.setting.sendlove.ALERT";
    public static final String KEY_SEND_LOVE_BROADCAST = BroadcastConstants.class.getName()+".sendLoveBroadcast";
    public static final IntentFilter SEND_LOVE_INTENT_FILTER = new IntentFilter(SEND_LOVE_INTENT_ACTION_NAME);
}
