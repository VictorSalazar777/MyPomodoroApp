package com.manuelsoft.mypomodoroapp.ui.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;
import com.manuelsoft.mypomodoroapp.BuildConfig;
import com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService;
import com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.MyChronometerBinder;
import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.common.Utilities;
import com.manuelsoft.mypomodoroapp.ui.credits.CreditsActivity;

import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_TEST;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_TICK;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.TIME;
import static com.manuelsoft.mypomodoroapp.ui.main.MainActivityViewModel.FIFTEEN;
import static com.manuelsoft.mypomodoroapp.ui.main.MainActivityViewModel.TWENTY;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    public static final String UI_SHARED_PREFERENCES = "com.manuelsoft.mypomodoroapp.UI_SHARED_PREFERENCES";
    public static final String CHRONOMETER_IS_RUNNING = "CHRONOMETER_IS_RUNNING";
    public static final String TIME_SELECTED = "TIME_SELECTED";
    private MainActivityViewModel mainActivityViewModel;
    private Button startStopBtn;
    private MaterialButton fifteenMinutesBtn;
    private MaterialButton twentyMinutesBtn;
    private Toolbar toolbar;
    private MyChronometer chronometerView;
    private MyChronometerService service;
    private ServiceConnection connection;
    private BroadcastReceiver receiver;
    private boolean bound = false;
    private boolean receiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewModel();
        setupToolbar();
        setupChronometer();
        setupReceiver();
        registerReceiver();
        startService();
        setupServiceConnection();
        bindService();
        setupStartStopBtn();
        setupFifteenMinutesBtn();
        setupTwentyMinutesBtn();
        setupTestButton();
    }

    private SharedPreferences getUISharedPreferences() {
        return getSharedPreferences(UI_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    private void saveUISharedPreferences(boolean chronometerIsRunning, int time) {
        getUISharedPreferences().edit()
                .putBoolean(CHRONOMETER_IS_RUNNING, chronometerIsRunning)
                .putInt(TIME_SELECTED, time)
        .apply();
    }

    private boolean loadChronometerIsRunning() {
        boolean result = getUISharedPreferences().getBoolean(CHRONOMETER_IS_RUNNING, false);
        Log.d(TAG, "loadChronometerIsRunning(): " + result);
        return result;
    }

    private int loadTimeSelected() {
        return getUISharedPreferences().getInt(TIME_SELECTED, TWENTY);
    }

    private void setupViewModel() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        if (loadChronometerIsRunning() && isChronometerServiceInTheForeground()) {
            mainActivityViewModel.setStateActive();
        } else {
            mainActivityViewModel.setStateInactive();
        }

        if (loadTimeSelected() == FIFTEEN) {
            mainActivityViewModel.setFifteenMinutes();
        } else {
            mainActivityViewModel.setTwentyMinutes();
        }

    }

    private boolean isChronometerServiceInTheForeground() {
        boolean result = Utilities.isForegroundServiceRunning(this, MyChronometerService.class);
        Log.d(TAG, "isChronometerServiceInTheForeground(): " + result);
        return result;
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_credits, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_credits) {
            Log.d(TAG, "Credits");
            Intent intent = new Intent(this, CreditsActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupTwentyMinutesBtn() {
        twentyMinutesBtn = findViewById(R.id.btn_twenty_min);
        if (mainActivityViewModel.isActive()) {
            twentyMinutesBtn.setEnabled(false);
            if (mainActivityViewModel.getHowManyMinutes() == TWENTY) {
                twentyMinutesBtn.setChecked(true);
            }
        }

        twentyMinutesBtn.setOnClickListener(v -> {
            if (!mainActivityViewModel.isActive()) {
                mainActivityViewModel.setTwentyMinutes();
                chronometerView.setText(R.string.txt_twenty_minutes);
            }
        });
    }

    private void setupFifteenMinutesBtn() {
        fifteenMinutesBtn = findViewById(R.id.btn_fifteen_min);
        if (mainActivityViewModel.isActive()) {
            fifteenMinutesBtn.setEnabled(false);
            if (mainActivityViewModel.getHowManyMinutes() == FIFTEEN) {
                fifteenMinutesBtn.setChecked(true);
            }
        }

        fifteenMinutesBtn.setOnClickListener(v -> {
            if (!mainActivityViewModel.isActive()) {
                mainActivityViewModel.setFifteenMinutes();
                chronometerView.setText(R.string.txt_fifteen_minutes);
            }
        });
    }

    private void setupStartStopBtn() {
        startStopBtn = findViewById(R.id.btn_start_stop);
        if (mainActivityViewModel.isActive()) {
            startStopBtn.setText(R.string.txt_btn_stop);
        } else {
            startStopBtn.setText(R.string.txt_btn_start);
        }
        startStopBtn.setOnClickListener(v -> {
            if (mainActivityViewModel.isActive()) {
                mainActivityViewModel.setStateInactive();
                chronometerView.setActive(false);
                startStopBtn.setText(R.string.txt_btn_start);
                if (mainActivityViewModel.getHowManyMinutes() == TWENTY) {
                    chronometerView.setText(R.string.txt_twenty_minutes);
                    saveUISharedPreferences(false, TWENTY);
                } else {
                    chronometerView.setText(R.string.txt_fifteen_minutes);
                    saveUISharedPreferences(false, FIFTEEN);
                }
                fifteenMinutesBtn.setEnabled(true);
                twentyMinutesBtn.setEnabled(true);
                stopChronometer();
            } else {
                mainActivityViewModel.setStateActive();
                chronometerView.setActive(true);
                saveUISharedPreferences(true, mainActivityViewModel.getHowManyMinutes());
                startStopBtn.setText(R.string.txt_btn_stop);
                fifteenMinutesBtn.setEnabled(false);
                twentyMinutesBtn.setEnabled(false);
                startChronometer();
            }
        });
    }

    private void setupChronometer() {
        chronometerView = findViewById(R.id.chronometer);
        String minutes = mainActivityViewModel.getHowManyMinutes() + ":00";
        chronometerView.setText(minutes);
        chronometerView.setActive(mainActivityViewModel.isActive());
    }

    private void startChronometer() {
        if (bound) {
            service.setChronometer(mainActivityViewModel.getHowManyMinutes());
            service.startChronometer();
        }
        Log.d(TAG, "Sending message");
        Log.d(TAG, "service running: " + Utilities.isServiceRunning(this, MyChronometerService.class));
    }

    private void stopChronometer() {
        if (bound) {
            service.stopChronometer();
        }
    }

    private void startService() {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        startService(intent);
    }

    private void stopForegroundService() {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        stopService(intent);
    }

    private void setupServiceConnection() {
        connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                bound = true;
                MyChronometerBinder binder = (MyChronometerBinder) service;
                MainActivity.this.service = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };
    }

    private void bindService() {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void setupReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_TICK:
                        String time = intent.getStringExtra(TIME);
                        chronometerView.setText(time);
                        break;
                    case ACTION_FINISH:
                        showFinishPomodoroDialog();
                        saveUISharedPreferences(false, mainActivityViewModel.getHowManyMinutes());
                        break;
                    case ACTION_TEST:
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "ACTION TEST received");
                            showFinishPomodoroDialog();
                        }
                        break;
                    default:
                        throw new RuntimeException("Receiver: unknown option");
                }
            }
        };
    }

    private void registerReceiver() {
        if (!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_TICK);
            filter.addAction(ACTION_FINISH);
            if (BuildConfig.DEBUG) {
                filter.addAction(ACTION_TEST);
            }
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
        }
    }

    private void unregisterReceiver() {
        if (receiverRegistered) {
            receiverRegistered = false;
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
        bindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver();
        unbindService();
    }

    private void onFinishPomodoro() {
        chronometerView.setActive(false);
        startStopBtn.setText(R.string.txt_btn_start);
        mainActivityViewModel.setStateInactive();
        fifteenMinutesBtn.setEnabled(true);
        twentyMinutesBtn.setEnabled(true);
    }

    private void showFinishPomodoroDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.txt_pomodoro_finished_dialog)
                .setNeutralButton(R.string.txt_btn_pomodoro_finished_dialog, (dialog, which) -> {
                    dialog.dismiss();
                    onFinishPomodoro();
                })
                .create()
                .show();
    }

    @VisibleForTesting
    public void setupTestButton() {
        if (BuildConfig.DEBUG) {
            Button testBtn = findViewById(R.id.btn_test);
            testBtn.setVisibility(View.VISIBLE);
            testBtn.setOnClickListener(v -> {
                Log.d(TAG, "Click on button Test");
                mainActivityViewModel.setStateActive();
                chronometerView.setActive(true);
                service.sendOneTick();
            });
        }
    }

    @VisibleForTesting
    public void destroyService() {
        stopForegroundService();
    }

}