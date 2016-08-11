package com.androidth.general.fragments.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.fragments.base.BaseDialogFragment;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;

public class LiveFormConfirmationFragment extends BaseDialogFragment {
    private static final String KEY_BUNDLE_RP_SUBMITTED_FLAG = LiveFormConfirmationFragment.class.getName() + ".residencyProof";
    private static final String KEY_BUNDLE_ID_SUBMITTED_FLAG = LiveFormConfirmationFragment.class.getName() + ".idProof";
    private static final String KEY_BUNDLE_PROVIDER_ID = LiveFormConfirmationFragment.class.getName() + ".providerId";
    private static final String KEY_BUNDLE_LOGO_URL = LiveFormConfirmationFragment.class.getName() + ".logoUrl";

    @Inject LiveServiceWrapper liveServiceWrapper;
    @Inject Picasso picasso;

    @Bind(R.id.live_form_brand_logo) ImageView logoIV;
    @Bind(R.id.live_form_competition_logo) ImageView competitionIV;
    @Bind(R.id.live_form_header) RelativeLayout header;

    @Bind(R.id.live_form_confirmation_description) TextView confirmationDescription;
    @Bind(R.id.live_form_below_description) TextView belowDescription;
    @Bind(R.id.live_form_reminder_header) TextView reminderHeader;

    public static String notificationLogoUrl = LiveSignUpMainFragment.notificationLogoUrl;
    public static String hexColor = LiveSignUpMainFragment.hexColor;
    private boolean isIdSubmitted, isRPSubmitted; //residency proof and ID
    private SubscriptionList onDestroyViewSubscriptions;
    private int providerId;
    private String logoUrl;

    private static LiveFormConfirmationFragment newInstance(boolean isIdSubmitted,
                                                            boolean isRPSubmitted,
                                                            int providerId,
                                                            String logoUrl) {
        Bundle b = new Bundle();
        b.putBoolean(KEY_BUNDLE_ID_SUBMITTED_FLAG, isIdSubmitted);
        b.putBoolean(KEY_BUNDLE_RP_SUBMITTED_FLAG, isRPSubmitted);
        b.putInt(KEY_BUNDLE_PROVIDER_ID, providerId);
        b.putString(KEY_BUNDLE_LOGO_URL, logoUrl);
        LiveFormConfirmationFragment fragment = new LiveFormConfirmationFragment();
        fragment.setArguments(b);
        return fragment;
    }

    public static LiveFormConfirmationFragment show(int requestCode,
                                                    Fragment targetFragment,
                                                    boolean isIdSubmitted,
                                                    boolean isRPSubmitted,
                                                    int providerId,
                                                    String logoUrl) {
        LiveFormConfirmationFragment vdf = newInstance(isIdSubmitted, isRPSubmitted, providerId, logoUrl);
        vdf.setTargetFragment(targetFragment, requestCode);
        vdf.show(targetFragment.getChildFragmentManager(), vdf.getClass().getName());
        return vdf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        isRPSubmitted = bundle.getBoolean(KEY_BUNDLE_RP_SUBMITTED_FLAG);
        isIdSubmitted = bundle.getBoolean(KEY_BUNDLE_ID_SUBMITTED_FLAG);
        logoUrl = bundle.getString(KEY_BUNDLE_LOGO_URL);
        providerId = bundle.getInt(KEY_BUNDLE_PROVIDER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_live_confirmation_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        confirmationDescription.setText("Your registration details have\nbeen submitted to");

        if(isIdSubmitted&&isRPSubmitted) {
            belowDescription.setText("A representative will get in touch with you soon!");
            reminderHeader.setVisibility(View.GONE);
        }else{
            reminderHeader.setVisibility(View.VISIBLE);
            belowDescription.setText("");
            int count = 1;

            String missingDocs = "";
            if(!isIdSubmitted){
                missingDocs = count++ +". "+"Proof of identity\n" +
                        "(e.g. Passport, driver\'s license)\n\n";
            }

            if(!isRPSubmitted){
                missingDocs = count++ +". "+"Proof of residency\n" +
                        "(e.g. Utility bill, credit card statement)\n\n";
            }

            belowDescription.setText(missingDocs);
        }

        header.setBackgroundColor(Color.parseColor("#" + hexColor));
        try {

            Observable<Bitmap> bannerObservable = Observable.defer(() -> {
                try {
                    return Observable.just(picasso.with(getContext()).load(notificationLogoUrl).get());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });

            Observable<Bitmap> logoObservable = Observable.defer(() -> {
                try {
                    return Observable.just(picasso.with(getContext()).load(logoUrl).get());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });

            onDestroyViewSubscriptions = new SubscriptionList();

            onDestroyViewSubscriptions.add(
                    bannerObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bitmap -> {
//                        int height = (int) (competitionIV.getHeight() * 0.6);
//                        int bitmapHt = bitmap.getHeight();
//                        int bitmapWd = bitmap.getWidth();
//                        int width = height * (bitmapWd / bitmapHt);
//                        bitmap = Bitmap.createScaledBitmap(bitmap, width, competitionIV.getHeight(), true);
                        competitionIV.setImageBitmap(bitmap);
                    }, throwable -> {
                        Log.e("Error", "" + throwable.getMessage());
                    })
            );

            onDestroyViewSubscriptions.add(
                    logoObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bitmap -> {
                        logoIV.setImageBitmap(bitmap);
                    }, throwable -> {
                        Log.e("Error", "" + throwable.getMessage());
                    })
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    @OnClick(android.R.id.closeButton)
    protected void onCloseClicked(View button) {
        Intent i = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        dismiss();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        onDestroyViewSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    protected boolean shouldCancelOnOutsideClicked() {
        return false;
    }
}