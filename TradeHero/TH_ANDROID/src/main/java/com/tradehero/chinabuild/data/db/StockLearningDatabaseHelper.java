package com.tradehero.chinabuild.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {

        }
    }

    public void insertQuestionRecord(ArrayList<QuestionStatusRecord> sets) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (QuestionStatusRecord questionStatusRecord : sets) {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_RECORD_QUESTION_ID, questionStatusRecord.question_id);
                values.put(SQLs.QUESTION_RECORD_QUESTION_CHOICE, questionStatusRecord.question_choice);
                values.put(SQLs.QUESTION_RECORD_QUESTION_STATUS, questionStatusRecord.question_status);
                values.put(SQLs.QUESTION_RECORD_USER_ID, questionStatusRecord.user_id);
                db.insert(SQLs.TABLE_QUESTION_RECORD, null, values);
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertQuestionRecord(QuestionStatusRecord questionStatusRecord){
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(SQLs.QUESTION_RECORD_QUESTION_ID, questionStatusRecord.question_id);
            values.put(SQLs.QUESTION_RECORD_QUESTION_CHOICE, questionStatusRecord.question_choice);
            values.put(SQLs.QUESTION_RECORD_QUESTION_STATUS, questionStatusRecord.question_status);
            values.put(SQLs.QUESTION_RECORD_USER_ID, questionStatusRecord.user_id);
            db.insert(SQLs.TABLE_QUESTION_RECORD, null, values);
            db.setTransactionSuccessful();
        }catch (SQLiteException e){
            e.printStackTrace();
        }finally{
            db.endTransaction();
            db.close();
        }
    }

    public QuestionStatusRecord retrieveQuestionSetUpdateRecord(int question_id, int user_id){
        QuestionStatusRecord questionStatusRecord = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_RECORD, new String[]{SQLs.QUESTION_RECORD_QUESTION_ID, SQLs.QUESTION_RECORD_QUESTION_CHOICE,
            SQLs.QUESTION_RECORD_QUESTION_STATUS, SQLs.QUESTION_RECORD_USER_ID}, SQLs.QUESTION_RECORD_USER_ID + "=?", new String[]{String.valueOf(user_id)}, null, null, null);
        while(cursor.moveToNext()){
            questionStatusRecord = new QuestionStatusRecord();
            String question_choice = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_RECORD_QUESTION_CHOICE));
            int question_status = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_RECORD_QUESTION_STATUS));
            questionStatusRecord.question_choice = question_choice;
            questionStatusRecord.question_status = question_status;
            questionStatusRecord.question_id = question_id;
            questionStatusRecord.user_id = user_id;
        }
        return questionStatusRecord;
    }
}
