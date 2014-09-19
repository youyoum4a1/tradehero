package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.QQUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.tencent_qq.QQAuthenticationProvider;
import java.text.ParseException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class QQCredentialsDTO extends BaseCredentialsDTO
{
    public static final String QQ_AUTH_TYPE = "TH-QQ";

    @NotNull public final String openId;
    @NotNull public final String accessToken;

    //<editor-fold desc="Constructors">
    public QQCredentialsDTO(@NotNull JSONObject object) throws JSONException, ParseException
    {
        this(object.getString(QQAuthenticationProvider.KEY_OPEN_ID),
                object.getString(QQAuthenticationProvider.KEY_ACCESS_TOKEN));
    }

    public QQCredentialsDTO(@NotNull String openId, @NotNull String accessToken)
    {
        super();
        this.openId = openId;
        this.accessToken = accessToken;
    }
    //</editor-fold>

    @Override @NotNull public String getAuthType()
    {
        return QQ_AUTH_TYPE;
    }

    @Override @NotNull public String getAuthHeaderParameter()
    {
        return String.format("%1$s:%2$s", openId, accessToken);
    }

    @Override protected void populate(@NotNull JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(QQAuthenticationProvider.KEY_OPEN_ID, openId);
        object.put(QQAuthenticationProvider.KEY_ACCESS_TOKEN, accessToken);
    }

    @Override @NotNull public UserFormDTO createUserFormDTO()
    {
        QQUserFormDTO userFormDTO = new QQUserFormDTO();
        userFormDTO.accessToken = accessToken;
        userFormDTO.openid = openId;
        return userFormDTO;
    }
}
