package com.androidth.general.models.fastfill;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Gender
{
    MALE(R.string.live_title_mr, 1),
    FEMALE(R.string.live_title_ms, 2),
    ;

    public static final Map<Integer, Gender> filedGenders;
    @StringRes public final int dropDownText;
    public final int fromServer;

    Gender(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static Gender getGender(int fromServer)
    {
        Gender candidate = filedGenders.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any Gender");
        }
        return candidate;
    }

    static
    {
        Map<Integer, Gender> map = new HashMap<>();
        for (Gender candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedGenders = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
