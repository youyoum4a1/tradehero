package com.androidth.general.common.facebook;

import android.support.annotation.NonNull;
import com.facebook.model.GraphUser;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookInvitableFriendGraphUser
{
    @NonNull public final GraphUser graphUser;
    @NonNull public final FacebookGraphPicture picture;

    //<editor-fold desc="Constructors">
    public FacebookInvitableFriendGraphUser(@NonNull GraphUser graphUser) throws JSONException
    {
        this.graphUser = graphUser;
        this.picture = new FacebookGraphPicture((JSONObject) ((JSONObject) graphUser.getProperty("picture")).get("data"));
    }
    //</editor-fold>
}
