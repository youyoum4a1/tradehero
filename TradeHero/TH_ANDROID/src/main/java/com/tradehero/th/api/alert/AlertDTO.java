package com.ayondo.academy.api.alert;

import android.support.annotation.Nullable;
import java.util.List;

public class AlertDTO extends AlertCompactDTO
{
    @Nullable public List<AlertEventDTO> alertEvents;
}
