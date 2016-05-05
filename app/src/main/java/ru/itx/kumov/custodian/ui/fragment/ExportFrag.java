package ru.itx.kumov.custodian.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.itx.kumov.custodian.CustodianDB;
import ru.itx.kumov.custodian.R;
import ru.itx.kumov.custodian.ui.MainActivity;

/**
 * Created by kumov on 29.04.16.
 */
public class ExportFrag extends Fragment {
    View view;
    CustodianDB custodianDB;
    SQLiteDatabase db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        custodianDB = new CustodianDB(context, "custodian.db", null, 1);
        db = custodianDB.getWritableDatabase();
        view = inflater.inflate(R.layout.f_export, container, false);
        Button bt_ok = (Button) view.findViewById(R.id.bt_export_data);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteFile wr = new WriteFile();
                wr.execute();
            }
        });
        return view;

    }
class WriteFile extends AsyncTask<Void, Void, Void> {
        String inputText;
        String out;
        String fname;

        @Override
        protected Void doInBackground(Void... params) {
            inputText = "Data File: \n";
            float maxValue =0;
            float minValue = 0;
            SharedPreferences sPref = getActivity().getSharedPreferences(getString
                    (R.string.shared_preference), Context.MODE_PRIVATE);
            minValue = sPref.getFloat(getString(R.string.MIN), 0);
            maxValue = sPref.getFloat(getString(R.string.MAX), 0);
            SimpleDateFormat form = new SimpleDateFormat(" dd MMM yyyy \"kk:mm\"", new Locale("ru"));

            Cursor cur = db.query(CustodianDB.TABLE_NAME, new String[]{CustodianDB.VALUE, CustodianDB.DATE},
                    null, null, null, null, null);
            while (cur.moveToNext()) {
                String value = cur.getString(cur.getColumnIndex(CustodianDB.VALUE));
                Long ldate = cur.getLong(cur.getColumnIndex(CustodianDB.DATE));
                double now = cur.getDouble(cur.getColumnIndex(CustodianDB.VALUE));
                if (now > minValue && now < maxValue){
                inputText = inputText + "    " + value + " - " + form.format(ldate) +" - норма "+ "\n";
                }
                else if (now < minValue)
                    inputText = inputText + "    " + value + " - " + form.format(ldate) +" - ниже нормы "+ "\n";
                else if (now > maxValue)
                    inputText = inputText + "    " + value + " - " + form.format(ldate) +" - превышение нормы "+ "\n";
            }
            cur.close();

            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Log.d("custodian err", "SD-карта не доступна: " + Environment.getExternalStorageState());
                return null;
            }
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/custodian");
            sdPath.mkdirs();
            out = sdPath.getAbsolutePath().toString();
            fname = "Otchet" + new SimpleDateFormat("kk:mm__dd-MM-yyyy'.txt'").format(new Date());
            File sdFile = new File(sdPath, fname);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                bw.write(inputText);
                bw.close();
                Log.d("custodian err", "Файл записан на SD: " + sdFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Context con = getActivity();
            Toast.makeText(con, out+" "+ fname, Toast.LENGTH_LONG).show();
        }
    }

}
