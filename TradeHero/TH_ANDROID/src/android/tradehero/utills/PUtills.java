package android.tradehero.utills;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.tradehero.models.ProfileDTO;

public class PUtills {

	private Context ctx;
	private ProfileDTO mdata;

	public PUtills(Context ctx) {
		super();
		this.ctx = ctx;
		mdata = new ProfileDTO();
	}
	
	public ProfileDTO _parseJson(JSONObject mjsonObject){
		
		if(mjsonObject != null)
		{
		
			try {
				/*thLinked": true,
		        "ccPerMonthBalance": 0,
		        "alertCount": 0,
		        "ccBalance": 0,
		        "id": 229667*/
				
				mdata.setDisplayName(mjsonObject.getString("displayName"));
				mdata.setPicture(mjsonObject.getString("picture"));
				mdata.setId(mjsonObject.getString("id"));
				mdata.setFollowerCount(mjsonObject.getString("followerCount"));
				mdata.setAlertCount(mjsonObject.getString("alertCount"));
				mdata.setLiLinked(mjsonObject.getString("thLinked"));
				mdata.setCcPerMonthBalance(mjsonObject.getString("ccPerMonthBalance"));
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		return mdata;
		
		
	}
	
	
}
