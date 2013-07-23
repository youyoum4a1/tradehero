package android.tradehero.utills;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dateutil {

	public static Date convertToDate(String dateString) {

		if (dateString.length() > 10) {
			dateString = dateString.substring(0, 10);
		}
		Date date = null;

		String pattern = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String convertToDateString(Date dateObj) {

		String dateString = null;
		String pattern = "MM-dd-yyyy";
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		try {
			dateString = format.format(dateObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}

	public static int compareDates(String dateString1, String dateString2) {

		Date date1 = convertToDate(dateString1);
		Date date2 = convertToDate(dateString2);

		if (date1 != null && date2 != null) {
			if (date1.after(date2)) {
				return -1;
			} else if (date1.before(date2)) {
				return 1;
			} else {
				return 0;
			}
		} else {
			if (date1 == null && date2 != null) {
				return 1;
			} else if (date1 != null && date2 == null) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
