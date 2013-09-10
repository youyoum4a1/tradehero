package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.adapters.SearchPeopleAdapter;
import com.tradehero.th.adapters.SearchStockAdapter;
import com.tradehero.th.adapters.TrendingAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.application.App;
import com.tradehero.th.application.Config;
import com.tradehero.th.http.THAsyncClientFactory;
import com.tradehero.th.models.Trend;
import com.tradehero.th.models.User;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import java.io.IOException;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TrendingFragment extends SherlockFragment
{
    private final static String TAG = TrendingFragment.class.getSimpleName();
    private final static String[] SEARCH_TYPE = {"Stocks", "People"};

    private GridView mTrendingGridView;
    private ListView mSearchListView;
    private ProgressBar mProgressSpinner;

    private List<SecurityCompactDTO> trendList;
    private List<Trend> searchStockList;
    private List<User> searchPeopleList;

    private Spinner mSearchTypeSpinner;
    private TextView mHeaderText;
    private EditText mSearchField;
    private View mSearchContainer;
    private ImageButton mBackBtn;
    private ImageButton mSearchBtn;
    private ImageView mBullIcon;
    private RelativeLayout header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v)
    {
        mTrendingGridView = (GridView) v.findViewById(R.id.trending_gridview);
        mSearchListView = (ListView) v.findViewById(R.id.trending_listview);
        mProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);
        mSearchTypeSpinner = (Spinner) v.findViewById(R.id.spinner);
        mHeaderText = (TextView) v.findViewById(R.id.header_txt);
        mSearchField = (EditText) v.findViewById(R.id.searh_field);
        mBullIcon = (ImageView) v.findViewById(R.id.logo_img);
        mSearchBtn = (ImageButton) v.findViewById(R.id.btn_search);
        mSearchBtn.setVisibility(View.VISIBLE);
        mBackBtn = (ImageButton) v.findViewById(R.id.btn_back);
        mSearchContainer = (RelativeLayout) v.findViewById(R.id.search_container);

        // TODO header bar
        //header = (RelativeLayout) getActivity().findViewById(R.id.top_tabactivity);
        //header.setVisibility(View.GONE);
        mHeaderText.setText(R.string.header_trending);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, SEARCH_TYPE);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSearchTypeSpinner.setAdapter(adapter);

        mSearchField.addTextChangedListener(new SearchFieldWatcher());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // TODO sliding menu
        //((TradeHeroTabActivity) getActivity()).showSlidingMenue(true);
        mBackBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showSearchView(false);
                showSearchList(false);
            }
        });

        mSearchBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showSearchView(true);
            }
        });

        if (trendList != null && trendList.size() > 0)
        {
            setDataAdapterToGridView(trendList);
        }
        //else {
        //	trendList = new ArrayList<Trend>();
        //	mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), trendList));
        //}

        mSearchTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3)
            {
                mSearchField.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        mTrendingGridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id)
            {

                SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);
                ((DashboardActivity)getActivity()).pushTrendingDetailFragment(securityCompactDTO);
            }
        });

        mSearchListView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position,
                    long id)
            {
                //((String)mSearchTypeSpinner.getSelectedItem()).equalsIgnoreCase(SEARCH_TYPE[0])
                if (adapter.getItemAtPosition(position) instanceof Trend)
                {
                    SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) adapter.getItemAtPosition(position);
                    ((DashboardActivity) getActivity()).pushTrendingDetailFragment(securityCompactDTO);
                }
                else
                {
                    Toast.makeText(getActivity(), "Under progress...", Toast.LENGTH_SHORT).show();
                    //TODO Push the user detail screen
                }
            }
        });

        if (mTrendingGridView != null && mTrendingGridView.getCount() == 0)
        {
            showProgressSpinner(true);
        }

        requestToGetTrendingInfo();
    }

   private void setDataAdapterToGridView(List<SecurityCompactDTO> trendList)
    {
        mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), trendList));
        showProgressSpinner(false);
    }

    //	private void setInitialAdapterToGridView() {
    //		trendList = new ArrayList<Trend>();
    //		mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), trendList));
    //	}

    private void setDataAdapterToSearchListView(List<Trend> trendList)
    {
        mSearchListView.setAdapter(new SearchStockAdapter(getActivity(), trendList));
    }

    private void requestToGetTrendingInfo()
    {
        SecurityService securityService = NetworkEngine.createService(SecurityService.class);
        securityService.getTrendingSecurities(createCallbackForTrending());

        if (1==1)
        {
            return;
        }

        AsyncHttpClient client = THAsyncClientFactory.getInstance(Constants.TH_EMAIL_PREFIX);
        client.get(Config.getTrendingFeed(), new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(String response)
            {
                try
                {
                    Logger.log(TAG, "Trending Response\n" + response, LogLevel.LOGGING_LEVEL_INFO);
                    ObjectMapper objectMapper = new ObjectMapper();
                    trendList = objectMapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, Trend.class));
                    setDataAdapterToGridView(trendList);
                } catch (JsonParseException e)
                {
                    showProgressSpinner(false);
                    e.printStackTrace();
                } catch (JsonMappingException e)
                {
                    showProgressSpinner(false);
                    e.printStackTrace();
                } catch (IOException e)
                {
                    showProgressSpinner(false);
                    e.printStackTrace();
                } catch (Exception e)
                {
                    showProgressSpinner(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable arg0, String arg1)
            {
                THToast.show(arg1);
            }

            @Override public void onFinish()
            {
                super.onFinish();
            }

            @Override public void onSuccess(int i, String s)
            {
                this.onSuccess(s);    //To change body of overridden methods use File | Settings | File Templates.
            }

            @Override public void onStart()
            {
                super.onStart();    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
    }

    private CallbackWithSpecificNotifiers<List<SecurityCompactDTO>> createCallbackForTrending ()
    {
        return new CallbackWithSpecificNotifiers<List<SecurityCompactDTO>>()
        {
            @Override public void notifyIsQuerying(boolean isQuerying)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override public void success(List<SecurityCompactDTO> returned, Response response)
            {
                super.success(returned, response);    //To change body of overridden methods use File | Settings | File Templates.
                setDataAdapterToGridView(returned);
            }

            @Override public void failure(RetrofitError retrofitError)
            {
                super.failure(retrofitError);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };
    }


    private void requestToGetSearchQuery(String searchQuery, String searchType, String pageNumber)
    {
        showProgressSpinner(true);

        AsyncHttpClient client = THAsyncClientFactory.getInstance(Constants.TH_EMAIL_PREFIX);
        client.get(String.format(Config.getTrendSearch(), searchType, searchQuery, pageNumber),
                new AsyncHttpResponseHandler()
                {

                    @Override
                    public void onSuccess(String response)
                    {

                        try
                        {
                            Logger.log(TAG, "Search Response: ---\n" + response, LogLevel.LOGGING_LEVEL_INFO);

                            if (((String) mSearchTypeSpinner.getSelectedItem()).equalsIgnoreCase(SEARCH_TYPE[0]))
                            {
                                ObjectMapper objectMapper = new ObjectMapper();
                                searchStockList = objectMapper.readValue(response,
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, Trend.class));
                                mSearchListView.setAdapter(new SearchStockAdapter(getActivity(), searchStockList));
                                setDataAdapterToSearchListView(searchStockList);
                            }
                            else
                            {
                                ObjectMapper objectMapper = new ObjectMapper();
                                searchPeopleList = objectMapper.readValue(response,
                                        TypeFactory.defaultInstance().constructCollectionType(List.class, User.class));
                                mSearchListView.setAdapter(new SearchPeopleAdapter(getActivity(), searchPeopleList));
                            }
                        } catch (JsonParseException e)
                        {
                            e.printStackTrace();
                        } catch (JsonMappingException e)
                        {
                            e.printStackTrace();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        showProgressSpinner(false);
                    }

                    @Override
                    public void onFailure(Throwable arg0, String arg1)
                    {
                        super.onFailure(arg0, arg1);
                    }
                });
    }

    private class SearchFieldWatcher implements TextWatcher
    {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {

            if (s.length() > 0)
            {

                showSearchList(true);

                String searchType = "securities";
                if (((String) mSearchTypeSpinner.getSelectedItem()).equalsIgnoreCase(SEARCH_TYPE[1]))
                {
                    searchType = "users";
                }

                requestToGetSearchQuery(s.toString(), searchType, "1");
            }
            else
            {
                showSearchList(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    }

    private void showSearchList(boolean flag)
    {
        mSearchListView.setVisibility(getVisibility(flag));
        mTrendingGridView.setVisibility(getVisibility(!flag));
        mProgressSpinner.setVisibility(View.GONE);
    }

    private void showSearchView(boolean flag)
    {
        mHeaderText.setVisibility(getVisibility(!flag));
        mBullIcon.setVisibility(getVisibility(!flag));
        mSearchContainer.setVisibility(getVisibility(flag));
        mSearchField.setText("");
    }

    private void showProgressSpinner(boolean flag)
    {
        mProgressSpinner.setVisibility(getVisibility(flag));
    }

    private int getVisibility(boolean flag)
    {
        return flag ? View.VISIBLE : View.INVISIBLE;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // TODO move to somewhere else
        //((TradeHeroTabActivity) getActivity()).showTabs(true);
        //((App) getActivity().getApplication()).setTrend(null);
    }
}
