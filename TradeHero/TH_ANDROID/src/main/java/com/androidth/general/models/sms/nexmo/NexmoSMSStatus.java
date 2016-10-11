package com.androidth.general.models.sms.nexmo;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.androidth.general.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * https://docs.nexmo.com/messaging/sms-api/api-reference#status-codes
 */
public class NexmoSMSStatus
{
    private static HashMap<Integer, String> status = new HashMap<>();
    private static ArrayList<HashMap<Integer, String>> statuses = new ArrayList<>();

    private static HashMap<Integer, String> getStatuses(){
        status.put(0, "Success!");
        status.put(1, "Throttled");
        status.put(2, "Missing params");
        status.put(3, "Invalid params");
        status.put(4, "Invalid credentials");
        status.put(5, "Internal error");
        status.put(6, "Invalid message");
        status.put(7, "Number barred");
        status.put(8, "Partner account barred");
        status.put(9, "Parner quota exceeded");
        status.put(13, "Communication failed");

        statuses.add(status);
        return status;
    }

    public static String getStatus(int statusNumber){
        if(getStatuses().containsKey(statusNumber)){
            return getStatuses().get(statusNumber);
        }else{
            if(statusNumber == R.string.sms_verification_button_empty_submitting || statusNumber<0){
                return "Processing";
            }else{
                return "SMS Error! Try again";
            }
        }
    }
}
