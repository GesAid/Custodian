package ru.itx.kumov.custodian.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.itx.kumov.custodian.CustodianDB;
import ru.itx.kumov.custodian.R;

/**
 * Created by kumov on 29.04.16.
 */
public class ListFra extends Fragment{
    CustodianDB custodianDB;
    SQLiteDatabase db;
    View view;
    ListView lv;
    SimpleAdapter adapter;
    String l_val = "value";
    String l_date = "date";
    String l_status = "status";
    String l_comment = "comment";
    private ArrayList<HashMap<String, Object>> valueList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        custodianDB = new CustodianDB(context, "custodian.db", null, 1);
        db = custodianDB.getWritableDatabase();
        view = inflater.inflate(R.layout.f_list, container,false);
        lv = (ListView) view.findViewById(R.id.lv_value);
        ListAsync la = new ListAsync();
        la.execute();

        return view;
    }

    public void loadList(){

        valueList = new ArrayList<HashMap<String, Object>>();
        SimpleDateFormat form = new SimpleDateFormat("Дата: dd MMM yyyy Время: kk:mm", new Locale("ru"));
        HashMap<String, Object> hm;
        Cursor cur = db.query(CustodianDB.TABLE_NAME, new String[]{CustodianDB.COMMENT,
                        CustodianDB.STATUS, CustodianDB.VALUE, CustodianDB.DATE},
                null, null, null, null, null);
        while (cur.moveToNext()) {
            String comment = cur.getString(cur.getColumnIndex(CustodianDB.COMMENT));
            String status = cur.getString(cur.getColumnIndex(CustodianDB.STATUS));
            Long date = cur.getLong(cur.getColumnIndex(CustodianDB.DATE));
            double value = cur.getDouble(cur.getColumnIndex(CustodianDB.VALUE));
            hm = new HashMap<>();
            hm.put(l_comment,comment);
            hm.put(l_status, status);
            hm.put(l_date, form.format(date));
            hm.put(l_val, value);
            valueList.add(hm);
        }
        cur.close();
    }

    public class ListAsync extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadList();
            Context con = getActivity();
            adapter = new SimpleAdapter(con,valueList, R.layout.list_view,
                    new String[]{l_val, l_date,l_status,l_comment},
                    new int[] {R.id.lv_val, R.id.lv_date,R.id.lv_stat,R.id.lv_comment});
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            lv.setAdapter(adapter);
        }
    }
}
