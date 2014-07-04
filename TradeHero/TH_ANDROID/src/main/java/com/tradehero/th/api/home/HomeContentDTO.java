package com.tradehero.th.api.home;

import com.tradehero.common.persistence.BaseHasExpiration;
import com.tradehero.common.persistence.DTO;

public class HomeContentDTO extends BaseHasExpiration
        implements DTO
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 120;

    public String content;

    //<editor-fold desc="Constructors">
    public HomeContentDTO(String content)
    {
        super(DEFAULT_LIFE_EXPECTANCY_SECONDS);
        this.content = content;
    }
    //</editor-fold>
}
