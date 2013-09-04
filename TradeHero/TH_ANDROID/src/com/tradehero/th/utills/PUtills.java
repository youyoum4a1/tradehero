package com.tradehero.th.utills;

import com.tradehero.th.api.users.UserProfileDTO;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import com.tradehero.th.models.ProfileDTO;
import com.tradehero.th.models.Profilio;

public class PUtills
{

    private Context ctx;
    private UserProfileDTO mdata;

    public PUtills(Context ctx)
    {
        super();
        this.ctx = ctx;
        mdata = new UserProfileDTO();
    }

    public UserProfileDTO _parseJson(JSONObject mjsonObject)
    {

        if (mjsonObject != null)
        {

            try
            {
                /*thLinked": true,
		        "ccPerMonthBalance": 0,
		        "alertCount": 0,
		        "ccBalance": 0,
		        "id": 229667*/

                //mdata.setDisplayName(mjsonObject.getString("displayName"));
                //mdata.setPicture(mjsonObject.getString("picture"));
                //mdata.setId(mjsonObject.getString("id"));
                //mdata.setFollowerCount(mjsonObject.getString("followerCount"));
                //mdata.setAlertCount(mjsonObject.getString("alertCount"));
                //mdata.setLiLinked(mjsonObject.getString("thLinked"));
                //mdata.setCcPerMonthBalance(mjsonObject.getString("ccPerMonthBalance"));

                JSONObject portfolioObj = mjsonObject.getJSONObject("portfolio");

                if (portfolioObj != null)
                {
                    Profilio portfolio = new Profilio();
                    portfolio.setId(portfolioObj.optString("id"));
                    portfolio.setCashBalance(portfolioObj.optInt("cashBalance"));

                    //mdata.setPortfolio(portfolio);
                }
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mdata;
    }
}
