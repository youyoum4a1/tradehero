package com.tradehero.th.fragments.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.utils.SlowedAsyncTask;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.WebViewFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.TwitterUtils;
import com.tradehero.th.utils.VersionUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: nia Date: 17/10/13 Time: 12:38 PM To change this template use File | Settings | File Templates. */
public class SettingsFragment extends PreferenceFragment
{
    public static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject UserService userService;
    @Inject SessionService sessionService;
    @Inject SocialService socialService;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    private ProgressDialog progressDialog;
    private CheckBoxPreference facebookSharing;
    private SocialNetworkEnum currentSocialNetworkConnect;
    private CheckBoxPreference twitterSharing;
    private CheckBoxPreference linkedInSharing;

    @Override public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        View view = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
        view.setBackgroundColor(getResources().getColor(R.color.white));
        return view;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.settings);

        DaggerUtils.inject(this);
        initPreferenceClickHandlers();
    }

    private void initPreferenceClickHandlers()
    {
        Preference settingFaq = findPreference(getString(R.string.settings_primary_faq));
        settingFaq.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override public boolean onPreferenceClick(Preference preference)
            {
                handleFaqClicked();
                return true;
            }
        });

        Preference settingAbout = findPreference(getString(R.string.settings_misc_about));

        if (settingAbout != null)
        {
            settingAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleAboutClicked();
                    return true;
                }
            });
        }

        Preference sendLoveBlock = findPreference(getString(R.string.settings_primary_send_love));
        if (sendLoveBlock != null)
        {
            sendLoveBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleSendLoveClicked();
                    return true;
                }
            });
        }

        Preference sendFeedbackBlock = findPreference(getString(R.string.settings_primary_send_feedback));
        if (sendFeedbackBlock != null)
        {
            sendFeedbackBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleSendFeedbackClicked();
                    return true;
                }
            });

            // TODO
            //sendFeedbackBlock.setOnLongClickListener(new View.OnLongClickListener()
            //{
            //    @Override public boolean onLongClick(View view)
            //    {
            //        handleSendFeedbackLongClicked();
            //        return true;
            //    }
            //});
        }

        Preference profileBlock = findPreference(getString(R.string.settings_primary_profile));
        if (profileBlock != null)
        {
            profileBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleProfileClicked();
                    return true;
                }
            });
        }

        Preference paypalBlock = findPreference(getString(R.string.settings_primary_paypal));
        if (paypalBlock != null)
        {
            paypalBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handlePaypalClicked();
                    return true;
                }
            });
        }

        Preference transactionHistoryBlock = findPreference(getString(R.string.settings_primary_transaction_history));
        if (transactionHistoryBlock != null)
        {
            transactionHistoryBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleTransactionHistoryClicked();
                    return true;
                }
            });
        }

        Preference resetHelpScreensBlock = findPreference(getString(R.string.settings_misc_reset_help_screens));
        if (resetHelpScreensBlock != null)
        {
            resetHelpScreensBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleResetHelpScreensClicked();
                    return true;
                }
            });
        }

        Preference clearCacheBlock = findPreference(getString(R.string.settings_misc_clear_cache));
        if (clearCacheBlock != null)
        {
            clearCacheBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleClearCacheClicked();
                    return true;
                }
            });
        }

        Preference signOutBlock = findPreference(getString(R.string.settings_misc_sign_out));
        if (signOutBlock != null)
        {
            signOutBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleSignOutClicked();
                    return true;
                }
            });
        }

        Preference aboutBlock = findPreference(getString(R.string.settings_about));
        if (aboutBlock != null)
        {
            aboutBlock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleAboutClicked();
                    return true;
                }
            });
        }

        // Sharing
        facebookSharing = (CheckBoxPreference) findPreference(getString(R.string.settings_sharing_facebook));
        if (facebookSharing != null)
        {
            facebookSharing.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeSharing(SocialNetworkEnum.FB, (boolean) newValue);
                }
            });
        }
        twitterSharing = (CheckBoxPreference) findPreference(getString(R.string.settings_sharing_twitter));
        if (twitterSharing != null)
        {
            twitterSharing.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeSharing(SocialNetworkEnum.TW, (boolean) newValue);
                }
            });
        }
        linkedInSharing = (CheckBoxPreference) findPreference(getString(R.string.settings_sharing_linked_in));
        if (linkedInSharing != null)
        {
            linkedInSharing.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeSharing(SocialNetworkEnum.LI, (boolean) newValue);
                }
            });
        }

        updateSocialConnectStatus();
        updateNotificationStatus();
    }

    private void updateNotificationStatus()
    {
        // notification
        UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        if (currentUserProfile == null)
        {
            return;
        }

        SwitchPreference pushNotification = (SwitchPreference) findPreference(getString(R.string.settings_notifications_push));
        if (pushNotification != null)
        {
            pushNotification.setChecked(currentUserProfile.pushNotificationsEnabled);
            pushNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changePushNotification((boolean) newValue);
                }
            });
        }

        SwitchPreference emailNotification = (SwitchPreference) findPreference(getString(R.string.settings_notifications_email));
        if (emailNotification != null)
        {
            emailNotification.setChecked(currentUserProfile.emailNotificationsEnabled);
            emailNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    return changeEmailNotification((boolean) newValue);
                }
            });
        }
    }

    private boolean changeEmailNotification(boolean newValue)
    {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    private boolean changePushNotification(boolean newValue)
    {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    private boolean changeSharing(SocialNetworkEnum socialNetwork, boolean enable)
    {
        currentSocialNetworkConnect = socialNetwork;
        if (enable)
        {
            switch (socialNetwork)
            {
                case FB:
                    progressDialog = ProgressDialog.show(getActivity(), getString(R.string.facebook), getString(R.string.connecting_to_facebook));
                    FacebookUtils.logIn(getActivity(), socialConnectCallback);
                    break;
                case TW:
                    progressDialog = ProgressDialog.show(getActivity(), getString(R.string.twiter), getString(R.string.connecting_to_twitter));
                    TwitterUtils.logIn(getActivity(), socialConnectCallback);
                    break;
                case TH:
                    break;
                case LI:
                    progressDialog = ProgressDialog.show(getActivity(), getString(R.string.linkedin), getString(R.string.connecting_to_linkedin));
                    LinkedInUtils.logIn(getActivity(), socialConnectCallback);
                    break;
            }
        }
        else
        {
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.connecting_tradehero_only));
            socialService.disconnect(
                    currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                    new SocialNetworkFormDTO(socialNetwork),
                    createSocialDisconnectCallback());

            if (socialNetwork.getAuthenticationHeader().equals(THUser.getCurrentAuthenticationType()))
            {
                effectSignOut();
                return false;
            }
        }
        return false;
    }

    private Callback<UserProfileDTO> createSocialDisconnectCallback()
    {
        return new SocialLinkingCallback()
        {
            @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
            {
                super.success(userProfileDTO, thResponse);
                THUser.removeCredential(currentSocialNetworkConnect.getAuthenticationHeader());
            }
        };
    }

    private Callback<UserProfileDTO> createSocialConnectCallback()
    {
        return new SocialLinkingCallback();
    }

    private void updateSocialConnectStatus()
    {
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (facebookSharing != null)
            {
                facebookSharing.setChecked(updatedUserProfileDTO.fbLinked);
            }
            if (twitterSharing != null)
            {
                twitterSharing.setChecked(updatedUserProfileDTO.twLinked);
            }
            if (linkedInSharing != null)
            {
                linkedInSharing.setChecked(updatedUserProfileDTO.liLinked);
            }
        }
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        getSherlockActivity().getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.settings));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getNavigator().popFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
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
        getNavigator().pushFragment(WebViewFragment.class, bundle);
    }

    private void handleProfileClicked()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(EmailSignUpFragment.BUNDLE_KEY_EDIT_CURRENT_USER, true);
        bundle.putBoolean(EmailSignUpFragment.BUNDLE_KEY_SHOW_BUTTON_BACK, true);
        getNavigator().pushFragment(EmailSignUpFragment.class, bundle);
    }

    private void handlePaypalClicked()
    {
        getNavigator().pushFragment(SettingsPayPalFragment.class);
    }

    private void handleTransactionHistoryClicked()
    {
        getNavigator().pushFragment(SettingsTransactionHistoryFragment.class);
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
                getView().postDelayed(new Runnable()
                {
                    @Override public void run()
                    {
                        progressDialog.hide();
                    }
                }, 3000);
            }
        };
    }

    private void handleAboutClicked()
    {
        getNavigator().pushFragment(AboutFragment.class);
    }

    private LogInCallback socialConnectCallback = new LogInCallback()
    {
        @Override public void done(UserBaseDTO user, THException ex)
        {}

        @Override public void onStart()
        {}

        @Override public boolean onSocialAuthDone(JSONObject json)
        {
            socialService.connect(
                    currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                    UserFormFactory.create(json),
                    createSocialConnectCallback());
            progressDialog.setMessage(String.format(getString(R.string.connecting_tradehero), currentSocialNetworkConnect.getName()));
            return false;
        }
    };

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {

        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            userProfileCache.get().put(currentUserBaseKeyHolder.getCurrentUserBaseKey(), userProfileDTO);
        }

        @Override protected void failure(THException ex)
        {
            // user unlinked current authentication
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            progressDialog.dismiss();
            updateSocialConnectStatus();
        }
    }
}
