/**
 * YUtils.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 26, 2013
 */
package android.tradehero.utills;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;
import android.tradehero.utills.Logger.LogLevel;

public class YUtils {
	
	private final static String TAG = YUtils.class.getSimpleName();
	
	private static String[] YAHOO_QUOTE_KEYS = 
		  { "s",
		    "x",
		    "n",
		    "p",
		    "d1",
		    "d2",
		    "a",
		    "b",
		    "o",
		    "y",
		    "v",
		    "a2",
		    "j1",
		    "p6",
		    "r",
		    "e",
		    "q",
		    "r1",
		    "d",
		    "l",
		    "l1",
		    "l2",
		    "l3",
		    "g",
		    "h",
		    "s7",
		    "j5",
		    "j6",
		    "k2",
		    "k4",
		    "k5",
		    "m",
		    "m2",
		    "m3",
		    "m4",
		    "m5",
		    "m6",
		    "m7",
		    "m8",
		    "t1",
		    "b2",
		    "b3",
		    "e1"};
	
	public static String[] YAHOO_QUOTE_VALUES = 
		{	"Symbol",
		    "Stock Exchange",
		    "Name",
		    "Previous Close",
		    "Last Trade Date",
		    "Trade Date",
		    "Ask",
		    "Bid",
		    "Open",
		    "Dividend Yield",
		    "Volume",
		    "Average Daily Volume",
		    "Market Capitalization",
		    "Price/Book",
		    "P/E Ratio",
		    "Earnings/Share",
		    "Ex-Dividend Date",
		    "Dividend Pay Date",
		    "Dividend/Share",
		    "Last Trade (With Time)",
		    "Last Trade (Price Only)",
		    "High Limit",
		    "Low Limit",
		    "Day's Low",
		    "Day's High",
		    "Short Ratio",
		    "Change From 52-week Low",
		    "Percent Change From 52-week Low",
		    "Change Percent (Real-time)",
		    "Change From 52-week High",
		    "Percent Change From 52-week High",
		    "Day's Range",
		    "Day's Range (Real-time)",
		    "50-day Moving Average",
		    "200-day Moving Average",
		    "Change From 200-day Moving Average",
		    "Percent Change From 200-day Moving Average",
		    "Change From 50-day Moving Average",
		    "Percent Change From 50-day Moving Average",
		    "Last Trade Time",
		    "Ask (Real-time)",
		    "Bid (Real-time)",
		    "Error Indication"
	};
	
	public static List<String> CSVToStringList(String csvStr) {
		if(csvStr != null && csvStr.length() > 0)
			return Arrays.asList(csvStr.split("\\s*,\\s*"));
		else
			return null;
	}
	
	public static String getYahooQuoteKeysString() {
		return join(Arrays.asList(YAHOO_QUOTE_KEYS).iterator(), "");
	}
	
	private static String join(Iterator<String> pieces, String glue) {
        StringBuilder s = new StringBuilder();
        while (pieces.hasNext()) {
            s.append(pieces.next());

            if (pieces.hasNext()) {
                s.append(glue);
            }
        }
        return s.toString();
    }
	
	public static double parseQuoteValue(String value) {
		Logger.log(TAG, "parseQuoteValue: "+value, LogLevel.LOGGING_LEVEL_DEBUG);
		if(!TextUtils.isEmpty(value)) {
			try {
				double requiredValue = Double.parseDouble(value);
				if(!Double.isNaN(requiredValue)) {
					return requiredValue;
				}
			}
			catch(NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}
		return Double.NaN;
	}
	
	public static String largeNumberFormat(String value) {
		 double d = parseQuoteValue(value);
		 
		 if(!Double.isNaN(d)) {
			 double absolute = Math.abs(d);
			 if(absolute >= 1000000000)
				 return String.format("%s%s", String.valueOf(d/1000000000), "B");
		 }
		 
		 if(!Double.isNaN(d)) {
			 double absolute = Math.abs(d);
			 if(absolute >= 1000000)
				 return String.format("%s%s", String.valueOf(d/1000000), "M");
		 }
		 
		 if(!Double.isNaN(d)) {
			 double absolute = Math.abs(d);
			 if(absolute >= 1000)
				 return String.format("%s%s", String.valueOf(d/1000), "K");
		 }
		 
		return value;
	}
		
}
