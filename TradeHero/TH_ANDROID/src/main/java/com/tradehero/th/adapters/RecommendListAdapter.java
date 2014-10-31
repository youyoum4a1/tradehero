package com.tradehero.th.adapters;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.activities.RecommendStocksActivity;
import com.tradehero.th.fragments.chinabuild.data.RecommendHero;
import com.tradehero.th.fragments.chinabuild.data.RecommendStock;
import com.tradehero.th.utils.ColorUtils;

import java.util.ArrayList;

/**
 * Created by palmer on 14-10-29.
 */
public class RecommendListAdapter extends BaseAdapter {

    private ArrayList<RecommendStock> securities = new ArrayList<RecommendStock>();
    private ArrayList<RecommendHero> heroes = new ArrayList<RecommendHero>();

    private ArrayList<Integer> securitiesSelected = new ArrayList<Integer>();
    private ArrayList<Integer> heroesSelected = new ArrayList<Integer>();

    private LayoutInflater inflater;

    private String numberOfHoldStr = "";
    private String rateOfReturnStr = "";
    private String RMBStr = "";

    private int RateOfReturnColor;

    private RecommendStocksActivity activity;

    public RecommendListAdapter(RecommendStocksActivity activity, ArrayList<RecommendStock> securities, ArrayList<RecommendHero> heroes){
        if(securities!=null){
            this.securities.addAll(securities);
            for(RecommendStock stock: securities){
                securitiesSelected.add(stock.id);
            }
        }
        if(heroes!=null){
            this.heroes.addAll(heroes);
            for(RecommendHero hero: heroes){
                heroesSelected.add(hero.id);
            }
        }
        this.activity = activity;
        inflater = LayoutInflater.from(activity);

        numberOfHoldStr = activity.getResources().getString(R.string.recommend_number_hold);
        rateOfReturnStr = activity.getResources().getString(R.string.recommend_stock_rate_of_return);
        RMBStr = activity.getResources().getString(R.string.RMB);

        RateOfReturnColor = activity.getResources().getColor(R.color.recomment_rate_of_return);
    }

    @Override
    public int getCount() {
        return securities.size() + heroes.size();
    }

