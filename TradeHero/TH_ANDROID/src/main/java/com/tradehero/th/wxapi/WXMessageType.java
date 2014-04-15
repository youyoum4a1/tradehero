package com.tradehero.th.wxapi;

import com.tradehero.th.R;

/**
 * Created by alex on 14-4-4.
 */
public enum WXMessageType
{
    News(1, R.string.share_to_wechat_timeline_news),
    CreateDiscussion(2, R.string.share_to_wechat_timeline_create_discussion),
    Discussion(3, R.string.share_to_wechat_timeline_discussion),
    Timeline(4, R.string.share_to_wechat_timeline_timeline),
    Trade(5, R.string.share_to_wechat_timeline_trade);

    private final int type;
    private final int titleResId;

    WXMessageType(int type, int titleResId)
    {
        this.type = type;
        this.titleResId = titleResId;
    }

    public int getTitleResId()
    {
        return titleResId;
    }

    public int getType()
    {
        return type;
    }

    static WXMessageType fromType(int type)
    {
        for (WXMessageType wxType: values())
        {
            if (wxType.type == type)
            {
                return wxType;
            }
        }
        return null;
    }
}
