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
import com.tradehero.chinabuild.data.SecurityUserPositionDTO;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubPositionFragment extends Fragment implements View.OnClickListener{

    @Inject QuoteServiceWrapper quoteServiceWrapper;

    private ImageView emptyIV;
    private LinearLayout positionsLL;
    private TextView moreTV;

    private SecurityId securityId;

    private PositionViewHolder[] viewHolders = new PositionViewHolder[5];

    private List<SecurityUserPositionDTO> sharePositionList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    private void initArguments() {
        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE);
        if (securityIdBundle != null) {
            securityId = new SecurityId(securityIdBundle);
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_detail_position, container, false);
        emptyIV = (ImageView)view.findViewById(R.id.imageview_sub_position_empty);
        positionsLL = (LinearLayout)view.findViewById(R.id.linearlayout_positions);
        moreTV = (TextView)view.findViewById(R.id.textview_more);
        moreTV.setOnClickListener(this);

        initViews(view);

        if(SecurityDetailSubCache.getInstance().isSecuritySame(securityId)){
            if(SecurityDetailSubCache.getInstance().getSharePositionList()!= null && SecurityDetailSubCache.getInstance().getSharePositionList().size() > 0){
                displayPositions(SecurityDetailSubCache.getInstance().getSharePositionList());
            } else {
                retrieveSharePositions();
            }
        } else {
            SecurityDetailSubCache.getInstance().clearAll();
            retrieveSharePositions();
        }

        return view;
    }

    private void initViews(View view) {
        View parent0 = view.findViewById(R.id.linearlayout_position0);
        ImageView avatar = (ImageView) view.findViewById(R.id.imageview_security_position_avator0);
        TextView username = (TextView) view.findViewById(R.id.textview_security_position_name0);
        TextView quantity = (TextView) view.findViewById(R.id.textview_security_position_amount0);
        TextView currency = (TextView) view.findViewById(R.id.textview_security_position_mark0);
        TextView price = (TextView) view.findViewById(R.id.textview_security_position_price0);
        TextView tvRoi = (TextView) view.findViewById(R.id.textview_security_position_percent0);
        View separate = view.findViewById(R.id.line0);
        viewHolders[0] = new PositionViewHolder(parent0, avatar, username, quantity, currency, price, tvRoi, separate);
        parent0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharePositionList!=null && sharePositionList.size()>0){
                    enterUserMainPage(sharePositionList.get(0).userId);
                }
            }
        });

        View parent1 = view.findViewById(R.id.linearlayout_position1);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_position_avator1);
        username = (TextView) view.findViewById(R.id.textview_security_position_name1);
        quantity = (TextView) view.findViewById(R.id.textview_security_position_amount1);
        currency = (TextView) view.findViewById(R.id.textview_security_position_mark1);
        price = (TextView) view.findViewById(R.id.textview_security_position_price1);
        tvRoi = (TextView) view.findViewById(R.id.textview_security_position_percent1);
        separate = view.findViewById(R.id.line1);
        viewHolders[1] = new PositionViewHolder(parent1, avatar, username, quantity, currency, price, tvRoi, separate);
        parent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharePositionList!=null && sharePositionList.size()>1){
                    enterUserMainPage(sharePositionList.get(1).userId);
                }
            }
        });

        View parent2 = view.findViewById(R.id.linearlayout_position2);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_position_avator2);
        username = (TextView) view.findViewById(R.id.textview_security_position_name2);
        quantity = (TextView) view.findViewById(R.id.textview_security_position_amount2);
        currency = (TextView) view.findViewById(R.id.textview_security_position_mark2);
        price = (TextView) view.findViewById(R.id.textview_security_position_price2);
        tvRoi = (TextView) view.findViewById(R.id.textview_security_position_percent2);
        separate = view.findViewById(R.id.line2);
        viewHolders[2] = new PositionViewHolder(parent2, avatar, username, quantity, currency, price, tvRoi, separate);
        parent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharePositionList!=null && sharePositionList.size()>2){
                    enterUserMainPage(sharePositionList.get(2).userId);
                }
            }
        });

        View parent3 = view.findViewById(R.id.linearlayout_position3);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_position_avator3);
        username = (TextView) view.findViewById(R.id.textview_security_position_name3);
        quantity = (TextView) view.findViewById(R.id.textview_security_position_amount3);
        currency = (TextView) view.findViewById(R.id.textview_security_position_mark3);
        price = (TextView) view.findViewById(R.id.textview_security_position_price3);
        tvRoi = (TextView) view.findViewById(R.id.textview_security_position_percent3);
        separate = view.findViewById(R.id.line3);
        viewHolders[3] = new PositionViewHolder(parent3, avatar, username, quantity, currency, price, tvRoi, separate);
        parent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharePositionList!=null && sharePositionList.size()>3){
                    enterUserMainPage(sharePositionList.get(3).userId);
                }
            }
        });

        View parent4 = view.findViewById(R.id.linearlayout_position4);
        avatar = (ImageView) view.findViewById(R.id.imageview_security_position_avator4);
        username = (TextView) view.findViewById(R.id.textview_security_position_name4);
        quantity = (TextView) view.findViewById(R.id.textview_security_position_amount4);
        currency = (TextView) view.findViewById(R.id.textview_security_position_mark4);
        price = (TextView) view.findViewById(R.id.textview_security_position_price4);
        tvRoi = (TextView) view.findViewById(R.id.textview_security_position_percent4);
        separate = view.findViewById(R.id.line4);
        viewHolders[4] = new PositionViewHolder(parent4, avatar, username, quantity, currency, price, tvRoi, separate);
        parent4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharePositionList!=null && sharePositionList.size()>4){
                    enterUserMainPage(sharePositionList.get(4).userId);
                }
            }
        });
        
    }

    private void retrieveSharePositions() {
        Callback<List<SecurityUserPositionDTO>> callback = new Callback<List<SecurityUserPositionDTO>>() {
            @Override
            public void success(List<SecurityUserPositionDTO> sharePositionList, Response response) {
                displayPositions(sharePositionList);
                SecurityDetailSubCache.getInstance().setSharePositionList(sharePositionList);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed to get user positions.");
            }
        };
        quoteServiceWrapper.getSharePosition(securityId, 1, 5, callback);
    }

    private void displayPositions(List<SecurityUserPositionDTO> sharePositionList){
        if(emptyIV==null || positionsLL == null){
            return;
        }
        this.sharePositionList = sharePositionList;
        if(sharePositionList == null || sharePositionList.size()<=0){
            emptyIV.setVisibility(View.VISIBLE);
            positionsLL.setVisibility(View.GONE);
            return;
        }else{
            emptyIV.setVisibility(View.GONE);
            positionsLL.setVisibility(View.VISIBLE);
        }
        if(sharePositionList.size() < 5){
            moreTV.setVisibility(View.GONE);
        } else {
            moreTV.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < sharePositionList.size(); i++) {
            viewHolders[i].display(sharePositionList.get(i));
        }
        for (int i = sharePositionList.size(); i < viewHolders.length; i++) {
            viewHolders[i].gone();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.textview_more:
                enterUserPositionsPage();
                break;
        }
    }

    private void enterUserPositionsPage(){
        Bundle bundle = getArguments();
        pushFragment(SecurityUserPositionFragment.class, bundle);
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

    static class PositionViewHolder {
        View parent;
        ImageView avatar;
        TextView username;
        TextView quantity;
        TextView currency;
        TextView price;
        TextView tvRoi;
        View separate;
        
        public PositionViewHolder(View parent,
                ImageView avatar,
                TextView username,
                TextView quantity,
                TextView currency,
                TextView price,
                TextView tvRoi,
                View separate) {
            this.parent = parent;
            this.avatar = avatar;
            this.username = username;
            this.quantity = quantity;
            this.currency = currency;
            this.price = price;
            this.tvRoi = tvRoi;
            this.separate = separate;
        }

        public void display(SecurityUserPositionDTO position) {
            parent.setVisibility(View.VISIBLE);
            if (separate != null) {
                separate.setVisibility(View.VISIBLE);
            }

            ImageLoader.getInstance().displayImage(position.userPicUrl,
                    avatar,
                    UniversalImageLoader.getAvatarImageLoaderOptions());
            username.setText(position.userName);
            THSignedNumber signedQuantity = THSignedNumber.builder(position.quantity).build();
            quantity.setText(signedQuantity.toString());
            currency.setText(position.currencyDisplay);
            price.setText(SecurityCompactDTO.getShortValue(position.price));
            THSignedNumber roi = THSignedPercentage.builder(position.roi * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvRoi.setText(roi.toString());
            tvRoi.setTextColor(roi.getColor());
        }

        public void gone() {
            parent.setVisibility(View.GONE);
            if (separate != null) {
                separate.setVisibility(View.GONE);
            }
        }
    }

    private void enterUserMainPage(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
        bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
        pushFragment(UserMainPage.class, bundle);
    }
}
