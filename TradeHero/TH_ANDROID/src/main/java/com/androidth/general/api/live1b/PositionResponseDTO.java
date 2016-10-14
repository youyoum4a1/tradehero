package com.androidth.general.api.live1b;

import android.os.Bundle;

import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.common.persistence.prefs.BooleanPreference;

import java.util.List;

public class PositionResponseDTO extends BaseMessageResponseDTO {


    public List<PositionDTO> positionDTOList;
    public Boolean isFullReport;

    public PositionResponseDTO() {
        super();
    }

    public PositionResponseDTO(List<PositionDTO> positionDTOList, Boolean isFullReport)
    {
        this.positionDTOList = positionDTOList;
        this.isFullReport = isFullReport;
    }
}
