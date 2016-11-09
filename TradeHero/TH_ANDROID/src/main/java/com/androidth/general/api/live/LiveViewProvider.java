package com.androidth.general.api.live;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.androidth.general.activities.SignUpLiveActivity;
import com.androidth.general.fragments.live.LiveViewFragment;
import com.androidth.general.fragments.trade.Live1BWebLoginDialogFragment;
import com.androidth.general.utils.ExceptionUtils;
import com.androidth.general.utils.LiveConstants;

import org.json.JSONObject;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

public class LiveViewProvider {

    public static final String BUNDLE_KEY_REDIRECT_URL_ID = "ssoUrl";

    private static String currentRedirectUrl;

    public static void showTradeHubLogin(Fragment fragment, Throwable errorResponse){
        try {
            if (LiveConstants.hasLiveAccount) {

                // user has a live account, but not logged in, redirect to the extracted json URL
                String redirectURL = ExceptionUtils.getStringElementFromThrowable(errorResponse, BUNDLE_KEY_REDIRECT_URL_ID);

                currentRedirectUrl = redirectURL;//just for backup

                showTradeHubLogin(fragment, redirectURL);

            }

        } catch (Exception e) {
            Toast.makeText(fragment.getContext(), "Error in redirection" , Toast.LENGTH_LONG).show();
            Log.d("flipLiveLogin Error ", e.toString());
        }
    }

    public static void showTradeHubLogin(Fragment fragment){
        showTradeHubLogin(fragment, currentRedirectUrl);
    }

//    public static void showTradeHubLogin(Activity activity, String redirectUrl){
//        Bundle args = new Bundle();
//        args.putString(BUNDLE_KEY_REDIRECT_URL_ID, redirectUrl);
////        Live1BWebLoginDialogFragment liveLoginFragment = new Live1BWebLoginDialogFragment();
////        liveLoginFragment.setArguments(args);
////
////        liveLoginFragment.setOnDismissListener(new DialogInterface.OnDismissListener(){
////            @Override
////            public void onDismiss(DialogInterface dialog) {
////                //    Toast.makeText(getContext(),"Now you can start trading Live!", Toast.LENGTH_LONG).show();
////
////            }
////        });
//
//        try{
//            Live1BWebLoginDialogFragment.show(activity.getFragmentManager().get,
//                    Live1BWebLoginDialogFragment.class.getName(), args, 0);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public static void showTradeHubLogin(Fragment fragment, String redirectUrl){
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_REDIRECT_URL_ID, redirectUrl);

        try{
            Live1BWebLoginDialogFragment.show(fragment,
                    Live1BWebLoginDialogFragment.class.getName(), args, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
