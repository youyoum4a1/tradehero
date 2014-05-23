package com.tradehero.th.models.push.baidu;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.tradehero.th.api.discussion.DiscussionType;
import org.json.JSONObject;
import timber.log.Timber;

public class PushMessageHandler
{
    public static final String BROADCAST_ACTION_MESSAGE_RECEIVED = "com.tradehero.th.ACTION_MESSAGE_RECEIVED";

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CUSTOM_CONTENT = "custom_content";
    public static final String KEY_CUSTOM_TYPE = "discussion-type";
    public static final String KEY_CUSTOM_ID = "i";

    //{"title":"Notification","description":"NeerajAhuja sent you a message: yyuhgfg！","custom_content": {"i":"8142167", "discussion-type":"6"}}
    // {"title":"Notification","description":"hero2 just bought 2 shares of Google Inc (NASDAQ:GOOGL) @ US$533.0000","custom_content": {"i":"8438430", "discussion-type":""}}, customContentString: null
    public static PushMessageDTO parseNotification(String content)
    {
        try
        {
            JSONObject object = new JSONObject(content);
            String title = object.getString(KEY_TITLE);
            String description = object.getString(KEY_DESCRIPTION);
            JSONObject customObj = object.getJSONObject(KEY_CUSTOM_CONTENT);
            String typeString = customObj.getString(KEY_CUSTOM_TYPE);
            DiscussionType discussionType = null;
            if (!TextUtils.isEmpty(typeString))
            {
                int type = Integer.parseInt(customObj.getString(KEY_CUSTOM_TYPE));
                discussionType = DiscussionType.fromValue(type);
            }

            int id = Integer.parseInt(customObj.getString(KEY_CUSTOM_ID));
            return new PushMessageDTO(title, description, discussionType, id);
        }
        catch (Exception e)
        {
            Timber.e("parseNotification error",e);
        }
        return null;
    }

    public static void notifyMessageReceived(Context context)
    {
        Intent requestUpdateIntent = new Intent(BROADCAST_ACTION_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(requestUpdateIntent);
    }
}
