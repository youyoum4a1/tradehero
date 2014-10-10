package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.WechatUserFormDTO;
import com.tradehero.th.auth.wechat.WechatAuthenticationProvider;
import java.text.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

public class WeChatCredentialsDTO extends BaseCredentialsDTO
{
    public static final String WECHAT_AUTH_TYPE = "TH-WeChat";

    public final String openId;
    public final String accessToken;

    //<editor-fold desc="Constructors">
    public WeChatCredentialsDTO(JSONObject object) throws JSONException, ParseException
    {
        this(object.getString(WechatAuthenticationProvider.KEY_OPEN_ID),
                object.getString(WechatAuthenticationProvider.KEY_ACCESS_TOKEN));
    }

    public WeChatCredentialsDTO(String openId, String accessToken)
    {
        super();
        this.openId = openId;
        this.accessToken = accessToken;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return WECHAT_AUTH_TYPE;
    }

    @Override public String getAuthHeaderParameter()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(openId).append(":").append(accessToken);
        return sb.toString();
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(WechatAuthenticationProvider.KEY_OPEN_ID, openId);
        object.put(WechatAuthenticationProvider.KEY_ACCESS_TOKEN, accessToken);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        WechatUserFormDTO userFormDTO = new WechatUserFormDTO();
        userFormDTO.accessToken = accessToken;
        userFormDTO.openid = openId;
        return userFormDTO;
    }
}
