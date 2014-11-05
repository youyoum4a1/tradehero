package com.tradehero.th.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import android.support.annotation.NonNull;
import timber.log.Timber;

public class ShortcutUtil
{
    public static void recreateShortcut(@NonNull Activity activity)
    {
        try
        {
            if (isInstallShortcut(activity))
            {
                removeShortcut(activity);
            }
            createShortcut(activity);
        } catch (SecurityException e)
        {
            Timber.e(e, null);
        }
    }

    private static void createShortcut(@NonNull Activity activity)
    {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), R.drawable.launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, createLaunchIntent(activity));
        activity.sendBroadcast(intent);
    }

    private static Intent createLaunchIntent(@NonNull Activity activity)
    {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setComponent(new ComponentName(activity.getPackageName(), AuthenticationActivity.class.getName()));
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return launchIntent;
    }

    private static boolean isInstallShortcut(@NonNull Activity activity)
    {
        String name = activity.getString(R.string.app_name);
        boolean isInstallShortcut = false;
        final ContentResolver cr = activity.getContentResolver();
        String AUTHORITY = "com.android.launcher.settings";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");

        Cursor c = cr.query(CONTENT_URI, new String[] {"title", "iconResource"},
                "title=?", new String[] {name}, null);

        if (c != null && c.getCount() > 0)
        {
            isInstallShortcut = true;
        }
        printShortcutName(c);

        if (c != null)
        {
            c.close();
        }

        if (isInstallShortcut)
        {
            return isInstallShortcut;
        }

        AUTHORITY = "com.android.launcher2.settings";
        CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        c = cr.query(CONTENT_URI, new String[] {"title", "iconResource"},
                "title=?", new String[] {name}, null);

        if (c != null && c.getCount() > 0)
        {
            isInstallShortcut = true;
        }
        printShortcutName(c);

        return isInstallShortcut;
    }

    private static void printShortcutName(Cursor c)
    {
        if (!BuildConfig.DEBUG)
        {
            return;
        }

        if (c == null || c.getCount() <= 0)
        {
            return;
        }

        String name = "title";
        c.moveToFirst();
        int index = c.getColumnIndex(name);
        if (index == -1)
        {
            return;
        }
        c.moveToPrevious();
        while (c.moveToNext())
        {
            String title = c.getString(index);
            Timber.d("Shortcut title:%s", title);
        }
    }

    private static void removeShortcut(@NonNull Context applicationContext)
    {
        Intent shortcutIntent = new Intent(applicationContext, AuthenticationActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, applicationContext.getString(R.string.app_name));

        addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        applicationContext.sendBroadcast(addIntent);
    }
}
