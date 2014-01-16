package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 5:59 PM To change this template use File | Settings | File Templates. */
public class HelpVideoDTO implements DTO
{
    public int id;
    public String title;
    public String subtitle;
    public String thumbnailUrl;
    public String videoUrl;
    public Integer providerId;
    public String embedCode;

    public HelpVideoId getHelpVideoId()
    {
        return new HelpVideoId(id);
    }
}
