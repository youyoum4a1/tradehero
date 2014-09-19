package com.tradehero.th.models.user.auth;

import com.tradehero.th.api.form.DeviceUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.DeviceAuthenticationProvider;
import java.text.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceCredentialsDTO extends BaseCredentialsDTO
{
    public static final String DEVICE_AUTH_TYPE = "TH-Device";

    public final String deviceAccessToken;

    //<editor-fold desc="Constructors">
    public DeviceCredentialsDTO(JSONObject object) throws JSONException, ParseException
    {
        this(object.getString(DeviceAuthenticationProvider.KEY_ACCESS_TOKEN));
    }

    public DeviceCredentialsDTO(String deviceAccessToken)
    {
        super();
        this.deviceAccessToken = deviceAccessToken;
    }
    //</editor-fold>

    @Override public String getAuthType()
    {
        return DEVICE_AUTH_TYPE;
    }

    @Override public String getAuthHeaderParameter()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(deviceAccessToken);
        return sb.toString();
    }

    @Override protected void populate(JSONObject object) throws JSONException
    {
        super.populate(object);
        object.put(DeviceAuthenticationProvider.KEY_ACCESS_TOKEN, deviceAccessToken);
    }

    @Override public UserFormDTO createUserFormDTO()
    {
        DeviceUserFormDTO userFormDTO = new DeviceUserFormDTO();
        userFormDTO.deviceAccessToken = deviceAccessToken;
        return userFormDTO;
    }
}
