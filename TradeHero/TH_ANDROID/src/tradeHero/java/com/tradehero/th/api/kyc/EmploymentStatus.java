package com.androidth.general.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EmploymentStatus
{
    EMPTY(R.string.employment_status_empty, "..."),
    EMPLOYED(R.string.employment_status_employed, "Employed"),
    SELFEMPLOYED(R.string.employment_status_self_employed, "Self-Employed"),
    UNEMPLOYED(R.string.employment_status_unemployed, "Unemployed"),
    RETIRED(R.string.employment_status_retired, "Retired"),
    STUDENT(R.string.employment_status_student, "Student"),
    OTHER(R.string.employment_status_other, "Other");

    private static final Map<String, EmploymentStatus> filedEmploymentStatuses;

    @StringRes public final int dropDownText;
    @NonNull private final String fromServer;

    EmploymentStatus(@StringRes int dropDownText, @NonNull String fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static EmploymentStatus getEmploymentStatus(@NonNull String fromServer)
    {
        EmploymentStatus candidate = filedEmploymentStatuses.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any EmploymentStatus");
        }
        return candidate;
    }

    static
    {
        Map<String, EmploymentStatus> map = new HashMap<>();
        for (EmploymentStatus candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedEmploymentStatuses = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue @NonNull String getFromServerCode()
    {
        return fromServer;
    }
}
