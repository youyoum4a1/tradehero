package com.tradehero.chinabuild.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.tradehero.chinabuild.fragment.stocklearning.Question;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionGroup;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionStatusRecord;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;

import java.util.ArrayList;

/**
 * Database
 * <p/>
 * Created by palmer on 15/4/13.
 */
public class THDatabaseHelper extends SQLiteOpenHelper {

    private final static int VERSION = 3;

    public THDatabaseHelper(Context context) {
        super(context, SQLs.SQL_DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLs.SQL_CREATE_TABLE_QUESTION_RECORD);
        db.execSQL(SQLs.SQL_CREATE_TABLE_QUESTION_GROUP);
        db.execSQL(SQLs.SQL_CREATE_TABLE_QUESTION);
        db.execSQL(SQLs.SQL_CREATE_TABLE_LEADERBOARD);
        db.execSQL(SQLs.SQL_CREATE_DW_SIGNUP_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            if (oldVersion < 2) {
                db.execSQL(SQLs.SQL_CREATE_TABLE_LEADERBOARD);
            }
            if (oldVersion < 3) {
                db.execSQL(SQLs.SQL_CREATE_DW_SIGNUP_INFO);
            }
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
                values.put(SQLs.QUESTION_RECORD_QUESTION_STATUS, -1);
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

    public void insertOrUpdateQuestionGroup(QuestionGroup questionGroup, int user_id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP, null, SQLs.QUESTION_GROUP_USER_ID + " =? and " + SQLs.QUESTION_GROUP_GROUP_ID + " =? ",
                    new String[]{String.valueOf(user_id), String.valueOf(questionGroup.id)}, null, null, null);
            if (cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_GROUP_PROGRESS, questionGroup.question_group_progress);
                values.put(SQLs.QUESTION_GROUP_NAME, questionGroup.name);
                values.put(SQLs.QUESTION_GROUP_COUNT, questionGroup.count);
                db.update(SQLs.TABLE_QUESTION_GROUP, values, SQLs.QUESTION_GROUP_USER_ID + " =? and " + SQLs.QUESTION_GROUP_GROUP_ID + " =? ",
                        new String[]{String.valueOf(user_id), String.valueOf(questionGroup.id)});
            } else {
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_GROUP_PROGRESS, questionGroup.question_group_progress);
                values.put(SQLs.QUESTION_GROUP_GROUP_ID, questionGroup.id);
                values.put(SQLs.QUESTION_GROUP_USER_ID, user_id);
                values.put(SQLs.QUESTION_GROUP_NAME, questionGroup.name);
                values.put(SQLs.QUESTION_GROUP_BELONG, questionGroup.categoryId);
                values.put(SQLs.QUESTION_GROUP_COUNT, questionGroup.count);
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

    public void insertOrUpdateQuestionGroups(QuestionGroup[] questionGroups, int user_id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (QuestionGroup questionGroup : questionGroups) {
                Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP, null, SQLs.QUESTION_GROUP_USER_ID + " =? and " + SQLs.QUESTION_GROUP_GROUP_ID + " =? ",
                        new String[]{String.valueOf(user_id), String.valueOf(questionGroup.id)}, null, null, null);
                if (cursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put(SQLs.QUESTION_GROUP_PROGRESS, questionGroup.question_group_progress);
                    values.put(SQLs.QUESTION_GROUP_NAME, questionGroup.name);
                    values.put(SQLs.QUESTION_GROUP_COUNT, questionGroup.count);
                    db.update(SQLs.TABLE_QUESTION_GROUP, values, SQLs.QUESTION_GROUP_USER_ID + " =? and " + SQLs.QUESTION_GROUP_GROUP_ID + " =? ",
                            new String[]{String.valueOf(user_id), String.valueOf(questionGroup.id)});
                } else {
                    ContentValues values = new ContentValues();
                    values.put(SQLs.QUESTION_GROUP_PROGRESS, questionGroup.question_group_progress);
                    values.put(SQLs.QUESTION_GROUP_GROUP_ID, questionGroup.id);
                    values.put(SQLs.QUESTION_GROUP_USER_ID, user_id);
                    values.put(SQLs.QUESTION_GROUP_NAME, questionGroup.name);
                    values.put(SQLs.QUESTION_GROUP_BELONG, questionGroup.categoryId);
                    values.put(SQLs.QUESTION_GROUP_COUNT, questionGroup.count);
                    db.insert(SQLs.TABLE_QUESTION_GROUP, null, values);
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

    public ArrayList<QuestionGroup> retrieveQuestionGroup(int user_id) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<QuestionGroup> questionGroups = new ArrayList();
        Cursor cursor = db.query(SQLs.TABLE_QUESTION_GROUP, null, SQLs.QUESTION_GROUP_USER_ID + " =? ",
                new String[]{String.valueOf(user_id)}, null, null, null);
        while (cursor.moveToNext()) {
            QuestionGroup questionGroup = new QuestionGroup();
            int progress = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_PROGRESS));
            int group_id = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_GROUP_ID));
            String group_name = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_GROUP_NAME));
            int group_belong = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_BELONG));
            int group_count = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_GROUP_COUNT));
            questionGroup.question_group_progress = progress;
            questionGroup.id = group_id;
            questionGroup.count = group_count;
            questionGroup.name = group_name;
            questionGroup.categoryId = group_belong;
            questionGroups.add(questionGroup);
        }
        cursor.close();
        return questionGroups;
    }

    public void insertQuestions(Question[] questions) {
        if (questions == null || questions.length <= 0) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (Question question : questions) {
                db.delete(SQLs.TABLE_QUESTION, SQLs.QUESTION_GROUP_GROUP_ID + " =? and " + SQLs.QUESTION_QUESTION_ID + " =? ",
                        new String[]{String.valueOf(question.subcategory), String.valueOf(question.id)});
                ContentValues values = new ContentValues();
                values.put(SQLs.QUESTION_DESCRIPTION, question.content);
                values.put(SQLs.QUESTION_QUESTION_ID, question.id);
                values.put(SQLs.QUESTION_QUESTION_GROUP_ID, question.subcategory);
                values.put(SQLs.QUESTION_CHOICE_A, question.option1);
                values.put(SQLs.QUESTION_CHOICE_B, question.option2);
                values.put(SQLs.QUESTION_CHOICE_C, question.option3);
                values.put(SQLs.QUESTION_CHOICE_D, question.option4);
                values.put(SQLs.QUESTION_ANSWERS, question.answer);
                values.put(SQLs.QUESTION_IMAGE_URL, question.imageUrl);
                db.insert(SQLs.TABLE_QUESTION, null, values);
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<Question> retrieveQuestions(int group_id) {
        ArrayList<Question> questions = new ArrayList();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(SQLs.TABLE_QUESTION, null, SQLs.QUESTION_QUESTION_GROUP_ID + " =? ", new String[]{String.valueOf(group_id)}, null, null, null);
        while (cursor.moveToNext()) {
            Question question = new Question();
            int question_id = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_QUESTION_ID));
            String content = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_DESCRIPTION));
            String optionA = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_CHOICE_A));
            String optionB = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_CHOICE_B));
            String optionC = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_CHOICE_C));
            String optionD = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_CHOICE_D));
            String answer = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_ANSWERS));
            String imageUrl = cursor.getString(cursor.getColumnIndex(SQLs.QUESTION_IMAGE_URL));
            int questionGroupId = cursor.getInt(cursor.getColumnIndex(SQLs.QUESTION_QUESTION_GROUP_ID));
            question.content = content;
            question.id = question_id;
            question.option1 = optionA;
            question.option2 = optionB;
            question.option3 = optionC;
            question.option4 = optionD;
            question.imageUrl = imageUrl;
            question.answer = answer;
            question.subcategory = questionGroupId;
            questions.add(question);
        }
        cursor.close();
        return questions;
    }


    /**
     * Insert users into leaderboard
     */
    public void storeLeaderboadrUsers(LeaderboardUserDTOList leaderboardUserDTOList, int type) {
        if (leaderboardUserDTOList == null || leaderboardUserDTOList.size() <= 0) {
            return;
        }
        ArrayList<LeaderboardUserDTO> userDTOs = leaderboardUserDTOList;
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(SQLs.TABLE_LEADERBOARD, SQLs.LB_TYPE + " = ? ", new String[]{String.valueOf(type)});
            for (LeaderboardUserDTO leaderboardUserDTO : userDTOs) {
                ContentValues values = new ContentValues();
                values.put(SQLs.LB_ID, leaderboardUserDTO.id);
                values.put(SQLs.LB_TYPE, type);
                values.put(SQLs.LB_DISPLAYNAME, leaderboardUserDTO.displayName);
                values.put(SQLs.LB_PICTURE, leaderboardUserDTO.picture);
                values.put(SQLs.LB_FOLLOWERCOUNT, leaderboardUserDTO.followerCount);
                values.put(SQLs.LB_PERFROI, leaderboardUserDTO.perfRoi);
                values.put(SQLs.LB_ROIINPERIOD, leaderboardUserDTO.roiInPeriod);
                values.put(SQLs.LB_TOTALWEALTH, leaderboardUserDTO.totalWealth);
                db.insert(SQLs.TABLE_LEADERBOARD, null, values);
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public LeaderboardUserDTOList retrieveUserDTOList(int type) {
        if (type <= 0) {
            return null;
        }
        LeaderboardUserDTOList leaderboardUserDTOList =  new LeaderboardUserDTOList();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(SQLs.TABLE_LEADERBOARD, null, SQLs.LB_TYPE + " =? ",
                new String[]{String.valueOf(type)}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(SQLs.LB_ID));
            String displayName = cursor.getString(cursor.getColumnIndex(SQLs.LB_DISPLAYNAME));
            String picture = cursor.getString(cursor.getColumnIndex(SQLs.LB_PICTURE));
            int followCount =  cursor.getInt(cursor.getColumnIndex(SQLs.LB_FOLLOWERCOUNT));
            double perfRoi = cursor.getDouble(cursor.getColumnIndex(SQLs.LB_PERFROI));
            double roiInPeriod = cursor.getDouble(cursor.getColumnIndex(SQLs.LB_ROIINPERIOD));
            double totalWealth = cursor.getDouble(cursor.getColumnIndex(SQLs.LB_TOTALWEALTH));
            LeaderboardUserDTO leaderboardUserDTO = new LeaderboardUserDTO();
            leaderboardUserDTO.id = id;
            leaderboardUserDTO.displayName = displayName;
            leaderboardUserDTO.picture = picture;
            leaderboardUserDTO.followerCount = followCount;
            leaderboardUserDTO.perfRoi = perfRoi;
            leaderboardUserDTO.roiInPeriod = roiInPeriod;
            leaderboardUserDTO.totalWealth = totalWealth;
            leaderboardUserDTOList.add(leaderboardUserDTO);
        }
        return leaderboardUserDTOList;
    }


    public void storeDWSignupInfo(String phoneNumber, String signupInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SQLs.DW_SIGNUP_PHONE, phoneNumber);
        cv.put(SQLs.DW_SIGNUP_INFO, signupInfo);


        Cursor cursor = db.query(SQLs.TABLE_DW_SIGNUP, null, SQLs.DW_SIGNUP_PHONE + " =? ",
                new String[]{String.valueOf(phoneNumber)}, null, null, null);

        if (cursor.getCount() == 0) {
            db.insert(SQLs.TABLE_DW_SIGNUP, null, cv);
        } else {

            db.update(SQLs.TABLE_DW_SIGNUP, cv, SQLs.DW_SIGNUP_PHONE + " =?",
                    new String[]{String.valueOf(phoneNumber)});
        }

        db.close();
    }

    public String retrieveDWSignupInfo(String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(SQLs.TABLE_DW_SIGNUP, null, SQLs.DW_SIGNUP_PHONE + " =? ",
                new String[]{String.valueOf(phoneNumber)}, null, null, null);

        String result = "";
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(SQLs.DW_SIGNUP_INFO));
        }

        db.close();
        return result;
    }

}
