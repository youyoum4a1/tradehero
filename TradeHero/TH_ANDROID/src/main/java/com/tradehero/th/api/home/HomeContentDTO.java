package com.tradehero.th.api.home;

import com.tradehero.common.persistence.DTO;

public class HomeContentDTO implements DTO
{
    public String content;

    //<editor-fold desc="Constructors">
    public HomeContentDTO(String content)
    {
        this.content = content;
    }
    //</editor-fold>
}
