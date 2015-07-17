package com.tradehero.chinabuild.fragment.security;

import com.tradehero.common.persistence.DTO;

import org.json.JSONObject;

/**
 * Created by palmer on 15/7/10.
 */
public class SecurityOptPositionMockDTO implements DTO{

    public int id;
    public int userId;
    public int securityId;
    public int portfolioId;
    public String name;
    public String exchange;
    public String symbol;
    public int shares = 0;
    public int sellableShares = 0;
    public double averagePriceRefCcy;
    public String currencyDisplay;
    public double fxRate;
    public double realizedPLRefCcy;
    public double unrealizedPLRefCcy;
    public double marketValueRefCcy;
    public double sumInvestedAmountRefCcy;
    public double roi;

    public static SecurityOptPositionMockDTO parseJSON(String jsonStr){
        SecurityOptPositionMockDTO securityOptPositionDTO = new SecurityOptPositionMockDTO();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if(jsonObject.has("sec_name")){
                securityOptPositionDTO.name = jsonObject.getString("sec_name");
            }
            if(jsonObject.has("sec_code")){
                securityOptPositionDTO.symbol = jsonObject.getString("sec_code");
            }
            if(jsonObject.has("current_amt")){
                securityOptPositionDTO.shares = jsonObject.getInt("current_amt");
            }
            if(jsonObject.has("enable_amt")){
                securityOptPositionDTO.sellableShares = jsonObject.getInt("enable_amt");
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return securityOptPositionDTO;
        }
    }
}
