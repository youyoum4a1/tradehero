package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class FilterLayout extends RelativeLayout implements View.OnClickListener
{
    public ImageButton mPrevious;
    public ImageButton mNext;
    public TextView mTitle;
    public ImageView mTitleIcon;
    public TextView mDescription;
    public Spinner mSpinner;
    private MyListAdapter mSpinnerAdapter;

    private OnFilterListener onFilterListener;

    private Filter filter;
    private int minPage = 0;
    private int currentPage = 0;
    private int maxPage = 0;

    public FilterLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FilterLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    protected void init()
    {
        //DaggerUtils.inject(this);
        //trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO();
    }

    public void fillDefaultData()
    {
        BaseFilter filter = new BaseFilter();
        //start fillData
        int page = buildDefaultPageData(filter);
        //end fillData

        this.filter = filter;
        showProperView();
    }

    private static final String KEY_CURRENT_PAGE = "current_page";
    private static final String KEY_MAX_PAGE = "max_page";
    private static final String KEY_MIN_PAGE = "min_page";

    public static class SavedState extends BaseSavedState
    {
        int currentPage;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(Parcelable superState)
        {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            super.writeToParcel(out, flags);
            out.writeInt(currentPage);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString()
        {
            return "FilterLayout#SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + currentPage + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>()
        {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader)
            {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        });

        SavedState(Parcel in, ClassLoader loader)
        {
            super(in);
            if (loader == null)
            {
                loader = getClass().getClassLoader();
            }
            currentPage = in.readInt();
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.currentPage = currentPage;
        //        if (mAdapter != null) {
        //            ss.adapterState = mAdapter.saveState();
        //        }
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (!(state instanceof SavedState))
        {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (filter != null)
        {
            currentPage = ss.currentPage;
            //mAdapter.restoreState(ss.adapterState, ss.loader);
            //setCurrentItemInternal(ss.position, false, true);
            showProperView();
        }
        else
        {
            currentPage = ss.currentPage;
            //mRestoredAdapterState = ss.adapterState;
            //mRestoredClassLoader = ss.loader;
        }
    }

    private int buildDefaultPageData(BaseFilter filter)
    {

        int minPage = 0;
        int maxPage = 0;
        NewsData.PageTab[] pages = NewsData.PageTab.values();

        for (NewsData.PageTab page : pages)
        {
            List<SpinnerItemData> spinnerItemDataList = null;
            if (page == NewsData.PageTab.REGION_NEWS)
            {

                List<CountryLanguagePairDTO> countryLanguagePairDTOList =
                        NewsData.buildCountriesPair();
                spinnerItemDataList =
                        new ArrayList<>(countryLanguagePairDTOList.size());
                int size = countryLanguagePairDTOList.size();
                for (int i = 0; i < size; i++)
                {
                    CountryLanguagePairDTO dto = countryLanguagePairDTOList.get(i);
                    spinnerItemDataList.add(new SpinnerItemData(i, dto.name, 0));
                }
            }
            else if (page == NewsData.PageTab.MY_HEADLINE_NEWS)
            {

            }
            else if (page == NewsData.PageTab.SOCIAL_NEWS)
            {
                List<NewsItemCategoryDTO> newsItemCategoryDTOList =
                        NewsData.buildSocialCategories();
                spinnerItemDataList =
                        new ArrayList<>(newsItemCategoryDTOList.size());
                int size = newsItemCategoryDTOList.size();
                for (int i = 0; i < size; i++)
                {
                    NewsItemCategoryDTO dto = newsItemCategoryDTOList.get(i);
                    spinnerItemDataList.add(new SpinnerItemData(dto.id, dto.name, 0));
                }
            }

            PageData pageData = new PageData(page.page, page.title, page.haveDesc, page.desc,
                    page.haveSubFilter, page.spinnerItemLayout, spinnerItemDataList
            );
            filter.setPageData(page.page, pageData);

            if (page.page > maxPage)
            {
                maxPage = page.page;
            }
            if (page.page < minPage)
            {
                minPage = page.page;
            }
        }
        int currentPage = minPage;
        this.minPage = minPage;
        this.currentPage = currentPage;
        this.maxPage = maxPage;
        return currentPage;
    }

    public void fillPageData(Map<Integer, PageData> dataMap)
    {
        fillPageData(0, dataMap);
    }

    public void fillPageData(int defaultPage, Map<Integer, PageData> dataMap)
    {
        BaseFilter filter = new BaseFilter();
        filter.setPageData(dataMap);
        this.filter = filter;
        this.currentPage = defaultPage;
        this.minPage = 0;
        this.maxPage = dataMap.size() - 1;
        showProperView();
    }

    private void test()
    {
        mTitle.setText("Regional");
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        mPrevious = (ImageButton) findViewById(R.id.previous_filter);
        mNext = (ImageButton) findViewById(R.id.next_filter);
        mTitle = (TextView) findViewById(R.id.title);
        mTitleIcon = (ImageView) findViewById(R.id.trending_filter_title_icon);
        mDescription = (TextView) findViewById(R.id.description);
        mSpinner = (Spinner) findViewById(R.id.exchange_selection);

        //mSpinnerAdapter = new MyListAdapter()

        fillDefaultData();
        //test();
        setClickEvent();
    }

    private void setClickEvent()
    {
        if (mPrevious != null)
        {
            mPrevious.setOnClickListener(this);
        }
        if (mNext != null)
        {
            mNext.setOnClickListener(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        setClickEvent();
    }

    @Override protected void onDetachedFromWindow()
    {
        if (mPrevious != null)
        {
            mPrevious.setOnClickListener(null);
        }
        if (mNext != null)
        {
            mNext.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    public void onDestroy()
    {
        mPrevious = null;
        mNext = null;

        if (mSpinner != null)
        {
            mSpinner.setOnItemSelectedListener(null);
            mSpinner.setAdapter(null);
        }
        mSpinner = null;
    }

    public void showProperView()
    {
        Timber.d("Wangliang  showProperView currentPage:%d", currentPage);
        mTitle.setText(filter.getTitle(currentPage));
        //mTitleIcon.setImageResource(typeDTO.titleIconResId);
        if (filter.isSubTitleVisible(currentPage))
        {
            mDescription.setText(filter.getSubTitle(currentPage));
        }
        if (filter.hasSpinner(currentPage))
        {
            if (mSpinnerAdapter == null)
            {
                mSpinnerAdapter =
                        new MyListAdapter(getContext(), R.layout.common_dialog_item_layout,
                                R.id.popup_text, filter.getSpinnerData(currentPage));
            }
            else
            {
                mSpinnerAdapter.setNotifyOnChange(false);
                mSpinnerAdapter.clear();
                mSpinnerAdapter.addAll(filter.getSpinnerData(currentPage));
                mSpinnerAdapter.notifyDataSetChanged();
                mSpinnerAdapter.setNotifyOnChange(true);
            }

            if (mSpinner.getAdapter() == null)
            {
                mSpinner.setAdapter(mSpinnerAdapter);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.previous_filter:
                handlePreviousClicked();
                break;
            case R.id.next_filter:
                handleNextClicked();
                break;
        }
    }

    private class MyListAdapter extends ArrayAdapter<SpinnerItemData>
    {

        public MyListAdapter(Context context, int resource, int textViewResourceId,
                List<SpinnerItemData> objects)
        {
            super(context, resource, textViewResourceId, objects);
        }
    }

    private boolean canForward()
    {
        boolean canForward = currentPage < maxPage;
        Timber.d("Wangliang  canForward ? %s currentPage:%d,maxPage:%s", canForward, currentPage,
                maxPage);
        return canForward;
    }

    private boolean canBackward()
    {
        boolean canBackward = currentPage > minPage;
        Timber.d("Wangliang  canBackward ? %s currentPage:%d,minPage:%s", canBackward, currentPage,
                minPage);
        //return canBackward;
        return canBackward;
    }

    private void showNextPage()
    {
        currentPage++;
        showProperView();
    }

    private void showPreviousPage()
    {
        currentPage--;
        showProperView();
    }

    private void showFirstPage()
    {
        currentPage = minPage;
        showProperView();
    }

    private void showLastPage()
    {
        currentPage = maxPage;
        showProperView();
    }

    private void handlePreviousClicked()
    {
        Timber.d("Wangliang  handlePreviousClicked");
        //apply(trendingFilterTypeDTO.getPrevious());
        if (canBackward())
        {
            int oldPage = currentPage;
            showPreviousPage();
            notifyPageChanged(oldPage);
        }
        else
        {
            int oldPage = currentPage;
            showLastPage();
            notifyPageChanged(oldPage);
        }
    }

    private void handleNextClicked()
    {
        Timber.d("Wangliang  handleNextClicked");
        if (canForward())
        {
            int oldPage = currentPage;
            showNextPage();
            notifyPageChanged(oldPage);
        }
        else
        {
            int oldPage = currentPage;
            showFirstPage();
            notifyPageChanged(oldPage);
        }
    }

    public void setOnFilterListener(OnFilterListener listener)
    {
        this.onFilterListener = listener;
    }

    private void notifyPageChanged(int oldPage)
    {
        if (onFilterListener != null)
        {
            onFilterListener.onPageChanged(oldPage, currentPage);
        }
    }

    private void notifyItemSelected()
    {
        if (onFilterListener != null)
        {
            onFilterListener.onPageItemSelected(currentPage, mSpinner.getSelectedItemPosition());
        }
    }

    private class TrendingFilterSelectorViewSpinnerListener
            implements AdapterView.OnItemSelectedListener
    {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                long id)
        {
            Timber.d("Wangliang TrendingFilterSelectorViewSpinnerListener onItemSelected");
            notifyItemSelected();
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView)
        {
            // Nothing to do
        }
    }

    public static interface OnFilterListener
    {

        void onPageChanged(int page, int position);

        void onPageItemSelected(int oldPage, int currentPage);
    }
}
