package com.androidth.general.fragments.live;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.DatePicker;

import com.androidth.general.R;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.base.BaseDialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerDialogFragment extends BaseDialogFragment implements DatePickerDialog.OnDateSetListener
{
    private static final String INTENT_KEY_DATE_BUNDLE = DatePickerDialogFragment.class.getName() + ".dateBundle";

    private static final String BUNDLE_KEY_MAX_DATE = DatePickerDialogFragment.class.getName() + ".maxDate";
    private static final String BUNDLE_KEY_SELECTED_DATE = DatePickerDialogFragment.class.getName() + ".selectedDate";

    private static final String BUNDLE_KEY_YEAR = DatePickerDialogFragment.class.getName() + ".year";
    private static final String BUNDLE_KEY_MONTH_OF_YEAR = DatePickerDialogFragment.class.getName() + ".monthOfYear";
    private static final String BUNDLE_KEY_DAY_OF_MONTH = DatePickerDialogFragment.class.getName() + ".dayOfMonth";

    private Date maxDate;

    @NonNull public static DatePickerDialogFragment newInstance(@NonNull Calendar maxDate, @Nullable Calendar selectedDate)
    {
        DatePickerDialogFragment dpf = new DatePickerDialogFragment();
        Bundle b = new Bundle();
        setCalendar(b, BUNDLE_KEY_MAX_DATE, maxDate);
        if (selectedDate != null)
        {
            setCalendar(b, BUNDLE_KEY_SELECTED_DATE, selectedDate);
        }
        dpf.setArguments(b);
        return dpf;
    }

    public static void setCalendar(@NonNull Bundle args, @NonNull String bundleKey, @NonNull Calendar calendar)
    {
        Bundle b = new Bundle();
        b.putInt(BUNDLE_KEY_YEAR, calendar.get(Calendar.YEAR));
        b.putInt(BUNDLE_KEY_MONTH_OF_YEAR, calendar.get(Calendar.MONTH));
        b.putInt(BUNDLE_KEY_DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        args.putBundle(bundleKey, b);
    }

    @NonNull public static Calendar getCalendarFromIntent(@NonNull Intent intent)
    {
        Bundle extras = intent.getBundleExtra(DatePickerDialogFragment.INTENT_KEY_DATE_BUNDLE);
        if (extras == null)
        {
            throw new NullPointerException("No bundle extra found with key " + DatePickerDialogFragment.INTENT_KEY_DATE_BUNDLE);
        }
        Bundle calendarBundle = extras.getBundle(DatePickerDialogFragment.BUNDLE_KEY_SELECTED_DATE);
        if (calendarBundle == null)
        {
            throw new NullPointerException("No calendar bundle found with key " + DatePickerDialogFragment.BUNDLE_KEY_SELECTED_DATE);
        }
        return getCalendar(calendarBundle);
    }

    @NonNull public static Calendar getCalendar(@NonNull Bundle args)
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
        Bundle maxDateBundle = getArguments().getBundle(BUNDLE_KEY_MAX_DATE);
        if (maxDateBundle == null)
        {
            throw new NullPointerException("No calendar bundle found with key " + DatePickerDialogFragment.BUNDLE_KEY_MAX_DATE);
        }
        // Get max date
        Calendar c = getCalendar(maxDateBundle);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        maxDate = c.getTime();

        Bundle selectedDateBundle = getArguments().getBundle(BUNDLE_KEY_SELECTED_DATE);
        // Get the selected Date if exist;
        if (selectedDateBundle != null)
        {
            Calendar selected = getCalendar(selectedDateBundle);
            year = selected.get(Calendar.YEAR);
            month = selected.get(Calendar.MONTH);
            day = selected.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog d = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, this, year, month, day);
//        d.getDatePicker().setMaxDate(maxDate.getTime());
//        d.getDatePicker().setSpinnersShown(true);
        d.getDatePicker().setCalendarViewShown(false);
        return d;
    }

    @Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);

        //We set the hour, minute, and seconds to 0 since we don't care about them,
        //we only need to compare the d-m-y portion of the date
        c.set(year, monthOfYear, dayOfMonth, 0, 0, 0);

        Date selected = c.getTime();
        if (selected.after(maxDate))
        {
            int minAge = currentYear - c.get(Calendar.YEAR);
            THToast.show(getString(R.string.error_date_earlier, minAge));
        }
        else
        {
            Intent i = new Intent();
            Bundle b = new Bundle();
            DatePickerDialogFragment.setCalendar(b, BUNDLE_KEY_SELECTED_DATE, c);
            i.putExtra(INTENT_KEY_DATE_BUNDLE, b);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
            dismiss();
        }
    }
}
