package android.tradehero.Utills;

import android.content.Context;
import android.widget.Toast;

public class Constants {
	
	
	public static void show_toast(Context ctx,String msg){
		
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
		
	}

}
