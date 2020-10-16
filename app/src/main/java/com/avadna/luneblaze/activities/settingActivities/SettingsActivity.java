package com.avadna.luneblaze.activities.settingActivities;

import android.app.Dialog;

import com.avadna.luneblaze.activities.assessment.AssessmentListActivity;
import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.activities.organisation.RegisterOrganisationActivity;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;

import com.avadna.luneblaze.pojo.pojoOrganisation.PojoUserOrganisationListResponse;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoLogoutResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserEmailPhoneResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//todo add active sessions list

public class SettingsActivity extends AppBaseActivity {

    ProgressBar pb_loading_content;
    LinearLayout ll_content_wrapper;

    ActionBar actionBar;
    TextView tv_add_mail, tv_add_phone, tv_change_password, tv_manage_feed, tv_verification_request;
    TextView tv_close_account, tv_withdraw_delete, tv_signout, tv_assessment_report, tv_organisation, tv_my_organisations,
            tv_organisation_name;
    LinearLayout ll_my_organisation_wrapper;
    ProgressBar pb_delete_withdraw_status;

    String myOrganisationId = "";

    TextView tv_who_can_send_req, tv_who_can_see_connections, tv_who_can_see_sessions, tv_who_can_message;
    TextView tv_who_can_see_choices, tv_who_can_see_profile_info, tv_who_can_see_content, tv_tagging;
    TextView tv_blocking, tv_session_blocking, tv_session_invite, tv_session_conduct_invite, tv_answer_invite;
    TextView tv_interest_invite, tv_two_step;
    SwitchCompat sw_two_step_verification;


    TabLayout tl_notification_tabs;
    int selectedTab = 0;
    TextView tv_invites_and_messages, tv_session_related, tv_question_and_answer, tv_article_related;
    TextView tv_update_on_activity, tv_message_read, tv_auto_play, tv_sound_and_vibration;
    TextView tv_language;
    SwitchCompat sw_invite_switch, sw_session_related_switch, sw_qna_related_switch;
    SwitchCompat sw_articles_related, sw_updates_on_activities;

    TextView tv_active_sessions, tv_feedback, tv_help_center, tv_privacy_policy, tv_session_policy,
            tv_terms_of_use, tv_report_bug;

