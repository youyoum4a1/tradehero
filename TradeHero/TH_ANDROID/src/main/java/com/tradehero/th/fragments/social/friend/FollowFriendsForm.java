package com.tradehero.th.fragments.social.friend;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.api.social.UserFriendsDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by tradehero on 14-5-26.
 * <p/>
 * {userIds:[1,2,3]}
 */
public class FollowFriendsForm {

    public List<UserFriendsDTO> userFriendsDTOs;

    @JsonValue
    public String getDescription() {

        JSONObject jsonObject = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();
            int size = userFriendsDTOs.size();
            for (int i = 0; i < size; i++) {
                jsonArray.put(userFriendsDTOs.get(i).thUserId);
            }
            jsonObject.put("userIds", jsonArray);
        } catch (JSONException e) {

        }
        return jsonObject.toString();
    }
}
