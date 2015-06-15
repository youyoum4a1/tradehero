package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.SecurityUserOptDTO;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.number.THSignedNumber;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * Created by palmer on 15/6/9.
 */
public class SecurityOptAdapter extends BaseAdapter {

    static PrettyTime prettyTime;

    private ArrayList<SecurityUserOptDTO> opts = new ArrayList();
    private LayoutInflater inflater;

    private static String[] TRADE_TYPES = new  String[2];

    public SecurityOptAdapter(Context context, ArrayList<SecurityUserOptDTO> opts){
        if(opts!=null){
            this.opts.addAll(opts);
        }
        inflater = LayoutInflater.from(context);

        prettyTime = new PrettyTime();
        TRADE_TYPES[0] = context.getString(R.string.trade_list_button_buy);
        TRADE_TYPES[1] = context.getString(R.string.trade_list_button_sell);
    }

    @Override
    public int getCount() {
        return opts.size();
    }

    @Override
    public Object getItem(int i) {
        return opts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_security_opt, null);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.imageview_security_opt_avator);
            TextView username = (TextView) convertView.findViewById(R.id.textview_security_opt_name);
            TextView optType = (TextView) convertView.findViewById(R.id.textview_security_opt_opt);
            TextView currency = (TextView) convertView.findViewById(R.id.textview_security_opt_mark);
            TextView price = (TextView) convertView.findViewById(R.id.textview_security_opt_price);
            TextView quantity = (TextView) convertView.findViewById(R.id.textview_security_opt_mount);
            TextView date = (TextView) convertView.findViewById(R.id.textview_security_opt_date);

            viewHolder = new ViewHolder(avatar, username, optType, currency, price, quantity, date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SecurityUserOptDTO optDTO = opts.get(i);
        viewHolder.displayDTO(optDTO);

        return convertView;
    }

    public void setData(ArrayList<SecurityUserOptDTO> opts){
        if(opts!=null){
            this.opts.clear();
            this.opts.addAll(opts);
            notifyDataSetChanged();
        }
    }

    public void addMoreData(List<SecurityUserOptDTO> opts){
        if(opts!=null){
            this.opts.addAll(opts);
            notifyDataSetChanged();
        }
    }

    public final class ViewHolder {
        public ImageView avatar;
        public TextView username;
        public TextView optType;
        public TextView currency;
        public TextView price;
        public TextView quantity;
        public TextView date;
        
        public ViewHolder(ImageView avatar,
                TextView username,
                TextView optType,
                TextView currency,
                TextView price,
                TextView quantity,
                TextView date) {
            this.avatar = avatar;
            this.username = username;
            this.optType = optType;
            this.currency = currency;
            this.price = price;
            this.quantity = quantity;
            this.date = date;
        }

        public void displayDTO(SecurityUserOptDTO securityUserOptDTO) {
            ImageLoader.getInstance().displayImage(securityUserOptDTO.userPicUrl,
                    avatar,
                    UniversalImageLoader.getAvatarImageLoaderOptions());
            username.setText(securityUserOptDTO.userName);

            currency.setText(securityUserOptDTO.currencyDisplay);
            if (securityUserOptDTO.price != null) {
                price.setText(SecurityCompactDTO.getShortValue(securityUserOptDTO.price));
            } else {
                price.setText("- -");
            }
            THSignedNumber signedQuantity = THSignedNumber.builder(securityUserOptDTO.quantity)
                    .build();
            quantity.setText(signedQuantity.toString());
            quantity.setTextColor(signedQuantity.getColor());
            date.setText(prettyTime.formatUnrounded(securityUserOptDTO.datetimeUtc));

            if (securityUserOptDTO.quantity > 0) {
                optType.setText(TRADE_TYPES[0]);
            } else {
                optType.setText(TRADE_TYPES[1]);
            }
            optType.setTextColor(signedQuantity.getColor());
        }
    }
}
