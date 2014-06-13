package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.QQUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.tencent_qq.QQAuthenticationProvider;
import java.text.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

public class QQCredentialsDTO extends BaseCredentialsDTO
{
    public static final String QQ_AUTH_TYPE = "TH-QQ";

    public final String openId;
    public final String accessToken;

    //<editor-fold desc="Constructors">
    public QQCredentialsDTO(JSONObject object) throws JSONException, ParseException
    {
        this(object.getString(QQAuthenticationProvider.KEY_OPEN_ID),
                object.getString(QQAuthenticationProvider.KEY_ACCESS_TOKEN));
    }

    public QQCredentialsDTO(String openId, String accessToken)
    {
        super();
        this.openId = openId;
        this.accessToken = accessToken;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return QQ_AUTH_TYPE;
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
        object.put(QQAuthenticationProvider.KEY_OPEN_ID, openId);
        object.put(QQAuthenticationProvider.KEY_ACCESS_TOKEN, accessToken);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        QQUserFormDTO userFormDTO = new QQUserFormDTO();
        userFormDTO.accessToken = accessToken;
        userFormDTO.openid = openId;
        return userFormDTO;
    }
}
