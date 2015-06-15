package com.tradehero.chinabuild.fragment.security;

import com.tradehero.chinabuild.data.TradeRecord;
import com.tradehero.th.api.security.SecurityId;

import java.util.List;

/**
 * Created by palmer on 15/6/15.
 */
public class SecurityDetailSubCache {

    private static SecurityDetailSubCache securityDetailSubCache;

    private List<TradeRecord> tradeRecordList;

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

    public void setTradeRecordList(List<TradeRecord> tradeRecordList){
        if(tradeRecordList==null){
            return;
        }
        this.tradeRecordList = tradeRecordList;
    }

    public List<TradeRecord> getTradeRecordList(){
        return tradeRecordList;
    }

    public void clearAll(){
        tradeRecordList = null;
        securityId = null;
    }
}
