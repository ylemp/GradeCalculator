package com.ylemp.gradecalculator;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;


public class CreateClass extends Activity {

    private EditText mCourseText;
    private Long mRowId;
    private CourseDbAdapter mDbHelper;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaclass);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mCourseText = (EditText) findViewById(R.id.class_name);
        Button submitButton = (Button) findViewById(R.id.submit);

        mDbHelper = new CourseDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CourseDbAdapter.KEY_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(CourseDbAdapter.KEY_ID)
                    : null;
        }

        try {
            populateFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });

    }

    private void populateFields() throws SQLException {
        if(mRowId != null){
            Cursor course = mDbHelper.fetchCourse(mRowId);
            startManagingCursor(course);
            mCourseText.setText(course.getString(
                    course.getColumnIndexOrThrow(CourseDbAdapter.KEY_COURSE)));
        }
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(CourseDbAdapter.KEY_ID, mRowId);
    }

    protected void onPause(){
        super.onPause();
        saveState();
    }

    protected void onResume(){
        super.onResume();
        try {
            populateFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveState(){
        String course = mCourseText.getText().toString();

        if (mRowId == null){
            long id = mDbHelper.createCourse(course);
            if (id > 0){
                mRowId = id;
            }
        } else {
            mDbHelper.updateCourse(mRowId, course);
        }
    }
}
