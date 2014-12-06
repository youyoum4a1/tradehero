package com.tradehero.th.api.games;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.KeyGenerator;

public class MiniGameDefDTO implements DTO, KeyGenerator
{
    public int id;
    public String name;
    public String text;
    public String subtext;
    public String image;
    public String url;
    public boolean comingSoon;
    public String howToPlayUrl;

    //<editor-fold desc="Constructors">
    public MiniGameDefDTO()
    {
    }
    //</editor-fold>

    @Override public MiniGameDefKey getDTOKey()
    {
        return new MiniGameDefKey(id);
    }
}