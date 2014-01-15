package com.tradehero.th.fragments.trending;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.persistence.market.ExchangeListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 10:32 AM To change this template use File | Settings | File Templates. */
abstract public class TrendingFilterSelectorFragment extends SherlockFragment
    implements AdapterView.OnItemSelectedListener
{
    public static final String TAG = TrendingFilterSelectorFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_SELECTED_EXCHANGE_INDEX = TrendingFilterSelectorFragment.class.getName() + ".exchangeIndex";

    protected TrendingFilterSelectorView selectorView;
    public SpinnerIconAdapter mExchangeSelectionAdapter;
    private WeakReference<OnPreviousNextListener> onPreviousNextListener = new WeakReference<>(null);
    private WeakReference<OnResumedListener> onResumedListener = new WeakReference<>(null);
    private WeakReference<OnExchangeSelectionChangedListener> onExchangeSelectionChangedListener = new WeakReference<>(null);

    @Inject protected Lazy<ExchangeListCache> exchangeListCache;
    private List<ExchangeDTO> exchangeDTOs;
    private List<CharSequence> dropDownTexts;
    private List<Drawable> dropDownIcons;
    private DTOCache.Listener<ExchangeListType, ExchangeDTOList> exchangeListTypeCacheListener;
    private DTOCache.GetOrFetchTask<ExchangeListType, ExchangeDTOList> exchangeListCacheFetchTask;
    private int selectedExchangeIndex;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_trending_filter, container, false);
        if (savedInstanceState != null)
        {
            selectedExchangeIndex = savedInstanceState.getInt(BUNDLE_KEY_SELECTED_EXCHANGE_INDEX, 0);
        }
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        if (view != null)
        {
            selectorView = (TrendingFilterSelectorView) view.findViewById(R.id.trending_filter_selector_view);
            if (selectorView != null)
            {
                if (selectorView.mPrevious != null)
                {
                    selectorView.mPrevious.setOnClickListener(new View.OnClickListener()
                    {
                        @Override public void onClick(View view)
                        {
                            handlePreviousClicked();
                        }
                    });

                }
                if (selectorView.mNext != null)
                {
                    selectorView.mNext.setOnClickListener(new View.OnClickListener()
                    {
                        @Override public void onClick(View view)
                        {
                            handleNextClicked();
                        }
                    });
                }
            }
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchExchangeList();
        display();
        notifyOnResumedListener();
    }

    @Override public void onDestroyView()
    {
        onResumedListener = new WeakReference<>(null);

        if (selectorView != null)
        {
            selectorView.onDestroy();
        }

        if (exchangeListCacheFetchTask != null)
        {
            exchangeListCacheFetchTask.setListener(null);
        }
        exchangeListCacheFetchTask = null;
        exchangeListTypeCacheListener = null;

        mExchangeSelectionAdapter = null;
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_SELECTED_EXCHANGE_INDEX, selectedExchangeIndex);
    }

    abstract TrendingFilterTypeDTO getTrendingFilterTypeDTO();
    abstract public TrendingSecurityListType getTrendingSecurityListType(String exchangeName, Integer page, Integer perPage);

    //<editor-fold desc="Display Methods">
    public void display()
    {
        if (selectorView != null)
        {
            selectorView.apply(getTrendingFilterTypeDTO());
        }
        displaySpinner();
    }

    public void displayPreviousButton()
    {
        if (selectorView != null && selectorView.mPrevious != null)
        {
            selectorView.mPrevious.setEnabled(true);
        }
    }

    public void displayNextButton()
    {
        if (selectorView != null && selectorView.mNext != null)
        {
            selectorView.mNext.setEnabled(true);
        }
    }

    public void displaySpinner()
    {
        if (selectorView != null && selectorView.mExchangeSelection != null && dropDownTexts != null)
        {
            // TODO make sure we do not need to check for null before
            mExchangeSelectionAdapter = new TrendingFilterSpinnerIconAdapter(
                    getActivity(),
                    getDropDownTextsArray(),
                    getDropDownIconsArray(),
                    getDropDownIconsArray());
            mExchangeSelectionAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
            selectorView.mExchangeSelection.setAdapter(mExchangeSelectionAdapter);
            selectorView.mExchangeSelection.setSelection(selectedExchangeIndex);
            selectorView.mExchangeSelection.setOnItemSelectedListener(this);
        }
    }
    //</editor-fold>

    private void handlePreviousClicked()
    {
        notifyPreviousRequested();
    }

    private void handleNextClicked()
    {
        notifyNextRequested();
    }

    private void fetchExchangeList()
    {
        if (exchangeListCacheFetchTask != null)
        {
            exchangeListCacheFetchTask.setListener(null);
        }
        if (exchangeListTypeCacheListener == null)
        {
            exchangeListTypeCacheListener = createExchangeListCacheListener();
        }
        exchangeListCacheFetchTask = exchangeListCache.get().getOrFetch(new ExchangeListType(), exchangeListTypeCacheListener);
        exchangeListCacheFetchTask.execute();
    }

    private DTOCache.Listener<ExchangeListType, ExchangeDTOList> createExchangeListCacheListener()
    {
        return new DTOCache.Listener<ExchangeListType, ExchangeDTOList>()
        {
            @Override public void onDTOReceived(ExchangeListType key, ExchangeDTOList value)
            {
                linkWith(value, true);
            }

            @Override public void onErrorThrown(ExchangeListType key, Throwable error)
            {
                THToast.show(getString(R.string.error_fetch_exchange_list_info));
                THLog.e(TAG, "Error fetching the list of exchanges " + key, error);
            }
        };
    }

    private void linkWith(ExchangeDTOList exchangeDTOs, boolean andDisplay)
    {
        if (exchangeDTOs == null)
        {
            this.exchangeDTOs = null;
        }
        else
        {
            // We keep only those included in Trending
            this.exchangeDTOs = new ArrayList<>();
            for (ExchangeDTO exchangeDTO: exchangeDTOs)
            {
                if (exchangeDTO.isIncludedInTrending)
                {
                    this.exchangeDTOs.add(exchangeDTO);
                }
            }
        }
        createDropDownTextsAndIcons();

        if (andDisplay)
        {
            displaySpinner();
        }
    }

    private void createDropDownTextsAndIcons()
    {
        if (exchangeDTOs == null)
        {
            dropDownTexts = null;
            dropDownIcons = null;
        }
        else
        {
            dropDownTexts = new ArrayList<>();
            dropDownIcons = new ArrayList<>();
            for (ExchangeDTO exchangeDTO: this.exchangeDTOs)
            {
                dropDownTexts.add(getString(R.string.trending_filter_exchange_drop_down, exchangeDTO.name, exchangeDTO.desc));
                try
                {
                    dropDownIcons.add(getResources().getDrawable(Exchange.valueOf(exchangeDTO.name).logoId));
                }
                catch (IllegalArgumentException ex)
                {
                    THLog.d(TAG, "Exchange logo does not exist: " + ex.getMessage());
                }
            }
        }
    }

    private CharSequence[] getDropDownTextsArray()
    {
        if (dropDownTexts == null)
        {
            return null;
        }
        CharSequence[] texts = new CharSequence[dropDownTexts.size() + 1];
        int index = 0;
        texts[index++] = getResources().getString(R.string.trending_filter_exchange_all);
        for (CharSequence charSequence: dropDownTexts)
        {
            texts[index++] = charSequence;
        }
        return texts;
    }

    private Drawable[] getDropDownIconsArray()
    {
        if (dropDownIcons == null)
        {
            return null;
        }
        Drawable[] drawables = new Drawable[dropDownIcons.size() + 1];
        int index = 0;
        drawables[index++] = null;
        for (Drawable drawable: dropDownIcons)
        {
            drawables[index++] = drawable;
        }
        return drawables;
    }

    private String getSelectedExchangeName()
    {
        String selectedExchangeName = "";
        if (selectedExchangeIndex > 0 && exchangeDTOs != null && exchangeDTOs.size() > 0)
        {
            selectedExchangeName = exchangeDTOs.get(selectedExchangeIndex - 1).name;
        }
        return selectedExchangeName;
    }

    //<editor-fold desc="AdapterView.OnItemSelectedListener">
    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        THLog.d(TAG, "onItemSelected Selected " + i);
        selectedExchangeIndex = i;
        notifyOnExchangeSelectionChangedListener();
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView)
    {
        THLog.d(TAG, "Nothing selected");
    }
    //</editor-fold>

    /**
     * The listener should be strongly referenced elsewhere
     * @param onPreviousNextListener
     */
    public void setOnPreviousNextListener(OnPreviousNextListener onPreviousNextListener)
    {
        this.onPreviousNextListener = new WeakReference<>(onPreviousNextListener);
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param onResumedListener
     */
    public void setOnResumedListener(OnResumedListener onResumedListener)
    {
        this.onResumedListener = new WeakReference<>(onResumedListener);
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param onExchangeSelectionChangedListener
     */
    public void setOnExchangeSelectionChangedListener(OnExchangeSelectionChangedListener onExchangeSelectionChangedListener)
    {
        this.onExchangeSelectionChangedListener = new WeakReference<>(onExchangeSelectionChangedListener);
    }

    private void notifyPreviousRequested()
    {
        OnPreviousNextListener listener = onPreviousNextListener.get();
        if (listener != null)
        {
            listener.onPreviousRequested();
        }
    }

    private void notifyNextRequested()
    {
        OnPreviousNextListener listener = onPreviousNextListener.get();
        if (listener != null)
        {
            listener.onNextRequested();
        }
    }

    private void notifyOnResumedListener()
    {
        OnResumedListener onResumedListenerCopy = onResumedListener.get();
        if (onResumedListenerCopy != null)
        {
            onResumedListenerCopy.onResumed(this);
        }
    }

    private void notifyOnExchangeSelectionChangedListener()
    {
        OnExchangeSelectionChangedListener selectionChangedListener = onExchangeSelectionChangedListener.get();
        if (selectionChangedListener != null)
        {
            selectionChangedListener.onExchangeSelectionChanged(new ExchangeStringId(getSelectedExchangeName()));
        }
    }

    public interface OnPreviousNextListener
    {
        void onPreviousRequested();
        void onNextRequested();
    }

    public interface OnResumedListener
    {
        void onResumed(Fragment fragment);
    }

    public interface OnExchangeSelectionChangedListener
    {
        void onExchangeSelectionChanged(ExchangeStringId exchangeId);
    }
}
