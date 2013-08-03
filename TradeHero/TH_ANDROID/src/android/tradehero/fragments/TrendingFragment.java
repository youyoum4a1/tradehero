package android.tradehero.fragments;


import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.tradehero.activities.R;
import android.tradehero.activities.TradeHeroTabActivity;
import android.tradehero.application.App;
import android.tradehero.application.Config;
import android.tradehero.cache.ImageLoader;
import android.tradehero.cache.ImageLoader.ImageLoadingListener;
import android.tradehero.models.Trend;
import android.tradehero.utills.ImageUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;



public class TrendingFragment extends Fragment{
	
	private final static String TAG = TrendingFragment.class.getSimpleName();
	
	private GridView mTrendingGridView;
	private ProgressBar mProgressSpinner;
	private List<Trend> trendList;
	private TextView mHeaderText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.fragment_trending, container, false);
		initViews(view);
		return view;
	}
	
	private void initViews(View v) {
		mTrendingGridView = (GridView) v.findViewById(R.id.trendig_gridview);
		mProgressSpinner = (ProgressBar) v.findViewById(R.id.progress_spinner);
		mHeaderText =  (TextView) v.findViewById(R.id.header_txt);
		mHeaderText.setText(R.string.header_trending);
				
		if(trendList != null && trendList.size() > 0)
			setDataAdapterToGridView(trendList);
		
		mTrendingGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Trend t = (Trend)parent.getItemAtPosition(position);
				((App)getActivity().getApplication()).setTrend(t);
				
				//Bundle b = new Bundle();
				//b.putString("header", t.getExchange()+":"+t.getSymbol());
				
				Fragment newFragment = Fragment.instantiate(getActivity(),
						TrendingDetailFragment.class.getName(), null);

		        // Add the fragment to the activity, pushing this transaction
		        // on to the back stack.
		        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		        ft.replace(R.id.realtabcontent, newFragment, "trending_detail");
		        ft.addToBackStack("trending_detail");
		        ft.commit();
			}
		});
		
		requestToGetTrendingInfo();
	}
	
	private void setDataAdapterToGridView(List<Trend> trendList) {
		mTrendingGridView.setAdapter(new TrendingAdapter(getActivity(), trendList));
	}
	
	private void requestToGetTrendingInfo() {
		
		if(mTrendingGridView != null && mTrendingGridView.getCount() == 0)
			mProgressSpinner.setVisibility(View.VISIBLE);
		
		AsyncHttpClient client = new AsyncHttpClient(); 
		client.get(Config.getTrendingFeed(), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				
				try {
					System.out.println("trending response---"+response);
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
	
	public class TrendingAdapter extends ArrayAdapter<Trend> {
		
		public TrendingAdapter(Context context, List<Trend> trendList) {
			super(context, 0, trendList);
		}
		
		@SuppressWarnings("deprecation")
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.trending_grid_item, null);
			}
			
			TextView stockName = (TextView) convertView.findViewById(R.id.stock_name);
			TextView exchangeSymbol = (TextView) convertView.findViewById(R.id.exchange_symbol);
			TextView profitIndicator = (TextView) convertView.findViewById(R.id.profit_indicator);
			TextView currencyDisplay = (TextView) convertView.findViewById(R.id.currency_display);
			TextView lastPrice = (TextView) convertView.findViewById(R.id.last_price);
			final SmartImageView stockLogo = (SmartImageView) convertView.findViewById(R.id.stock_logo);
			final SmartImageView stockBgLogo = (SmartImageView) convertView.findViewById(R.id.stock_bg_logo);
			
			Trend trend = getItem(position);
			
			stockName.setText(trend.getName());
			exchangeSymbol.setText(String.format("%s:%s", trend.getExchange(), trend.getSymbol()));
			currencyDisplay.setText(trend.getCurrencyDisplay());
			lastPrice.setText(trend.getLastPrice());
			
			if(trend.getPc50DMA() > 0)
				profitIndicator.setText(getContext().getString(R.string.positive_prefix));
			else if(trend.getPc50DMA() < 0) 
				profitIndicator.setText(getContext().getString(R.string.negetive_prefix));
			
			profitIndicator.setTextColor(colorForPercentage(trend.getPc50DMA()));
			
				
			stockBgLogo.setAlpha(33); //15% opaque
			if(trend.getImageBlobUrl() != null && trend.getImageBlobUrl().length() > 0) {
				//Bitmap b = convertToMutable((new WebImageCache(TrendingActivity.this)).get(trend.getImageBlobUrl()));
				new ImageLoader(getContext()).getBitmapImage(trend.getImageBlobUrl(), new ImageLoadingListener() {
					public void onLoadingComplete(Bitmap loadedImage) {
						final Bitmap b = ImageUtils.convertToMutableAndRemoveBackground(loadedImage);
						stockLogo.setImageBitmap(b);
						stockBgLogo.setImageBitmap(b);
					}
				});	
			}

			return convertView;
		}
	}
	
	
	private int colorForPercentage(int percentage) {
		
		 final int grayValue = 140;
		 final int maxGreenValue = 200;
		 final int maxRedValue = 255;
		    
		 int redValue = 0;
		 int greenValue = 0;
		 int blueValue = 0;
		 float pct = CLAMP(Math.abs(percentage) / 100, 0, 1);
		 if (percentage > 0) {
		      greenValue = (int) (50 + (pct * (maxGreenValue - 50)));
		      blueValue = (int) (greenValue * .3f);
		      redValue = (int) (greenValue * .3f);
		 } 
		 else if (percentage < 0) {
		      redValue = (int) (50 + (pct * (maxRedValue - 50)));
		      blueValue = (int) (redValue * .2f);
		 } else {
		      redValue = grayValue;
		      greenValue = grayValue;
		      blueValue = grayValue;
		 }
		 
		 return Color.rgb(redValue, greenValue, blueValue);
	}
	
	private float CLAMP(float n, int min, int max) {
		 return ((n < min) ? min : (n > max) ? max : n);
	}
	

	@Override
	public void onResume() {
		super.onResume();
		((TradeHeroTabActivity)getActivity()).showTabs(true);
		((App)getActivity().getApplication()).setTrend(null);
	}
	
	
}
