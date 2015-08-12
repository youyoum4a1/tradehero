package com.tradehero.hengsheng;

import org.json.JSONObject;

/**
 * Created by palmer on 15/8/11.
 */
public class HengShengJSONUtils {

    public static String getAccessToken(String jsonStr){
        String accessToken = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if(jsonObject.has("access_token")){
                accessToken = jsonObject.getString("access_token");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return accessToken;
    }
}
