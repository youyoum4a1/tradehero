package com.ayondo.academyapp.api.kyc;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum StepStatus
{
    UNSTARTED(1),
    COMPLETE(2),
    ;

    private static final Map<Integer, StepStatus> filedStepStatuses;

    public final int fromServer;

    StepStatus(int fromServer)
    {
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static StepStatus getStepStatus(int fromServer)
    {
        StepStatus candidate = filedStepStatuses.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any StepStatus");
        }
        return candidate;
    }

    static
    {
        Map<Integer, StepStatus> map = new HashMap<>();
        for (StepStatus candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedStepStatuses = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
