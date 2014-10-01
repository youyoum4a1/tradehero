package com.tradehero.th.base;

import com.tradehero.th.api.form.UserFormDTO;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.mime.TypedOutput;

public class JSONCredentials extends JSONObject
{
    public TypedOutput profilePicture;

    public JSONCredentials()
    {
        super();
    }

    public JSONCredentials(String source) throws JSONException
    {
        super(source);
    }

    public JSONCredentials(Map map)
    {
        super(map);
        putProfilePicture(map);
    }

    public void putProfilePicture(Map<String, Object> map)
    {
        if (map != null && map.containsKey(UserFormDTO.KEY_PROFILE_PICTURE))
        {
            profilePicture = (TypedOutput) map.get(UserFormDTO.KEY_PROFILE_PICTURE);
        }
    }
}
