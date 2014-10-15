package com.tradehero.th.fragments.chinabuild.fragment.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SecuritySearchListAdapter;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.analytics.SearchSecurityEventForm;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.SearchHotSecurityListType;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.StringUtils;
import dagger.Lazy;

import java.util.ArrayList;
import java.util.Date;
import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

/*
   搜索  热门／历史
 */
public class SearchFragment extends DashboardFragment implements HasSelectedItem {

    @Inject
    Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject
    CurrentUserId currentUserId;
    @Inject
    UserServiceWrapper userServiceWrapper;
    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeCacheListener;
    public DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListTypeHotCacheListener;

    public SecuritySearchListAdapter adapter;

    @InjectView(R.id.tvSearch)TextView tvSearch;
    @InjectView(R.id.edtSearchInput)EditText tvSearchInput;
    @InjectView(R.id.btn_search_x)Button btnSearch_x;
    @InjectView(R.id.listSearch)SecurityListView listSearch;
    @InjectView(R.id.textview_security_searchresult)TextView tvResult;
    @InjectView(R.id.progressbar_trade_security_search) ProgressBar pbSearch;

    private SearchHotSecurityListType keyHot;
    private SearchSecurityListType keySearch;
    boolean isUserSearch = false;
    private String searchStr;
    private String searchCancelStr;

    protected SecurityCompactDTO selectedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        securityListTypeCacheListener = createSecurityListFetchListener();
        securityListTypeHotCacheListener = createSecurityListFetchListener();
        adapter = new SecuritySearchListAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        //setHeadViewMiddleMain("搜索");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    public void initView() {
        searchStr = getActivity().getResources().getString(R.string.search_search);
        searchCancelStr = getActivity().getResources().getString(R.string.search_cancel);
        if (StringUtils.isNullOrEmptyOrSpaces(getSearchString()) && !isUserSearch) {
            fetchHotSecuritySearchList(true);
        }

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

        tvSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, android.view.KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        fetchSecuritySearchList(true);
                        break;
                    case EditorInfo.IME_ACTION_DONE:
                        break;
                }
                return true;
            }
        });

        listSearch.setMode(PullToRefreshBase.Mode.BOTH);
        listSearch.setAdapter(adapter);
        listSearch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Timber.d("下拉刷新");
                if (isUserSearch) {
                    fetchSecuritySearchList(true);
                } else {
                    fetchHotSecuritySearchList(true);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                Timber.d("上拉加载更多");
                if (isUserSearch) {
                    fetchSecuritySearchListMore();
                } else {
                    fetchHotSecuritySearchListMore();
                }
            }
        });

        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long position) {
                SecurityCompactDTO dto = (SecurityCompactDTO) adapter.getItem((int) position);
                if (dto != null) {
                    Timber.d("list item clicked %s", dto.name);
                    enterSecurity(dto.getSecurityId(), dto.name, dto);
                    if (isUserSearch) {
                        sendAnalytics(dto);
                    }
                }
            }
        });
        listSearch.setEmptyView(tvResult);
    }

    public void enterSecurity(SecurityId securityId, String securityName, SecurityCompactDTO dto) {
        if (getArguments() != null && getArguments().containsKey(
                DiscussSendFragment.BUNDLE_KEY_RETURN_FRAGMENT)) {
            selectedItem = dto;
            popCurrentFragment();
            return;
        } else {
            Bundle bundle = new Bundle();
            bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
            pushFragment(SecurityDetailFragment.class, bundle);
        }

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        detachSecurityHotListCache();
        detachSecurityListCache();
        ButterKnife.reset(this);
        closeInputMethod();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> createSecurityListFetchListener() {
        return new TrendingSecurityListFetchListener();
    }

    @Nullable
    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    protected class TrendingSecurityListFetchListener implements DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> {
        @Override
        public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value) {
            dismissLoadingProgress();
            initAdapterSecurity(value, key);
            onFinish();
        }

        @Override
        public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error) {
            dismissLoadingProgress();
            Timber.e("Error fetching the list of security %s", key, error);
            onFinish();
        }

        private void onFinish() {
            listSearch.onRefreshComplete();
        }
    }

    private void initAdapterSecurity(@NotNull SecurityCompactDTOList value, @NotNull SecurityListType key) {
        if (key instanceof SearchSecurityListType) {
            isUserSearch = true;
        }

        if (key.page == 1) {
            adapter.setSecurityList(value);
        } else {
            adapter.addItems(value);
        }

        if (value != null && value.size() > 0) {
            key.page += 1;
        }

        adapter.notifyDataSetChanged();
    }

    private void detachSecurityListCache() {
        if (securityListTypeCacheListener != null) {
            securityCompactListCache.get().unregister(securityListTypeCacheListener);
        }
    }

    private void detachSecurityHotListCache() {
        if (securityListTypeHotCacheListener != null) {
            securityCompactListCache.get().unregister(securityListTypeHotCacheListener);
        }
    }

    private void fetchSecuritySearchList(boolean force) {
        if (StringUtils.isNullOrEmptyOrSpaces(getSearchString())) return;
        detachSecurityListCache();
        keySearch = new SearchSecurityListType(getSearchString(), 1, 50);
        securityCompactListCache.get().register(keySearch, securityListTypeCacheListener);
        securityCompactListCache.get().getOrFetchAsync(keySearch, force);
    }

    private void fetchHotSecuritySearchList(boolean force) {
        detachSecurityHotListCache();
        keyHot = new SearchHotSecurityListType(1, 50);
        securityCompactListCache.get().register(keyHot, securityListTypeHotCacheListener);
        securityCompactListCache.get().getOrFetchAsync(keyHot, force);
    }

    private void fetchSecuritySearchListMore() {
        if (StringUtils.isNullOrEmptyOrSpaces(getSearchString())) return;
        detachSecurityListCache();
        securityCompactListCache.get().register(keySearch, securityListTypeCacheListener);
        securityCompactListCache.get().getOrFetchAsync(keySearch, true);
    }

    private void fetchHotSecuritySearchListMore() {
        detachSecurityHotListCache();
        securityCompactListCache.get().register(keyHot, securityListTypeHotCacheListener);
        securityCompactListCache.get().getOrFetchAsync(keyHot, true);
    }

    public String getSearchString() {
        String strSearch = tvSearchInput.getText().toString();
        if (strSearch != null && strSearch.length() > 0) {
            return strSearch;
        } else {
            return "";
        }
    }

    @OnClick(R.id.tvSearch)
    public void onSearchClicked() {
        if(TextUtils.isEmpty(getSearchString())){
            popCurrentFragment();
            return;
        }
        if (!StringUtils.isNullOrEmptyOrSpaces(getSearchString())) {
            showLoadingProgress();
            fetchSecuritySearchList(true);
        }
    }

    @OnClick(R.id.btn_search_x)
    public void onClearClicked() {
        tvSearchInput.setText("");
    }


    private void sendAnalytics(final SecurityCompactDTO dto) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SearchSecurityEventForm analyticsEventForm = new SearchSecurityEventForm("search",
                            DateUtils.getFormattedUtcDateFromDate(getActivity().getResources(),
                                    new Date(System.currentTimeMillis())), dto.id,
                            currentUserId.toUserBaseKey().getUserId());
                    BatchAnalyticsEventForm batchAnalyticsEventForm = new BatchAnalyticsEventForm();
                    batchAnalyticsEventForm.events = new ArrayList<>();
                    batchAnalyticsEventForm.events.add(analyticsEventForm);
                    userServiceWrapper.sendAnalytics(batchAnalyticsEventForm);
                } catch (Exception e) {
                    THToast.show(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void showLoadingProgress() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (pbSearch != null) {
                    pbSearch.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void dismissLoadingProgress() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (pbSearch != null&&pbSearch.getVisibility()==View.VISIBLE) {
                    pbSearch.setVisibility(View.GONE);
                }
            }
        });
    }
}