    @Override
    public Object getItem(int i) {
        if( i > (heroes.size() - 1)){
            return securities.get(i + 1 - heroes.size());
        }else{
            return heroes.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        if( i > (heroes.size() - 1)){
            convertView = inflater.inflate(R.layout.recommend_list_stock_item, null);
            View downStockDividerView = (View)convertView.findViewById(R.id.view_recommend_list_stock_down_divider);
            ImageView stockSelectIV = (ImageView)convertView.findViewById(R.id.imageview_recommend_list_stock_select);
            TextView stockNameTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_stock_name);
            TextView numberHoldTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_stock_number_owner);
            TextView currencyTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_stock_currency);
            TextView priceTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_stock_price);
            TextView rateTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_stock_rate);
            TextView stockToolbar = (TextView)convertView.findViewById(R.id.textview_recommend_list_stock_toolbar);
            RelativeLayout stockRL = (RelativeLayout)convertView.findViewById(R.id.relativelayout_recommend_list_stock_item);
            if(i == (heroes.size() + securities.size()-1)){
                downStockDividerView.setVisibility(View.GONE);
            }else{
                downStockDividerView.setVisibility(View.VISIBLE);
            }
            int realIndex = i-heroes.size();
            if(realIndex == 0){
                stockToolbar.setVisibility(View.VISIBLE);
            }else{
                stockToolbar.setVisibility(View.GONE);
            }
            RecommendStock recommendStock = securities.get(realIndex);
            stockNameTV.setText(recommendStock.name);
            numberHoldTV.setText(recommendStock.holdCount + numberOfHoldStr);
            if(TextUtils.isEmpty(recommendStock.currencyDisplay)){
                currencyTV.setText(RMBStr);
            }else{
                currencyTV.setText(recommendStock.currencyDisplay);
            }
            priceTV.setText(String.valueOf(recommendStock.lastPrice));
            int color = activity.getResources().getColor(ColorUtils.getColorResourceIdForNumber(recommendStock.risePercent));
            String percent = "";
            if(recommendStock.risePercent>0){
                percent = "+" + keyTwoDecimals(recommendStock.risePercent) + "%";
            }else{
                percent = keyTwoDecimals(recommendStock.risePercent) + "%";
            }
            rateTV.setTextColor(color);
            rateTV.setText(percent);
            if(isSelectedSecurity(recommendStock)){
                stockSelectIV.setBackgroundResource(R.drawable.checkbox_normal);
            }else{
                stockSelectIV.setBackgroundResource(R.drawable.checkbox_active);
            }
            stockRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedItem(i);
                }
            });
        }else{
            convertView = inflater.inflate(R.layout.recommend_list_hero_item, null);
            ImageView avatarIV = (ImageView)convertView.findViewById(R.id.imageview_recommend_list_hero_avatar);
            TextView nameHeroTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_hero_name);
            TextView rateOfReturnTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_hero_rate);
            TextView heroToolbarTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_hero_toolbar);
            TextView titleHeroTV = (TextView)convertView.findViewById(R.id.textview_recommend_list_hero_title);
            ImageView heroSelectIV = (ImageView)convertView.findViewById(R.id.imageview_recommend_list_hero_select);
            View downHeroDividerView = (View)convertView.findViewById(R.id.view_recommend_list_hero_down_divider);
            RelativeLayout heroRL = (RelativeLayout)convertView.findViewById(R.id.relativelayout_recommend_list_hero_item);
            RecommendHero hero = heroes.get(i);
            if(!TextUtils.isEmpty(hero.picUrl)){
                Picasso.with(activity).load(hero.picUrl).
                        placeholder(R.drawable.superman_facebook).error(R.drawable.superman_facebook).into(avatarIV);
            }else{
                avatarIV.setImageResource(R.drawable.superman_facebook);
            }
            nameHeroTV.setText(hero.name);
            if(i == 0){
                heroToolbarTV.setVisibility(View.VISIBLE);
            }else{
                heroToolbarTV.setVisibility(View.GONE);
            }
            titleHeroTV.setText(hero.description);
            if(i == (heroes.size()-1)){
                downHeroDividerView.setVisibility(View.GONE);
            }else{
                downHeroDividerView.setVisibility(View.VISIBLE);
            }
            if(isSelectedHero(hero)){
                heroSelectIV.setBackgroundResource(R.drawable.checkbox_normal);
            }else{
                heroSelectIV.setBackgroundResource(R.drawable.checkbox_active);
            }
            int color = activity.getResources().getColor(ColorUtils.getColorResourceIdForNumber(hero.roi));
            String percent = "";
            if(hero.roi>0){
                percent = "+" + keyTwoDecimals(hero.roi) + "%";
            }else{
                percent = keyTwoDecimals(hero.roi) + "%";
            }
            String rateOfReturnFinalStr = rateOfReturnStr + " " + percent;
            SpannableStringBuilder style=new SpannableStringBuilder(rateOfReturnFinalStr);
            style.setSpan(new ForegroundColorSpan(RateOfReturnColor),rateOfReturnStr.length()+1,rateOfReturnFinalStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            rateOfReturnTV.setText(style);
            heroRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedItem(i);
                }
            });
        }
        return convertView;
    }

    public void setRecommendItems(ArrayList<RecommendStock> securities, ArrayList<RecommendHero> heroes){
        this.securities = securities;
        securitiesSelected.clear();
        for(RecommendStock stock: securities){
            securitiesSelected.add(stock.id);
        }
        this.heroes = heroes;
        heroesSelected.clear();
        for(RecommendHero hero: heroes){
            heroesSelected.add(hero.id);
        }
    }

    private String keyTwoDecimals(double data){
        String result = String.format("%.2f", data * 100);
        return result;
    }

    private boolean isSelectedSecurity(RecommendStock stock){
        for(Integer stockId :securitiesSelected){
            if(stockId == stock.id){
                return true;
            }
        }
        return false;
    }

    private boolean isSelectedHero(RecommendHero hero){
        for (Integer heroId: heroesSelected){
            if(heroId == hero.id){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Integer> getSecuritiesSelected(){
        return securitiesSelected;
    }

    public ArrayList<Integer> getHeroesSelected(){
        return heroesSelected;
    }

    public void setSelectedItem(int position){
        if(position > (heroes.size() - 1)){
            int current = position - heroes.size();
            RecommendStock stock = securities.get(current);
            int target = getSelectedStock(stock);
            if(target == -1){
                securitiesSelected.add(stock.id);
            }else{
                securitiesSelected.remove(target);
            }
        }else{
            RecommendHero hero = heroes.get(position);
            int target = getSelectedHero(hero);
            if(target==-1){
                heroesSelected.add(hero.id);
            }else{
                heroesSelected.remove(target);
            }
        }
        notifyDataSetChanged();
        activity.checkFollowingItems();
    }

    private int getSelectedHero(RecommendHero hero){
        for (int num = 0;num<heroesSelected.size();num++){
            Integer selectHeroId = heroesSelected.get(num);
            if(selectHeroId==hero.id){
                return num;
            }
        }
        return -1;
    }

    private int getSelectedStock(RecommendStock stock){
        for (int num = 0;num<securitiesSelected.size();num++){
            Integer selectStockId = securitiesSelected.get(num);
            if(selectStockId==stock.id){
                return num;
            }
        }
        return -1;
    }

}