    TextView tv_user_agreement, tv_end_user_agreement;
    View.OnClickListener mOnClickListener;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    CommonFunctions commonFunctions;
    PojoUserSettingsResponseData pojoUserSettingsResponseData;
    boolean isSettingChanged = false;
    String oldSetting = "";
    Dialog confirmationDialog;
    private int PHONE_VERIFIED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();
        commonFunctions = new CommonFunctions(this);
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        hitGetUserOrganisationsApi(user_id, "my_organisations");
    }

    @Override
    public void onResume() {
        super.onResume();
        hitGetUserPrivacySettingApi(user_id, "privacy");
        pojoUserSettingsResponseData = preferenceUtils.getUserSettings();
        if (pojoUserSettingsResponseData != null) {
            setDataOnViews();
        } else {
            ll_content_wrapper.setVisibility(View.GONE);
            pb_loading_content.setVisibility(View.VISIBLE);
        }
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.settings));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SettingsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SettingsActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        ll_content_wrapper = (LinearLayout) findViewById(R.id.ll_content_wrapper);

        //Account settings
        tv_add_mail = (TextView) findViewById(R.id.tv_add_mail);
        tv_add_phone = (TextView) findViewById(R.id.tv_add_phone);
        tv_change_password = (TextView) findViewById(R.id.tv_change_password);
        tv_manage_feed = (TextView) findViewById(R.id.tv_manage_feed);
        tv_verification_request = (TextView) findViewById(R.id.tv_verification_request);
        tv_close_account = (TextView) findViewById(R.id.tv_close_account);
        tv_withdraw_delete = (TextView) findViewById(R.id.tv_withdraw_delete);
        tv_withdraw_delete.setVisibility(View.GONE);
        pb_delete_withdraw_status = (ProgressBar) findViewById(R.id.pb_delete_withdraw_status);
        pb_delete_withdraw_status.setVisibility(View.GONE);
        tv_signout = (TextView) findViewById(R.id.tv_signout);
        tv_assessment_report = (TextView) findViewById(R.id.tv_assessment_report);
        tv_organisation = (TextView) findViewById(R.id.tv_organisation);
        tv_my_organisations = (TextView) findViewById(R.id.tv_my_organisations);
        tv_organisation_name = (TextView) findViewById(R.id.tv_organisation_name);
        tv_organisation_name.setVisibility(View.GONE);
        ll_my_organisation_wrapper = (LinearLayout) findViewById(R.id.ll_my_organisation_wrapper);
        ll_my_organisation_wrapper.setVisibility(View.GONE);

        //privacy settings
        tv_who_can_send_req = (TextView) findViewById(R.id.tv_who_can_send_req);
        tv_who_can_see_connections = (TextView) findViewById(R.id.tv_who_can_see_connections);
        tv_who_can_see_sessions = (TextView) findViewById(R.id.tv_who_can_see_sessions);
        tv_who_can_message = (TextView) findViewById(R.id.tv_who_can_message);
        tv_who_can_see_choices = (TextView) findViewById(R.id.tv_who_can_see_choices);
        tv_who_can_see_profile_info = (TextView) findViewById(R.id.tv_who_can_see_profile_info);
        tv_who_can_see_content = (TextView) findViewById(R.id.tv_who_can_see_content);
        tv_tagging = (TextView) findViewById(R.id.tv_tagging);
        tv_blocking = (TextView) findViewById(R.id.tv_blocking);
        tv_session_blocking = (TextView) findViewById(R.id.tv_session_blocking);
        tv_session_invite = (TextView) findViewById(R.id.tv_session_invite);
        tv_session_conduct_invite = (TextView) findViewById(R.id.tv_session_conduct_invite);
        tv_answer_invite = (TextView) findViewById(R.id.tv_answer_invite);
        tv_interest_invite = (TextView) findViewById(R.id.tv_interest_invite);
        tv_two_step = (TextView) findViewById(R.id.tv_two_step);
        sw_two_step_verification = (SwitchCompat) findViewById(R.id.sw_two_step_verification);


        //App settings
        setupNotificationSettingTabs();
        sw_invite_switch = (SwitchCompat) findViewById(R.id.sw_invite_switch);
        sw_session_related_switch = (SwitchCompat) findViewById(R.id.sw_session_related_switch);
        sw_qna_related_switch = (SwitchCompat) findViewById(R.id.sw_qna_related_switch);
        sw_articles_related = (SwitchCompat) findViewById(R.id.sw_articles_related);
        sw_updates_on_activities = (SwitchCompat) findViewById(R.id.sw_updates_on_activities);
        tv_invites_and_messages = (TextView) findViewById(R.id.tv_invites_and_messages);
        tv_session_related = (TextView) findViewById(R.id.tv_session_related);
        tv_question_and_answer = (TextView) findViewById(R.id.tv_question_and_answer);
        tv_article_related = (TextView) findViewById(R.id.tv_article_related);
        tv_update_on_activity = (TextView) findViewById(R.id.tv_update_on_activity);
        tv_message_read = (TextView) findViewById(R.id.tv_message_read);
        tv_auto_play = (TextView) findViewById(R.id.tv_auto_play);
        tv_sound_and_vibration = (TextView) findViewById(R.id.tv_sound_and_vibration);
        tv_language = (TextView) findViewById(R.id.tv_language);

        //help and support
        tv_active_sessions = (TextView) findViewById(R.id.tv_active_sessions);
        tv_feedback = (TextView) findViewById(R.id.tv_feedback);
        tv_help_center = (TextView) findViewById(R.id.tv_help_center);
        tv_privacy_policy = (TextView) findViewById(R.id.tv_privacy_policy);
        tv_session_policy = (TextView) findViewById(R.id.tv_session_policy);
        tv_terms_of_use = (TextView) findViewById(R.id.tv_terms_of_use);

        tv_user_agreement = (TextView) findViewById(R.id.tv_user_agreement);
        tv_end_user_agreement = (TextView) findViewById(R.id.tv_end_user_agreement);
        tv_report_bug = (TextView) findViewById(R.id.tv_report_bug);

    }

    private void setupNotificationSettingTabs() {
        tl_notification_tabs = (TabLayout) findViewById(R.id.tl_notification_tabs);
        tl_notification_tabs.addTab(tl_notification_tabs.newTab().setText("Email"));
        tl_notification_tabs.addTab(tl_notification_tabs.newTab().setText("Push"));
        //  tl_notification_tabs.addTab(tl_notification_tabs.newTab().setText("On App"));
        tl_notification_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        //  setEmailSwitchValues();
        //   setPushSwitchValues();

    }

    private void setEmailSwitchValues() {
        sw_invite_switch.setChecked(pojoUserSettingsResponseData.appSettings.email
                .privacyNotificationEmailInvitesMessages.equals("yes"));
        sw_session_related_switch.setChecked(pojoUserSettingsResponseData.appSettings.email
                .privacyNotificationEmailSessions.equals("yes"));
        sw_qna_related_switch.setChecked(pojoUserSettingsResponseData.appSettings.email
                .privacyNotificationEmailQa.equals("yes"));
        sw_articles_related.setChecked(pojoUserSettingsResponseData.appSettings.email
                .privacyNotificationEmailArticles.equals("yes"));
        sw_updates_on_activities.setChecked(pojoUserSettingsResponseData.appSettings.email
                .privacyNotificationEmailUpdate.equals("yes"));
    }

    private void setPushSwitchValues() {
        sw_invite_switch.setChecked(pojoUserSettingsResponseData.appSettings.push
                .privacyNotificationPushInvitesMessages.equals("yes"));
        sw_session_related_switch.setChecked(pojoUserSettingsResponseData.appSettings.push
                .privacyNotificationPushSessions.equals("yes"));
        sw_qna_related_switch.setChecked(pojoUserSettingsResponseData.appSettings.push
                .privacyNotificationPushQa.equals("yes"));
        sw_articles_related.setChecked(pojoUserSettingsResponseData.appSettings.push
                .privacyNotificationPushArticles.equals("yes"));
        sw_updates_on_activities.setChecked(pojoUserSettingsResponseData.appSettings.push
                .privacyNotificationPushUpdate.equals("yes"));
    }


    private void initClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    //Account Settings
                    case R.id.tv_add_mail:
                        Intent addMailActivityIntent = new Intent(SettingsActivity.this, AddedEmailsActivity.class);
                        startActivity(addMailActivityIntent);
                        break;
                    case R.id.tv_add_phone:
                        Intent addPhoneNumActivityIntent = new Intent(SettingsActivity.this, AddedPhoneNumbersActivity.class);
                        startActivity(addPhoneNumActivityIntent);
                        break;
                    case R.id.tv_change_password:
                        Intent changepasswordActivityIntent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                        startActivity(changepasswordActivityIntent);
                        break;
                    case R.id.tv_manage_feed:
                        Intent manageFeedActivityIntent = new Intent(SettingsActivity.this, ManageFeedActivity.class);
                        startActivity(manageFeedActivityIntent);
                        break;


                    case R.id.tv_verification_request:
                        Intent verificationIntent = new Intent(SettingsActivity.this,
                                VerificationRequestActivity.class);
                        startActivity(verificationIntent);
                        break;

                    case R.id.tv_close_account:
                      /*  Intent addMailActivityIntent=new Intent(SettingsActivity.this,AddedEmailsActivity.class);
                        startActivity(addMailActivityIntent);*/
                        openConfirmationDialog("deleteAcc");
                        break;

                    case R.id.tv_withdraw_delete:
                        hitDeleteAccountApi(user_id, "withdraw_delete_account");
                        tv_withdraw_delete.setVisibility(View.GONE);
                        pojoUserSettingsResponseData.privacy.withdrawDeleteRequest = "Yes";
                        preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);

                        break;

                    case R.id.tv_signout:
                      /*  Intent addMailActivityIntent=new Intent(SettingsActivity.this,AddedEmailsActivity.class);
                        startActivity(addMailActivityIntent);*/
                        openConfirmationDialog("logout");
                        break;

                    case R.id.tv_assessment_report:
                      /*  Intent addMailActivityIntent=new Intent(SettingsActivity.this,AddedEmailsActivity.class);
                        startActivity(addMailActivityIntent);*/
                        Intent assessmentIntent = new Intent(SettingsActivity.this,
                                AssessmentListActivity.class);
                        startActivity(assessmentIntent);
                        break;

                    case R.id.tv_organisation:
                        Intent createOrganisationIntent = new Intent(SettingsActivity.this,
                                RegisterOrganisationActivity.class);
                        startActivity(createOrganisationIntent);
                        break;

                    case R.id.tv_my_organisations:
                        Intent myOrganisationIntent = new Intent(SettingsActivity.this,
                                MyOrganisationsListActivity.class);
                        startActivity(myOrganisationIntent);
                        break;

                    case R.id.tv_organisation_name:
                        if (!myOrganisationId.isEmpty()) {
                            Intent organisationIntent = new Intent(SettingsActivity.this,
                                    OrganisationProfileInfoActivity.class);
                            organisationIntent.putExtra(AppKeys.OWNER_ID, user_id);
                            organisationIntent.putExtra(AppKeys.ORGANISATION_ID, myOrganisationId);
                            startActivity(organisationIntent);
                        }
                        break;


                    //Privacy Settings
                    case R.id.tv_who_can_send_req:
                        openPrivacyChooserDialog(R.string.privacy_connection_request);
                        break;

                    case R.id.tv_who_can_see_connections:
                        openPrivacyChooserDialog(R.string.privacy_see_connections);
                        break;

                    case R.id.tv_who_can_see_sessions:
                        openPrivacyChooserDialog(R.string.privacy_see_sessions);
                        break;

                    case R.id.tv_who_can_message:
                        openPrivacyChooserDialog(R.string.privacy_who_can_message);
                        break;

                    case R.id.tv_who_can_see_choices:
                        openPrivacyChooserDialog(R.string.privacy_see_choices);
                        break;


                    case R.id.tv_who_can_see_profile_info:
                        openPrivacyChooserDialog(R.string.privacy_profile_info);
                        break;

                    case R.id.tv_who_can_see_content:
                        openPrivacyChooserDialog(R.string.privacy_see_content);
                        break;

                    case R.id.tv_tagging:
                        openPrivacyChooserDialog(R.string.privacy_tagging);
                        break;

                    case R.id.tv_blocking:
                        Intent blockingListActivityIntent = new Intent(SettingsActivity.this, BlockingListActivity.class);
                        startActivity(blockingListActivityIntent);
                        break;

                    case R.id.tv_session_blocking:
                        Intent sessionBlockingIntent = new Intent(SettingsActivity.this, SessionBlockingActivity.class);
                        startActivity(sessionBlockingIntent);
                        break;

                    case R.id.tv_session_invite:
                        openPrivacyChooserDialog(R.string.privacy_session_invite);
                        break;

                    case R.id.tv_session_conduct_invite:
                        openPrivacyChooserDialog(R.string.privacy_session_conduct);
                        break;

                    case R.id.tv_answer_invite:
                        openPrivacyChooserDialog(R.string.privacy_answer_invite);
                        break;

                    case R.id.tv_interest_invite:
                        openPrivacyChooserDialog(R.string.privacy_interest_invite);
                        break;

                    case R.id.tv_two_step:
                        //  openTwoStepVerificationDialog();
                        break;

                    case R.id.sw_two_step_verification:
                        if (sw_two_step_verification.isChecked()) {
                            hitGetUserPhoneEmailApi(user_id);

                        } else {
                            hitUpdateTwoStepVerificationSettingApi(user_id, "two_step_verifications",
                                    "no");
                        }
                        break;


                    //App Settings
                    case R.id.tv_invites_and_messages:

                        break;
                    case R.id.tv_session_related:

                        break;
                    case R.id.tv_question_and_answer:

                        break;
                    case R.id.tv_article_related:

                        break;
                    case R.id.tv_update_on_activity:

                        break;


                    case R.id.sw_invite_switch:

                        String inviteType = "";
                        if (selectedTab == 0) {
                            inviteType = "privacy_notification_email_invites_messages";
                        } else {
                            inviteType = "privacy_notification_push_invites_messages";
                        }
                        if (sw_invite_switch.isChecked()) {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", inviteType,
                                    "yes", v.getId(), selectedTab);
                        } else {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", inviteType,
                                    "no", v.getId(), selectedTab);
                        }

                        if (sw_invite_switch.isChecked()) {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailInvitesMessages = "yes";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushInvitesMessages = "yes";
                            }

                        } else {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailInvitesMessages = "no";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushInvitesMessages = "no";
                            }
                        }
                        break;

                    case R.id.sw_session_related_switch:
                        String sessionType = "";
                        if (selectedTab == 0) {
                            sessionType = "privacy_notification_email_sessions";
                        } else {
                            sessionType = "privacy_notification_push_sessions";
                        }

                        if (sw_session_related_switch.isChecked()) {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", sessionType,
                                    "yes", v.getId(), selectedTab);
                        } else {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", sessionType,
                                    "no", v.getId(), selectedTab);
                        }

                        if (sw_session_related_switch.isChecked()) {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailSessions = "yes";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushSessions = "yes";
                            }

                        } else {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailSessions = "no";

                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushSessions = "no";
                            }
                        }
                        break;

                    case R.id.sw_qna_related_switch:
                        String qnaType = "";
                        if (selectedTab == 0) {
                            qnaType = "privacy_notification_email_qa";
                        } else {
                            qnaType = "privacy_notification_push_qa";
                        }
                        if (sw_qna_related_switch.isChecked()) {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", qnaType,
                                    "yes", v.getId(), selectedTab);
                        } else {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", qnaType,
                                    "no", v.getId(), selectedTab);
                        }

                        if (sw_qna_related_switch.isChecked()) {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailQa = "yes";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushQa = "yes";
                            }

                        } else {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailQa = "no";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushQa = "no";
                            }

                        }
                        break;

                    case R.id.sw_articles_related:
                        String articleType = "";
                        if (selectedTab == 0) {
                            articleType = "privacy_notification_email_articles";
                        } else {
                            articleType = "privacy_notification_push_articles";
                        }
                        if (sw_articles_related.isChecked()) {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", articleType,
                                    "yes", v.getId(), selectedTab);
                        } else {
                            hitUpdatePrivacySettingsApi(user_id, "app_settings", articleType,
                                    "no", v.getId(), selectedTab);
                        }

                        if (sw_articles_related.isChecked()) {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailArticles = "yes";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushArticles = "yes";
                            }

                        } else {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailArticles = "no";

                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushArticles = "no";
                            }
                        }
                        break;

                    case R.id.sw_updates_on_activities:
                        String activitiesType = "";

                        if (selectedTab == 0) {
                            activitiesType = "privacy_notification_email_update";
                        } else {
                            activitiesType = "privacy_notification_push_update";
                        }
                        if (sw_updates_on_activities.isChecked()) {

                            hitUpdatePrivacySettingsApi(user_id, "app_settings", activitiesType,
                                    "yes", v.getId(), selectedTab);
                        } else {

                            hitUpdatePrivacySettingsApi(user_id, "app_settings", activitiesType,
                                    "no", v.getId(), selectedTab);
                        }

                        if (sw_updates_on_activities.isChecked()) {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailUpdate = "yes";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushUpdate = "yes";
                            }

                        } else {
                            if (selectedTab == 0) {
                                pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailUpdate = "no";
                            } else {
                                pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushUpdate = "no";
                            }
                        }
                        break;


                    case R.id.tv_message_read:

                        break;

                    case R.id.tv_auto_play:

                        break;

                    case R.id.tv_sound_and_vibration:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                            startActivity(intent);
                        } else {
                            openSoundAndVibrationDialog();
                        }
                        break;

                    case R.id.tv_language:
                        Intent languageIntent = new Intent(SettingsActivity.this, LanguageSelectorActivity.class);
                        startActivity(languageIntent);
                        break;

                    //help and support
                    case R.id.tv_active_sessions:
                        Intent activeIntent = new Intent(SettingsActivity.this, ActiveSessionActivity.class);
                        startActivity(activeIntent);
                        break;
                    case R.id.tv_feedback:
                        Intent feedbackActivityIntent = new Intent(SettingsActivity.this, FeedBackActivity.class);
                        startActivity(feedbackActivityIntent);

                        break;
                    case R.id.tv_help_center:

                        break;
                    case R.id.tv_privacy_policy:
                        commonFunctions.urlLoader( AppKeys.privacy_policy_link);
                       /* Intent privacyIntent = new Intent(SettingsActivity.this, WebViewActivity.class);
                        privacyIntent.putExtra(AppKeys.URL, AppKeys.privacy_policy_link);
                        startActivity(privacyIntent);*/
                        break;

                    case R.id.tv_session_policy:
                        commonFunctions.urlLoader( AppKeys.session_policy_link);
                      /*  Intent sessionIntent = new Intent(SettingsActivity.this, WebViewActivity.class);
                        sessionIntent.putExtra(AppKeys.URL, AppKeys.session_policy_link);
                        startActivity(sessionIntent);*/
                        break;

                    case R.id.tv_terms_of_use:
                        commonFunctions.urlLoader(AppKeys.terms_of_use_link);
                        /*Intent tncintent = new Intent(SettingsActivity.this, WebViewActivity.class);
                        tncintent.putExtra("url", AppKeys.terms_of_use_link);
                        startActivity(tncintent);*/
                        break;


                    case R.id.tv_user_agreement:

                        break;
                    case R.id.tv_end_user_agreement:

                        break;
                    case R.id.tv_report_bug:
                        Intent reportBugActivityIntent = new Intent(SettingsActivity.this, ReportBugActivity.class);
                        startActivity(reportBugActivityIntent);
                        break;


                }
            }
        };
    }

    private void hitGetUserPhoneEmailApi(final String user_id) {
        Call<PojoUserEmailPhoneResponse> call = apiService.getUserPhoneEmail(user_id);
        commonFunctions.openProgressDialog();
        call.enqueue(new Callback<PojoUserEmailPhoneResponse>() {
            @Override
            public void onResponse(Call<PojoUserEmailPhoneResponse> call, Response<PojoUserEmailPhoneResponse> response) {
                String phone = "";
                if (response != null && response.body() != null) {

                    boolean verfiedPresent = false;
                    boolean primaryPresent = false;

                    for (int i = 0; i < response.body().data.mobile.size(); i++) {

                        if (response.body().data.mobile.get(i).status.equals("1")) {
                            verfiedPresent = true;
                        }
                        if (response.body().data.mobile.get(i).primary.equals("1")) {
                            phone = response.body().data.mobile.get(i).data;
                            primaryPresent = true;
                            verfiedPresent = true;
                            break;
                        }
                    }
                    if (!verfiedPresent) {
                        openMessageDialog(getString(R.string.you_dont_have_a_verified_phone));
                    } else if (!primaryPresent) {
                        openMessageDialog(getString(R.string.you_dont_have_a_primary_phone));
                    } else {
                        hitUpdateTwoStepVerificationSettingApi(user_id, "two_step_verifications",
                                "yes");
                    }
                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoUserEmailPhoneResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();

            }
        });
    }


    private void openMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(SettingsActivity.this);
        confirmationDialog.setContentView(R.layout.message_dialog);

        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(message);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
                Intent addPhoneIntent = new Intent(SettingsActivity.this, AddedPhoneNumbersActivity.class);
                startActivityForResult(addPhoneIntent, PHONE_VERIFIED);
            }
        });
        confirmationDialog.show();
    }


    private void openConfirmationDialog(final String type) {
        TextView tv_dialog_description, tv_yes, tv_no;
        confirmationDialog = new MyCustomThemeDialog(SettingsActivity.this);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);


      /*  if (type.equals(AppKeys.SEND_VERIFICATION_REQUEST)) {
            tv_dialog_description.setText(getString(R.string.verification_request_dialog_description));
        } else if (type.equals(AppKeys.CANCEL_VERIFICATION_REQUEST)) {
            tv_dialog_description.setText(getString(R.string.your_verification_request_is_pending));
        } else*/
        if (type.equals("deleteAcc")) {
            tv_dialog_description.setText(getString(R.string.delete_account_confirm));
        } else if (type.equals("logout")) {
            tv_dialog_description.setText(getString(R.string.logout_confirm));
        }


        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("logout")) {
                    hitLogoutApi(user_id);
                    confirmationDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    preferenceUtils.logOut();
                    finish();
                } else if (type.equals("deleteAcc")) {
                    hitDeleteAccountApi(user_id, "delete_account");
                    tv_withdraw_delete.setVisibility(View.VISIBLE);
                    pojoUserSettingsResponseData.privacy.deleteRequestOn = Calendar.getInstance().getTime().toString();
                    pojoUserSettingsResponseData.privacy.withdrawDeleteRequest = "No";
                    preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);
                    confirmationDialog.dismiss();
                    /*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    preferenceUtils.logOut();*/
                } /*else if (type.equals(AppKeys.SEND_VERIFICATION_REQUEST)) {
                    hitSendVerificationRequestApi(user_id, "send_verification_request");
                    confirmationDialog.dismiss();
                } else if (type.equals(AppKeys.CANCEL_VERIFICATION_REQUEST)) {
                    hitSendVerificationRequestApi(user_id, "cancel_verification_request");
                    confirmationDialog.dismiss();
                }*/
            }
        });
        confirmationDialog.show();
    }


    private void setClickListener() {
        //Account settings
        tv_add_mail.setOnClickListener(mOnClickListener);
        tv_add_phone.setOnClickListener(mOnClickListener);
        tv_change_password.setOnClickListener(mOnClickListener);
        tv_manage_feed.setOnClickListener(mOnClickListener);
        tv_verification_request.setOnClickListener(mOnClickListener);
        tv_close_account.setOnClickListener(mOnClickListener);
        tv_withdraw_delete.setOnClickListener(mOnClickListener);
        tv_signout.setOnClickListener(mOnClickListener);
        tv_assessment_report.setOnClickListener(mOnClickListener);
        tv_assessment_report.setVisibility(View.GONE);
        tv_organisation.setOnClickListener(mOnClickListener);
        tv_my_organisations.setOnClickListener(mOnClickListener);
        tv_organisation_name.setOnClickListener(mOnClickListener);

        //Privacy Settings
        tv_who_can_send_req.setOnClickListener(mOnClickListener);
        tv_who_can_see_connections.setOnClickListener(mOnClickListener);
        tv_who_can_see_sessions.setOnClickListener(mOnClickListener);
        tv_who_can_message.setOnClickListener(mOnClickListener);
        tv_who_can_see_choices.setOnClickListener(mOnClickListener);
        tv_who_can_see_profile_info.setOnClickListener(mOnClickListener);
        tv_who_can_see_content.setOnClickListener(mOnClickListener);
        tv_tagging.setOnClickListener(mOnClickListener);
        tv_blocking.setOnClickListener(mOnClickListener);
        tv_session_blocking.setOnClickListener(mOnClickListener);
        tv_session_invite.setOnClickListener(mOnClickListener);
        tv_session_conduct_invite.setOnClickListener(mOnClickListener);
        tv_answer_invite.setOnClickListener(mOnClickListener);
        tv_interest_invite.setOnClickListener(mOnClickListener);
        tv_two_step.setOnClickListener(mOnClickListener);
        sw_two_step_verification.setOnClickListener(mOnClickListener);


        //App settings
        sw_invite_switch.setOnClickListener(mOnClickListener);
        sw_session_related_switch.setOnClickListener(mOnClickListener);
        sw_qna_related_switch.setOnClickListener(mOnClickListener);
        sw_articles_related.setOnClickListener(mOnClickListener);
        sw_updates_on_activities.setOnClickListener(mOnClickListener);
        tv_invites_and_messages.setOnClickListener(mOnClickListener);
        tv_session_related.setOnClickListener(mOnClickListener);
        tv_question_and_answer.setOnClickListener(mOnClickListener);
        tv_article_related.setOnClickListener(mOnClickListener);
        tv_update_on_activity.setOnClickListener(mOnClickListener);
        tv_message_read.setOnClickListener(mOnClickListener);
        tv_auto_play.setOnClickListener(mOnClickListener);
        tv_sound_and_vibration.setOnClickListener(mOnClickListener);
        tv_language.setOnClickListener(mOnClickListener);


        //help and support
        tv_active_sessions.setOnClickListener(mOnClickListener);
        tv_feedback.setOnClickListener(mOnClickListener);
        tv_help_center.setOnClickListener(mOnClickListener);
        tv_privacy_policy.setOnClickListener(mOnClickListener);

        tv_session_policy.setOnClickListener(mOnClickListener);
        tv_terms_of_use.setOnClickListener(mOnClickListener);
        tv_report_bug.setOnClickListener(mOnClickListener);
        tv_user_agreement.setOnClickListener(mOnClickListener);
        tv_end_user_agreement.setOnClickListener(mOnClickListener);
        tv_report_bug.setOnClickListener(mOnClickListener);
    }


    public void openPrivacyChooserDialog(final int id) {
        final TextView tv_dialog_description, tv_done_button;
        final RadioButton rb_my_connections, rb_everyone, rb_none;
        final Dialog dialog = new MyCustomThemeDialog(SettingsActivity.this);
        dialog.setContentView(R.layout.everyone_vs_connection_dialog);
        tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);
        tv_done_button = (TextView) dialog.findViewById(R.id.tv_done_button);
        rb_my_connections = (RadioButton) dialog.findViewById(R.id.rb_my_connections);
        rb_everyone = (RadioButton) dialog.findViewById(R.id.rb_everyone);
        rb_none = (RadioButton) dialog.findViewById(R.id.rb_none);



      /*  View.OnClickListener rbClickListener;

        rbClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSettingChanged=true;
            }
        };

        rb_my_connections.setOnClickListener(rbClickListener);
        rb_everyone.setOnClickListener(rbClickListener);
        rb_none.setOnClickListener(rbClickListener);*/

        switch (id) {
            case R.string.privacy_connection_request:
                rb_my_connections.setVisibility(View.GONE);
                if (pojoUserSettingsResponseData.privacy.privacyConnectionRequest.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.privacyConnectionRequest.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_connection_request);
                break;

            case R.string.privacy_see_connections:
                if (pojoUserSettingsResponseData.privacy.userPrivacyFriends.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.userPrivacyFriends.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }

                tv_dialog_description.setText(R.string.privacy_see_connections);
                break;

            case R.string.privacy_see_sessions:
                if (pojoUserSettingsResponseData.privacy.privacySession == null
                        || pojoUserSettingsResponseData.privacy.privacySession.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.userPrivacyFriends.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_see_sessions);
                break;


            case R.string.privacy_who_can_message:
                if (pojoUserSettingsResponseData.privacy.privacyMessage.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.privacyMessage.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }

                tv_dialog_description.setText(R.string.privacy_who_can_message);
                break;

            case R.string.privacy_see_choices:
                if (pojoUserSettingsResponseData.privacy.privacyChoices.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.privacyChoices.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_see_choices);
                break;


            case R.string.privacy_profile_info:
                if (pojoUserSettingsResponseData.privacy.userPrivacyBasic.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.userPrivacyBasic.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_profile_info);
                break;

            case R.string.privacy_see_content:
                if (pojoUserSettingsResponseData.privacy.privacyContent.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.privacyContent.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_see_content);
                break;

            case R.string.privacy_tagging:
                rb_everyone.setVisibility(View.GONE);
                if (pojoUserSettingsResponseData.privacy.privacyTag.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else if (pojoUserSettingsResponseData.privacy.privacyTag.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_tagging);
                break;

            case R.string.privacy_session_invite:
                rb_everyone.setVisibility(View.GONE);
               /* if (pojoUserSettingsResponseData.privacy.privacyInviteSession.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else*/
                if (pojoUserSettingsResponseData.privacy.privacyInviteSession.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_session_invite);
                break;

            case R.string.privacy_session_conduct:
                rb_everyone.setVisibility(View.GONE);

               /* if (pojoUserSettingsResponseData.privacy.privacyInviteConductSession.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else */
                if (pojoUserSettingsResponseData.privacy.privacyInviteConductSession.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_session_conduct);
                break;

            case R.string.privacy_answer_invite:
                rb_everyone.setVisibility(View.GONE);

               /* if (pojoUserSettingsResponseData.privacy.privacyInviteAnswer.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else */
                if (pojoUserSettingsResponseData.privacy.privacyInviteAnswer.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }
                tv_dialog_description.setText(R.string.privacy_answer_invite);
                break;

            case R.string.privacy_interest_invite:
                rb_everyone.setVisibility(View.GONE);

                /*if (pojoUserSettingsResponseData.privacy.privacyInviteInterest.equals(AppKeys.PUBLIC)) {
                    rb_everyone.setChecked(true);
                } else*/
                if (pojoUserSettingsResponseData.privacy.privacyInviteInterest.equals(AppKeys.FRIENDS)) {
                    rb_my_connections.setChecked(true);
                } else {
                    rb_none.setChecked(true);
                }

                tv_dialog_description.setText(R.string.privacy_interest_invite);
                break;


        }

        if (rb_everyone.isChecked()) {
            oldSetting = AppKeys.PUBLIC;
        } else if (rb_my_connections.isChecked()) {
            oldSetting = AppKeys.FRIENDS;
        } else {
            oldSetting = AppKeys.ME;
        }

        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSetting = "";
                String settingType = "";

                if (rb_everyone.isChecked()) {
                    newSetting = AppKeys.PUBLIC;
                } else if (rb_my_connections.isChecked()) {
                    newSetting = AppKeys.FRIENDS;
                } else {
                    newSetting = AppKeys.ME;
                }

                switch (id) {
                    case R.string.privacy_connection_request:
                        settingType = "privacy_connection_request";
                        pojoUserSettingsResponseData.privacy.privacyConnectionRequest = newSetting;
                        break;

                    case R.string.privacy_see_connections:
                        settingType = "user_privacy_friends";
                        pojoUserSettingsResponseData.privacy.userPrivacyFriends = newSetting;

                        break;

                    case R.string.privacy_see_sessions:
                        settingType = "privacy_session";
                        pojoUserSettingsResponseData.privacy.privacySession = newSetting;

                        break;


                    case R.string.privacy_who_can_message:
                        settingType = "privacy_message";
                        pojoUserSettingsResponseData.privacy.privacyMessage = newSetting;

                        break;

                    case R.string.privacy_see_choices:
                        settingType = "privacy_choices";
                        pojoUserSettingsResponseData.privacy.privacyChoices = newSetting;

                        break;


                    case R.string.privacy_profile_info:
                        settingType = "user_privacy_basic";
                        pojoUserSettingsResponseData.privacy.userPrivacyBasic = newSetting;

                        break;

                    case R.string.privacy_see_content:
                        settingType = "privacy_content";
                        pojoUserSettingsResponseData.privacy.privacyContent = newSetting;

                        break;

                    case R.string.privacy_tagging:
                        settingType = "privacy_tag";
                        pojoUserSettingsResponseData.privacy.privacyTag = newSetting;

                        break;

                    case R.string.privacy_session_invite:
                        settingType = "privacy_invite_session";
                        pojoUserSettingsResponseData.privacy.privacyInviteSession = newSetting;

                        break;

                    case R.string.privacy_session_conduct:
                        settingType = "privacy_invite_conduct_session ";
                        pojoUserSettingsResponseData.privacy.privacyInviteConductSession = newSetting;

                        break;

                    case R.string.privacy_answer_invite:
                        settingType = "privacy_invite_answer";
                        pojoUserSettingsResponseData.privacy.privacyInviteAnswer = newSetting;

                        break;

                    case R.string.privacy_interest_invite:
                        settingType = "privacy_invite_interest";
                        pojoUserSettingsResponseData.privacy.privacyInviteInterest = newSetting;

                        break;

                }
                if (!oldSetting.equals(newSetting)) {
                    hitUpdatePrivacySettingsApi(user_id, "app_settings", settingType, newSetting, id, selectedTab);
                }

                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    private void openSoundAndVibrationDialog() {
        TextView tv_dialog_description, tv_done;
        SwitchCompat sw_switch1, sw_switch2;


        final Dialog dialog = new MyCustomThemeDialog(SettingsActivity.this);
        dialog.setContentView(R.layout.two_switch_dialog);

        tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);
        sw_switch1 = (SwitchCompat) dialog.findViewById(R.id.sw_switch1);
        sw_switch2 = (SwitchCompat) dialog.findViewById(R.id.sw_switch2);


        tv_dialog_description.setText("Turn sounds and vibrations On or Off");

        sw_switch1.setChecked(preferenceUtils.getSoundSettings());

        sw_switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferenceUtils.saveSoundSettings(b);
            }
        });

        sw_switch2.setChecked(preferenceUtils.getVibrationSettings());
        sw_switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preferenceUtils.saveVibrationSettings(b);
            }
        });


        tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void setDataOnViews() {
        pb_loading_content.setVisibility(View.GONE);
        ll_content_wrapper.setVisibility(View.VISIBLE);
        pojoUserSettingsResponseData = preferenceUtils.getUserSettings();

        if (pojoUserSettingsResponseData != null) {
            if (!pojoUserSettingsResponseData.privacy.deleteRequestOn.isEmpty()
                    && pojoUserSettingsResponseData.privacy.withdrawDeleteRequest.equals("No")) {
                tv_withdraw_delete.setVisibility(View.VISIBLE);
            } else {
                tv_withdraw_delete.setVisibility(View.GONE);
            }

            if (pojoUserSettingsResponseData.privacy.assessmentAllowed == null
                    || pojoUserSettingsResponseData.privacy.assessmentAllowed.equals("0")) {
                tv_assessment_report.setVisibility(View.GONE);
            } else {
                tv_assessment_report.setVisibility(View.VISIBLE);
            }

            tl_notification_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    selectedTab = tab.getPosition();
                    if (tab.getPosition() == 0) {
                        setEmailSwitchValues();
                    } else {
                        setPushSwitchValues();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            setEmailSwitchValues();
            if (pojoUserSettingsResponseData.privacy.twoStepVerifications != null &&
                    pojoUserSettingsResponseData.privacy.twoStepVerifications.equals("yes")) {
                sw_two_step_verification.setChecked(true);
            } else {
                sw_two_step_verification.setChecked(false);
            }
        }
    }

    private void hitGetUserOrganisationsApi(final String user_id, String type) {
        Call<PojoUserOrganisationListResponse> call = apiService.getUserOrganisations(user_id, type);
        call.enqueue(new Callback<PojoUserOrganisationListResponse>() {
            @Override
            public void onResponse(Call<PojoUserOrganisationListResponse> call, Response<PojoUserOrganisationListResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (!response.body().data.organization.isEmpty()) {
                            myOrganisationId = response.body().data.organization.get(0).id;
                            tv_organisation_name.setText(response.body().data.organization.get(0).name);
                            tv_organisation_name.setVisibility(View.VISIBLE);
                            ll_my_organisation_wrapper.setVisibility(View.VISIBLE);
                        } else {
                            tv_organisation_name.setVisibility(View.GONE);
                            ll_my_organisation_wrapper.setVisibility(View.GONE);
                        }
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserOrganisationListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitGetUserPrivacySettingApi(final String user_id, String type) {
        Call<PojoUserSettingsResponse> call = apiService.getUserSettings(user_id, type);
        call.enqueue(new Callback<PojoUserSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUserSettingsResponse> call, Response<PojoUserSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        pojoUserSettingsResponseData = response.body().data;
                        preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);
                        setDataOnViews();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitLogoutApi(String user_id) {
        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        Call<PojoLogoutResponse> call = apiService.logout(user_id, android_id, pojoLoginResponseData.sessionId.toString());
        call.enqueue(new Callback<PojoLogoutResponse>() {
            @Override
            public void onResponse(Call<PojoLogoutResponse> call, Response<PojoLogoutResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        /*confirmationDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        preferenceUtils.logOut();*/
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoLogoutResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitSendVerificationRequestApi(String user_id, final String type) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateUserSettings(user_id, type, "", "");
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    confirmationDialog.dismiss();
                    if (type.equals("send_verification_request")) {
                        pojoUserSettingsResponseData.kase = "pending";
                    } else if (type.equals("cancel_verification_request")) {
                        pojoUserSettingsResponseData.kase = "request";
                    }
                    preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitDeleteAccountApi(String user_id, String type) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateUserSettings(user_id, type, "", "");

        if (type.equals("withdraw_delete_account")) {
            pb_delete_withdraw_status.setVisibility(View.VISIBLE);
            tv_withdraw_delete.setVisibility(View.GONE);
        }
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    /*confirmationDialog.dismiss();
                    preferenceUtils.logOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    if (response.body().status == 1) {
                        pb_delete_withdraw_status.setVisibility(View.GONE);
                    } else {
                        pb_delete_withdraw_status.setVisibility(View.GONE);
                        tv_withdraw_delete.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_delete_withdraw_status.setVisibility(View.GONE);
                tv_withdraw_delete.setVisibility(View.VISIBLE);
            }
        });
    }


    private void hitUpdateTwoStepVerificationSettingApi(String user_id, String type, final String value) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.twoStepVerification(user_id, type, value);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pojoUserSettingsResponseData.privacy.twoStepVerifications = value;
                    preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitUpdatePrivacySettingsApi(String user_id, String type, String update_field,
                                             final String update_value, final int id, final int selectedTab) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateUserSettings(user_id, type,
                update_field, update_value);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;

                    switch (id) {
                        case R.string.privacy_connection_request:
                            pojoUserSettingsResponseData.privacy.privacyConnectionRequest = update_value;
                            break;

                        case R.string.privacy_see_connections:
                            pojoUserSettingsResponseData.privacy.userPrivacyFriends = update_value;

                            break;

                        case R.string.privacy_who_can_message:
                            pojoUserSettingsResponseData.privacy.privacyMessage = update_value;

                            break;

                        case R.string.privacy_see_choices:
                            pojoUserSettingsResponseData.privacy.privacyChoices = update_value;

                            break;

                        case R.string.privacy_profile_info:
                            pojoUserSettingsResponseData.privacy.userPrivacyBasic = update_value;

                            break;

                        case R.string.privacy_see_content:
                            pojoUserSettingsResponseData.privacy.privacyContent = update_value;

                            break;

                        case R.string.privacy_tagging:
                            pojoUserSettingsResponseData.privacy.privacyTag = update_value;

                            break;

                        case R.string.privacy_session_invite:
                            pojoUserSettingsResponseData.privacy.privacyInviteSession = update_value;

                            break;

                        case R.string.privacy_session_conduct:
                            pojoUserSettingsResponseData.privacy.privacyInviteConductSession = update_value;

                            break;

                        case R.string.privacy_answer_invite:
                            pojoUserSettingsResponseData.privacy.privacyInviteAnswer = update_value;

                            break;

                        case R.string.privacy_interest_invite:
                            pojoUserSettingsResponseData.privacy.privacyInviteInterest = update_value;

                            break;

                        /*case R.id.sw_invite_switch:
                            if (sw_invite_switch.isChecked()) {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailInvitesMessages = "yes";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushInvitesMessages = "yes";
                                }

                            } else {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailInvitesMessages = "no";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushInvitesMessages = "no";
                                }
                            }
                            break;

                        case R.id.sw_session_related_switch:
                            if (sw_session_related_switch.isChecked()) {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailSessions = "yes";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushSessions = "yes";
                                }

                            } else {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailSessions = "no";

                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushSessions = "no";
                                }
                            }
                            break;

                        case R.id.sw_qna_related_switch:
                            if (sw_qna_related_switch.isChecked()) {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailQa = "yes";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushQa = "yes";
                                }

                            } else {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailQa = "no";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushQa = "no";
                                }

                            }
                            break;

                        case R.id.sw_articles_related:
                            if (sw_articles_related.isChecked()) {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailArticles = "yes";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushArticles = "yes";
                                }

                            } else {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailArticles = "no";

                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushArticles = "no";
                                }
                            }
                            break;

                        case R.id.sw_updates_on_activities:
                            if (sw_updates_on_activities.isChecked()) {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailUpdate = "yes";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushUpdate = "yes";
                                }

                            } else {
                                if (selectedTab == 0) {
                                    pojoUserSettingsResponseData.appSettings.email.privacyNotificationEmailUpdate = "no";
                                } else {
                                    pojoUserSettingsResponseData.appSettings.push.privacyNotificationPushUpdate = "no";
                                }
                            }
                            break;*/
                    }
                    preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_VERIFIED) {
            //  hitGetUserPhoneEmailApi(user_id);
        }
    }

}
