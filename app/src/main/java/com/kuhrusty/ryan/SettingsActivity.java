package com.kuhrusty.ryan;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    //  this value is duplicated in pref_general.xml.
    public static final int DEFAULT_TIMER_DURATION = 30;

    /**  in seconds. */
    public static final String KEY_PREF_TIMER_DURATION = "timer_duration";
    public static final String KEY_PREF_SOUND = "sound";
    public static final String KEY_PREF_PAUSE = "pause";
    public static final String KEY_PREF_SAND = "sand";
    public static final String KEY_PREF_COUNTDOWN_DURATION = "countdown_duration";
    public static final String KEY_PREF_COUNTDOWN_SOUND = "countdown_sound";

    /**
     * Returns the currently selected timer duration in seconds.
     *
     * @param prefs must not be null.
     */
    public static int getTimerDuration(SharedPreferences prefs) {
        return Integer.parseInt(prefs.getString(KEY_PREF_TIMER_DURATION,
                Integer.toString(DEFAULT_TIMER_DURATION)));
    }
    /**
     * Returns the currently selected sound name (or other code, like "random").
     *
     * @param prefs must not be null.
     */
    public static String getSound(SharedPreferences prefs) {
        return prefs.getString(KEY_PREF_SOUND, "ryan");
    }
    /**
     * Returns true if tap-to-pause-instead-of-reset is currently selected.
     * If this is true, isSand() will be ignored.
     *
     * @param prefs must not be null.
     */
    public static boolean hasPause(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_PREF_PAUSE, false);
    }
    /**
     * Returns true if sand-timer mode is currently selected.
     *
     * @param prefs must not be null.
     */
    public static boolean isSand(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_PREF_SAND, false);
    }
    /**
     * Returns the currently selected countdown duration (the period in which
     * we'll play a sound every second) in seconds.
     *
     * @param prefs must not be null.
     */
    public static int getCountdownDuration(SharedPreferences prefs) {
        return Integer.parseInt(prefs.getString(KEY_PREF_COUNTDOWN_DURATION, "0"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setupActionBar();
getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

//    /**
//     * Set up the {@link android.app.ActionBar}, if the API is available.
//     */
//    private void setupActionBar() {
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }


//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this);
//    }

//    /**
//     * Helper method to determine if the device has an extra-large screen. For
//     * example, 10" tablets are extra-large.
//     */
//    private static boolean isXLargeTablet(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (KEY_PREF_PAUSE.equals(preference.getKey())) {
                preference.setSummary(R.string.pref_summary_pause);
            } else if (KEY_PREF_SAND.equals(preference.getKey())) {
                preference.setSummary(R.string.pref_summary_sand);
            } else if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

//            } else if (preference instanceof RingtonePreference) {
//                // For ringtone preferences, look up the correct display value
//                // using RingtoneManager.
//                if (TextUtils.isEmpty(stringValue)) {
//                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent);
//
//                } else {
//                    Ringtone ringtone = RingtoneManager.getRingtone(
//                            preference.getContext(), Uri.parse(stringValue));
//
//                    if (ringtone == null) {
//                        // Clear the summary if there was a lookup error.
//                        preference.setSummary(null);
//                    } else {
//                        // Set the summary to reflect the new ringtone display
//                        // name.
//                        String name = ringtone.getTitle(preference.getContext());
//                        preference.setSummary(name);
//                    }
//                }
//
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference,
                                                     Preference.OnPreferenceChangeListener pcl) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(pcl);

        // Trigger the listener immediately with the preference's
        // current value.
        Object value = null;
        if ((preference instanceof CheckBoxPreference) || (preference instanceof SwitchPreference)) {
            value = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getBoolean(preference.getKey(), false);
        } else {
            value = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), "");
        }
        pcl.onPreferenceChange(preference, value);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return /* PreferenceFragment.class.getName().equals(fragmentName)
                || */ GeneralPreferenceFragment.class.getName().equals(fragmentName) /*
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName) */;
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_TIMER_DURATION), this);
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_SOUND), this);
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_PAUSE), this);
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_SAND), this);
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_COUNTDOWN_DURATION), this);
            bindPreferenceSummaryToValue(findPreference(KEY_PREF_COUNTDOWN_SOUND), this);
        }

        /**
         * This is sort of a wrapper around the static
         * sBindPreferenceSummaryToValueListener, which didn't have access to
         * the PreferenceFragment for findPreference().
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            //  Pause and Sand Timer are mutually exclusive.
            if (KEY_PREF_PAUSE.equals(preference.getKey())) {
                if ((value instanceof Boolean) && ((Boolean)value)) {
                    turnOff(KEY_PREF_SAND);
                }
            } else if (KEY_PREF_SAND.equals(preference.getKey())) {
                if ((value instanceof Boolean) && ((Boolean)value)) {
                    turnOff(KEY_PREF_PAUSE);
                }
            }
            return sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
        }

        private void turnOff(String prefKey) {
            SwitchPreference sp = (SwitchPreference)findPreference(prefKey);
            if ((sp != null) && sp.isChecked()) {
                sp.setChecked(false);
            }
        }
    }

//    /**
//     * This fragment shows notification preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class NotificationPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_notification);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }

//    /**
//     * This fragment shows data and sync preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class DataSyncPreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_data_sync);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
}
