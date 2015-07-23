package com.tradehero.th.api.kyc;

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
    EMPLOYED(R.string.employment_status_employed, 1),
    SELFEMPLOYED(R.string.employment_status_self_employed, 2),
    UNEMPLOYED(R.string.employment_status_unemployed, 3),
    RETIRED(R.string.employment_status_retired, 4),
    STUDENT(R.string.employment_status_student, 5),;

    private static final Map<Integer, EmploymentStatus> filedEmploymentStatuses;

    @StringRes public final int dropDownText;
    private final int fromServer;

    EmploymentStatus(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static EmploymentStatus getEmploymentStatus(int fromServer)
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
        Map<Integer, EmploymentStatus> map = new HashMap<>();
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
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
