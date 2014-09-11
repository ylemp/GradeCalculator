package com.ylemp.gradecalculator;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by home on 8/11/14.
 * List view that shows the grades for a class
 * Called after a course is clicked
 */


public class GradeView extends ListActivity {
    private CourseDbAdapter mDbHelper;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private Long mRowId;
    private Double grade = 0.0;
    private String s = new String();
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private ArrayList<Integer> weightList = new ArrayList<Integer>();
    private ArrayList<Long> gradeIdList = new ArrayList<Long>();
    private ArrayList<String> DeBug1 = new ArrayList<String>();
    private ArrayList<Double> scoresList = new ArrayList();
    private Integer weightTotal = 0;
    private double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade_layout);
        mDbHelper = new CourseDbAdapter(this);
            try {
             mDbHelper.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        //gets the courses_id
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CourseDbAdapter.KEY_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(CourseDbAdapter.KEY_ID)
                    : null;
        }

        if(mRowId != null) {
            fillDataById(mRowId);
        }
        registerForContextMenu(getListView());

        weightList = mDbHelper.getWeights(mRowId);
        gradeIdList = mDbHelper.getGradeIds(mRowId);
        DeBug1 = mDbHelper.gradeDeBug();

        for(int i=0; i<weightList.size(); i++){
            weightTotal = weightTotal + weightList.get(i);
        }

        if (weightTotal != 100 && weightTotal != 0){
            Toast.makeText(getApplicationContext(),
                    "Total Weight is not 100, current total weight is " + weightTotal.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        for(int i=0; i<gradeIdList.size(); i++){
            total = getAverage(gradeIdList.get(i));
            total = total * weightList.get(i);
            scoresList.add(total);
        }

        for(int j=0; j<scoresList.size(); j++){
            grade = grade + scoresList.get(j);
        }
        grade = Double.valueOf(Math.round(grade*100.00)/100.00);
        s = grade.toString() + "%";

        TextView tv1 = (TextView) findViewById(R.id.grade_per);
        tv1.setText(s);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressStatus = grade.intValue();
        progressBar.setProgress(progressStatus);

    }

    private Double getAverage(Long mGradeId){
        Double average;
        Double sum = 0.0;
        ArrayList<Integer> a;

        a = mDbHelper.fetchAllScoresIntoAL(mGradeId);

        if (a.size() == 0){
            return 0.0;
        }

        for(int i=0; i<a.size(); i++){
            sum = sum + a.get(i);
        }
        average = sum/a.size();
        average = average/100;

        return average;
    }

    private void fillData(){
        Cursor gradesCursor = mDbHelper.fetchAllGrades();
        startManagingCursor(gradesCursor);

        String[] from = new String[]{CourseDbAdapter.KEY_GRADE};

        int[] to = new int[]{R.id.text2};

        SimpleCursorAdapter grades =
                new SimpleCursorAdapter(this, R.layout.grades_row, gradesCursor, from, to);
        setListAdapter(grades);

    }

    private void fillDataById(long row_id){
        //populates the list view
        Cursor gradesCursor = mDbHelper.fetchAllGradesByFK(row_id);
        startManagingCursor(gradesCursor);

        String[] from = new String[]{CourseDbAdapter.KEY_GRADE};

        int[] to = new int[]{R.id.text2};

        SimpleCursorAdapter grades =
                new SimpleCursorAdapter(this, R.layout.grades_row, gradesCursor, from, to);
        setListAdapter(grades);

    }

    public void openCreateGrade(){
            Intent intent = new Intent(this, CreateGrade.class);
            intent.putExtra(CourseDbAdapter.KEY_ID, mRowId);
            startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, "Add Grade");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                openCreateGrade();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(1, DELETE_ID, 0, "DELETE GRADE");
    }

    //edit this method to change what happens on click
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ScoreView.class);

        //attaches grade_id to the intent which is then pulled via getSerializable
        i.putExtra(CourseDbAdapter.KEY_ID, id);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillDataById(mRowId);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteGrade(info.id);
                //fillData();
                fillDataById(mRowId);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
