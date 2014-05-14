package com.tradehero.th.models.push.baidu;

class BaiduDeviceMode
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
        this.token = channelId + "-" + userId;
    }
}
