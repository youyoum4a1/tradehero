package com.tradehero.th.api.social;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 10:17 PM To change this template use File | Settings | File Templates. */
public class SocialNetworkFormDTO
{
    public static final String TAG = SocialNetworkFormDTO.class.getSimpleName();

    public SocialNetworkEnum socialNetwork;

    public SocialNetworkFormDTO()
    {
        super();
    }

    public SocialNetworkFormDTO(SocialNetworkEnum socialNetwork)
    {
        super();
        this.socialNetwork = socialNetwork;
    }
}
