package com.tradehero.th.api.games;

import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.Random;

public class ViralMiniGameDefDTOList extends BaseArrayList<ViralMiniGameDefDTO>
    implements DTO
{
    @Nullable public ViralMiniGameDefDTO getViralMiniGameDefDTO(final int viralMiniGameId)
    {
        return this.findFirstWhere(new Predicate<ViralMiniGameDefDTO>()
        {
            @Override public boolean apply(ViralMiniGameDefDTO viralMiniGameDefDTO)
            {
                return viralMiniGameDefDTO.viralMiniGameId == viralMiniGameId;
            }
        });
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
