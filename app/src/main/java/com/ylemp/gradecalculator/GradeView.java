package com.ylemp.gradecalculator;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;

/**
 * Created by home on 8/11/14.
 * List view that shows the grades for a class
 * Called after a course is clicked
 */
public class GradeView extends ListActivity {
    private CourseDbAdapter mDbHelper;
    private static final int INSERT_ID = Menu.FIRST;
    private Long mRowId;
    private static final int DELETE_ID = Menu.FIRST + 1;


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

        //gets the courses row id to bind to the grades course_id field
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CourseDbAdapter.KEY_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(CourseDbAdapter.KEY_ID)
                    : null;
        }

        fillDataById(mRowId);
        registerForContextMenu(getListView());
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

        //attaches course_id to the intent which is then pulled via getSerializable
        i.putExtra(CourseDbAdapter.KEY_COURSE_ID, id);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillDataById(mRowId);
    }
}
