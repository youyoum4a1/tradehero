package com.tradehero.th.api.games;

import com.tradehero.common.persistence.DTO;

public class GamesDTO implements DTO
{
    public int id;
    public String name;
    public String text;
    public String subtext;
    public String image;
    public String url;
    public boolean comingSoon;
    public String title;

    //<editor-fold desc="Constructors">
    protected GamesDTO() // For deserialiser
    {
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "GamesDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", subtext='" + subtext + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", comingSoon=" + comingSoon +
                ", title='" + title + '\'' +
                '}';
    }
}
