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
            + QUESTION_RECORD_QUESTION_ID + "' INTEGER NOT NULL , '"
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
    public final static String QUESTION_GROUP_COUNT = "question_group_count";
    public final static String SQL_CREATE_TABLE_QUESTION_GROUP = "CREATE TABLE '"
            + TABLE_QUESTION_GROUP + "' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , '"
            + QUESTION_GROUP_GROUP_ID + "' INTEGER NOT NULL  , '"
            + QUESTION_GROUP_PROGRESS + "' INTEGER NOT NULL  , '"
            + QUESTION_GROUP_USER_ID + "' INTEGER NOT NULL  DEFAULT -1, '"
            + QUESTION_GROUP_NAME + "' TEXT, '"
            + QUESTION_GROUP_BELONG + "' INTEGER NOT NULL  DEFAULT -1, '"
            + QUESTION_GROUP_COUNT + "' INTEGER NOT NULL  DEFAULT 20)";

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


    //LeaderBoard
    public final static String TABLE_LEADERBOARD = "leaderboard";
    public final static String LB_TYPE = "lb_type";
    public final static String LB_ID = "lb_id";
    public final static String LB_DISPLAYNAME = "lb_displayName";
    public final static String LB_PICTURE = "lb_picture";
    public final static String LB_PERFROI = "lb_perfRoi";
    public final static String LB_FOLLOWERCOUNT = "lb_followerCount";
    public final static String LB_TOTALWEALTH = "lb_totalWealth";
    public final static String LB_ROIINPERIOD = "lb_roiInPeriod";

    public final static String SQL_CREATE_TABLE_LEADERBOARD = "CREATE TABLE '"
            + TABLE_LEADERBOARD + "' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , '"
            + LB_TYPE +"' INTEGER NOT NULL , '"
            + LB_ID +"' TEXT NOT NULL , '"
            + LB_DISPLAYNAME +"' TEXT, '"
            + LB_PICTURE +"' TEXT, '"
            + LB_PERFROI +"' DOUBLE, '"
            + LB_FOLLOWERCOUNT +"' INTEGER, '"
            + LB_TOTALWEALTH+"' DOUBLE, '"
            + LB_ROIINPERIOD+"' DOUBLE)";

    // DW Sign up info.
    public final static String TABLE_DW_SIGNUP = "dwsignup";
    public final static String DW_SIGNUP_PHONE = "dwsignup_phone";
    public final static String DW_SIGNUP_INFO = "dwsignup_info";
    public final static String SQL_CREATE_DW_SIGNUP_INFO =
            "CREATE TABLE '" + TABLE_DW_SIGNUP +
                    "' ('" + DW_SIGNUP_PHONE + "' TEXT NOT NULL , '" +
                    DW_SIGNUP_INFO + "' TEXT NOT NULL)";

}
