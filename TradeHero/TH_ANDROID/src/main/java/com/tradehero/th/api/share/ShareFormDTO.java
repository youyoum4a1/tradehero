package com.tradehero.th.api.share;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOKey;
import org.jetbrains.annotations.NotNull;

public class ShareFormDTO implements DTO
{
    private static final String BUNDLE_KEY_TYPE = ShareFormDTO.class.getName() + ".shareType";
    private static final String BUNDLE_KEY_DTO_KEY = ShareFormDTO.class.getName() + ".dtoKey";
    @NotNull final ShareType shareType;
    final DTOKey shareDTOKey;

    //<editor-fold desc="Constructors">
    public ShareFormDTO(@NotNull ShareType shareType, DTOKey dtoKey)
    {
        this.shareType = shareType;
        this.shareDTOKey = dtoKey;
    }
    //</editor-fold>

    public Bundle getArgs()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_TYPE, shareType.ordinal());

        return bundle;
    }
}
