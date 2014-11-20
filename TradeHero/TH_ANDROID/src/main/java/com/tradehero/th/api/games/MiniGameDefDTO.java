package com.tradehero.th.api.games;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.KeyGenerator;
import java.util.List;

public class MiniGameDefDTO implements DTO, KeyGenerator
{
    public int id;
    public String name;
    public String text;
    public String subtext;
    public String image;
    public String url;
    public boolean comingSoon;
    public List<String> howToPlayUrls;

    @Override public MiniGameDefKey getDTOKey()
    {
        return new MiniGameDefKey(id);
    }
}
