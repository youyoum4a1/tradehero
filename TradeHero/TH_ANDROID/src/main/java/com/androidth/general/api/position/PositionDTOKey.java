package com.androidth.general.api.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTOKey;

public interface PositionDTOKey extends DTOKey
{
    @NonNull Bundle getArgs();
}
