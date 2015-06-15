package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.SecurityUserPositionDTO;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityPostionAdapter extends BaseAdapter {

    private ArrayList<SecurityUserPositionDTO> opts = new ArrayList();
    private LayoutInflater inflater;

    public SecurityPostionAdapter(Context context, ArrayList<SecurityUserPositionDTO> opts) {
        if (opts != null) {
            this.opts.addAll(opts);
        }
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.item_security_position, null);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.imageview_security_position_avator);
            TextView username = (TextView) convertView.findViewById(R.id.textview_security_position_name);
            TextView quantity = (TextView) convertView.findViewById(R.id.textview_security_position_amount);
            TextView currency = (TextView) convertView.findViewById(R.id.textview_security_position_mark);
            TextView price = (TextView) convertView.findViewById(R.id.textview_security_position_price);
            TextView tvRoi = (TextView) convertView.findViewById(R.id.textview_security_position_percent);
            viewHolder = new ViewHolder(avatar, username, quantity, currency, price, tvRoi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SecurityUserPositionDTO dto = opts.get(i);
        viewHolder.displayPositionDTO(dto);
        return convertView;
    }

    public void setData(ArrayList<SecurityUserPositionDTO> opts) {
        if (opts != null) {
            this.opts.clear();
            this.opts.addAll(opts);
            notifyDataSetChanged();
        }
    }

    public void addMoreData(List<SecurityUserPositionDTO> opts) {
        if (opts != null) {
            this.opts.addAll(opts);
            notifyDataSetChanged();
        }
    }

    public final class ViewHolder {
        ImageView avatar;
        TextView username;
        TextView quantity;
        TextView currency;
        TextView price;
        TextView tvRoi;
        
        public ViewHolder(ImageView avatar,
                TextView username,
                TextView quantity,
                TextView currency,
                TextView price,
                TextView tvRoi) {
            this.avatar = avatar;
            this.username = username;
            this.quantity = quantity;
            this.currency = currency;
            this.price = price;
            this.tvRoi = tvRoi;
            
        }

        public void displayPositionDTO(SecurityUserPositionDTO dto) {
            if (dto == null) {
                return;
            }
            ImageLoader.getInstance().displayImage(dto.userPicUrl,
                    avatar,
                    UniversalImageLoader.getAvatarImageLoaderOptions());
            username.setText(dto.userName);
            THSignedNumber signedQuantity = THSignedNumber.builder(dto.quantity).build();
            quantity.setText(signedQuantity.toString());
            currency.setText(dto.currencyDisplay);
            price.setText(SecurityCompactDTO.getShortValue(dto.price));
            THSignedNumber roi = THSignedPercentage.builder(dto.roi * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvRoi.setText(roi.toString());
            tvRoi.setTextColor(roi.getColor());
        }
    }
}
