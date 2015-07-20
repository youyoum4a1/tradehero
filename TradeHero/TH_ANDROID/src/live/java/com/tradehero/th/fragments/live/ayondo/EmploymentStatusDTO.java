package com.tradehero.th.fragments.live.ayondo;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.api.kyc.EmploymentStatus;
import java.util.ArrayList;
import java.util.List;

class EmploymentStatusDTO
{
    @NonNull public final EmploymentStatus employmentStatus;
    @NonNull public final String text;

    public EmploymentStatusDTO(@NonNull Resources resources, @NonNull EmploymentStatus employmentStatus)
    {
        this(employmentStatus, resources.getString(employmentStatus.dropDownText));
    }

    public EmploymentStatusDTO(@NonNull EmploymentStatus employmentStatus, @NonNull String text)
    {
        this.employmentStatus = employmentStatus;
        this.text = text;
    }

    @Override public String toString()
    {
        return text;
    }

    @NonNull static List<EmploymentStatusDTO> createList(@NonNull Resources resources, @NonNull List<EmploymentStatus> employmentStatuses)
    {
        List<EmploymentStatusDTO> created = new ArrayList<>();
        for (EmploymentStatus employmentStatus : employmentStatuses)
        {
            created.add(new EmploymentStatusDTO(resources, employmentStatus));
        }
        return created;
    }
}
