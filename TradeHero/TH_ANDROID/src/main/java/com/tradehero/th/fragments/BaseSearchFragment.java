package com.tradehero.th.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.utils.DeviceUtil;

import java.util.List;

abstract public class BaseSearchFragment<
        PagedDTOKeyType extends DTOKey, // But it also needs to be a PagedDTOKey
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>,
        ViewType extends View & DTOView<DTOType>>
        extends BasePagedListFragment<
        PagedDTOKeyType,
        DTOType,
        DTOListType,
        ViewType>
{
    private final static String BUNDLE_KEY_CURRENT_SEARCH_STRING = BaseSearchFragment.class.getName() + ".currentSearchString";

    @InjectView(R.id.search_empty_textview) protected TextView searchEmptyTextView;
    @InjectView(R.id.search_empty_textview_wrapper) protected View searchEmptyTextViewWrapper;

    protected EditText mSearchTextField;
    protected String mSearchText;
    protected SearchTextWatcher mSearchTextWatcher;

    public static void putSearchString(Bundle args, String searchText)
    {
        args.putString(BUNDLE_KEY_CURRENT_SEARCH_STRING, searchText);
    }

    public static String getSearchString(Bundle args)
    {
        if (args != null && args.containsKey(BUNDLE_KEY_CURRENT_SEARCH_STRING))
        {
            return args.getString(BUNDLE_KEY_CURRENT_SEARCH_STRING);
        }
        return null;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSearchText = getSearchString(getArguments());
        mSearchText = getSearchString(savedInstanceState);
    }

    @Override protected int getFragmentLayoutResId()
    {
        return R.layout.fragment_search_stock;
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putSearchString(outState, mSearchText);
    }

    @Override public void onDestroyView()
    {
        DeviceUtil.dismissKeyboard(getActivity());
        super.onDestroyView();
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return mSearchText != null && !mSearchText.isEmpty();
    }

    @Override protected void updateVisibilities()
    {
        super.updateVisibilities();
        searchEmptyTextViewWrapper.setVisibility(hasEmptyResult() ? View.VISIBLE : View.GONE);
    }

    protected class SearchTextWatcher implements TextWatcher
    {
        @Override public void afterTextChanged(Editable editable)
        {
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count)
        {
            mSearchText = charSequence.toString();
            if (mSearchText == null || mSearchText.isEmpty())
            {
                startAnew();
            }
            else
            {
                scheduleRequestData();
            }
        }
    }
}