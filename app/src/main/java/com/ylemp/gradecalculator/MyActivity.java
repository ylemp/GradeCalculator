package com.ylemp.gradecalculator;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.sql.SQLException;

public class MyActivity extends ListActivity {
    private static final int INSERT_ID = Menu.FIRST;
    private CourseDbAdapter mDbHelper;
    private static final int DELETE_ID = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mDbHelper = new CourseDbAdapter(this);
        try {
            mDbHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData(){
        Cursor coursesCursor = mDbHelper.fetchAllCourses();
        startManagingCursor(coursesCursor);

        String[] from = new String[]{CourseDbAdapter.KEY_COURSE};

        int[] to = new int[]{R.id.text1};

        SimpleCursorAdapter courses =
                new SimpleCursorAdapter(this, R.layout.courses_row, coursesCursor, from, to);
        setListAdapter(courses);

    }

    public void openCreate(){
        Intent intent = new Intent(this, CreateClass.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, "Add Course");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                openCreate();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenuInfo menuInfo){
            super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(1, DELETE_ID, 0, "DELETE COURSE");
     }

    //edit this method to change what happens on click
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, GradeView.class);

        //attaches course_id to the intent which is then pulled via getSerializable
        i.putExtra(CourseDbAdapter.KEY_ID, id);
        //i.putExtra(CourseDbAdapter.)
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
         super.onActivityResult(requestCode, resultCode, intent);
         fillData();
        }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteCourse(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

}
