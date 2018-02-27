package vkmsgr.com.vkmessenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.ArrayList;
import java.util.List;

public class DataBaseRequest {

    private DBHelper mDBHelper;
    private SQLiteDatabase db;

    public DataBaseRequest(Context context) {
        mDBHelper = new DBHelper(context);
        db = mDBHelper.getWritableDatabase();
    }

    public void insertList(final VKUsersArray usersArray){
        for (VKApiUserFull userFull: usersArray){
            ContentValues contentValues = new ContentValues();
            contentValues.put("id_vk", userFull.id);
            contentValues.put("full_name", userFull.first_name+" "+userFull.last_name);
            contentValues.put("photo_100", userFull.photo_100);
            contentValues.put("sex", userFull.sex);
            contentValues.put("online", userFull.online);
            db.insert("Friends",null, contentValues);
        }
    }

    public List<Friend> queryFriends(){
        Cursor cursor = db.query("Friends", null, null, null, null, null, null);
        return createListFromDB(cursor);
    }

    public List<Friend> find(String text,boolean[] filters){
        String sex = "%";
        String online = "0";
        if (filters[0]) online = "1";
        if (filters[1] && !filters[2]) sex = "2";
        else if (!filters[1] && filters[2]) sex = "1";
        Cursor cursor = db.query("Friends", null,
                "full_name like ? and online = ? and sex like ?",
                new String[] { "%"+text+"%", online, sex},
                null, null, null);
        return createListFromDB(cursor);
    }

    private List<Friend> createListFromDB(Cursor cursor) {
        List<Friend> list = new ArrayList<>();
        if (cursor.getCount() != 0) {
            int indexID = cursor.getColumnIndex("id_vk");
            int indexFullName = cursor.getColumnIndex("full_name");
            int indexPhoto100 = cursor.getColumnIndex("photo_100");
            int indexSex = cursor.getColumnIndex("sex");
            int indexOnline = cursor.getColumnIndex("online");
            if (cursor.moveToFirst()) {
                do {
                    list.add(new Friend(
                            cursor.getInt(indexID),
                            cursor.getString(indexFullName),
                            cursor.getString(indexPhoto100),
                            cursor.getInt(indexSex),
                            cursor.getInt(indexOnline)));
                } while (cursor.moveToNext());
            } else cursor.close();
        }
        return list;
    }

    public void deleteAll() {
        db.delete("Friends", null, null);
    }
}
