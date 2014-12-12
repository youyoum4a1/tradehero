package com.tradehero.th.utils.broadcast;

import android.content.IntentFilter;

public class BroadcastConstants
{
    public static final String KEY_USER_ACHIEVEMENT_ID = BroadcastConstants.class.getName() + ".achievementId";
    public static final String KEY_ACHIEVEMENT_NODE = "achievements";
    public static final String ACHIEVEMENT_INTENT_ACTION_NAME = "com.tradehero.th.achievement.ALERT";
    public static final IntentFilter ACHIEVEMENT_INTENT_FILTER = new IntentFilter(ACHIEVEMENT_INTENT_ACTION_NAME);

    public static final String XP_INTENT_ACTION_NAME = "com.tradehero.th.level.xp.ALERT";
    public static final String KEY_XP_BROADCAST = BroadcastConstants.class.getName()+".xpBroadcast";
    public static final String KEY_XP_NODE = "xpEarned";
    public static final IntentFilter XP_INTENT_FILTER = new IntentFilter(XP_INTENT_ACTION_NAME);

    private static final String ON_BOARD_INTENT_ACTION_NAME = "com.tradehero.th.onboard.ALERT";
    private static final String KEY_ON_BOARD_BROADCAST = BroadcastConstants.class.getName()+".onboardBroadcast";
    public static final IntentFilter ONBOARD_INTENT_FILTER = new IntentFilter(ON_BOARD_INTENT_ACTION_NAME);
    public static final BroadcastData ON_BOARDING_BROADCAST_DATA = ImmutableBroadcastData.create(KEY_ON_BOARD_BROADCAST, ON_BOARD_INTENT_ACTION_NAME);

    private static final String ENROLLMENT_INTENT_ACTION_NAME = "com.tradehero.th.enrollment.ALERT";
    private static final String KEY_ENROLLMENT_BROADCAST = BroadcastConstants.class.getName()+".enrollmentBroadcast";
    public static final IntentFilter ENROLLMENT_INTENT_FILTER = new IntentFilter(ENROLLMENT_INTENT_ACTION_NAME);
    public static final BroadcastData COMPETITION_ENROLLMENT_BROADCAST_DATA = ImmutableBroadcastData.create(KEY_ENROLLMENT_BROADCAST, ENROLLMENT_INTENT_ACTION_NAME);

    private static final String SEND_LOVE_INTENT_ACTION_NAME = "com.tradehero.th.setting.sendlove.ALERT";
    private static final String KEY_SEND_LOVE_BROADCAST = BroadcastConstants.class.getName()+".sendLoveBroadcast";
    public static final IntentFilter SEND_LOVE_INTENT_FILTER = new IntentFilter(SEND_LOVE_INTENT_ACTION_NAME);
    public static final BroadcastData SEND_LOVE_BROADCAST_DATA = ImmutableBroadcastData.create(KEY_SEND_LOVE_BROADCAST, SEND_LOVE_INTENT_ACTION_NAME);

    private static final String FX_ONBOARD_INTENT_ACTION_NAME = "com.tradehero.th.fxonboard.ALERT";
    private static final String KEY_FX_ONBOARD_BROADCAST = BroadcastConstants.class.getName()+".sendFxOnboardBroadcast";
    public static final IntentFilter FX_ONBOARD_INTENT_FILTER = new IntentFilter(FX_ONBOARD_INTENT_ACTION_NAME);
    public static final BroadcastData FX_ONBOARD_BROADCAST_DATA =
            ImmutableBroadcastData.create(KEY_FX_ONBOARD_BROADCAST, FX_ONBOARD_INTENT_ACTION_NAME);

    private BroadcastConstants() {} // No sub class
}
