package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.TradeRecord;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.pagination.PaginationDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubOptFragment extends Fragment implements View.OnClickListener{

    @Inject Analytics analytics;
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    @Inject public Lazy<PrettyTime> prettyTime;

    private SecurityId securityId;

    private ImageView emptyIV;
    private LinearLayout optsLL;
    private TextView moreTV;

    private TradeRecordViewHolder[] viewHolders = new TradeRecordViewHolder[5];

    private static String[] TRADE_TYPES = new  String[2];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
        TRADE_TYPES[0] = getString(R.string.trade_list_button_buy);
        TRADE_TYPES[1] = getString(R.string.trade_list_button_sell);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_detail_opt, container, false);
        emptyIV = (ImageView)view.findViewById(R.id.imageview_sub_opt_empty);
        optsLL = (LinearLayout)view.findViewById(R.id.linearlayout_opts);
        moreTV = (TextView)view.findViewById(R.id.textview_more);
        moreTV.setOnClickListener(this);
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveTradeRecords();
    }

    private void initArguments() {
        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE);
        if (securityIdBundle != null) {
            securityId = new SecurityId(securityIdBundle);
        }
    }

    private void initViews(View view) {
        View parent = view.findViewById(R.id.linearlayout_opt0);
        ImageView avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator0);
        TextView username = (TextView) view.findViewById(R.id.textview_security_opt_name0);
        TextView tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt0);
        TextView currency = (TextView) view.findViewById(R.id.textview_security_opt_mark0);
        TextView cost = (TextView) view.findViewById(R.id.textview_security_opt_price0);
        TextView quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount0);
        TextView date = (TextView) view.findViewById(R.id.textview_security_opt_date0);
        View separate = view.findViewById(R.id.line0);
        viewHolders[0] = new TradeRecordViewHolder(parent, avatar, username, tradeType, currency, cost, quantity, date, separate);

        parent = view.findViewById(R.id.linearlayout_opt1);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator1);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name1);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt1);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark1);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price1);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount1);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date1);
        separate = view.findViewById(R.id.line1);
        viewHolders[1] = new TradeRecordViewHolder(parent, avatar, username, tradeType, currency, cost, quantity, date, separate);

        parent = view.findViewById(R.id.linearlayout_opt2);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator2);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name2);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt2);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark2);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price2);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount2);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date2);
        separate = view.findViewById(R.id.line2);
        viewHolders[2] = new TradeRecordViewHolder(parent, avatar, username, tradeType, currency, cost, quantity, date, separate);

        parent = view.findViewById(R.id.linearlayout_opt3);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator3);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name3);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt3);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark3);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price3);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount3);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date3);
        separate = view.findViewById(R.id.line3);
        viewHolders[3] = new TradeRecordViewHolder(parent, avatar, username, tradeType, currency, cost, quantity, date, separate);

        parent = view.findViewById(R.id.linearlayout_opt4);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator4);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name4);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt4);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark4);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price4);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount4);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date4);
        separate = view.findViewById(R.id.line4);
        viewHolders[4] = new TradeRecordViewHolder(parent, avatar, username, tradeType, currency, cost, quantity, date, separate);
        
    }


    private void enterUserOptsPage() {
        Bundle bundle = new Bundle();
        pushFragment(SecurityUserOptFragment.class, bundle);
    }

    private DashboardNavigator getDashboardNavigator() {
        DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null) {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    private Fragment pushFragment(Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.textview_more:
                enterUserOptsPage();
                break;
        }
    }

    private void retrieveTradeRecords() {
        Callback<List<TradeRecord>> callback = new Callback<List<TradeRecord>>() {
            @Override
            public void success(List<TradeRecord> tradeRecordList, Response response) {
                for (int i = 0; i < tradeRecordList.size(); i++) {
                    viewHolders[i].display(tradeRecordList.get(i), prettyTime.get());
                }
                for (int i = tradeRecordList.size(); i < viewHolders.length; i++) {
                    viewHolders[i].gone();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        quoteServiceWrapper.getTradeRecords(securityId, 1, 5, callback);
    }

    static class TradeRecordViewHolder {
        private View parent;
        private ImageView avatar;
        private TextView username;
        private TextView tradeType;
        private TextView currency;
        private TextView cost;
        private TextView quantity;
        private TextView date;
        private View separate;

        public TradeRecordViewHolder(View parent, ImageView avatar,
                 TextView username, TextView tradeType,
                 TextView currency, TextView cost, TextView quantity,
                 TextView date, View separate) {
            this.parent = parent;
            this.avatar = avatar;
            this.username = username;
            this.tradeType = tradeType;
            this.currency = currency;
            this.cost = cost;
            this.quantity = quantity;
            this.date = date;
            this.separate = separate;
        }

        public void display(TradeRecord tradeRecord, PrettyTime prettyTime) {
            parent.setVisibility(View.VISIBLE);
            if (separate != null) {
                separate.setVisibility(View.VISIBLE);
            }

            ImageLoader.getInstance().displayImage(tradeRecord.userPicUrl,
                    avatar,
                    UniversalImageLoader.getAvatarImageLoaderOptions());
            username.setText(tradeRecord.userName);

            currency.setText(tradeRecord.currencyDisplay);
            cost.setText(SecurityCompactDTO.getShortValue(tradeRecord.price));
            THSignedNumber signedQuantity = THSignedNumber.builder(tradeRecord.quantity)
                    .build();
            quantity.setText(signedQuantity.toString());
            date.setText(prettyTime.formatUnrounded(tradeRecord.datetimeUtc));

            if (tradeRecord.quantity > 0) {
                tradeType.setText(TRADE_TYPES[0]);
            } else {
                tradeType.setText(TRADE_TYPES[1]);
            }
            tradeType.setTextColor(signedQuantity.getColor());

        }

        public void gone() {
            if (separate != null) {
                separate.setVisibility(View.GONE);
            }
            parent.setVisibility(View.GONE);
        }
    }
}
