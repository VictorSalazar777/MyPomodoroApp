package com.manuelsoft.mypomodoroapp.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.manuelsoft.mypomodoroapp.BuildConfig;
import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.chronometer.ChronometerService;
import com.manuelsoft.mypomodoroapp.common.Utilities;
import com.manuelsoft.mypomodoroapp.databinding.ActivityMainBinding;
import com.manuelsoft.mypomodoroapp.databinding.DialogCreditsBinding;
import com.manuelsoft.mypomodoroapp.databinding.DialogPomodoroFinishedBinding;

import static android.view.Menu.NONE;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_5_SEC_TEST_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_FINISH;
import static com.manuelsoft.mypomodoroapp.chronometer.ChronometerService.ACTION_ONE_TICK_TEST;
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
    private ReceiverAccessor receiverAccessor;
    private UISharedPreferences uiSharedPreferences;
    private ChronometerServiceAccessor chronometerServiceAccessor;
    private ActivityMainBinding binding;
    private boolean isTesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupReceiver();
        uiSharedPreferences = new UISharedPreferences(this);
        chronometerServiceAccessor = new ChronometerServiceAccessor(this);

        setupToolbar();
        setupViewModel();
        setupChronometer();
        setupButtons();
        setupStartStopBtnAction();
        setupFifteenMinutesBtnAction();
        setupTwentyMinutesBtnAction();
    }

    private void setupViewModel() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
