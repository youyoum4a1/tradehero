package com.tradehero.th.api.games;

import android.support.annotation.Nullable;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;

public class ViralMiniGameDefDTOList extends BaseArrayList<ViralMiniGameDefDTO>
    implements DTO
{
    @Nullable public ViralMiniGameDefDTO getViralMiniGameDefDTO(int viralMiniGameId)
    {
        return this.findFirstWhere(viralMiniGameDefDTO -> viralMiniGameDefDTO.viralMiniGameId == viralMiniGameId);
    }
}
