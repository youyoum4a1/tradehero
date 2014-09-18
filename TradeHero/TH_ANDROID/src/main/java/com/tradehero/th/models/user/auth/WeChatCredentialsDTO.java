package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.UserFormDTO;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class WeChatCredentialsDTO extends BaseCredentialsDTO
{
    public static final String WECHAT_AUTH_TYPE = "TH-WeChat";

    //<editor-fold desc="Constructors">
    public WeChatCredentialsDTO()
    {
        super();
    }
    //</editor-fold>

    @Override @NotNull public String getAuthType()
    {
        return WECHAT_AUTH_TYPE;
    }

    @Override @NotNull public String getAuthHeaderParameter()
    {
        throw new IllegalStateException("Not Implemented");
    }

    @Override protected void populate(@NotNull JSONObject object) throws JSONException
    {
        super.populate(object);
        // TODO
    }

    @Override @NotNull public UserFormDTO createUserFormDTO()
    {
        return null;
    }
}
