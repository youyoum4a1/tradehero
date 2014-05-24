package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class WeChatCredentialsDTO extends BaseCredentialsDTO
{
    public static final String WECHAT_AUTH_TYPE = "TH-Wehat";

    public WeChatCredentialsDTO()
    {
        super();
    }

    @Override public String getAuthType()
    {
        return WECHAT_AUTH_TYPE;
    }

    @Override public String getAuthHeaderParameter()
    {
        throw new IllegalStateException("Not Implemented");
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        // TODO
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        return null;
    }
}
