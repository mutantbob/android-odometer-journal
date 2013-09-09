package com.purplefrog.odometerJournal;

import android.content.*;
import android.database.sqlite.*;
import android.provider.*;

/**
* Created with IntelliJ IDEA.
* User: thoth
* Date: 9/9/13
* Time: 3:56 PM
* To change this template use File | Settings | File Templates.
*/
class MyDBHelper
    extends SQLiteOpenHelper
{
    public static final String[] COLUMN_NAMES = new String[]{
        "odometer", "date", "volume",
        BaseColumns._ID,
    };

    public MyDBHelper(Context context)
    {
        super(context, "odometerJournal", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table journal("+ BaseColumns._ID+" integer primary key, odometer real, date string, volume real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newVersion)
    {
        //  aw man
    }
}
