package com.androidth.general.fragments.competition;

import com.androidth.general.api.competition.ProviderDTO;

/**
 * Created by ayushnvijay on 7/1/16.
 */
public class ActionbarColor {
    String hexColor;
    String notificationUrl;
    ActionbarColor(ProviderDTO providerDTO){
        this.hexColor = providerDTO.hexColor;
        this.notificationUrl = providerDTO.navigationLogoUrl;
    }
    public String getHexColor(){
        return hexColor;
    }
    public String getNotificationUrl(){
        return notificationUrl;
    }
}
