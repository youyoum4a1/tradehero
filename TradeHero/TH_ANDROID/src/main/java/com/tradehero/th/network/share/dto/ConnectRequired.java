package com.ayondo.academy.network.share.dto;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.share.SocialShareFormDTO;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import java.util.List;

public class ConnectRequired extends SocialShareResult
{
    @NonNull public final List<SocialNetworkEnum> toConnect;

    //<editor-fold desc="Constructors">
    public ConnectRequired(
            @NonNull SocialShareFormDTO shareFormDTO,
            @NonNull List<SocialNetworkEnum> toConnect)
    {
        super(shareFormDTO);
        this.toConnect = toConnect;
    }
    //</editor-fold>
}
