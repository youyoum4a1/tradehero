package android.tradehero.fragments;

import java.io.IOException;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.tradehero.activities.R;
import android.tradehero.activities.TradeHeroTabActivity;
import android.tradehero.adapters.SearchPeopleAdapter;
import android.tradehero.adapters.SearchStockAdapter;
import android.tradehero.adapters.TrendingAdapter;
import android.tradehero.application.App;
import android.tradehero.application.Config;
import android.tradehero.models.Token;
import android.tradehero.models.Trend;
import android.tradehero.models.User;
import android.tradehero.utills.Constants;
import android.tradehero.utills.Logger;
import android.tradehero.utills.Logger.LogLevel;
import android.util.Base64;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


public class TrendingFragment extends Fragment {
	
	private final static String TAG = TrendingFragment.class.getSimpleName();
	private final static String[] SEARCH_TYPE = {"Stocks", "People"};
	
	private GridView mTrendingGridView;
	private ListView mSearchListView;
	private ProgressBar mProgressSpinner;
	
	private List<Trend> trendList;
	private List<Trend> searchStockList;
	private List<User> searchPoepleList;
	
	private Spinner mSearchTypeSpinner;
	private TextView mHeaderText;
	private EditText mSearchField;
	private View mSearchContainer;
	private ImageButton mBackBtn;
	private ImageButton mSearchBtn;
	private ImageView mBullIcon;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.fragment_trending, container, false);
		initViews(view);
		return view;
	}
	
	private void initViews(View v) {
		mTrendingGridView = (GridView) v.findViewById(R.id.trendig_gridview);
		mSearchListView = (ListView) v.findViewById(R.id.trendig_listview);
		mProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);
		mSearchTypeSpinner = (Spinner) v.findViewById(R.id.spinner);
		mHeaderText =  (TextView) v.findViewById(R.id.header_txt);
		mSearchField = (EditText) v.findViewById(R.id.searh_field);
		mBullIcon = (ImageView) v.findViewById(R.id.logo_img);
		mSearchBtn = (ImageButton) v.findViewById(R.id.btn_search);
		mSearchBtn.setVisibility(View.VISIBLE);
		mBackBtn = (ImageButton) v.findViewById(R.id.btn_back);		
		mSearchContainer = (RelativeLayout) v.findViewById(R.id.search_container);
		
		mHeaderText.setText(R.string.header_trending);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, SEARCH_TYPE);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSearchTypeSpinner.setAdapter(adapter);
		
		mSearchField.addTextChangedListener(new SearchFieldWatcher());
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSearchView(false);
				showSearchList(false);
				
			}
		});
		
		mSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSearchView(true);
			}
		});
		
		
		if(trendList != null && trendList.size() > 0)
			setDataAdapterToGridView(trendList);
		//else {
		//	trendList = new ArrayList<Trend>();
		//	mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), trendList));
		//}

		mSearchTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mSearchField.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		
		mTrendingGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Trend t = (Trend)parent.getItemAtPosition(position);
				((App)getActivity().getApplication()).setTrend(t);
				pushTrendingDetailFragment();
			}
		});
		
		mSearchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				//((String)mSearchTypeSpinner.getSelectedItem()).equalsIgnoreCase(SEARCH_TYPE[0])
				if(adapter.getItemAtPosition(position) instanceof Trend) {
					Trend t = (Trend)adapter.getItemAtPosition(position);
					((App)getActivity().getApplication()).setTrend(t);
					pushTrendingDetailFragment();
				}
				else {
					Toast.makeText(getActivity(), "Under progress...", Toast.LENGTH_SHORT).show();
					//TODO Push the user detail screen
				}
				
			}
		});
		
		requestToGetTrendingInfo();	
	}
	
	private void pushTrendingDetailFragment() {
		Fragment newFragment = Fragment.instantiate(getActivity(),
				TrendingDetailFragment.class.getName(), null);
        // Add the fragment to the activity, pushing this transaction
        // on to the back stack.
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.realtabcontent, newFragment, "trending_detail");
        ft.addToBackStack("trending_detail");
        ft.commit();
	}
	
	private void setDataAdapterToGridView(List<Trend> trendList) {
		mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), trendList));
	}
	
