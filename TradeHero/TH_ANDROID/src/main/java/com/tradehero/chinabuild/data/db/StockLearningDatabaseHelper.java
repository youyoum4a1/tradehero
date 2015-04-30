package com.tradehero.chinabuild.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionGroupProgress;
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

    public void insertOrUpdateQuestionGroupProgress(QuestionGroupProgress questionGroupProgress) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP_PROGRESS, null, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? and " + SQLs.QUESTION_GROUP_PROGRESS_GROUP_ID + " =? ",
                    new String[]{String.valueOf(questionGroupProgress.user_id), String.valueOf(questionGroupProgress.question_group_id)}, null, null, null);
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_GROUP_PROGRESS_PROGRESS, questionGroupProgress.question_group_progress);
                db.update(SQLs.TABLE_QUESTION_GROUP_PROGRESS, values, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? and " + SQLs.QUESTION_GROUP_PROGRESS_GROUP_ID + " =? ",
                        new String[]{String.valueOf(questionGroupProgress.user_id), String.valueOf(questionGroupProgress.question_group_id)});
            } else {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_GROUP_PROGRESS_PROGRESS, questionGroupProgress.question_group_progress);
                values.put(SQLs.QUESTION_GROUP_PROGRESS_GROUP_ID, questionGroupProgress.question_group_id);
                values.put(SQLs.QUESTION_GROUP_PROGRESS_USER_ID, questionGroupProgress.user_id);
                db.insert(SQLs.TABLE_QUESTION_GROUP_PROGRESS, null, values);
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

    public QuestionGroupProgress retrieveQuestionGroupProgress(int user_id, int question_group_id) {
        SQLiteDatabase db = getReadableDatabase();
        QuestionGroupProgress questionGroupProgress = null;
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP_PROGRESS, null, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? and " + SQLs.QUESTION_GROUP_PROGRESS_GROUP_ID + " =? ",
                new String[]{String.valueOf(user_id), String.valueOf(question_group_id)}, null, null, null);
        if(cursor.moveToFirst()){
            int progress = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_PROGRESS_PROGRESS));
            questionGroupProgress = new QuestionGroupProgress();
            questionGroupProgress.question_group_id = question_group_id;
            questionGroupProgress.user_id = user_id;
            questionGroupProgress.question_group_progress = progress;
        }
        return questionGroupProgress;
    }

    public ArrayList<QuestionGroupProgress> retrieveQuestionGroupProgress(int user_id){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<QuestionGroupProgress> progresses = new ArrayList();
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP_PROGRESS, null, SQLs.QUESTION_GROUP_PROGRESS_USER_ID + " =? ",
                new String[]{String.valueOf(user_id)}, null, null, null);
        while (cursor.moveToNext()){
            QuestionGroupProgress questionGroupProgress = new QuestionGroupProgress();
            int progress = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_PROGRESS_PROGRESS));
            int group_id = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_PROGRESS_GROUP_ID));
            questionGroupProgress.question_group_progress = progress;
            questionGroupProgress.question_group_id = group_id;
            questionGroupProgress.user_id = user_id;
            progresses.add(questionGroupProgress);
        }
        return progresses;
    }




}
