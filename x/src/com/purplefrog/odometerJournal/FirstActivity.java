package com.purplefrog.odometerJournal;

import android.app.Activity;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.text.*;
import java.util.*;

public class FirstActivity extends Activity
{

    private static final String LOG_TAG = "FirstActivity";
    protected final DateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
    private SimpleCursorAdapter adapter;
    private Cursor gridCursor;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);

        fillGridData();

        Button b = (Button) findViewById(R.id.add);
        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                addRow();
            }
        });
    }

    private void fillGridData()
    {
        final ListView gv = (ListView) findViewById(R.id.grid);

        final int[] widget_ids = {
            R.id.odometer,
            R.id.date,
            R.id.volume
        };

        Runnable runnable = new Runnable()
        {
            public void run()
            {
                bgFillGridFromDB(widget_ids, gv);
            }
        };

        Thread t = new Thread(runnable, "db query");
        t.start();

    }

    private void bgFillGridFromDB(final int[] widget_ids, final ListView gv)
    {
        gridCursor = queryGridAdapter();

        Runnable runnable = new Runnable()
        {
            public void run()
            {
                adapter = new SimpleCursorAdapter(FirstActivity.this, R.layout.odometer_grid, gridCursor, MyDBHelper.COLUMN_NAMES, widget_ids);
                gv.setAdapter(adapter);
            }
        };
        runOnUiThread(runnable);
    }

    public Cursor queryGridAdapter()
    {
        SQLiteOpenHelper helper = new MyDBHelper(this);

        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query("journal", MyDBHelper.COLUMN_NAMES, null, null, null, null, "odometer");
    }

    public void addRow()
    {
        try {
            MyDBHelper helper = new MyDBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();

            Double odometer;
            String date;
            Double volume;
            {
                TextView tv = (TextView) findViewById(R.id.odometer);
                String odometer_ = tv.getText().toString();
                try {
                    odometer = Double.parseDouble(odometer_);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("bad value for odometer : "+odometer_);
                }
            }
            {
                TextView tv = (TextView) findViewById(R.id.date);
                date = tv.getText().toString();
            }
            {
                TextView tv = (TextView) findViewById(R.id.volume);
                String volume_ = tv.getText().toString();
                if (volume_.length()==0)
                    volume = null;
                else
                    try {
                        volume = Double.parseDouble(volume_);
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException("bad value for volume : "+volume_);
                    }
            }


            ContentValues values = new ContentValues();
            values.put("odometer", odometer);
            values.put("date", date);
            values.put("volume", volume);


            long id = db.insert("journal", null, values);

            Log.d(LOG_TAG, "row id = "+id);

            if (id<0) {
                bgToast("malfunction updating DB");
            } else {
                bgToast("journal entry added to DB");

                gridCursor.requery();
            }
        } catch (Exception e) {
            bgToast("malfunction : "+e.getMessage());
        }
    }

    public void bgToast(final String msg)
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                Toast.makeText(FirstActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        };
        runOnUiThread(runnable);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        EditText date = (EditText) findViewById(R.id.date);
        date.setText(df.format(new Date()));
    }

}
