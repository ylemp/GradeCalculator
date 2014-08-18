package com.ylemp.gradecalculator;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;


/**
 * Created by home on 8/11/14.
 * View that allows the user to input a new Grade and Weight
 */
public class CreateGrade extends Activity {
    private EditText mGradeText;
    private EditText mGradeWeight;
    private Long mRowId;
    private Long courseId;
    private CourseDbAdapter mDbHelper;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_a_grade);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mGradeText = (EditText) findViewById(R.id.grade_name);
        mGradeWeight = (EditText) findViewById(R.id.weight);
        Button submitButton = (Button) findViewById(R.id.submit2);

        mDbHelper = new CourseDbAdapter(this);
            try {
                mDbHelper.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        //gets _id of the clicked course
        courseId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CourseDbAdapter.KEY_ID);
        if (courseId == null) {
            Bundle extras = getIntent().getExtras();
            courseId = extras != null ? extras.getLong(CourseDbAdapter.KEY_ID)
                    : null;
        }

/*
            try {
               populateFields();
            } catch (SQLException e) {
             e.printStackTrace();
            }
*/
        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    //gets fields from a grade to edit
    private void populateFields() throws SQLException {
        if (mRowId != null){
            Cursor grade = mDbHelper.fetchAllGrades();
            startManagingCursor(grade);
            mGradeText.setText(grade.getString(
                    grade.getColumnIndexOrThrow(CourseDbAdapter.KEY_GRADE)));
            mGradeWeight.setText(grade.getInt(
                    grade.getColumnIndexOrThrow(CourseDbAdapter.KEY_GRADE_WEIGHT)));
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
/*        try {
            populateFields();
        } catch (SQLException e) {
            e.printStackTrace();
       }
*/     }

    private void saveState() {
        String grade = mGradeText.getText().toString();
        String test = mGradeWeight.getText().toString();

        int weight = Integer.parseInt(test);

        if (mRowId == null){
            long id = mDbHelper.createGrade(grade, weight, courseId);

        }
    }


}
