package com.androidth.general.fragments.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Filter;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.quote.QuoteDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.common.widget.filter.ListCharSequencePredicateFilter;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

public class SimpleSecurityItemViewAdapter extends SecurityItemViewAdapter
{
    protected final Filter filterToUse;
    @Inject ListCharSequencePredicateFilter<SecurityCompactDTO> securityCompactPredicateFilter;

    @Inject protected SecurityCompositeListCacheRx securityCompositeListCacheRx;

    //<editor-fold desc="Constructors">
    int layourResourceId;
    public SimpleSecurityItemViewAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
        this.layourResourceId = layoutResourceId;
        filterToUse = new SecurityItemFilter(securityCompactPredicateFilter);
    }
    //</editor-fold>

    @Override public ListCharSequencePredicateFilter<SecurityCompactDTO> getPredicateFilter()
    {
        return securityCompactPredicateFilter;
    }


    @Override public Filter getFilter()
    {
        return filterToUse;
    }



    /*public void updatePrices(@NonNull Context context, @NonNull List<SecurityCompactDTO> quotes)
    {
        Map<Integer, SecurityCompactDTO> map = new HashMap<>();
        for (SecurityCompactDTO quote : quotes)
        {
            map.put(quote.id, quote);
        }
        updatePrices(map);
    }*/
    //we can improve the complexity [Later]
    public void updatePrices(LiveQuoteDTO quoteUpdate, AbsListView listView)// Map is about mapping vertex(in listview) with SecurityCompactDTO
    {
        SecurityCompactDTO securityCompactDTO;
        if (getCount() > 0)
        {
            for (int index = 0; index < getCount(); index++)
            {
                securityCompactDTO = (SecurityCompactDTO) getItem(index);
                if (quoteUpdate != null && quoteUpdate.getSecurityId() == securityCompactDTO.id)
                {
                    Double askPrice = quoteUpdate.getAskPrice();
                    Double bidPrice = quoteUpdate.getBidPrice();
                    Double lastPrice = securityCompactDTO.lastPrice;
                    Double risePercent = securityCompactDTO.risePercent;

                    boolean hasRisePercent = false;
                    if(quoteUpdate.getRisePercent()!=null){
                        hasRisePercent = true;
                    }

                    if (securityCompactDTO instanceof FxSecurityCompactDTO)
                    {

                        ((FxSecurityCompactDTO) securityCompactDTO).currencyDisplay = quoteUpdate.getCurrencyDisplay();
                        ((FxSecurityCompactDTO) securityCompactDTO).currencyISO = quoteUpdate.getCurrencyISO();
                        ((FxSecurityCompactDTO) securityCompactDTO).volume = quoteUpdate.getVolume();
                        ((FxSecurityCompactDTO) securityCompactDTO).toUSDRate = quoteUpdate.getUsdRate();
                        ((FxSecurityCompactDTO) securityCompactDTO).setAskPrice(askPrice);
                        ((FxSecurityCompactDTO) securityCompactDTO).setBidPrice(bidPrice);
                        if(askPrice!=null && bidPrice!=null) {
                            ((FxSecurityCompactDTO) securityCompactDTO).lastPrice = (askPrice + bidPrice) / 2;
                        }
                        else if(askPrice != null){
                            ((FxSecurityCompactDTO) securityCompactDTO).lastPrice = askPrice;
                        }
                        else if(bidPrice != null){
                            ((FxSecurityCompactDTO) securityCompactDTO).lastPrice = bidPrice;
                        }
                        if(risePercent!=null && hasRisePercent){
                            ((FxSecurityCompactDTO) securityCompactDTO).risePercent = risePercent;
                        }

                    } else {
                        securityCompactDTO.currencyDisplay = quoteUpdate.getCurrencyDisplay();
                        securityCompactDTO.currencyISO = quoteUpdate.getCurrencyISO();
                        securityCompactDTO.volume = quoteUpdate.getVolume();
                        securityCompactDTO.toUSDRate = quoteUpdate.getUsdRate();
                        securityCompactDTO.askPrice = askPrice;
                        securityCompactDTO.bidPrice = bidPrice;
                        if(askPrice!=null && bidPrice!=null) {
                            securityCompactDTO.lastPrice = (askPrice + bidPrice) / 2;
                        }
                        else if(askPrice != null){
                            securityCompactDTO.lastPrice = askPrice;
                        }
                        else if(bidPrice != null){
                            securityCompactDTO.lastPrice = bidPrice;
                        }
                        if(risePercent!=null && hasRisePercent){
                            securityCompactDTO.risePercent = quoteUpdate.getRisePercent();
                        }
                    }

                    Double newLastPrice = securityCompactDTO.lastPrice;

                    notifyDataSetChanged();
                    if(listView != null && listView.getChildAt(index)!=null){
                            View v = listView.getChildAt(index);
                        if(lastPrice!=null &&
                                newLastPrice!=null &&
                                !lastPrice.equals(newLastPrice)){
                            TextView txtView = (TextView)v.findViewById(R.id.last_price);
                            YoYo.with(Techniques.Flash).playOn(txtView);
                        }
                        if(hasRisePercent){
                            Double newRisePercent = securityCompactDTO.risePercent;
                            if(newRisePercent!=null &&
                                    risePercent!=null &&
                                    !risePercent.equals(newRisePercent)){
                                TextView txtView = (TextView)v.findViewById(R.id.tv_stock_roi);
                                YoYo.with(Techniques.Flash).playOn(txtView);
                            }
                        }
//                        Log.v("SignalR", "Updating cache "+securityCompactDTO.getExchangeSymbol());//affects performance!
//                        securityCompositeDTO.Securities.set(index, securityCompactDTO);
//                        securityCompositeListCacheRx.onNext(new BasicProviderSecurityV2ListType(providerId), securityCompositeDTO);
                    }
                }
            }
        }
    }


    public void updatePricesQuoteDTO(@NonNull Context context, @NonNull List<? extends QuoteDTO> quotes)
    {
        Map<SecurityIntegerId, QuoteDTO> map = new HashMap<>();
        for (QuoteDTO quote : quotes)
        {
            map.put(quote.getSecurityIntegerId(), quote);
        }
        updatePricesQuoteDTO(map);
    }

    public void updatePricesQuoteDTO(@NonNull Map<SecurityIntegerId, ? extends QuoteDTO> quotes)
    {
        SecurityCompactDTO securityCompactDTO;
        QuoteDTO quote;
        if (getCount() > 0)
        {
            for (int index = 0; index < getCount(); index++)
            {
                securityCompactDTO = (SecurityCompactDTO) getItem(index);
                quote = quotes.get(securityCompactDTO.getSecurityIntegerId());
                if (quote != null)
                {
                    if (securityCompactDTO instanceof FxSecurityCompactDTO)
                    {
                        ((FxSecurityCompactDTO) securityCompactDTO).setAskPrice(quote.ask);
                        ((FxSecurityCompactDTO) securityCompactDTO).setBidPrice(quote.bid);
                    }
                    else
                    {
                        securityCompactDTO.askPrice = quote.ask;
                        securityCompactDTO.bidPrice = quote.bid;
                    }
                }
            }
        }
    }
}