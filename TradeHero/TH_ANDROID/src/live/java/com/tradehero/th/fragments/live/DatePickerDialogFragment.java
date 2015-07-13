package com.tradehero.th.fragments.live;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import java.util.Calendar;
import java.util.Date;
import timber.log.Timber;

public class DatePickerDialogFragment extends BaseDialogFragment implements DatePickerDialog.OnDateSetListener
{
    public static final String INTENT_KEY_DATE_BUNDLE = DatePickerDialogFragment.class.getName() + ".dateBundle";
    private static final String BUNDLE_KEY_YEAR = DatePickerDialogFragment.class.getName() + ".year";
    private static final String BUNDLE_KEY_MONTH_OF_YEAR = DatePickerDialogFragment.class.getName() + ".monthOfYear";
    private static final String BUNDLE_KEY_DAY_OF_MONTH = DatePickerDialogFragment.class.getName() + ".dayOfMonth";

    private Date maxDate;

    public static DatePickerDialogFragment newInstance(@NonNull Calendar maxDate)
    {
        DatePickerDialogFragment dpf = new DatePickerDialogFragment();
        Bundle b = new Bundle();
        setCalendar(b, maxDate);
        dpf.setArguments(b);
        return dpf;
    }

    public static void setCalendar(@NonNull Bundle args, @NonNull Calendar calendar)
    {
        args.putInt(BUNDLE_KEY_YEAR, calendar.get(Calendar.YEAR));
        args.putInt(BUNDLE_KEY_MONTH_OF_YEAR, calendar.get(Calendar.MONTH));
        args.putInt(BUNDLE_KEY_DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar getCalendar(Bundle args)
    {
        int year = args.getInt(BUNDLE_KEY_YEAR);
        int month = args.getInt(BUNDLE_KEY_MONTH_OF_YEAR);
        int day = args.getInt(BUNDLE_KEY_DAY_OF_MONTH);

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        return c;
    }

    @NonNull @Override public Dialog onCreateDialog(@NonNull Bundle savedInstanceState)
    {
        // Use the max date as the default date in the picker
        final Calendar c = getCalendar(getArguments());

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        maxDate = c.getTime();
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog d = new DatePickerDialog(getActivity(), this, year, month, day);
        d.getDatePicker().setMaxDate(maxDate.getTime());
        return d;
    }

    @Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        Date selected = c.getTime();
        if (selected.after(maxDate))
        {
            THToast.show(R.string.error_date_earlier);
            Timber.e("Selected date is not allowed");
        }
        else
        {
            Timber.d("Date picked %d %d %d", year, monthOfYear, dayOfMonth);
            Intent i = new Intent();
            Bundle b = new Bundle();
            DatePickerDialogFragment.setCalendar(b, c);
            i.putExtra(INTENT_KEY_DATE_BUNDLE, b);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
            dismiss();
        }
    }
}
