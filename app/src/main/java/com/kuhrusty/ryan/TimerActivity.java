package com.kuhrusty.ryan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.support.v7.widget.AppCompatTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * This displays the remaining time; when tapped, it starts the countdown, and
 * when tapped again, it resets.
 */
public class TimerActivity extends AppCompatActivity {//implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = "AllOverButTheRyan";

    private static final int REQUEST_CODE_SETTINGS = 1;

    //  savedInstanceState keys
    private static final String KEY_TIMER_RUNNING = "timerRunning";
    private static final String KEY_TIMER_FINISHED = "timerFinished";
    private static final String KEY_SECONDS_REMAINING = "secondsRemaining";
    private static final String KEY_LAST_TICK_SECONDS_REMAINING = "lastTickSecondsRemaining";
    private static final String KEY_RUNS = "runs";
    private static final String KEY_TIMEOUTS = "timeouts";

    //  this value is duplicated in pref_general.xml.
    private int timerDuration = SettingsActivity.DEFAULT_TIMER_DURATION;
    private int countdownDuration = 0;
    private int secondsRemaining = timerDuration;
    private int lastTickSecondsRemaining = -1;
    private long timerIntervalMS = 1000L;
//            getDefaultSharedPreferences(this).getInt(KEY_PREF_TIMER_SECONDS, 30);
    private boolean timerRunning = false;
    private boolean timerFinished = false;
    private boolean isSand = false;
    private int runs = 0;
    private int timeouts = 0;

    private CountDownTimer timer;
    private MediaPlayer mediaPlayer;
    private TextView timerDisplay;
    private static Random random = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_timer);

        //  When people hit the volume buttons, we want to change the media
        //  volume, not the ringtone volume.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //  We want the device to stay on, so it doesn't have to be unlocked
        //  to start the timer.  We *could* set this when resetting the timer,
        //  and clear it when the timer expires (so that the device *can* turn
        //  off on the "take your damn turn" screen)...
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Resources res = getResources();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        timerDuration = SettingsActivity.getTimerDuration(sharedPref);
        countdownDuration = SettingsActivity.getCountdownDuration(sharedPref);
        secondsRemaining = timerDuration;
        isSand = SettingsActivity.isSand(sharedPref);

        if (savedInstanceState != null) {
            timerRunning = savedInstanceState.getBoolean(KEY_TIMER_RUNNING);
            timerFinished = savedInstanceState.getBoolean(KEY_TIMER_FINISHED);
            secondsRemaining = savedInstanceState.getInt(KEY_SECONDS_REMAINING);
            lastTickSecondsRemaining = savedInstanceState.getInt(KEY_LAST_TICK_SECONDS_REMAINING);
            runs = savedInstanceState.getInt(KEY_RUNS);
            timeouts = savedInstanceState.getInt(KEY_TIMEOUTS);
        }

        // Create the text view
        //  This seems... excessive...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timerDisplay = new TextView(this);
            timerDisplay.setAutoSizeTextTypeUniformWithConfiguration(
                    40, 200, 20, TypedValue.COMPLEX_UNIT_DIP);
        } else {
            timerDisplay = new AppCompatTextView(this);
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(timerDisplay,
                    40, 200, 20, TypedValue.COMPLEX_UNIT_DIP);
        }
        timerDisplay.setGravity(Gravity.CENTER);

        if (timerRunning) {
            startTimer(true);
        } else if (timerFinished) {
            handleTimeUp(true);
        } else {
            resetTimer();
        }
