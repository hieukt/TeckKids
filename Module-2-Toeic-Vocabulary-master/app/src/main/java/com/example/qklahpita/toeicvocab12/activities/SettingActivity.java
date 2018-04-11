package com.example.qklahpita.toeicvocab12.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.qklahpita.toeicvocab12.R;
import com.example.qklahpita.toeicvocab12.ReminderService;
import com.example.qklahpita.toeicvocab12.databases.DatabaseManager;
import com.example.qklahpita.toeicvocab12.databases.models.TopicModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {
    public final String TIME_REMINDER = "time_reminder";

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.sw_reminder)
    Switch swReminder;
    @BindView(R.id.tv_pick_timer)
    TextView tvPickTimer;
    @BindView(R.id.sw_review)
    Switch swReview;
    @BindView(R.id.lv_topics)
    ListView lvTopics;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        List<TopicModel> topicModels = DatabaseManager.getInstance(this).getListTopic();
        List<String> topicName = new ArrayList<>();
        for (TopicModel topicModel : topicModels) {
            topicName.add(topicModel.name);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_list_review,
                topicName
        );
        lvTopics.setAdapter(arrayAdapter);

        String time = sharedPreferences.getString(TIME_REMINDER, null);
        if (time != null) {
            tvPickTimer.setText(time);
            swReminder.setChecked(true);
        } else {
            tvPickTimer.setVisibility(View.GONE);
        }

        swReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    tvPickTimer.setVisibility(View.VISIBLE);
                } else {
                    tvPickTimer.setVisibility(View.GONE);
                }

                if (compoundButton.isShown()) {
                    //

                    if (isChecked) {
                        setTimeReminder();
                    }
                }
            }
        });
    }

    private void setTimeReminder() {

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                time = String.format("%02d:%02d", hour, minute);
                tvPickTimer.setText(time);
            }
        }, currentHour, currentMin, true);

        timePickerDialog.show();
    }

    @OnClick({R.id.iv_back, R.id.iv_save, R.id.tv_pick_timer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_save:
                saveSetting();
                break;
            case R.id.tv_pick_timer:
                setTimeReminder();
                break;
        }
    }

    private void saveSetting() {
        //save timer
        if (swReminder.isChecked()) {

            //save
            editor.putString(TIME_REMINDER, time);

            //noti
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent = new Intent(this, ReminderService.class);
            PendingIntent pendingIntent = PendingIntent.getService(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Calendar calendar = Calendar.getInstance();
            //hh:mm
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3,5)));

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        } else {
            editor.putString(TIME_REMINDER, null);
        }

        //save topic review

        editor.commit();

        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
