package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class TopStockListView extends LinearLayout
    implements DTOView<SecurityCompactDTOList>
{
    @LayoutRes private static final int DEFAULT_RES_TOP_STOCK_LOGO = R.layout.on_board_top_stock_logo;

    @Inject Picasso picasso;
    @LayoutRes private final int topStockRes;

    //<editor-fold desc="Constructors">
    public TopStockListView(Context context)
    {
        super(context);
        topStockRes = DEFAULT_RES_TOP_STOCK_LOGO;
    }

    public TopStockListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        topStockRes = getTopStockLayoutRes(context, attrs);
    }

    public TopStockListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        topStockRes = getTopStockLayoutRes(context, attrs);
    }
    //</editor-fold>

    @LayoutRes static int getTopStockLayoutRes(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopStockListView);
        int topStockRes = a.getResourceId(R.styleable.TopStockListView_topStock, DEFAULT_RES_TOP_STOCK_LOGO);
        a.recycle();
        return topStockRes;
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
    }

    @Override public void display(@Nullable SecurityCompactDTOList dto)
    {
        removeAllViews();
        if (dto != null && dto.size() > 0)
        {
            for (final SecurityCompactDTO compactDTO : dto)
            {
                if (compactDTO.imageBlobUrl != null)
                {
                    ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(topStockRes, null);
                    picasso.load(compactDTO.imageBlobUrl)
                            .resizeDimen(R.dimen.size_10, R.dimen.size_3)
                            .centerInside()
                            .into(imageView);
                    addView(imageView);
                }
            }
        }
    }
}
