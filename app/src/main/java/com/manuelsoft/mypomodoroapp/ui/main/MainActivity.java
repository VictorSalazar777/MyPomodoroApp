package com.manuelsoft.mypomodoroapp.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.manuelsoft.mypomodoroapp.BuildConfig;
import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.chronometer.ChronometerService;
import com.manuelsoft.mypomodoroapp.common.Utilities;
import com.manuelsoft.mypomodoroapp.ui.credits.CreditsActivity;

import static android.view.Menu.NONE;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_5_SECONDS_TEST;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_TICK;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.TIME;
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
    private ChronometerView chronometerView;
    private ReceiverAccessor receiverAccessor;
    private UISharedPreferences uiSharedPreferences;
    private ChronometerServiceAccessor chronometerServiceAccessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupReceiver();
        uiSharedPreferences = new UISharedPreferences(this);
        chronometerServiceAccessor = new ChronometerServiceAccessor(this);

        setupViewModel();
        setupToolbar();
        setupChronometer();
        setupStartStopBtn();
        setupFifteenMinutesBtn();
        setupTwentyMinutesBtn();
    }

    private void setupViewModel() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        if (uiSharedPreferences.loadChronometerIsRunning() && isChronometerServiceInTheForeground()) {
            mainActivityViewModel.setStateActive();
        } else {
            mainActivityViewModel.setStateInactive();
        }

        if (uiSharedPreferences.loadTimeSelected() == FIFTEEN) {
            mainActivityViewModel.setFifteenMinutes();
        } else {
            mainActivityViewModel.setTwentyMinutes();
        }

    }

    private boolean isChronometerServiceInTheForeground() {
        boolean result = Utilities.isForegroundServiceRunning(this, ChronometerService.class);
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
        createTest5SecondsItem(menu);
        return true;
    }

    private void createTest5SecondsItem(Menu menu) {
        if (BuildConfig.DEBUG) {
            menu.add(NONE, R.id.menu_item_5_sec, NONE, R.string.menu_item_test_5_sec);
            menu.add(NONE, R.id.menu_item_reset_tests, NONE, R.string.menu_item_reset_tests);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_credits) {
            Log.d(TAG, "Credits");
            Intent intent = new Intent(this, CreditsActivity.class);
            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_item_5_sec) {
            setupTestButton();
        } else {
            resetTests();
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
                    uiSharedPreferences.saveUISharedPreferences(false, TWENTY);
                } else {
                    chronometerView.setText(R.string.txt_fifteen_minutes);
                    uiSharedPreferences.saveUISharedPreferences(false, FIFTEEN);
                }
                fifteenMinutesBtn.setEnabled(true);
                twentyMinutesBtn.setEnabled(true);
                chronometerServiceAccessor.stopChronometer();
            } else {
                mainActivityViewModel.setStateActive();
                chronometerView.setActive(true);
                uiSharedPreferences
                        .saveUISharedPreferences(true,
                                mainActivityViewModel.getHowManyMinutes());
                startStopBtn.setText(R.string.txt_btn_stop);
                fifteenMinutesBtn.setEnabled(false);
                twentyMinutesBtn.setEnabled(false);
                chronometerServiceAccessor.startChronometer(mainActivityViewModel.getHowManyMinutes());
            }
        });
    }

    private void setupChronometer() {
        chronometerView = findViewById(R.id.chronometer);
        String minutes = mainActivityViewModel.getHowManyMinutes() + ":00";
        chronometerView.setText(minutes);
        chronometerView.setActive(mainActivityViewModel.isActive());
    }

    private void setupReceiver() {
        ReceiverAccessor.OnReceive onReceive = (context, intent) -> {
            switch (intent.getAction()) {
                case ACTION_TICK:
                    String time = intent.getStringExtra(TIME);
                    chronometerView.setText(time);
                    break;
                case ACTION_FINISH:
                    showFinishPomodoroDialog();
                    uiSharedPreferences
                            .saveUISharedPreferences(false,
                                    mainActivityViewModel.getHowManyMinutes());
                    break;
                case ACTION_5_SECONDS_TEST:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "ACTION 5 SECONDS TEST received");
                        showFinishPomodoroDialog();
                    }
                    break;
                default:
                    throw new RuntimeException("Receiver: unknown option");
            }
        };

        receiverAccessor = new ReceiverAccessor(this, onReceive);
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
        Button testBtn = findViewById(R.id.btn_test);
        testBtn.setVisibility(View.VISIBLE);
        testBtn.setOnClickListener(v -> {
            Log.d(TAG, "Click on button test 5 sec");
            mainActivityViewModel.setStateActive();
            chronometerView.setActive(true);
            chronometerServiceAccessor.sendOneTick();
        });
    }

    @VisibleForTesting
    private void resetTests() {
        Button testBtn = findViewById(R.id.btn_test);
        testBtn.setVisibility(View.GONE);
    }

    @VisibleForTesting
    public void destroyService() {
        chronometerServiceAccessor.stopForegroundService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        receiverAccessor.connect();
        chronometerServiceAccessor.bindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        receiverAccessor.disconnect();
        chronometerServiceAccessor.unbindService();
    }
}