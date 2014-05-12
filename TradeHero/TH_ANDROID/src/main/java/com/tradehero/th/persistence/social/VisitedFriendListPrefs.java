package com.tradehero.th.persistence.social;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.Application;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import timber.log.Timber;

public class VisitedFriendListPrefs
{
    public static final String TAG = VisitedFriendListPrefs.class.getSimpleName();
    public static final String KEY_PREFS = VisitedFriendListPrefs.class.getName();
    public static final String KEY_VISITED_ID_SET = VisitedFriendListPrefs.class.getName() + ".VISITED_ID_SET";

    @Inject static CurrentUserId currentUserId;

    public static void addVisitedId(UserBaseKey userBaseKey)
    {
        if (userBaseKey == null)
        {
            return;
        }
        if (userBaseKey.equals(currentUserId.toUserBaseKey()))
        {
            return;
        }
        // We cannot modify the returned Set, so we have to go the long route
        List<UserBaseKey> userBaseKeys = getVisitedIdList();
        userBaseKeys.add(userBaseKey); // TODO add at head
        saveVisitedIdList(userBaseKeys);
    }

    private static void saveVisitedIdList(final List<UserBaseKey> userBaseKeys)
    {
        Set<String> userKeys = new TreeSet<>();
        if (userBaseKeys != null)
        {
            for (UserBaseKey userBaseKey: userBaseKeys)
            {
                if (userBaseKey.key != null && userBaseKey.key > 0)
                {
                    userKeys.add(userBaseKey.key.toString());
                }
            }
        }

        SharedPreferences.Editor pref = Application.context().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE).edit();
        pref.putStringSet(KEY_VISITED_ID_SET, userKeys);
        pref.commit();
    }

    public static List<UserBaseKey> getVisitedIdList()
    {
        final SharedPreferences preferences = Application.context().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        final Set<String> userKeys = preferences.getStringSet(KEY_VISITED_ID_SET, new TreeSet<String>());

        final List<UserBaseKey> userBaseKeys = new ArrayList<>();
        for (String userKey: userKeys)
        {
            int parsedInt;
            try
            {
                parsedInt = Integer.parseInt(userKey);
                if (parsedInt > 0)
                {
                    userBaseKeys.add(new UserBaseKey(parsedInt));
                }
                else
                {
                    Timber.d("A userKey was not positive %s", userKey);
                }
            }
            catch (NumberFormatException e)
            {
                Timber.e(e, "There was a bad userKey %s", userKey);
            }
        }

        // Potentially resaved sanitised list
        if (userBaseKeys.size() != userKeys.size())
        {
            saveVisitedIdList(userBaseKeys);
        }

        return userBaseKeys;
    }

    public static void clearVisitedIdList()
    {
        SharedPreferences.Editor pref = Application.context().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE).edit();
        pref.putStringSet(KEY_VISITED_ID_SET, new TreeSet<String>());
        pref.commit();
    }
}
