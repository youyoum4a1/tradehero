package com.tradehero.chinabuild.fragment.security;

import com.tradehero.chinabuild.data.SecurityUserOptDTO;
import com.tradehero.chinabuild.data.SecurityUserPositionDTO;
import com.tradehero.th.api.security.SecurityId;

import java.util.List;

/**
 * Created by palmer on 15/6/15.
 */
public class SecurityDetailSubCache {

    private static SecurityDetailSubCache securityDetailSubCache;

    private List<SecurityUserOptDTO> tradeRecordList;
    private List<SecurityUserPositionDTO> sharePositionList;

    private SecurityId securityId;

    private SecurityDetailSubCache(){

    }

    public static SecurityDetailSubCache getInstance(){
        synchronized (SecurityDetailSubCache.class){
            if(securityDetailSubCache == null){
                securityDetailSubCache = new SecurityDetailSubCache();
            }
            return  securityDetailSubCache;
        }
    }

    public void setSecurityId(SecurityId securityId){
        clearAll();
        this.securityId = securityId;
    }

    public boolean isSecuritySame(SecurityId newSecurityId){
        if(securityId == null || newSecurityId == null){
            return false;
        }
        if(securityId.getSecuritySymbol().equals(newSecurityId.getSecuritySymbol()) && securityId.getExchange().equals(newSecurityId.getExchange())){
            return true;
        }
        return false;
    }

    public void setTradeRecordList(List<SecurityUserOptDTO> tradeRecordList){
        this.tradeRecordList = tradeRecordList;
    }

    public void setSharePositionList(List<SecurityUserPositionDTO> sharePositionList){
        this.sharePositionList = sharePositionList;
    }

    public List<SecurityUserPositionDTO> getSharePositionList(){
        return sharePositionList;
    }

    public List<SecurityUserOptDTO> getTradeRecordList(){
        return tradeRecordList;
    }

    public void clearAll(){
        tradeRecordList = null;
        sharePositionList = null;
        securityId = null;
    }
}
