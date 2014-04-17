package com.tradehero.th.models.push.baidu;

/**
 * Created by wangliang on 14-4-16.
 */
public class BaiduDeviceMode
{
    public final String channelId;
    public final String userId;
    public final String appId;

    public String token;

    public BaiduDeviceMode(String channelId, String userId, String appId)
    {
        this.channelId = channelId;
        this.userId = userId;
        this.appId = appId;
        processToken();
    }

    private void processToken()
    {
        this.token = channelId + "-" + userId;
    }
}
