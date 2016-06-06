package com.ayondo.academyapp.api.kyc;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ayondo.academyapp.common.persistence.DTO;
import java.util.Collections;
import java.util.List;

public class StepStatusesDTO implements DTO
{
    @NonNull public final List<StepStatus> stepStatuses;

    public StepStatusesDTO(@NonNull @JsonProperty("stepStatuses") List<StepStatus> stepStatuses)
    {
        this.stepStatuses = Collections.unmodifiableList(stepStatuses);
    }
}
