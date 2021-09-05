package com.manuelsoft.mypomodoroapp.ui.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;
import com.manuelsoft.mypomodoroapp.BuildConfig;
import com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService;
import com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.MyChronometerBinder;
import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.common.Utilities;
import com.manuelsoft.mypomodoroapp.ui.credits.CreditsActivity;

import static com.manuelsoft.mypomodoroapp.ui.main.MainActivityPresenter.TWENTY;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_TEST;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.ACTION_TICK;
import static com.manuelsoft.mypomodoroapp.chronometer.MyChronometerService.TIME;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private MainActivityPresenter mainActivityPresenter;
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
        mainActivityPresenter = new MainActivityPresenterImpl();
        mainActivityPresenter.setStateInactive();
        setupToolbar();
        setupChronometer();
        setupReceiver();
        registerReceiver();
        startForegroundService();
        setupServiceConnection();
        bindService();
        setupStartStopBtn();
        setupFifteenMinutesBtn();
        setupTwentyMinutesBtn();
        setupTestButton();
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
        twentyMinutesBtn.setOnClickListener(v -> {
            if (!mainActivityPresenter.isActive()) {
                mainActivityPresenter.setTwentyMinutes();
                chronometerView.setText(R.string.txt_twenty_minutes);
            }
        });
    }

    private void setupFifteenMinutesBtn() {
        fifteenMinutesBtn = findViewById(R.id.btn_fifteen_min);
        fifteenMinutesBtn.setOnClickListener(v -> {
            if (!mainActivityPresenter.isActive()) {
                mainActivityPresenter.setFifteenMinutes();
                chronometerView.setText(R.string.txt_fifteen_minutes);
            }
        });
    }

    private void setupStartStopBtn() {
        startStopBtn = findViewById(R.id.btn_start_stop);
        startStopBtn.setOnClickListener(v -> {
            if (mainActivityPresenter.isActive()) {
                mainActivityPresenter.setStateInactive();
                chronometerView.setActive(false);
                startStopBtn.setText(R.string.txt_btn_start);
                if (mainActivityPresenter.getHowManyMinutes() == TWENTY) {
                    chronometerView.setText(R.string.txt_twenty_minutes);
                } else {
                    chronometerView.setText(R.string.txt_fifteen_minutes);
                }
                fifteenMinutesBtn.setEnabled(true);
                twentyMinutesBtn.setEnabled(true);
                stopChronometer();
            } else {
                mainActivityPresenter.setStateActive();
                chronometerView.setActive(true);
                startStopBtn.setText(R.string.txt_btn_stop);
                fifteenMinutesBtn.setEnabled(false);
                twentyMinutesBtn.setEnabled(false);
                startChronometer();
            }
        });
    }

    private void setupChronometer() {
        chronometerView = findViewById(R.id.chronometer);
        String minutes = mainActivityPresenter.getHowManyMinutes() + ":00";
        chronometerView.setText(minutes);
    }

    private void startChronometer() {
        if (bound) {
            service.setChronometer(mainActivityPresenter.getHowManyMinutes());
            service.startChronometer();
        }
        Log.d(TAG, "Sending message");
        Log.d(TAG, "service running: " + Utilities.isMyServiceRunning(this, MyChronometerService.class));
    }

    private void stopChronometer() {
        if (bound) {
            service.stopChronometer();
        }
    }

    private void startForegroundService() {
        Intent intent = new Intent(getApplicationContext(), MyChronometerService.class);
        ContextCompat.startForegroundService(this,intent );
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
        // startService(intent);
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
        mainActivityPresenter.setStateInactive();
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
                mainActivityPresenter.setStateActive();
                chronometerView.setActive(true);
                service.sendOneTick();
            });
        }
    }

    @VisibleForTesting
    public void destroyService() {
        stopForegroundService();
    }

    @Override
    protected void onDestroy() {
        if (!mainActivityPresenter.isActive()) {
            stopForegroundService();
        }
        super.onDestroy();
    }
}