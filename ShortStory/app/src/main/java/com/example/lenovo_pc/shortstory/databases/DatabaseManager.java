package com.example.lenovo_pc.shortstory.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lenovo_pc.shortstory.databases.model.StoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo-PC on 3/7/2018.
 */

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private static final String TABLE_STORY = "tbl_short_story";
    private static DatabaseManager databaseManager;
    private SQLiteDatabase sqLiteDatabase;
    private AssetHelper assetHelper;

    public DatabaseManager(Context context) {
        assetHelper = new AssetHelper(context);
    }

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }

    public List<StoryModel> getStoryModelList() {
        sqLiteDatabase = assetHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_STORY, null);
        List<StoryModel> storyModelList = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(0);
            String image = cursor.getString(1);
            String title = cursor.getString(2);
            String description = cursor.getString(3);
            String content = cursor.getString(4);
            String author = cursor.getString(5);
            Integer bookmark = cursor.getInt(6);
            storyModelList.add(new StoryModel(id, image, title, description, content, author, bookmark));
        }
        return storyModelList;
    }

}
