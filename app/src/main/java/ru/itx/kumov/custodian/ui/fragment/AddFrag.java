package ru.itx.kumov.custodian.ui.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import ru.itx.kumov.custodian.CustodianDB;
import ru.itx.kumov.custodian.R;

/**
 * Created by kumov on 29.04.16.
 */
public class AddFrag extends Fragment implements View.OnClickListener{
    CustodianDB custodianDB;
    SQLiteDatabase db;
    SharedPreferences sPref;
    View view;
    float min,max;
    EditText et_value, et_comment;
    Button bt_add;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        custodianDB = new CustodianDB(context, "custodian.db", null, 1);
        view = inflater.inflate(R.layout.f_add, container, false);

        et_comment = (EditText) view.findViewById(R.id.et_add_comment);
        et_value = (EditText) view.findViewById(R.id.et_add_value);
        bt_add = (Button) view.findViewById(R.id.bt_add_value);
         bt_add.setOnClickListener(this);
        db = custodianDB.getWritableDatabase();
        sPref = getActivity().getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        min = sPref.getFloat(getString(R.string.MIN), 0);
        max = sPref.getFloat(getString(R.string.MAX), 0);
        return view;
    }
    public void onClick(View v){
        String status = "норма";
        String value = et_value.getText().toString() + "";
        String comment = et_comment.getText().toString() + "";
        Calendar calendar = Calendar.getInstance();
        long date = calendar.getTime().getTime();
        if (value != "") {
            if (min > Float.parseFloat(value)){
                status = "меньше нормы";
            }
            if (max <  Float.parseFloat(value))
                 status = "больше нормы";
            ContentValues values = new ContentValues();
            values.put(CustodianDB.VALUE, value);
            values.put(CustodianDB.DATE, date);
            values.put(CustodianDB.COMMENT, comment);
            values.put(CustodianDB.STATUS, status);
            db.insert(CustodianDB.TABLE_NAME, null, values);
            et_value.setText("");
            et_comment.setText("");
        } else
            Toast.makeText(context, "Нет значения", Toast.LENGTH_SHORT).show();
    }
}
