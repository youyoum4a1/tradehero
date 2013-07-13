package android.tradehero.Http;

import org.json.JSONObject;



public interface RequestTaskCompleteListener {
   public void onTaskComplete(JSONObject pResponseObject);
   public void onErrorOccured(int pErrorCode,String pErrorMessage);
}
