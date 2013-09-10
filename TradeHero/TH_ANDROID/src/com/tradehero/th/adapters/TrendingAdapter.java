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
    static public ImageLoader mImageLoader;

    public TrendingAdapter(final Context context, final List<SecurityCompactDTO> trendList)
    {
        super(context, 0, trendList);
        if (mImageLoader == null)
        {
            mImageLoader = new ImageLoader(context, new WhiteToTransparentTransformation(), 3, R.drawable.default_image);
            //mImageLoader = new com.fedorvlasov.lazylist.ImageLoader(context);
        }
    }

    @SuppressWarnings("deprecation")
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

    private ImageLoader.ImageLoadingListener createLoadingListener (final View trendingView, final ImageView stockLogo, final ImageView stockBgLogo)
    {
        return new ImageLoader.ImageLoadingListener()
        {
            public void onLoadingComplete(final String url, final Bitmap b)
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