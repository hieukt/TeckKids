package com.example.lenovo_pc.shortstory.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.lenovo_pc.shortstory.R;
import com.example.lenovo_pc.shortstory.adapter.StoryAdapter;
import com.example.lenovo_pc.shortstory.databases.DatabaseManager;
import com.example.lenovo_pc.shortstory.databases.model.StoryModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<StoryModel> storyModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listview = findViewById(R.id.lv_story);
        storyModelList = DatabaseManager.getInstance(this).getStoryModelList();
        StoryAdapter storyAdapter = new StoryAdapter(storyModelList,this);
        listview.setAdapter(storyAdapter);
    }
}
