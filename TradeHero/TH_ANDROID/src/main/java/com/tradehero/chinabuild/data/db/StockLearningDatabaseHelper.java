package com.tradehero.chinabuild.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionGroup;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionStatusRecord;

import java.util.ArrayList;

/**
 * Database
 * <p>
 * Created by palmer on 15/4/13.
 */
public class StockLearningDatabaseHelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;

    public StockLearningDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                       int version) {
        super(context, name, factory, version);
    }

    public StockLearningDatabaseHelper(Context context) {
        super(context, SQLs.SQL_DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLs.SQL_CREATE_TABLE_QUESTION_RECORD);
        db.execSQL(SQLs.SQL_CREATE_TABLE_QUESTION_GROUP_PROGRESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

        }
    }

    public void insertOrUpdateQuestionRecord(ArrayList<QuestionStatusRecord> sets) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (QuestionStatusRecord questionStatusRecord : sets) {
                Cursor cursor = db.query(SQLs.TABLE_QUESTION_RECORD, null, SQLs.QUESTION_RECORD_USER_ID + " =? and " + SQLs.QUESTION_RECORD_GROUP_ID + " =? and " + SQLs.QUESTION_RECORD_QUESTION_ID + " =?",
                        new String[]{String.valueOf(questionStatusRecord.user_id), String.valueOf(questionStatusRecord.question_group_id), String.valueOf(questionStatusRecord.question_id)}, null, null, null);
                if (cursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put(SQLs.QUESTION_RECORD_QUESTION_CHOICE, questionStatusRecord.question_choice);
                    values.put(SQLs.QUESTION_RECORD_QUESTION_STATUS, questionStatusRecord.question_status);
                    db.update(SQLs.TABLE_QUESTION_RECORD, values, SQLs.QUESTION_RECORD_USER_ID + " =? and " + SQLs.QUESTION_RECORD_GROUP_ID + " =? and " + SQLs.QUESTION_RECORD_QUESTION_ID + " =?",
                            new String[]{String.valueOf(questionStatusRecord.user_id), String.valueOf(questionStatusRecord.question_group_id), String.valueOf(questionStatusRecord.question_id)});
                } else {
                    ContentValues values = new ContentValues();
                    values.put(SQLs.QUESTION_RECORD_QUESTION_ID, questionStatusRecord.question_id);
                    values.put(SQLs.QUESTION_RECORD_QUESTION_CHOICE, questionStatusRecord.question_choice);
                    values.put(SQLs.QUESTION_RECORD_QUESTION_STATUS, questionStatusRecord.question_status);
                    values.put(SQLs.QUESTION_RECORD_USER_ID, questionStatusRecord.user_id);
                    values.put(SQLs.QUESTION_RECORD_GROUP_ID, questionStatusRecord.question_group_id);
                    db.insert(SQLs.TABLE_QUESTION_RECORD, null, values);
                }
                cursor.close();
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertQuestionRecord(QuestionStatusRecord questionStatusRecord) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            Cursor cursor = db.query(SQLs.TABLE_QUESTION_RECORD, null, SQLs.QUESTION_RECORD_USER_ID + " =? and " + SQLs.QUESTION_RECORD_GROUP_ID + " =? and " + SQLs.QUESTION_RECORD_QUESTION_ID + " =?",
                    new String[]{String.valueOf(questionStatusRecord.user_id), String.valueOf(questionStatusRecord.question_group_id), String.valueOf(questionStatusRecord.question_id)}, null, null, null);
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_RECORD_QUESTION_CHOICE, questionStatusRecord.question_choice);
                values.put(SQLs.QUESTION_RECORD_QUESTION_STATUS, questionStatusRecord.question_status);
                db.update(SQLs.TABLE_QUESTION_RECORD, values, SQLs.QUESTION_RECORD_USER_ID + " =? and " + SQLs.QUESTION_RECORD_GROUP_ID + " =? and " + SQLs.QUESTION_RECORD_QUESTION_ID + " =?",
                        new String[]{String.valueOf(questionStatusRecord.user_id), String.valueOf(questionStatusRecord.question_group_id), String.valueOf(questionStatusRecord.question_id)});
            } else {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_RECORD_QUESTION_ID, questionStatusRecord.question_id);
                values.put(SQLs.QUESTION_RECORD_QUESTION_CHOICE, questionStatusRecord.question_choice);
                values.put(SQLs.QUESTION_RECORD_QUESTION_STATUS, questionStatusRecord.question_status);
                values.put(SQLs.QUESTION_RECORD_USER_ID, questionStatusRecord.user_id);
                values.put(SQLs.QUESTION_RECORD_GROUP_ID, questionStatusRecord.question_group_id);
                db.insert(SQLs.TABLE_QUESTION_RECORD, null, values);
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public QuestionStatusRecord retrieveQuestionRecord(int question_id, int user_id, int question_group_id) {
        QuestionStatusRecord questionStatusRecord = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_RECORD, null, SQLs.QUESTION_RECORD_USER_ID + " =? and " + SQLs.QUESTION_RECORD_GROUP_ID + " =? and " + SQLs.QUESTION_RECORD_QUESTION_ID + " =?",
                new String[]{String.valueOf(user_id), String.valueOf(question_group_id), String.valueOf(question_id)}, null, null, null);
        if (cursor.moveToFirst()) {
            questionStatusRecord = new QuestionStatusRecord();
            String question_choice = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_RECORD_QUESTION_CHOICE));
            int question_status = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_RECORD_QUESTION_STATUS));
            questionStatusRecord.question_choice = question_choice;
            questionStatusRecord.question_status = question_status;
            questionStatusRecord.question_id = question_id;
            questionStatusRecord.user_id = user_id;
            questionStatusRecord.question_group_id = question_group_id;
        }
        cursor.close();
        return questionStatusRecord;
    }

    public ArrayList<QuestionStatusRecord> retrieveQuestionRecords(int user_id, int question_group_id) {
        ArrayList<QuestionStatusRecord> records = new ArrayList();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_RECORD, null, SQLs.QUESTION_RECORD_USER_ID + " =? and " + SQLs.QUESTION_RECORD_GROUP_ID + " =? ",
                new String[]{String.valueOf(user_id), String.valueOf(question_group_id)}, null, null, null);
        while (cursor.moveToNext()) {
            QuestionStatusRecord questionStatusRecord = new QuestionStatusRecord();
            String question_choice = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_RECORD_QUESTION_CHOICE));
            int question_status = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_RECORD_QUESTION_STATUS));
            int question_id = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_RECORD_QUESTION_ID));
            questionStatusRecord.question_choice = question_choice;
            questionStatusRecord.question_status = question_status;
            questionStatusRecord.question_id = question_id;
            questionStatusRecord.user_id = user_id;
            questionStatusRecord.question_group_id = question_group_id;
            records.add(questionStatusRecord);
        }
        cursor.close();
        return records;
    }

    public void insertOrUpdateQuestionGroup(QuestionGroup questionGroup) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP, null, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? and " + SQLs.QUESTION_GROUP_GROUP_ID + " =? ",
                    new String[]{String.valueOf(questionGroup.user_id), String.valueOf(questionGroup.question_group_id)}, null, null, null);
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_GROUP_PROGRESS, questionGroup.question_group_progress);
                values.put(SQLs.QUESTION_GROUP_NAME, questionGroup.quesitong_group_name);
                db.update(SQLs.TABLE_QUESTION_GROUP, values, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? and " + SQLs.QUESTION_GROUP_GROUP_ID + " =? ",
                        new String[]{String.valueOf(questionGroup.user_id), String.valueOf(questionGroup.question_group_id)});
            } else {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_GROUP_PROGRESS, questionGroup.question_group_progress);
                values.put(SQLs.QUESTION_GROUP_GROUP_ID, questionGroup.question_group_id);
                values.put(SQLs.QUESTION_GROUP_PROGRESS_USER_ID, questionGroup.user_id);
                values.put(SQLs.QUESTION_GROUP_NAME, questionGroup.quesitong_group_name);
                db.insert(SQLs.TABLE_QUESTION_GROUP, null, values);
            }
            cursor.close();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public QuestionGroup retrieveQuestionGroupProgress(int user_id, int question_group_id) {
        SQLiteDatabase db = getReadableDatabase();
        QuestionGroup questionGroup = null;
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP, null, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? and " + SQLs.QUESTION_GROUP_GROUP_ID + " =? ",
                new String[]{String.valueOf(user_id), String.valueOf(question_group_id)}, null, null, null);
        if(cursor.moveToFirst()){
            int progress = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_PROGRESS));
            String group_name = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_GROUP_NAME));
            questionGroup = new QuestionGroup();
            questionGroup.question_group_id = question_group_id;
            questionGroup.user_id = user_id;
            questionGroup.question_group_progress = progress;
            questionGroup.quesitong_group_name = group_name;
        }
        cursor.close();
        return questionGroup;
    }

    public ArrayList<QuestionGroup> retrieveQuestionGroupProgress(int user_id){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<QuestionGroup> progresses = new ArrayList();
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP, null, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? ",
                new String[]{String.valueOf(user_id)}, null, null, null);
        while (cursor.moveToNext()){
            QuestionGroup questionGroup = new QuestionGroup();
            int progress = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_PROGRESS));
            int group_id = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_GROUP_ID));
            String group_name = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_GROUP_NAME));
            questionGroup.question_group_progress = progress;
            questionGroup.question_group_id = group_id;
            questionGroup.user_id = user_id;
            questionGroup.quesitong_group_name = group_name;
            progresses.add(questionGroup);
        }
        cursor.close();
        return progresses;
    }




}
