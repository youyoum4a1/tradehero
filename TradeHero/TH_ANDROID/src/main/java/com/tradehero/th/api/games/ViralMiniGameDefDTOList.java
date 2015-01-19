package com.tradehero.th.api.games;

import android.support.annotation.Nullable;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.Random;

public class ViralMiniGameDefDTOList extends BaseArrayList<ViralMiniGameDefDTO>
    implements DTO
{
    @Nullable public ViralMiniGameDefDTO getViralMiniGameDefDTO(int viralMiniGameId)
    {
        return this.findFirstWhere(viralMiniGameDefDTO -> viralMiniGameDefDTO.viralMiniGameId == viralMiniGameId);
    }

    @Nullable public ViralMiniGameDefDTO getRandomViralMiniGameDefDTO()
    {
        if(isEmpty())
        {
            return null;
        }
        return this.get(new Random().nextInt(this.size()));
    }
}
