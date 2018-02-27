package vkmsgr.com.vkmessenger;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zodiakaio on 22.02.2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, "VK", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Friends ("
                                + "id integer primary key autoincrement,"
                                + "id_vk integer,"
                                + "full_name text,"
                                + "photo_100 text,"
                                + "sex integer,"
                                + "online integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
