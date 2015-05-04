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

    public final static String TABLE_QUESTION_GROUP = "question_group";
    public final static String QUESTION_GROUP_GROUP_ID = "question_group_id";
    public final static String QUESTION_GROUP_PROGRESS = "question_group_progress";
    public final static String QUESTION_GROUP_NAME = "question_group_name";
    public final static String QUESTION_GROUP_USER_ID = "user_id";
    public final static String QUESTION_GROUP_BELONG = "question_group_belong";
    public final static String SQL_CREATE_TABLE_QUESTION_GROUP = "CREATE TABLE '"
            + TABLE_QUESTION_GROUP + "' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , '"
            + QUESTION_GROUP_GROUP_ID + "' INTEGER NOT NULL  UNIQUE , '"
            + QUESTION_GROUP_PROGRESS + "' INTEGER NOT NULL  , '"
            + QUESTION_GROUP_USER_ID + "' INTEGER NOT NULL  DEFAULT -1, '"
            + QUESTION_GROUP_NAME + "' TEXT, '"
            + QUESTION_GROUP_BELONG + "' INTEGER NOT NULL  DEFAULT -1)";

    public final static String TABLE_QUESTION = "question";
    public final static String QUESTION_QUESTION_ID = "question_id";
    public final static String QUESTION_QUESTION_GROUP_ID = "question_group_id";
    public final static String QUESTION_CHOICE_A = "question_choice_a";
    public final static String QUESTION_CHOICE_B = "question_choice_b";
    public final static String QUESTION_CHOICE_C = "question_choice_c";
    public final static String QUESTION_CHOICE_D = "question_choice_d";
    public final static String QUESTION_ANSWERS = "question_answers";
    public final static String QUESTION_IMAGE_URL = "question_image_url";
    public final static String QUESTION_DESCRIPTION = "question_description";
    public final static String SQL_CREATE_TABLE_QUESTION = "CREATE TABLE '"
            + TABLE_QUESTION + "' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , '"
            + QUESTION_QUESTION_ID + "' INTEGER NOT NULL  UNIQUE , '"
            + QUESTION_QUESTION_GROUP_ID + "' INTEGER NOT NULL , '"
            + QUESTION_CHOICE_A + "' TEXT, '"
            + QUESTION_CHOICE_B + "' TEXT, '"
            + QUESTION_CHOICE_C + "' TEXT, '"
            + QUESTION_CHOICE_D + "' TEXT, '"
            + QUESTION_ANSWERS + "' TEXT, '"
            + QUESTION_IMAGE_URL + "' TEXT, '"
            + QUESTION_DESCRIPTION + "' TEXT)";
    //Stock Learning

}
