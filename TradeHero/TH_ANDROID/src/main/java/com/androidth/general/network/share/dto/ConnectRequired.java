package com.androidth.general.network.share.dto;

import android.support.annotation.NonNull;
import com.androidth.general.api.share.SocialShareFormDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
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
