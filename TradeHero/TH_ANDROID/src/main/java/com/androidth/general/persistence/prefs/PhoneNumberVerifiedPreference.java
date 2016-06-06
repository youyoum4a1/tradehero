package com.androidth.general.persistence.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.prefs.StringSetPreference;
import java.util.Set;

/**
 * Stores verified numbers on the client.
 * TODO Eventually, ony the server should store verified numbers, and we should remove this class.
 */
public class PhoneNumberVerifiedPreference extends StringSetPreference
{
    public PhoneNumberVerifiedPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            @NonNull Set<String> defaultValue)
    {
        super(preference, key, defaultValue);
    }

    public void addVerifiedNumber(@NonNull String phoneNumber)
    {
        Set<String> numbers = get();
        numbers.add(phoneNumber);
        set(numbers);
    }
}
