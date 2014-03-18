package com.tradehero.th.fragments.news;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.news.NewsHeadline;
import com.tradehero.th.api.news.NewsItemDTO;

/**
 * Created by julien on 11/10/13
 *
 * Map a Yahoo News object to a NewsHeadlineView.
 */
public class NewsHeadlineAdapter extends ArrayDTOAdapter<NewsItemDTO, NewsHeadlineView>
{
    private final static String TAG = NewsHeadlineAdapter.class.getSimpleName();

    public int[] backgrounds = {
            R.drawable.img_placeholder_news_1,
            R.drawable.img_placeholder_news_2,
            R.drawable.img_placeholder_news_3,
            R.drawable.img_placeholder_news_4,
            R.drawable.img_placeholder_news_5,
    };
    //public int[] backgrounds = null;
    public NewsHeadlineAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        //backgrounds = context.getResources().getIntArray(R.array.news_item_background_list2);
        //loadBackground();
    }

    private void loadBackground() {
         int[] backgroundResArray = null;
        TypedArray array = context.getResources().obtainTypedArray(R.array.news_item_background_list2);
        int len = array.length();
        backgroundResArray = new int[len];
        for (int i=0;i<len;i++){
            backgroundResArray[i] = array.getInt(i,0);
        }
        backgrounds = backgroundResArray;
    }
    @Override
    protected View conditionalInflate(View convertView, ViewGroup viewGroup) {
        View view = super.conditionalInflate(convertView, viewGroup);
        return view;
    }

    @Override protected void fineTune(final int position, NewsItemDTO dto, final NewsHeadlineView dtoView)
    {
        View wrapperView = dtoView.findViewById(R.id.news_item_placeholder);
        int index = position % backgrounds.length;
        wrapperView.setBackgroundResource(backgrounds[index]);
    }
}
