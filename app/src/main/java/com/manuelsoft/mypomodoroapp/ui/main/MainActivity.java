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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.manuelsoft.mypomodoroapp.BuildConfig;
import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.chronometer.ChronometerService;
import com.manuelsoft.mypomodoroapp.common.Utilities;
import com.manuelsoft.mypomodoroapp.ui.credits.CreditsActivity;

import static android.view.Menu.NONE;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_5_SEC_TEST_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_ONE_TICK_TEST;
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
    private AppCompatTextView chronometerView;
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
        setupButtons();
        setupStartStopBtnAction();
        setupFifteenMinutesBtnAction();
        setupTwentyMinutesBtnAction();
    }

    private void setupViewModel() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.runChronometer(
                uiSharedPreferences.loadChronometerIsRunning()
                        && isChronometerServiceInTheForeground());

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
        createDebugMenuItems(menu);
        return true;
    }

    private void createDebugMenuItems(Menu menu) {
        if (BuildConfig.DEBUG) {
            menu.add(NONE, R.id.menu_item_one_tick_test, NONE, R.string.txt_menu_item_one_tick_test);
            menu.add(NONE, R.id.menu_item_5_sec, NONE, R.string.txt_menu_item_5_sec_test);
            menu.add(NONE, R.id.menu_item_reset_tests, NONE, R.string.txt_menu_item_reset_tests);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_credits) {
            Log.d(TAG, "Credits");
            Intent intent = new Intent(this, CreditsActivity.class);
            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_item_one_tick_test) {
            showOneTickTestButton();
        }
        else if (item.getItemId() == R.id.menu_item_5_sec) {
            show5SecTestButton();
        } else {
            hideTestButton();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupButtons() {
        twentyMinutesBtn = findViewById(R.id.btn_twenty_min);
        fifteenMinutesBtn = findViewById(R.id.btn_fifteen_min);
        startStopBtn = findViewById(R.id.btn_start_stop);

        if (mainActivityViewModel.isChronometerRunning()) {
            twentyMinutesBtn.setEnabled(false);
            fifteenMinutesBtn.setEnabled(false);

            if (mainActivityViewModel.getHowManyMinutes() == TWENTY) {
                twentyMinutesBtn.setChecked(true);
            }
            else if (mainActivityViewModel.getHowManyMinutes() == FIFTEEN) {
                fifteenMinutesBtn.setChecked(true);
            }

            startStopBtn.setText(R.string.txt_btn_stop);
        } else {
            startStopBtn.setText(R.string.txt_btn_start);
        }
    }

    private void setupTwentyMinutesBtnAction() {
        twentyMinutesBtn.setOnClickListener(v -> {
            if (!mainActivityViewModel.isChronometerRunning()) {
                setChronometerToTwentyMinutes();
            }
        });
    }

    private void setupFifteenMinutesBtnAction() {
        fifteenMinutesBtn.setOnClickListener(v -> {
            if (!mainActivityViewModel.isChronometerRunning()) {
                setChronometerToFifteenMinutes();
            }
        });
    }

    private void setChronometerToTwentyMinutes() {
        mainActivityViewModel.setTwentyMinutes();
        chronometerView.setText(R.string.txt_twenty_minutes);
    }

    private void setChronometerToFifteenMinutes() {
        mainActivityViewModel.setFifteenMinutes();
        chronometerView.setText(R.string.txt_fifteen_minutes);
    }

    private void setupStartStopBtnAction() {
        startStopBtn.setOnClickListener(v -> {
            if (mainActivityViewModel.isChronometerRunning()) {
                stopChronometer();
            } else {
                startChronometer();
            }
        });
    }

    private void startChronometer() {
        mainActivityViewModel.runChronometer(true);
        saveCurrentChronometerTimeSet(true, mainActivityViewModel.getHowManyMinutes());
        startStopBtn.setText(R.string.txt_btn_stop);
        disableTimeButtons();
        chronometerServiceAccessor.startChronometer(mainActivityViewModel.getHowManyMinutes());
    }

    private void stopChronometer() {
        mainActivityViewModel.runChronometer(false);
        startStopBtn.setText(R.string.txt_btn_start);
        if (mainActivityViewModel.getHowManyMinutes() == TWENTY) {
            chronometerView.setText(R.string.txt_twenty_minutes);
            saveCurrentChronometerTimeSet(false, TWENTY);
        } else {
            chronometerView.setText(R.string.txt_fifteen_minutes);
            saveCurrentChronometerTimeSet(false, FIFTEEN);
        }
        enableTimeButtons();
        chronometerServiceAccessor.stopChronometer();
    }

    private void enableTimeButtons() {
        fifteenMinutesBtn.setEnabled(true);
        twentyMinutesBtn.setEnabled(true);
    }

    private void disableTimeButtons() {
        fifteenMinutesBtn.setEnabled(false);
        twentyMinutesBtn.setEnabled(false);
    }

    private void setupChronometer() {
        chronometerView = findViewById(R.id.chronometer);
        setChronometerTimeDisplayed(mainActivityViewModel.getHowManyMinutes() + ":00");
    }

    private void setupReceiver() {
        ReceiverAccessor.OnReceive onReceive = (context, intent) -> {
            switch (intent.getAction()) {
                case ACTION_TICK:
                    setChronometerTimeDisplayed(intent.getStringExtra(TIME));
                    break;
                case ACTION_FINISH:
                    showFinishPomodoroDialog();
                    saveCurrentChronometerTimeSet(false, mainActivityViewModel.getHowManyMinutes());
                    break;
                case ACTION_ONE_TICK_TEST:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Action one tick test received");
                        showFinishPomodoroDialog();
                    }
                case ACTION_5_SEC_TEST_FINISH:
                    break;
                default:
                    throw new RuntimeException("Receiver: unknown option");
            }
        };

        receiverAccessor = new ReceiverAccessor(this, onReceive);
    }

    private void setChronometerTimeDisplayed(String time) {
        chronometerView.setText(time);
    }

    private void saveCurrentChronometerTimeSet(boolean chronometerIsRunning, int howManyMinutes) {
        uiSharedPreferences
                .saveChronometerState(chronometerIsRunning, howManyMinutes);
    }

    private void onFinishPomodoro() {
        mainActivityViewModel.runChronometer(false);
        startStopBtn.setText(R.string.txt_btn_start);
        enableTimeButtons();
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
    private void showOneTickTestButton() {
        Button testBtn = findViewById(R.id.btn_test);
        testBtn.setText(R.string.txt_menu_item_one_tick_test);
        testBtn.setVisibility(View.VISIBLE);
        testBtn.setOnClickListener(v -> {
            Log.d(TAG, "Click on button test one tick");
            mainActivityViewModel.runChronometer(true);
            chronometerServiceAccessor.sendOneTick();
        });
    }

    @VisibleForTesting
    public void show5SecTestButton() {
        Button testBtn = findViewById(R.id.btn_test);
        testBtn.setText(R.string.txt_menu_item_5_sec_test);
        testBtn.setVisibility(View.VISIBLE);
        testBtn.setOnClickListener(v -> {
            Log.d(TAG, "Click on button test 5 sec");
            mainActivityViewModel.runChronometer(true);
            chronometerServiceAccessor.start5secCount();
        });
    }

    @VisibleForTesting
    private void hideTestButton() {
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