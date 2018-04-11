package com.example.qklahpita.toeicvocab12.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.qklahpita.toeicvocab12.databases.models.CategoryModel;
import com.example.qklahpita.toeicvocab12.databases.models.TopicModel;
import com.example.qklahpita.toeicvocab12.databases.models.WordModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by qklahpita on 3/1/18.
 */

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";

    private static final String TABLE_TOPIC = "tbl_topic";
    private static final String TABLE_WORD = "tbl_word";

    private SQLiteDatabase sqLiteDatabase;
    private AssetHelper assetHelper;

    private static DatabaseManager databaseManager;

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }

    public DatabaseManager(Context context) {
        assetHelper = new AssetHelper(context);
    }

    public List<TopicModel> getListTopic() {
        sqLiteDatabase = assetHelper.getReadableDatabase();

        List<TopicModel> topicModelList = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_TOPIC, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            //read data
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String imageUrl = cursor.getString(3);
            String category = cursor.getString(4);
            String color = cursor.getString(5);
            String lastTime = cursor.getString(6);

            TopicModel topicModel = new TopicModel(id, name, imageUrl, category, color, lastTime);
            topicModelList.add(topicModel);

            //next
            cursor.moveToNext();
        }

        Log.d(TAG, "getListTopic: " + topicModelList);

        return topicModelList;
    }

    public List<CategoryModel> getListCategory(List<TopicModel> topicModelList) {
        List<CategoryModel> categoryModelList = new ArrayList<>();
        for (int i = 0; i < topicModelList.size(); i = i+5) {
            CategoryModel categoryModel = new CategoryModel(
                    topicModelList.get(i).category,
                    topicModelList.get(i).color);
            categoryModelList.add(categoryModel);
        }

        return categoryModelList;
    }

    public HashMap<String, List<TopicModel>> getHashMapTopic(
            List<TopicModel> topicModelList,
            List<CategoryModel> categoryModelList) {
        HashMap<String, List<TopicModel>> hashMap = new HashMap<>();
        for (int i = 0; i < categoryModelList.size(); i++) {
            int positionTopic = i*5;

            hashMap.put(categoryModelList.get(i).name,
                    topicModelList.subList(positionTopic, positionTopic + 5));
        }
        return hashMap;
    }

    public WordModel getRandomWord(int topicId, int preId) {
        sqLiteDatabase = assetHelper.getReadableDatabase();

        Cursor cursor;
        int level = 0;
        do {
            //1. level?
            double random = Math.random() * 100; // 0 <= random < 100
            if (random < 5) level = 4;
            else if (random < 15) level = 3;
            else if (random < 30) level = 2;
            else if (random < 60) level = 1;
            else level = 0;

            //2. word?
            cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_WORD +
                    " where topic_id = " + topicId +
                    " and level = " + level +
                    " and id <> " + preId +
                    " order by random() limit 1", null);
        } while (cursor.getCount() == 0);

        cursor.moveToFirst();
        int id = cursor.getInt(0);
        String origin = cursor.getString(1);
        String explanation = cursor.getString(2);
        String type = cursor.getString(3);
        String pronunciation = cursor.getString(4);
        String imageUrl = cursor.getString(5);
        String example = cursor.getString(6);
        String exampleTrans = cursor.getString(7);

        WordModel wordModel = new WordModel(id, origin, explanation,
                type, pronunciation, imageUrl, example, exampleTrans, topicId, level);

        return wordModel;
    }

    public void updateWordLevel(WordModel wordModel, boolean isKnown) {
        sqLiteDatabase = assetHelper.getWritableDatabase();

        int level = wordModel.level;
        if (isKnown && level < 4) {
            level++;
        } else if (!isKnown && level > 0) {
            level--;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("level", level);
        sqLiteDatabase.update(TABLE_WORD, contentValues,
                "id = " + wordModel.id, null);
    }

    public void updateLastTime(TopicModel topicModel, String lastTime) {
        sqLiteDatabase = assetHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("last_time", lastTime);
        sqLiteDatabase.update(TABLE_TOPIC, contentValues,
                "id = " + topicModel.id, null);
    }

    public int getNumOfMasterWordByTopicId(int topicId) {
        sqLiteDatabase = assetHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select level from " + TABLE_WORD
                + " where level = 4 and topic_id = " + topicId,
                null);
        Log.d(TAG, "getNumOfMasterWordByTopicId: " + cursor.getCount());
        return cursor.getCount();
    }

    public int getNumOfNewWordByTopicId(int topicId) {
        sqLiteDatabase = assetHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select level from " + TABLE_WORD
                        + " where level = 0 and topic_id = " + topicId,
                null);
        Log.d(TAG, "getNumOfNewWordByTopicId: " + cursor.getCount());
        return cursor.getCount();
    }
}
