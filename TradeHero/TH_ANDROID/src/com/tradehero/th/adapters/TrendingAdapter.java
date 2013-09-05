package com.tradehero.th.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fedorvlasov.lazylist.ImageLoader;
import com.loopj.android.image.SmartImageView;
import com.tradehero.common.graphics.ImageUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utills.TrendUtils;
import com.tradehero.th.utills.YUtils;
import com.tradehero.th.widget.trending.TrendingSecurityView;
import java.util.List;

public class TrendingAdapter extends ArrayAdapter<SecurityCompactDTO>
{
    private final static String TAG = TrendingAdapter.class.getSimpleName();
    static public ImageLoader mImageLoader;

    public TrendingAdapter(Context context, List<SecurityCompactDTO> trendList)
    {
        super(context, 0, trendList);
        if (mImageLoader == null)
        {
            mImageLoader = new ImageLoader(context, ImageUtils.createDefaultWhiteToTransparentProcessor(), 3, R.drawable.default_image);
            //mImageLoader = new com.fedorvlasov.lazylist.ImageLoader(context);
        }
    }

    @SuppressWarnings("deprecation")
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trending_grid_item, null);
        }

        ((TrendingSecurityView) convertView).display(getItem(position));

        return convertView;
    }

    private ImageLoader.ImageLoadingListener createLoadingListener (final View trendingView, final ImageView stockLogo, final ImageView stockBgLogo)
    {
        return new ImageLoader.ImageLoadingListener()
        {
            public void onLoadingComplete(Bitmap b)
            {
                //final Bitmap bCleaned =
                //        ImageUtils.convertToMutableAndRemoveBackground(loadedImage);
                if (b != null)
                {
                    stockLogo.setImageBitmap(b);
                    stockBgLogo.setImageBitmap(b);
                    trendingView.invalidate();
                }
            }
        };
    }


}