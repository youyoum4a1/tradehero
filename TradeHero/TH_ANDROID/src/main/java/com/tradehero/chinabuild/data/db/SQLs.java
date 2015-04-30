package com.tradehero.chinabuild.data.db;

/**
 * Created by palmer on 15/4/13.
 */
public class SQLs {

    public final static String SQL_DB_NAME = "db_cn_tradehero";

    //Stock Learning

    public final static String TABLE_QUESTION_RECORD = "question_record";
    public final static String QUESTION_RECORD_QUESTION_ID = "question_id";
    public final static String QUESTION_RECORD_QUESTION_CHOICE = "question_choice";
    public final static String QUESTION_RECORD_QUESTION_STATUS = "question_status";
    public final static String QUESTION_RECORD_USER_ID = "user_id";
    public final static String QUESTION_RECORD_GROUP_ID = "question_group_id";
    public final static String SQL_CREATE_TABLE_QUESTION_RECORD = "CREATE TABLE '"
            + TABLE_QUESTION_RECORD + "' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , '"
            + QUESTION_RECORD_QUESTION_ID + "' INTEGER NOT NULL  UNIQUE , '"
            + QUESTION_RECORD_QUESTION_CHOICE + "' TEXT, '"
            + QUESTION_RECORD_QUESTION_STATUS + "' INTEGER NOT NULL , '"
            + QUESTION_RECORD_USER_ID + "' int NOT NULL  DEFAULT -1 , '"
            + QUESTION_RECORD_GROUP_ID + "' INTEGER NOT NULL  DEFAULT -1)";


    public final static String TABLE_QUESTION_GROUP_PROGRESS = "question_group_progress";
    public final static String QUESTION_GROUP_PROGRESS_GROUP_ID = "question_group_id";
    public final static String QUESTION_GROUP_PROGRESS_PROGRESS = "question_group_progress";
    public final static String QUESTION_GROUP_PROGRESS_USER_ID = "user_id";
    public final static String SQL_CREATE_TABLE_QUESTION_GROUP_PROGRESS = "CREATE TABLE '"
            + TABLE_QUESTION_GROUP_PROGRESS + "' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , '"
            + QUESTION_GROUP_PROGRESS_GROUP_ID + "' INTEGER NOT NULL  UNIQUE , '"
            + QUESTION_GROUP_PROGRESS_PROGRESS + "' INTEGER NOT NULL  , '"
            + QUESTION_GROUP_PROGRESS_USER_ID + "' INTEGER NOT NULL  DEFAULT -1)";

    //Stock Learning

}
