package com.ylemp.gradecalculator;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by home on 8/17/14.
 * List view that shows scores for a grade
 */
public class ScoreView extends ListActivity {

    private CourseDbAdapter mDbHelper;
    private static final int INSERT_ID = Menu.FIRST;
    private Long mGradeId;
    public ArrayList<Integer> scoresList = new ArrayList();
    public Double average = 0.0;
    public Double sum = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
        mDbHelper = new CourseDbAdapter(this);
            try {
              mDbHelper.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        //gets the grades row id to bind to the scores grade_id field
        mGradeId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CourseDbAdapter.KEY_ID);
        if (mGradeId == null) {
            Bundle extras = getIntent().getExtras();
            mGradeId = extras != null ? extras.getLong(CourseDbAdapter.KEY_ID)
                    : null;
        }

        fillDataById(mGradeId);
        registerForContextMenu(getListView());

        scoresList = mDbHelper.fetchAllScoresIntoAL();
        for(int i=0; i<scoresList.size(); i++){
            sum = sum + scoresList.get(i);
        }
        average = sum/scoresList.size();
    }

    //update with scores instead of grades
    private void fillDataById(long row_id){
        //populates the list view
        Cursor scoreCursor = mDbHelper.fetchAllScoresByFK(row_id);
        startManagingCursor(scoreCursor);

        String[] from = new String[]{CourseDbAdapter.KEY_SCORE};

        int[] to = new int[]{R.id.text3};

        SimpleCursorAdapter grades =
                new SimpleCursorAdapter(this, R.layout.scores_row, scoreCursor, from, to);
        setListAdapter(grades);

    }

    public void openCreateScore(){
        Intent intent = new Intent(this, CreateScore.class);
        intent.putExtra(CourseDbAdapter.KEY_ID, mGradeId);
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, "Add Score");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                openCreateScore();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    //edit this method to change what happens on click
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ScoreView.class);

        //attaches course_id to the intent which is then pulled via getSerializable
        i.putExtra(CourseDbAdapter.KEY_ID, id);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillDataById(mGradeId);
    }
}

