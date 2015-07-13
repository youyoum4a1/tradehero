package com.tradehero.th.api.kyc;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EmploymentStatus
{
    EMPLOYED(R.string.employment_status_employed, "a"),
    SELFEMPLOYED(R.string.employment_status_self_employed, "b"),
    UNEMPLOYED(R.string.employment_status_unemployed, "c"),
    RETIRED(R.string.employment_status_retired, "d"),
    STUDENT(R.string.employment_status_student, "e"),;

    public static final Map<String, EmploymentStatus> filedEmploymentStatuses;

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

    @NonNull public static List<String> createTexts(@NonNull Resources resources, @NonNull Collection<EmploymentStatus> employmentStatuses)
    {
        List<String> created = new ArrayList<>();
        for (EmploymentStatus employmentStatus : employmentStatuses)
        {
            created.add(resources.getString(employmentStatus.dropDownText));
        }
        return created;
    }
}
