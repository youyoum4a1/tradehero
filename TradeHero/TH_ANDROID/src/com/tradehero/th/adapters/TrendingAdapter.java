package com.tradehero.th.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.fedorvlasov.lazylist.ImageLoader;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.widget.trending.TrendingSecurityView;
import java.util.List;

public class TrendingAdapter extends ArrayAdapter<SecurityCompactDTO>
{
    private final static String TAG = TrendingAdapter.class.getSimpleName();

    public TrendingAdapter(final Context context, final List<SecurityCompactDTO> trendList)
    {
        super(context, 0, trendList);
    }

    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trending_grid_item, null);
        }

        final TrendingSecurityView trendingSecurityView = ((TrendingSecurityView) convertView);
        trendingSecurityView.display(getItem(position));

        return convertView;
    }
}