//        mainActivityViewModel.runChronometer(
//                uiSharedPreferences.loadChronometerIsRunning()
//                        && isChronometerServiceInTheForeground());
//
//        if (uiSharedPreferences.loadTimeSelected() == FIFTEEN) {
//            setChronometerToFifteenMinutes();
//        } else {
//            setChronometerToTwentyMinutes();
//        }
    }

    private boolean isChronometerServiceInTheForeground() {
        boolean result = Utilities.isForegroundServiceRunning(this, ChronometerService.class);
        Log.d(TAG, "isChronometerServiceInTheForeground(): " + result);
        return result;
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
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
            showCreditsDialog();
        } else if (item.getItemId() == R.id.menu_item_one_tick_test) {
            showOneTickTestButton();
        }
        else if (item.getItemId() == R.id.menu_item_5_sec) {
            show5SecTestButton();
        } else {
            resetTest();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCreditsDialog() {
        DialogCreditsBinding binding = DialogCreditsBinding.inflate(getLayoutInflater());

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                .setView(binding.getRoot())
                .create();

        binding.btnDialogOk.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    private void setupButtons() {
        if (mainActivityViewModel.getHowManyMinutes() == TWENTY) {
            setTwentyMinBtnChecked();
        }
        else if (mainActivityViewModel.getHowManyMinutes() == FIFTEEN) {
            setFifteenMinBtnChecked();
        }
        if (mainActivityViewModel.isChronometerRunning()) {
            disableTimeButtons();
            setStartStopBtnToStop();
        } else {
            enableTimeButtons();
            setStartStopBtnToStart();
        }
    }

    private void setFifteenMinBtnChecked() {
        binding.btnFifteenMin.setChecked(true);
    }

    private void setTwentyMinBtnChecked() {
        binding.btnTwentyMin.setChecked(true);
    }

    private void setupTwentyMinutesBtnAction() {
        binding.btnTwentyMin.setOnClickListener(v -> {
            if (!mainActivityViewModel.isChronometerRunning()) {
                setChronometerToTwentyMinutes();
            }
        });
    }

    private void setupFifteenMinutesBtnAction() {
        binding.btnFifteenMin.setOnClickListener(v -> {
            if (!mainActivityViewModel.isChronometerRunning()) {
                setChronometerToFifteenMinutes();
            }
        });
    }

    private void showTwentyMinutes() {
        binding.chronometer.setText(getString(R.string.txt_twenty_minutes));
    }

    private void showFifteenMinutes() {
        binding.chronometer.setText(getString(R.string.txt_fifteen_minutes));
    }

    private void showTime(String time) {
        binding.chronometer.setText(time);
    }

    private void setChronometerToFifteenMinutes() {
        setFifteenMinutes();
        showFifteenMinutes();
    }

    private void setChronometerToTwentyMinutes() {
        setTwentyMinutes();
        showTwentyMinutes();
    }

    private void setFifteenMinutes() {
        mainActivityViewModel.setFifteenMinutes();
    }

    private void setTwentyMinutes() {
        mainActivityViewModel.setTwentyMinutes();
    }

    private void setupChronometer() {
        if (mainActivityViewModel.getHowManyMinutes() == TWENTY) {
            setChronometerToTwentyMinutes();
        }
        else if (mainActivityViewModel.getHowManyMinutes() == FIFTEEN) {
            setChronometerToFifteenMinutes();
        }
    }

    private void saveCurrentChronometerTimeSet(boolean chronometerIsRunning, int howManyMinutes) {
        uiSharedPreferences
                .saveChronometerState(chronometerIsRunning, howManyMinutes);
    }

    private void setupStartStopBtnAction() {
        binding.btnStartStop.setOnClickListener(v -> {
            if (mainActivityViewModel.isChronometerRunning()) {
                stopChronometer();
                if (isTesting) {
                    enableTestBtn();
                }
            } else {
                startChronometer();
            }
        });
    }

    private void setStartStopBtnToStop() {
        binding.btnStartStop.setText(R.string.txt_btn_stop);
    }

    private void setStartStopBtnToStart() {
        binding.btnStartStop.setText(R.string.txt_btn_start);
    }

    private void startChronometer() {
        setRunChronometerTrue();
        saveCurrentChronometerTimeSet(true, mainActivityViewModel.getHowManyMinutes());
        setStartStopBtnToStop();
        disableTimeButtons();
        chronometerServiceAccessor.startChronometer(mainActivityViewModel.getHowManyMinutes());
    }

    private void setRunChronometerTrue() {
        mainActivityViewModel.runChronometer(true);
    }

    private void setRunChronometerFalse() {
        mainActivityViewModel.runChronometer(false);
    }

    private void stopChronometer() {
        setRunChronometerFalse();
        setStartStopBtnToStart();
        if (mainActivityViewModel.getHowManyMinutes() == TWENTY) {
            showTwentyMinutes();
            saveCurrentChronometerTimeSet(false, TWENTY);
        } else {
            showFifteenMinutes();
            saveCurrentChronometerTimeSet(false, FIFTEEN);
        }
        enableTimeButtons();
        chronometerServiceAccessor.stopChronometer();
    }

    private void enableTimeButtons() {
        binding.btnFifteenMin.setEnabled(true);
        binding.btnTwentyMin.setEnabled(true);
    }

    private void disableTimeButtons() {
        binding.btnFifteenMin.setEnabled(false);
        binding.btnTwentyMin.setEnabled(false);
    }

    private void setupReceiver() {
        ReceiverAccessor.OnReceive onReceive = (context, intent) -> {
            switch (intent.getAction()) {
                case ACTION_TICK:
                    showTime(intent.getStringExtra(TIME));
                    break;
                case ACTION_FINISH:
                    Log.d(TAG, "Action finish received");
                    showFinishPomodoroDialog();
                    saveCurrentChronometerTimeSet(false, mainActivityViewModel.getHowManyMinutes());
                    break;
                case ACTION_ONE_TICK_TEST:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Action one tick test received");
                        showFinishPomodoroTestDialog();
                    }
                    break;
                case ACTION_5_SEC_TEST_FINISH:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Action 5 sec test received");
                        showFinishPomodoroTestDialog();
                    }
                    break;
                default:
                    throw new RuntimeException("Receiver: unknown option");
            }
        };

        receiverAccessor = new ReceiverAccessor(this, onReceive);
    }

    private void onFinishPomodoro() {
        setRunChronometerFalse();
        setStartStopBtnToStart();
        enableTimeButtons();
        setupChronometer();
    }

    private void onFinishPomodoroTest() {
        setRunChronometerFalse();
        setStartStopBtnToStart();
        enableTimeButtons();
        setupChronometer();
        enableTestBtn();
    }

    private void showFinishPomodoroDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.txt_dialog_pomodoro_finished)
                .setPositiveButton(R.string.txt_dialog_ok_btn, (dialog, which) -> {
                    dialog.dismiss();
                    onFinishPomodoro();
                })
                .create()
                .show();
    }

    private void showFinishPomodoroTestDialog() {
        DialogPomodoroFinishedBinding binding = DialogPomodoroFinishedBinding.inflate(getLayoutInflater());

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                .setView(binding.getRoot())
                .create();

        binding.btnDialogOk.setOnClickListener(v -> {
            alertDialog.dismiss();
            onFinishPomodoroTest();
        });

        alertDialog.show();
    }

    @VisibleForTesting
    private void showOneTickTestButton() {
        isTesting = true;
        binding.btnTest.setText(R.string.txt_menu_item_one_tick_test);
        binding.btnTest.setVisibility(View.VISIBLE);
        binding.btnTest.setOnClickListener(v -> {
            Log.d(TAG, "Click on button test one tick");
            disableTestBtn();
            setRunChronometerTrue();
            chronometerServiceAccessor.sendOneTick();
        });
    }

    @VisibleForTesting
    public void show5SecTestButton() {
        isTesting = true;
        binding.btnTest.setText(R.string.txt_menu_item_5_sec_test);
        binding.btnTest.setVisibility(View.VISIBLE);
        binding.btnTest.setOnClickListener(v -> {
            disableTestBtn();
            Log.d(TAG, "Click on button test 5 sec");
            setRunChronometerTrue();
            setStartStopBtnToStop();
            disableTimeButtons();
            chronometerServiceAccessor.start5secCount();
        });
    }

    private void enableTestBtn() {
        binding.btnTest.setEnabled(true);
    }

    private void disableTestBtn() {
        binding.btnTest.setEnabled(false);
    }

    @VisibleForTesting
    private void resetTest() {
        isTesting = false;
        hideTestButton();
        setupChronometer();
    }

    @VisibleForTesting
    private void hideTestButton() {
        binding.btnTest.setVisibility(View.GONE);
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