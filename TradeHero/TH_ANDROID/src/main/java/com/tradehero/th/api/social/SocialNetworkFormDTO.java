package com.tradehero.th.api.social;


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
