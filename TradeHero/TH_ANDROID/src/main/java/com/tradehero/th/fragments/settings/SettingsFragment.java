package com.tradehero.th.fragments.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.utils.SlowedAsyncTask;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.WebViewFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.VersionUtils;
import dagger.Lazy;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: nia Date: 17/10/13 Time: 12:38 PM To change this template use File | Settings | File Templates. */
public class SettingsFragment extends DashboardFragment
{
    public static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject UserService userService;
    @Inject SessionService sessionService;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    private ProgressDialog progressDialog;
    private Timer signOutTimer;

    private View sendLoveBlock;
    private View sendFeedbackBlock;
    private View faqBlock;
    private View profileBlock;
    private View paypalBlock;
    private View transactionHistoryBlock;

    private CheckBox emailNotificationsCheckbox;

    private View resetHelpScreensBlock;
    private View clearCacheBlock;
    private View signOutBlock;
    private View aboutBlock;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        sendLoveBlock = view.findViewById(R.id.send_love_block);
        if (sendLoveBlock != null)
        {
            sendLoveBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleSendLoveClicked();
                }
            });
        }

        sendFeedbackBlock = view.findViewById(R.id.send_feedback_block);
        if (sendFeedbackBlock != null)
        {
            sendFeedbackBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleSendFeedbackClicked();
                }
            });
            sendFeedbackBlock.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override public boolean onLongClick(View view)
                {
                    handleSendFeedbackLongClicked();
                    return true;
                }
            });
        }

        faqBlock = view.findViewById(R.id.faq_block);
        if (faqBlock != null)
        {
            faqBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleFaqClicked();
                }
            });
        }

        profileBlock = view.findViewById(R.id.profile_block);
        if (profileBlock != null)
        {
            profileBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleProfileClicked();
                }
            });
        }

        paypalBlock = view.findViewById(R.id.paypal_block);
        if (paypalBlock != null)
        {
            paypalBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handlePaypalClicked();
                }
            });
        }

        transactionHistoryBlock = view.findViewById(R.id.transaction_history_block);
        if (transactionHistoryBlock != null)
        {
            transactionHistoryBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleTransactionHistoryClicked();
                }
            });
        }

        emailNotificationsCheckbox = (CheckBox) view.findViewById(R.id.email_notifications_checkbox);
        if (emailNotificationsCheckbox != null)
        {
            emailNotificationsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override public void onCheckedChanged(CompoundButton compoundButton, boolean newStatus)
                {
                    handleEmailNotificationsCheckedChanged(newStatus);
                }
            });
        }

        resetHelpScreensBlock = view.findViewById(R.id.reset_help_screens_block);
        if (resetHelpScreensBlock != null)
        {
            resetHelpScreensBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleResetHelpScreensClicked();
                }
            });
        }

        clearCacheBlock = view.findViewById(R.id.clear_cache_block);
        if (clearCacheBlock != null)
        {
            clearCacheBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleClearCacheClicked();
                }
            });
        }

        signOutBlock = view.findViewById(R.id.sign_out_block);
        if (signOutBlock != null)
        {
            signOutBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleSignOutClicked();
                }
            });
        }

        aboutBlock = view.findViewById(R.id.about_block);
        if (aboutBlock != null)
        {
            aboutBlock.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleAboutClicked();
                }
            });
        }
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar()
                .setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSherlockActivity().getSupportActionBar().setTitle(getResources().getString(R.string.action_settings));
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onDestroyView()
    {
        sendLoveBlock = null;
        sendFeedbackBlock = null;
        faqBlock = null;
        profileBlock = null;
        paypalBlock = null;
        transactionHistoryBlock = null;
        emailNotificationsCheckbox = null;
        resetHelpScreensBlock = null;
        clearCacheBlock = null;
        signOutBlock = null;
        aboutBlock = null;

        if (progressDialog != null)
        {
            progressDialog.hide();
            progressDialog = null;
        }
        if (signOutTimer != null)
        {
            signOutTimer.cancel();
            signOutTimer = null;
        }

        super.onDestroyView();
    }

    private void handleSendLoveClicked()
    {
        THToast.show("Love");
        final String appName = "TradeHero";
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        }
    }

    private void handleSendFeedbackClicked()
    {
        startActivity(Intent.createChooser(VersionUtils.getSupportEmailIntent(getSherlockActivity()), ""));
    }

    private void handleSendFeedbackLongClicked()
    {
        startActivity(Intent.createChooser(VersionUtils.getSupportEmailIntent(getSherlockActivity(), true), ""));
    }

    private void handleFaqClicked()
    {
        String faqUrl = getResources().getString(R.string.th_faq_url);
        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.BUNDLE_KEY_URL, faqUrl);
        navigator.pushFragment(WebViewFragment.class, bundle);
    }

    private void handleProfileClicked()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(EmailSignUpFragment.BUNDLE_KEY_EDIT_CURRENT_USER, true);
        bundle.putBoolean(EmailSignUpFragment.BUNDLE_KEY_SHOW_BUTTON_BACK, true);
        navigator.pushFragment(EmailSignUpFragment.class, bundle);
    }

    private void handlePaypalClicked()
    {
        navigator.pushFragment(SettingsPayPalFragment.class);
    }

    private void handleTransactionHistoryClicked()
    {
        navigator.pushFragment(SettingsTransactionHistoryFragment.class);
    }

    private void handleEmailNotificationsCheckedChanged(boolean newStatus)
    {
        progressDialog = ProgressDialog.show(
                getSherlockActivity(),
                Application.getResourceString(R.string.settings_notifications_email_alert_title),
                Application.getResourceString(R.string.settings_notifications_email_alert_message),
                true);

        userService.updateProfile(THUser.getCurrentUserBase().id, newStatus, createUserProfileCallback());
    }

    private Callback<UserProfileDTO> createUserProfileCallback()
    {
        return new Callback<UserProfileDTO>()
        {
            @Override
            public void success(UserProfileDTO userProfileDTO, Response response)
            {
                progressDialog.hide();
                userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
            }

            @Override
            public void failure(RetrofitError error)
            {
                progressDialog.hide();
            }
        };
    }

    private void handleResetHelpScreensClicked()
    {
        // TODO
        THToast.show("Not implemented yet");
    }

    private void handleClearCacheClicked()
    {
        progressDialog = ProgressDialog.show(
                getActivity(),
                Application.getResourceString(R.string.cache_clearing_alert_title),
                Application.getResourceString(R.string.cache_clearing_alert_message),
                true);
        new SlowedAsyncTask<Void, Void, Void>(500)
        {
            @Override protected Void doBackgroundAction(Void... voids)
            {
                LruMemFileCache.getInstance().flush();
                return null;
            }

            @Override protected void onPostExecute(Void aVoid)
            {
                handleCacheCleared();
            }
        }.execute();
    }

    private void handleCacheCleared()
    {
        if (progressDialog == null)
        {
            THLog.d(TAG, "handleCacheCleared: progressDialog is null");
            return;
        }
        progressDialog.setTitle(R.string.cache_cleared_alert_title);
        progressDialog.setMessage("");
        getView().postDelayed(new Runnable()
        {
            @Override public void run()
            {
                progressDialog.hide();
            }
        }, 500);
    }

    private void handleSignOutClicked()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(R.string.settings_misc_sign_out_are_you_sure)
                .setCancelable(true)
                .setNegativeButton(R.string.settings_misc_sign_out_no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.settings_misc_sign_out_yes, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        effectSignOut();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void effectSignOut()
    {
        progressDialog = ProgressDialog.show(
                getActivity(),
                Application.getResourceString(R.string.settings_misc_sign_out_alert_title),
                Application.getResourceString(R.string.settings_misc_sign_out_alert_message),
                true);

        THLog.d(TAG, "Before signout current user base key " + currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
        sessionService.logout(createSignOutCallback());
    }

    private Callback<UserProfileDTO> createSignOutCallback()
    {
        return new Callback<UserProfileDTO>()
        {
            @Override
            public void success(UserProfileDTO o, Response response)
            {
                THUser.clearCurrentUser();
                ActivityHelper.launchAuthentication(getActivity());
                getActivity().finish();
                progressDialog.hide();
                // TODO clear caches
                THLog.d(TAG, "After successful signout current user base key " + currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
            }

            @Override public void failure(RetrofitError error)
            {
                progressDialog.setTitle(R.string.settings_misc_sign_out_failed);
                progressDialog.setMessage("");
                signOutTimer = new Timer();
                signOutTimer.schedule(new TimerTask()
                {
                    public void run()
                    {
                        signOutTimer.cancel();
                        progressDialog.hide();
                    }
                }, 3000);
            }
        };
    }

    private void handleAboutClicked()
    {
        navigator.pushFragment(AboutFragment.class);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
