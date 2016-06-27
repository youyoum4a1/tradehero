package com.androidth.general.network.service;

import com.androidth.general.api.quote.QuoteDTO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ayushnvijay on 6/22/16.
 */
    public class JsonToQuoteDTO {

    JSONObject jsonObject;
    QuoteDTO quoteDTO;

    JsonToQuoteDTO(JSONObject jsonObject){
        this.jsonObject = jsonObject;
        quoteDTO = new QuoteDTO();
    }

    public QuoteDTO getQuoteDTO(){
        try {
            quoteDTO.ask = (double)jsonObject.getJSONObject("signedObject").getInt("id");
            //quoteDTO.
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return quoteDTO;
    }

}
