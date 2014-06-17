package com.tradehero.th.api.share.wechat;

import android.os.Bundle;
import com.tradehero.th.api.share.SocialShareFormDTO;
import org.jetbrains.annotations.NotNull;

public class WeChatDTO implements SocialShareFormDTO
{
    public static final String WECHAT_MESSAGE_ID_KEY = "wechat_message_id_key";
    public static final String WECHAT_MESSAGE_TYPE_KEY = "wechat_message_type_key";
    public static final String WECHAT_MESSAGE_IMAGE_URL_KEY = "wechat_message_image_url_key";
    public static final String WECHAT_MESSAGE_TITLE_KEY = "wechat_message_title_key";

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

    public WeChatDTO(@NotNull Bundle args)
    {
        id = args.getInt(WECHAT_MESSAGE_ID_KEY);
        if (args.containsKey(WECHAT_MESSAGE_TYPE_KEY))
        {
            type = WeChatMessageType.fromValue(args.getInt(WECHAT_MESSAGE_TYPE_KEY));
        }
        title = args.getString(WECHAT_MESSAGE_TITLE_KEY);
        imageURL = args.getString(WECHAT_MESSAGE_IMAGE_URL_KEY);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "WeChatDTO{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        populate(args);
        return args;
    }

    protected void populate(@NotNull Bundle args)
    {
        args.putInt(WECHAT_MESSAGE_ID_KEY, id);
        if (type != null)
        {
            args.putInt(WECHAT_MESSAGE_TYPE_KEY, type.getValue());
        }
        else
        {
            args.remove(WECHAT_MESSAGE_TYPE_KEY);
        }
        args.putString(WECHAT_MESSAGE_TITLE_KEY, title);
        args.putString(WECHAT_MESSAGE_IMAGE_URL_KEY, imageURL);
    }

    // TODO make a put to Bundle method to ease passage to WxEntryActivity
}
