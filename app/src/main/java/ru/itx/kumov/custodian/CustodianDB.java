package ru.itx.kumov.custodian;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by kumov on 27.04.16.
 */
public class CustodianDB extends SQLiteOpenHelper implements BaseColumns {

    public CustodianDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                       int version) {
        super(context, name, factory, version);
    }

    public CustodianDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                       int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    private final static String DB_NAME = "custodian.db";

    private final static int DB_VERSION = 1;

    public final static String TABLE_NAME = "value_tab";

    public final static String VALUE = "s_value";
    public final static String DATE = "s_date";
    public final static String STATUS = "s_status";
    public final static String COMMENT = "s_comment";

    public final static String SCRIPT = "create table " + TABLE_NAME + "("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + VALUE + " real, " + DATE + " integer,"
            + STATUS + " text, " + COMMENT + " text);";

}
