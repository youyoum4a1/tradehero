package com.androidth.general.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import com.androidth.general.models.fastfill.Gender;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AyondoGender
{
    MALE(R.string.live_title_mr, "Male", Gender.MALE),
    FEMALE(R.string.live_title_ms, "Female", Gender.FEMALE);

    public static final Map<String, AyondoGender> filedAyondoGendersPerServerCode;
    public static final Map<Gender, AyondoGender> filedAyondoGendersPerGender;
    @StringRes public final int dropDownText;
    @NonNull public final String fromServer;
    @NonNull public final Gender gender;

    AyondoGender(@StringRes int dropDownText, @NonNull String fromServer, @NonNull Gender gender)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
        this.gender = gender;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static AyondoGender getAyondoGender(@NonNull String fromServer)
    {
        AyondoGender candidate = filedAyondoGendersPerServerCode.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any AyondoGender");
        }
        return candidate;
    }

    static
    {
        Map<String, AyondoGender> codeMap = new HashMap<>();
        Map<Gender, AyondoGender> genderMap = new HashMap<>();
        for (AyondoGender candidate : values())
        {
            if (codeMap.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            codeMap.put(candidate.fromServer, candidate);

            if (genderMap.get(candidate.gender) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.gender + "' a second time");
            }
            genderMap.put(candidate.gender, candidate);
        }
        filedAyondoGendersPerServerCode = Collections.unmodifiableMap(codeMap);
        filedAyondoGendersPerGender = Collections.unmodifiableMap(genderMap);
    }

    @NonNull public static AyondoGender getAyondoGender(@NonNull Gender gender)
    {
        AyondoGender candidate = filedAyondoGendersPerGender.get(gender);
        if (candidate == null)
        {
            throw new IllegalArgumentException(gender + " does not match any AyondoGender");
        }
        return candidate;
    }

    @SuppressWarnings("unused")
    @JsonValue @NonNull @Override public String toString()
    {
        return fromServer;
    }
}
