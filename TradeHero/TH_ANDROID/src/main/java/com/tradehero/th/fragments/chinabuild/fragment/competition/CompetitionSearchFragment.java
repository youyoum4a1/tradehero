package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.CompetitionListAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.cache.CompetitionListType;
import com.tradehero.th.fragments.chinabuild.cache.CompetitionListTypeSearch;
import com.tradehero.th.fragments.chinabuild.cache.CompetitionNewCache;
import com.tradehero.th.fragments.chinabuild.data.CompetitionDataItem;
import com.tradehero.th.fragments.chinabuild.data.CompetitionInterface;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.StringUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-9. 搜索比赛页面
 */
public class CompetitionSearchFragment extends DashboardFragment
{
    @Inject Lazy<CompetitionNewCache> competitionNewCacheLazy;
    private DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList> competitionListCacheListenerSearch;


    private CompetitionListAdapter adapterList;
    private ProgressDialog mTransactionDialog;
    @Inject ProgressDialogUtil progressDialogUtil;


    @InjectView(R.id.listSearch) SecurityListView listCompetitions;//比赛列表
    @InjectView(R.id.tvSearch) TextView tvSearch;
    @InjectView(R.id.edtSearchInput) EditText tvSearchInput;
    @InjectView(R.id.btn_search_x) Button btnSearch_x;
    @InjectView(R.id.listSearch) SecurityListView listSearch;
    @InjectView(R.id.textview_security_searchresult)TextView tvResult;

    private String searchNoResult;

    private String searchStr;
    private String searchCancelStr;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        competitionListCacheListenerSearch = createCompetitionListCacheListenerSearch();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        hideActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.competition_search_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        hideActionBar();
        return view;
    }



    private void initView()
    {
        searchNoResult = getActivity().getResources().getString(R.string.search_no_result);
        searchStr = getActivity().getResources().getString(R.string.search_search);
        searchCancelStr = getActivity().getResources().getString(R.string.search_cancel);
        initListView();
        tvSearchInput.setHint("请输入比赛关键字");
        tvSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override public boolean onEditorAction(TextView textView, int actionId, android.view.KeyEvent keyEvent)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_SEARCH:
                        toSearch();
                        break;
                    case EditorInfo.IME_ACTION_DONE:
                        break;
                }
                return true;
            }
        });
        tvSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputStr = editable.toString();
                if(TextUtils.isEmpty(inputStr)){
                    tvSearch.setText(searchCancelStr);
                }else{
                    tvSearch.setText(searchStr);
                }
            }
        });
    }

    @OnClick(R.id.btn_search_x)
    public void onSearchXClicked()
    {
        Timber.d("onSearchXClicked!");
        if (tvSearchInput != null)
        {
            tvSearchInput.setText("");
        }
    }

    @OnClick(R.id.tvSearch)
    public void onSearch()
    {
        if(TextUtils.isEmpty(getSearchString())){
            popCurrentFragment();
            return;
        }
        toSearch();
    }

    private String getSearchString()
    {
        return StringUtils.isNullOrEmptyOrSpaces(tvSearchInput.getText().toString()) ? "" : tvSearchInput.getText().toString();
    }

    private void toSearch()
    {
        if (StringUtils.isNullOrEmptyOrSpaces(tvSearchInput.getText().toString()))
        {
            return;
        }
        else
        {
            fetchSearchCompetition(true, getSearchString());
        }
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        //ButterKnife.reset(this);
        closeInputMethod();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    protected DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList> createCompetitionListCacheListenerSearch()
    {
        return new CompetitionListCacheListener();
    }

    protected class CompetitionListCacheListener implements DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList>
    {
        @Override public void onDTOReceived(@NotNull CompetitionListType key, @NotNull UserCompetitionDTOList value)
        {
            if (key instanceof CompetitionListTypeSearch)
            {
                initSearchCompetition(value);
            }
            tvResult.setText(searchNoResult);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull CompetitionListType key, @NotNull Throwable error)
        {
            tvResult.setText(searchNoResult);
            THToast.show(getString(R.string.fetch_error));
            onFinish();
        }

        private void onFinish()
        {
            listCompetitions.onRefreshComplete();
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }
        }
    }

    protected void detachSearchCompetition()
    {
        competitionNewCacheLazy.get().unregister(competitionListCacheListenerSearch);
    }

    private void fetchSearchCompetition(boolean refresh, String searchWord)
    {
        mTransactionDialog = progressDialogUtil.show(CompetitionSearchFragment.this.getActivity(),
                R.string.processing, R.string.alert_dialog_please_wait);
        detachSearchCompetition();
        CompetitionListTypeSearch searchKey = new CompetitionListTypeSearch(searchWord);
        competitionNewCacheLazy.get().register(searchKey, competitionListCacheListenerSearch);
        competitionNewCacheLazy.get().getOrFetchAsync(searchKey, refresh);
    }

    //搜索出来的比赛
    private void initSearchCompetition(UserCompetitionDTOList userCompetitionDTOs)
    {
        if (adapterList != null)
        {
            adapterList.setSearchCompetitionDtoList(userCompetitionDTOs);
        }
    }

    public int getCompetitionPageType()
    {
        return CompetitionUtils.COMPETITION_PAGE_SEARCH;
    }

    private void gotoCompetitionDetailFragment(UserCompetitionDTO userCompetitionDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, userCompetitionDTO);
        pushFragment(CompetitionDetailFragment.class, bundle);
    }

    private void initListView()
    {
        if(adapterList == null)
        {
            adapterList = new CompetitionListAdapter(getActivity(), getCompetitionPageType());
        }

        listCompetitions.setAdapter(adapterList);
        listCompetitions.setMode(PullToRefreshBase.Mode.DISABLED);
        listCompetitions.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
            {
                CompetitionInterface item = adapterList.getItem((int) position);
                if (item instanceof CompetitionDataItem)
                {
                    gotoCompetitionDetailFragment(((CompetitionDataItem) item).userCompetitionDTO);
                }
            }
        });
        listCompetitions.setEmptyView(tvResult);
    }
}
