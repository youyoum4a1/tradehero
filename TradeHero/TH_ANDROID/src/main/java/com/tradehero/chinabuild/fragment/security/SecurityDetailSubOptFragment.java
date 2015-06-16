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
import com.tradehero.chinabuild.data.SecurityUserOptDTO;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubOptFragment extends Fragment implements View.OnClickListener{

    @Inject QuoteServiceWrapper quoteServiceWrapper;
    @Inject public Lazy<PrettyTime> prettyTime;

    private SecurityId securityId;

    private ImageView emptyIV;
    private LinearLayout optsLL;
    private TextView moreTV;

    private TradeRecordViewHolder[] viewHolders = new TradeRecordViewHolder[5];

    private static String[] TRADE_TYPES = new  String[2];

    private List<SecurityUserOptDTO> tradeRecordList = new ArrayList();

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

        if(SecurityDetailSubCache.getInstance().isSecuritySame(securityId)){
            if(SecurityDetailSubCache.getInstance().getTradeRecordList()!=null && SecurityDetailSubCache.getInstance().getTradeRecordList().size()>0){
                displayTrades(SecurityDetailSubCache.getInstance().getTradeRecordList());
            } else {
                retrieveTradeRecords();
            }
        } else {
            SecurityDetailSubCache.getInstance().clearAll();
            retrieveTradeRecords();
        }

        return view;
    }

    private void initArguments() {
        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE);
        if (securityIdBundle != null) {
            securityId = new SecurityId(securityIdBundle);
        }
    }

    private void initViews(View view) {
        View parent0 = view.findViewById(R.id.linearlayout_opt0);
        ImageView avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator0);
        TextView username = (TextView) view.findViewById(R.id.textview_security_opt_name0);
        TextView tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt0);
        TextView currency = (TextView) view.findViewById(R.id.textview_security_opt_mark0);
        TextView cost = (TextView) view.findViewById(R.id.textview_security_opt_price0);
        TextView quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount0);
        TextView date = (TextView) view.findViewById(R.id.textview_security_opt_date0);
        View separate = view.findViewById(R.id.line0);
        viewHolders[0] = new TradeRecordViewHolder(parent0, avatar, username, tradeType, currency, cost, quantity, date, separate);
        parent0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tradeRecordList!=null && tradeRecordList.size() > 0){
                    SecurityUserOptDTO dto = tradeRecordList.get(0);
                    enterUserMainPage(dto.userId);
                }
            }
        });

        View parent1 = view.findViewById(R.id.linearlayout_opt1);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator1);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name1);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt1);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark1);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price1);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount1);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date1);
        separate = view.findViewById(R.id.line1);
        viewHolders[1] = new TradeRecordViewHolder(parent1, avatar, username, tradeType, currency, cost, quantity, date, separate);
        parent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tradeRecordList!=null && tradeRecordList.size() > 1){
                    SecurityUserOptDTO dto = tradeRecordList.get(1);
                    enterUserMainPage(dto.userId);
                }
            }
        });

        View parent2 = view.findViewById(R.id.linearlayout_opt2);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator2);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name2);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt2);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark2);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price2);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount2);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date2);
        separate = view.findViewById(R.id.line2);
        viewHolders[2] = new TradeRecordViewHolder(parent2, avatar, username, tradeType, currency, cost, quantity, date, separate);
        parent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tradeRecordList!=null && tradeRecordList.size() > 2){
                    SecurityUserOptDTO dto = tradeRecordList.get(2);
                    enterUserMainPage(dto.userId);
                }
            }
        });

        View parent3 = view.findViewById(R.id.linearlayout_opt3);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator3);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name3);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt3);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark3);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price3);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount3);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date3);
        separate = view.findViewById(R.id.line3);
        viewHolders[3] = new TradeRecordViewHolder(parent3, avatar, username, tradeType, currency, cost, quantity, date, separate);
        parent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tradeRecordList!=null && tradeRecordList.size() > 3){
                    SecurityUserOptDTO dto = tradeRecordList.get(3);
                    enterUserMainPage(dto.userId);
                }
            }
        });

        View parent4 = view.findViewById(R.id.linearlayout_opt4);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_opt_avator4);
        username = (TextView) view.findViewById(R.id.textview_security_opt_name4);
        tradeType = (TextView) view.findViewById(R.id.textview_security_opt_opt4);
        currency = (TextView) view.findViewById(R.id.textview_security_opt_mark4);
        cost = (TextView) view.findViewById(R.id.textview_security_opt_price4);
        quantity = (TextView) view.findViewById(R.id.textview_security_opt_mount4);
        date = (TextView) view.findViewById(R.id.textview_security_opt_date4);
        separate = view.findViewById(R.id.line4);
        viewHolders[4] = new TradeRecordViewHolder(parent4, avatar, username, tradeType, currency, cost, quantity, date, separate);
        parent4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tradeRecordList!=null && tradeRecordList.size() > 4){
                    SecurityUserOptDTO dto = tradeRecordList.get(4);
                    enterUserMainPage(dto.userId);
                }
            }
        });
        
    }

    private void enterUserMainPage(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
        bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
        pushFragment(UserMainPage.class, bundle);
    }


    private void enterUserOptsPage() {
        Bundle bundle = getArguments();
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
        Callback<List<SecurityUserOptDTO>> callback = new Callback<List<SecurityUserOptDTO>>() {
            @Override
            public void success(List<SecurityUserOptDTO> tradeRecordList, Response response) {
                SecurityDetailSubCache.getInstance().setTradeRecordList(tradeRecordList);
                displayTrades(tradeRecordList);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed to get user operations.");
            }
        };
        quoteServiceWrapper.getTradeRecords(securityId, 1, 5, callback);
    }

    private void displayTrades(List<SecurityUserOptDTO> tradeRecordList){
        if(emptyIV ==null || optsLL == null){
            return;
        }
        this.tradeRecordList = tradeRecordList;
        if(tradeRecordList == null || tradeRecordList.size()<=0){
            emptyIV.setVisibility(View.VISIBLE);
            optsLL.setVisibility(View.GONE);
            return;
        }else{
            emptyIV.setVisibility(View.GONE);
            optsLL.setVisibility(View.VISIBLE);
        }
        if(tradeRecordList.size()<5){
            moreTV.setVisibility(View.GONE);
        }else{
            moreTV.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < tradeRecordList.size(); i++) {
            viewHolders[i].display(tradeRecordList.get(i), prettyTime.get());
        }
        for (int i = tradeRecordList.size(); i < viewHolders.length; i++) {
            viewHolders[i].gone();
        }
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

        public void display(SecurityUserOptDTO securityUserOptDTO, PrettyTime prettyTime) {
            parent.setVisibility(View.VISIBLE);
            if (separate != null) {
                separate.setVisibility(View.VISIBLE);
            }

            ImageLoader.getInstance().displayImage(securityUserOptDTO.userPicUrl,
                    avatar,
                    UniversalImageLoader.getAvatarImageLoaderOptions());
            username.setText(securityUserOptDTO.userName);

            currency.setText(securityUserOptDTO.currencyDisplay);
            cost.setText(SecurityCompactDTO.getShortValue(securityUserOptDTO.price));
            THSignedNumber signedQuantity = THSignedNumber.builder(securityUserOptDTO.quantity)
                    .build();
            quantity.setText(signedQuantity.toString());
            quantity.setTextColor(signedQuantity.getColor());
            date.setText(prettyTime.formatUnrounded(securityUserOptDTO.datetimeUtc));

            if (securityUserOptDTO.quantity > 0) {
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
