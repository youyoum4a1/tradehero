package com.tradehero.th.api.share.wechat;

import com.tradehero.th.api.share.SocialShareFormDTO;

public class WeChatDTO implements SocialShareFormDTO
{
    public int id;
    public WeChatMessageType type;
    public String title;
    public String imageURL;

    //<editor-fold desc="Constructors">
    public WeChatDTO()
    {
    }

    public WeChatDTO(int id, WeChatMessageType type, String title)
    {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public WeChatDTO(int id, WeChatMessageType type, String title, String imageURL)
    {
        this.id = id;
        this.type = type;
        this.title = title;
        this.imageURL = imageURL;
    }

    @Override public String toString()
    {
        return "WeChatDTO{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
    //</editor-fold>

    // TODO make a put to Bundle method to ease passage to WxEntryActivity
}
