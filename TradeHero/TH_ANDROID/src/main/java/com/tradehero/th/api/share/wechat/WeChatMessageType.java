package com.ayondo.academy.api.share.wechat;

import android.support.annotation.StringRes;
import com.ayondo.academy.R;

public enum WeChatMessageType
{
    News(1, R.string.share_to_wechat_timeline_news),
    CreateDiscussion(2, R.string.share_to_wechat_timeline_create_discussion),
    Discussion(3, R.string.share_to_wechat_timeline_discussion),
    Timeline(4, R.string.share_to_wechat_timeline_timeline),
    Trade(5, R.string.share_to_wechat_timeline_trade),
    Invite(6, R.string.share_to_wechat_invite_friends),
    Achievement(7, R.string.share_to_wechat_achievement),
    QuestBonus(8, R.string.share_to_wechat_quest_bonus),
    PreSeason(9, R.string.share_to_wechat_preseason),
    Referral(10, R.string.share_to_wechat_referral);

    private final int value;
    @StringRes private final int titleResId;

    private WeChatMessageType(int value, @StringRes int titleResId)
    {
        this.value = value;
        this.titleResId = titleResId;
    }

    @StringRes public int getTitleResId()
    {
        return titleResId;
    }

    public int getValue()
    {
        return value;
    }

    public static WeChatMessageType fromValue(int value)
    {
        for (WeChatMessageType wxType: values())
        {
            if (wxType.value == value)
            {
                return wxType;
            }
        }
        return null;
    }
}
