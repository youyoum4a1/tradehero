package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Subscription;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class BaseFragment extends Fragment
{
    private static final String BUNDLE_KEY_HAS_OPTION_MENU = BaseFragment.class.getName() + ".hasOptionMenu";
    private static final String BUNDLE_KEY_IS_OPTION_MENU_VISIBLE = BaseFragment.class.getName() + ".isOptionMenuVisible";

    public static final boolean DEFAULT_HAS_OPTION_MENU = true;
    public static final boolean DEFAULT_IS_OPTION_MENU_VISIBLE = true;
    private static final int MENU_GROUP_HELP = "MENU_GROUP_HELP".hashCode();

    protected boolean hasOptionMenu;
    protected boolean isOptionMenuVisible;

    protected ActionBarOwnerMixin actionBarOwnerMixin;
    protected SubscriptionList onStopSubscriptions;

    @Inject protected Lazy<DashboardNavigator> navigator;

    public static void setHasOptionMenu(@NonNull Bundle args, boolean hasOptionMenu)
    {
        args.putBoolean(BUNDLE_KEY_HAS_OPTION_MENU, hasOptionMenu);
    }

    public static boolean getHasOptionMenu(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_HAS_OPTION_MENU;
        }
        return args.getBoolean(BUNDLE_KEY_HAS_OPTION_MENU, DEFAULT_HAS_OPTION_MENU);
    }

    public static void putIsOptionMenuVisible(@NonNull Bundle args, boolean optionMenuVisible)
    {
        args.putBoolean(BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, optionMenuVisible);
    }

    public static boolean getIsOptionMenuVisible(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_IS_OPTION_MENU_VISIBLE;
        }
        return args.getBoolean(BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, DEFAULT_IS_OPTION_MENU_VISIBLE);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        actionBarOwnerMixin = ActionBarOwnerMixin.of(this);

        isOptionMenuVisible = getIsOptionMenuVisible(getArguments());
        hasOptionMenu = getHasOptionMenu(getArguments());
        setHasOptionsMenu(hasOptionMenu);
    }

    @Override public void onStart()
    {
        super.onStart();
        this.onStopSubscriptions = new SubscriptionList();
    }

    @Override public void onStop()
    {
        this.onStopSubscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (this.onStopSubscriptions != null)
        {
            this.onStopSubscriptions.unsubscribe();
        }
    }

    @Override public void onDestroy()
    {
        actionBarOwnerMixin.onDestroy();
        super.onDestroy();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if (!hasOptionMenu)
        {
            return;
        }

        if (isOptionMenuVisible)
        {
            showSupportActionBar();
        }
        else
        {
            hideSupportActionBar();
        }

        /*
        P2: There is a unnecessary menu button on Me page
        https://www.pivotaltracker.com/n/projects/559137/stories/91165728
        if (this instanceof WithTutorial)
        {
            menu.removeGroup(MENU_GROUP_HELP);
            MenuItem item = menu.add(MENU_GROUP_HELP, getMenuHelpID(), Menu.NONE, R.string.help);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }*/

        actionBarOwnerMixin.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private int getMenuHelpID()
    {
        return (getClass().getName() + ".help").hashCode();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    navigator.get().popFragment();
                    return true;
                }
                break;
        }
        if (item.getItemId() == getMenuHelpID())
        {
            return handleInfoMenuItemClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean handleInfoMenuItemClicked()
    {
        if (this instanceof WithTutorial)
        {
            AlertDialogUtil.popTutorialContent(
                    getActivity(),
                    ((WithTutorial) this).getTutorialLayout());
            return true;
        }
        else
        {
            Timber.d("%s is not implementing WithTutorial interface, but has info menu", getClass().getName());
            return false;
        }
    }

    @Nullable protected ActionBar getSupportActionBar()
    {
        if (getActivity() != null)
        {
            return ((ActionBarActivity) getActivity()).getSupportActionBar();
        }
        else
        {
            Timber.e(new Exception(), "getActivity is Null");
            return null;
        }
    }

    protected void hideSupportActionBar()
    {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.hide();
        }
    }

    protected void showSupportActionBar()
    {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.show();
        }
    }

    protected final void setActionBarTitle(String string)
    {
        actionBarOwnerMixin.setActionBarTitle(string);
    }

    protected final void setActionBarTitle(@StringRes int stringResId)
    {
        actionBarOwnerMixin.setActionBarTitle(stringResId);
    }

    protected void setActionBarSubtitle(@StringRes int subTitleResId)
    {
        actionBarOwnerMixin.setActionBarSubtitle(subTitleResId);
    }

    protected void setActionBarSubtitle(String subtitle)
    {
        actionBarOwnerMixin.setActionBarSubtitle(subtitle);
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null && !subscription.isUnsubscribed())
        {
            subscription.unsubscribe();
        }
    }

    protected void configureDefaultSpinner(String[] data, AdapterView.OnItemSelectedListener listener, int selectedPosition)
    {
        ArrayAdapter adapter = new ToolbarSpinnerAdapter(
                getActivity(),
                R.layout.action_bar_spinner,
                R.id.spinner_text,
                data);
        configureSpinner(R.id.action_bar_spinner, adapter, listener, selectedPosition);
    }

    /**
     * Configure Spinner in the ActionBar, nothing happens if the action bar does not have a spinner.
     */
    protected void configureSpinner(int toolbarSpinnerResId, ArrayAdapter adapter, AdapterView.OnItemSelectedListener listener, int selectedPosition)
    {
        actionBarOwnerMixin.configureSpinner(toolbarSpinnerResId, adapter, listener, selectedPosition);
    }

    /**
     * This method only set Visibility to visible.
     */
    protected void showToolbarSpinner()
    {
        actionBarOwnerMixin.showToolbarSpinner();
    }

    /**
     * Set the spinner's visibility to GONE. Nothing happens if the action bar does not contain a spinner.
     */
    protected void hideToolbarSpinner()
    {
        actionBarOwnerMixin.hideToolbarSpinner();
    }

    class ToolbarSpinnerAdapter extends ArrayAdapter<String>
    {
        int textViewResourceId;

        public ToolbarSpinnerAdapter(Context context, int resource, int textViewResourceId, String[] objects)
        {
            super(context, resource, textViewResourceId, objects);
            this.textViewResourceId = textViewResourceId;
        }

        @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.action_bar_spinner_dropdown, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(textViewResourceId);
            textView.setText(getItem(position));
            return convertView;
        }
    }

    public <T extends Fragment> boolean allowNavigateTo(@NonNull Class<T> fragmentClass, Bundle args)
    {
        return true;
    }
}
