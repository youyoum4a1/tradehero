package com.androidth.general.api.competition;

import android.support.annotation.Nullable;

public class JumioVerifyBodyDTO
{
    @Nullable public String type;
    @Nullable public String scanReference;

    public JumioVerifyBodyDTO(String type, String scanReference) {
        this.type = type;
        this.scanReference = scanReference;
    }
}