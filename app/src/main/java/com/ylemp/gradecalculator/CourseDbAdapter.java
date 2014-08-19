package com.ylemp.gradecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by home on 8/10/14.
 * TODO add final grades
 * if you create a current grade key for grades
 * then you can pull them easily and calculate the current grade for the class easily
 * current_grade * weight
 */


public class CourseDbAdapter {
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "courseManager";

    // Table Names
    private static final String TABLE_COURSE = "courses";
    private static final String TABLE_GRADES = "grades";
    private static final String TABLE_SCORES= "scores";

    // Common column names
    static final String KEY_ID = "_id";

    // courses Table - column name
    static final String KEY_COURSE = "course";

    // grades Table - column names
    static final String KEY_GRADE = "grade";
    static final String KEY_GRADE_WEIGHT = "grade_weight";
    static final String KEY_COURSE_ID = "course_id";

    // scores Table - column names
    static final String KEY_SCORE = "score";
    static final String KEY_GRADE_ID = "grade_id";


    // Course table create statement
    private static final String CREATE_TABLE_COURSE =
            "CREATE TABLE " + TABLE_COURSE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_COURSE + " TEXT"
            + ")";


    //new create statement for grades table with FK to course
    private static final String CREATE_TABLE_GRADES =
            "CREATE TABLE " + TABLE_GRADES
                    + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_GRADE + " TEXT,"
                    + KEY_GRADE_WEIGHT + " INTEGER,"
                    + KEY_COURSE_ID + " INTEGER"
                    + ")";

    // scores table create statement
    private static final String CREATE_TABLE_SCORES =
            "CREATE TABLE " + TABLE_SCORES
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_SCORE + " INTEGER,"
            + KEY_GRADE_ID + " INTEGER"
            + ")";



    // nested class DatabaseHelper
    public static class DatabaseHelper extends SQLiteOpenHelper{


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            // creating required tables
            database.execSQL(CREATE_TABLE_COURSE);
            database.execSQL(CREATE_TABLE_GRADES);
            database.execSQL(CREATE_TABLE_SCORES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            // on upgrade drop older tables
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_GRADES);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);

            // create new tables
            onCreate(database);
        }
    }

    public CourseDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public CourseDbAdapter open() throws SQLException{
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        //not used becuase of startmanaging(cursor)
        mDbHelper.close();
    }

    // ------------------------ "course" table methods ----------------//


    public long createCourse(String course_name){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_COURSE, course_name);

        return mDb.insert(TABLE_COURSE, null, initialValues);
    }

    public boolean deleteCourse(long rowId){
        return mDb.delete(TABLE_COURSE, KEY_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllCourses(){
        return mDb.query(TABLE_COURSE, new String[] {KEY_ID, KEY_COURSE},
                null, null, null, null, null);
    }

    public Cursor fetchCourse(long rowId) throws SQLException{
        Cursor mCursor =

                mDb.query(true, TABLE_COURSE, new String[] {KEY_ID,
                KEY_COURSE}, KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateCourse(long rowId, String course){
        ContentValues args = new ContentValues();
        args.put(KEY_COURSE, course);

        return mDb.update(TABLE_COURSE, args, KEY_ID + "=" + rowId, null) > 0;
    }

    // ------------------------ "grades" table methods ----------------//

    public long createGrade(String grade_name, Integer weight, Long course_id){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GRADE, grade_name);
        initialValues.put(KEY_GRADE_WEIGHT, weight);
        initialValues.put(KEY_COURSE_ID, course_id);

        return mDb.insert(TABLE_GRADES, null, initialValues);
    }

    public boolean deleteGrade(long rowId){
        return mDb.delete(TABLE_GRADES, KEY_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllGrades(){

        return mDb.query(TABLE_GRADES, new String[] {KEY_ID, KEY_GRADE,
                        KEY_GRADE_WEIGHT}, null, null, null, null, null);
    }

    public Cursor fetchAllGradesByFK(long rowId) {
        //returns cursor that contains the course_id matching rowId
        String s = String.valueOf(rowId);

        return mDb.rawQuery("select * from grades where course_id =" + s, null);
    }

    public Cursor fetchGrade(long rowId) throws SQLException{
        Cursor mCursor = 
                
                mDb.query(true, TABLE_GRADES,
                        new String[] {KEY_ID, KEY_GRADE, KEY_GRADE_WEIGHT},
                        KEY_ID + "=" + rowId, null, null, null, null, null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateGrade(long rowId, String grade, Integer weight, Long course_id){
        ContentValues args = new ContentValues();
        args.put(KEY_GRADE, grade);
        args.put(KEY_GRADE_WEIGHT, weight);
        args.put(KEY_COURSE_ID, course_id);
        
        return mDb.update(TABLE_GRADES, args, KEY_ID + "=" + rowId, null) > 0;
    }


    // ------------------------ "scores" table methods ----------------//

    public long createScore(Integer score_value, long grade_id){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SCORE, score_value);
        initialValues.put(KEY_GRADE_ID, grade_id);

        return mDb.insert(TABLE_SCORES, null, initialValues);
    }

    public boolean deleteScore(long rowId){
        return mDb.delete(TABLE_SCORES, KEY_ID + "=" + rowId, null) > 0;
    }

    //public Cursor fetchAllScores(){}

    public Cursor fetchAllScoresByFK(long rowId) {
        //returns cursor that contains the course_id matching rowId
        String s = String.valueOf(rowId);

        return mDb.rawQuery("select * from scores where grade_id =" + s, null);
    }

    public ArrayList<Integer> fetchAllScoresIntoAL(Long rowId){
        ArrayList<Integer> a = new ArrayList();
        String s = String.valueOf(rowId);
        //Cursor c = mDb.query(TABLE_SCORES, new String[] {KEY_SCORE}, null, null, null, null, null);
        Cursor c = mDb.rawQuery("select * from scores where grade_id =" + s, null);
        if (c.moveToFirst()){
           do{
               a.add(c.getInt(c.getColumnIndexOrThrow(KEY_SCORE)));
           } while(c.moveToNext());
       }

        return a;
    }
/*
    public Cursor fetchScore(long rowId) throws SQLException{

    }
    public boolean updateGrade(){
        return true;
    }
    */

}