//	private void setInitialAdapterToGridView() {
//		trendList = new ArrayList<Trend>();
//		mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), trendList));
//	}
	
	private void setDataAdapterToSearchListView(List<Trend> trendList) {
		mSearchListView.setAdapter(new SearchStockAdapter(getActivity(), trendList));
	}
	
	private void requestToGetTrendingInfo() {
		
		if(mTrendingGridView != null && mTrendingGridView.getCount() == 0)
			mProgressSpinner.setVisibility(View.VISIBLE);
		
		AsyncHttpClient client = new AsyncHttpClient(); 
		client.get(Config.getTrendingFeed(), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				
				try {
					Logger.log(TAG, "Trending Response: ---\n"+response, LogLevel.LOGGING_LEVEL_INFO);
					ObjectMapper objectMapper = new ObjectMapper();
					trendList = objectMapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, Trend.class));
					setDataAdapterToGridView(trendList);
				} 
				catch (JsonParseException e) {
					e.printStackTrace();
				} 
				catch (JsonMappingException e) {
					e.printStackTrace();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				mProgressSpinner.setVisibility(View.GONE);
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				
			}
		});
	}
	
	private void requestToGetSearchQuery(String searchQuery, String searchType, String pageNumber) {
		
		mProgressSpinner.setVisibility(View.VISIBLE);
		
		Token mToken = ((App)getActivity().getApplication()).getToken();
		
		AsyncHttpClient client = new AsyncHttpClient(); 
		String  authToken = Base64.encodeToString(mToken.getToken().getBytes(), Base64.DEFAULT);
		client.addHeader(Constants.TH_CLIENT_VERSION, Constants.TH_CLIENT_VERSION_VALUE);
		client.addHeader(Constants.AUTHORIZATION, String.format("%s %s", Constants.TH_EMAIL_PREFIX, authToken));
		
		client.get(String.format(Config.getTrendSearch(), searchType, searchQuery, pageNumber), 
				new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				
				try {
					Logger.log(TAG, "Search Response: ---\n"+response, LogLevel.LOGGING_LEVEL_INFO);
					
					if(((String)mSearchTypeSpinner.getSelectedItem()).equalsIgnoreCase(SEARCH_TYPE[0])) {
						ObjectMapper objectMapper = new ObjectMapper();
						searchStockList = objectMapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, Trend.class));
						mSearchListView.setAdapter(new SearchStockAdapter(getActivity(), searchStockList));
						setDataAdapterToSearchListView(searchStockList);
					}
					else {
						ObjectMapper objectMapper = new ObjectMapper();
						searchPoepleList = objectMapper.readValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, User.class));
						mSearchListView.setAdapter(new SearchPeopleAdapter(getActivity(), searchPoepleList));
						
					}
					
					
				} 
				catch (JsonParseException e) {
					e.printStackTrace();
				} 
				catch (JsonMappingException e) {
					e.printStackTrace();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				mProgressSpinner.setVisibility(View.GONE);
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				System.out.println("onFailure: "+arg1);
				
			}
		});
	}
	
	private class SearchFieldWatcher implements TextWatcher {
	
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			if (s.length() > 0) {
				
				showSearchList(true);
				
				String searchType = "securities";
				if(((String)mSearchTypeSpinner.getSelectedItem()).equalsIgnoreCase(SEARCH_TYPE[1])) {
					searchType = "users";
				}
				
				requestToGetSearchQuery(s.toString(), searchType, "1");
			}
			else
				showSearchList(false);
				
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}
	
	private void showSearchList(boolean flag) {
		mSearchListView.setVisibility(flag ? View.VISIBLE : View.GONE);
		mTrendingGridView.setVisibility(!flag ? View.VISIBLE : View.GONE);
		mProgressSpinner.setVisibility(View.GONE);
	}
	
	private void showSearchView(boolean flag) {
		mHeaderText.setVisibility(!flag ? View.VISIBLE : View.INVISIBLE);
		mBullIcon.setVisibility(!flag ? View.VISIBLE : View.INVISIBLE);
		mSearchContainer.setVisibility(flag ? View.VISIBLE : View.GONE);
		mSearchField.setText("");
	}

	@Override
	public void onResume() {
		super.onResume();
		((TradeHeroTabActivity)getActivity()).showTabs(true);
		((App)getActivity().getApplication()).setTrend(null);
	}
	
	
}
