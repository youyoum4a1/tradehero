package com.tradehero.livetrade.thirdPartyServices.hengsheng;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengBalanceDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengPositionDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengSessionDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
@Singleton
public class HengshengManager {

    public static final String SESSION_TIME_OUT = "session_time_out";

    private HengshengSessionDTO sessionDTO;
    private HengshengBalanceDTO balanceDTO;
    private HengshengPositionDTO positionDTO;

    @Inject public HengshengManager() {
    }

    public void setSessionDTO(HengshengSessionDTO sessionDTO) {
        this.sessionDTO = sessionDTO;
    }

    public void setBalanceDTO(HengshengBalanceDTO balanceDTO) {
        this.balanceDTO = balanceDTO;
    }

    public void setPositionDTO(HengshengPositionDTO positionDTO) {
        this.positionDTO = positionDTO;
    }

    public String getAccessToken()
    {
        if (sessionDTO == null)
        {
            return SESSION_TIME_OUT;
        }
        //Todo check session time out by check its expires_in.
        return "Bearer " + sessionDTO.access_token;
    }
}