//        tv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (timerRunning) {
//                    resetTimer();
//                } else {
//                    startTimer();
//                }
////                finish();
//                return true;
//            }
//        });
        timerDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerFinished) {
                    resetTimer();
                } else if (timerRunning) {
                    if (isSand) {
                        flipTimer();
                    } else {
                        resetTimer();
                    }
                } else {
                    startTimer(false);
                }
            }
        });
        timerDisplay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //  originally this was if (sand && timerRunning), but we want
                //  long clicks to reset regular non-sand timers too.
                if (timerRunning) {
                    resetTimer();
                    return true;
                }
                return false;
            }
        });
        setContentView(timerDisplay);
    }

    void resetTimer() {
        timerRunning = false;
        timerFinished = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        secondsRemaining = timerDuration;
        lastTickSecondsRemaining = -1;

        Resources res = getResources();
        //  setMaxLines() is because, otherwise, even though we're auto-sizing
        //  the text, "5:00" will get wrapped to "5:0\n0" !?
        timerDisplay.setMaxLines(1);
        timerDisplay.setText(formatSeconds(secondsRemaining));
        timerDisplay.setTextColor(res.getColor(R.color.waitingFG));
        timerDisplay.setBackgroundColor(res.getColor(R.color.waitingBG));
    }

    void flipTimer() {
        //  necessary?
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        //  This is not *exactly* right, as if the timer runs 4.5 seconds,
        //  we're gaining or losing half a second.  I think that'll be OK
        //  for board games, though.
        secondsRemaining = timerDuration - secondsRemaining;
        startTimer(false);
    }

    void startTimer(boolean restoringState) {
        if (timer != null) {
            timer.cancel();
        }
        if (!restoringState) {
            ++runs;
        }
        //  OK... there is a, uhh... well, let's say "bug," where CountDownTimer
        //  won't deliver the last tick because a few ms fewer than a full
        //  interval remains, so it goes 5, 4, 3, 2... ... onFinish().  So one
        //  workaround is to crank down the timer interval; the fact that we're
        //  firing twice per second won't be visible, because the second fire
        //  just sets the text to what it already is.  Ugh.
        timer = new CountDownTimer(secondsRemaining * 1000L, timerIntervalMS / 2) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = (int)((millisUntilFinished - 1L) / 1000L) + 1;
                //Log.d(LOG_TAG, "timer onTick(" + millisUntilFinished +
                //        "), calling it " + secondsRemaining + "s remaining");
                handleTick(secondsRemaining);
            }
            @Override
            public void onFinish() {
                //Log.d(LOG_TAG, "timer onFinish()");
                handleTimeUp(false);
            }
        };
        timer.start();
        timerRunning = true;
        timerFinished = false;
        Resources res = getResources();
        timerDisplay.setTextColor(res.getColor(R.color.runningFG));
        timerDisplay.setBackgroundColor(res.getColor(R.color.runningBG));
    }

    /**
     * Format seconds remaining as minutes:seconds.
     */
    String formatSeconds(int secondsRemaining) {
        if (secondsRemaining < 60) {
            return Integer.toString(secondsRemaining);
        }
        int mins = secondsRemaining / 60;
        int secs = secondsRemaining % 60;
        //  real classy
        return mins + ":" + ((secs > 9) ? "" : "0") + secs;
    }

    void handleTick(int secondsRemaining) {
        timerDisplay.setText(formatSeconds(secondsRemaining));
        if ((countdownDuration > 0) && (secondsRemaining <= countdownDuration) &&
            (secondsRemaining != lastTickSecondsRemaining)) {
            //  figure out which audio file to play
//well, initially, the only option is beeping
            int sound = R.raw.lowbeep;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.start();
        }
        lastTickSecondsRemaining = secondsRemaining;
    }

    void handleTimeUp(boolean restoringState) {
        //  set timer = null?
        if (!restoringState) {
            ++timeouts;
        }
        timerRunning = false;
        timerFinished = true;
        Resources res = getResources();
        CharSequence text = res.getText(R.string.arghh1);
        //  for debugging, let's run through the list of strings sequentially.
        //  There's got to be a better way than hard-coding the number of
        //  strings here...
        switch ((timeouts - 1) % 3) {  //  #6 doesn't fit
            //handled above case 0: text = res.getText(R.string.arghh1); break;
            case 1: text = res.getText(R.string.arghh2); break;
            case 2: text = res.getText(R.string.arghh3); break;
            //case 3: text = res.getText(R.string.arghh4); break;
            //case 4: text = res.getText(R.string.arghh5); break;
            //case 5: text = res.getText(R.string.arghh6); break;
        }
        //  bad hard-coding: we "know" TAKE YOUR DAMN TURN can be 4 lines, but
        //  we need to undo the setMaxLines(1) done earlier.
        timerDisplay.setMaxLines(4);
        timerDisplay.setText(text);
        timerDisplay.setTextColor(res.getColor(R.color.endedFG));
        timerDisplay.setBackgroundColor(res.getColor(R.color.endedBG));

        if (restoringState) return;  //  we don't want to re-play the sound

        //  figure out which audio file to play
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String soundName = SettingsActivity.getSound(sharedPref);
        int sound = R.raw.ryan;
        //Log.d(LOG_TAG, "choosing sound: " + soundName);
        if (soundName.equals("random")) sound = nextRand();
        else if (soundName.equals("sequence")) sound = nextSequence(timeouts - 1);
        else if (soundName.equals("ryan")) sound = R.raw.ryan;
        else if (soundName.equals("takeyourdamnturn")) sound = R.raw.takeyourdamnturn;
        else if (soundName.equals("cletus")) sound = R.raw.cletus;
        else if (soundName.equals("go1")) sound = R.raw.go1;
        else if (soundName.equals("stab")) sound = R.raw.stab;
        else if (soundName.equals("youpass")) sound = R.raw.youpass;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
        mediaPlayer.start();
    }

    private int nextRand() {
        if (random == null) {
            random = new Random();
        }
        int rv = random.nextInt(6);
        //Log.d(LOG_TAG, "got random " + rv);
        return nextSequence(rv);
    }

    private int nextSequence(int count) {
        switch (count % 6) {
            case 0: return R.raw.ryan;
            case 1: return R.raw.takeyourdamnturn;
            case 2: return R.raw.youpass;
            case 3: return R.raw.go1;
            case 4: return R.raw.cletus;
            case 5: return R.raw.stab;
        }
        return R.raw.ryan;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_TIMER_RUNNING, timerRunning);
        savedInstanceState.putBoolean(KEY_TIMER_FINISHED, timerFinished);
        //  Because we only store the number of seconds remaining, not
        //  milliseconds, you can extend the timer by a fraction of a second
        //  when rotating the phone.  But as long as this is just used for
        //  boardgames, not brain surgery or navigating a nuclear submarine, we
        //  should be OK.
        savedInstanceState.putInt(KEY_SECONDS_REMAINING, secondsRemaining);
        savedInstanceState.putInt(KEY_LAST_TICK_SECONDS_REMAINING, lastTickSecondsRemaining);
        savedInstanceState.putInt(KEY_RUNS, runs);
        savedInstanceState.putInt(KEY_TIMEOUTS, timeouts);
    }

    @Override
    public void onStop() {
        if (timer != null) {
            timer.cancel();

        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
//Log.d(LOG_TAG, "got onPause(), unregistering pref change listener");
//        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
//                .unregisterOnSharedPreferenceChangeListener(this);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(SettingsActivity.KEY_PREF_TIMER_DURATION)) {
//Log.d(LOG_TAG, "got onSharedPreferenceChanged(sp, " + key + "), timerRunning == " + timerRunning);
//            if (!timerRunning) {
//                resetTimer();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SETTINGS) {
            //  we don't care about the resultCode.

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            timerDuration = SettingsActivity.getTimerDuration(sharedPref);
            countdownDuration = SettingsActivity.getCountdownDuration(sharedPref);
            isSand = SettingsActivity.isSand(sharedPref);
            resetTimer();
        }
    }
}
