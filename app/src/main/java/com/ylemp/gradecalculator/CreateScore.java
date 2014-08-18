package com.ylemp.gradecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;

/**
 * Created by home on 8/17/14.
 * View that allows user to input a score for a grade for a class
 */
public class CreateScore extends Activity {
    private EditText mScore;
    private Long mRowId;
    private Long mGradeId;
    private CourseDbAdapter mDbHelper;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_a_score);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mScore = (EditText) findViewById(R.id.score);
        Button submitButton = (Button) findViewById(R.id.submit3);

        mDbHelper = new CourseDbAdapter(this);
            try {
                mDbHelper.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        mGradeId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CourseDbAdapter.KEY_ID);
        if (mGradeId == null) {
            Bundle extras = getIntent().getExtras();
            mGradeId = extras != null ? extras.getLong(CourseDbAdapter.KEY_ID)
                    : null;
        }

        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

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
    }

    private void saveState() {
        String s = mScore.getText().toString();

        int score = Integer.parseInt(s);

        if (mRowId == null){
            long id = mDbHelper.createScore(score, mGradeId);

        }
    }
}
