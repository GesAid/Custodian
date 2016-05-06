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
import android.widget.EditText;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.itx.kumov.custodian.CustodianDB;
import ru.itx.kumov.custodian.R;

/**
 * Created by kumov on 29.04.16.
 */
public class ExportFrag extends Fragment {
    private static final String TAG = "test ";
    View view;
    CustodianDB custodianDB;
    SQLiteDatabase db;
    EditText ed_fName;
    String fname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context context = getActivity();
        custodianDB = new CustodianDB(context, "custodian.db", null, 1);


        db = custodianDB.getWritableDatabase();
        view = inflater.inflate(R.layout.f_export, container, false);
        ed_fName = (EditText) view.findViewById(R.id.et_file_name);
        Button bt_ok = (Button) view.findViewById(R.id.bt_export_data);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = ed_fName.getText().toString();
                if (fname == "") {
                    fname = new Date().toString();
                }
                WriteFile wr = new WriteFile();
                wr.execute();
            }
        });

        return view;

    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean saveExcelFile(Context context, String fileName,
                                  List<Double> value, List<String> date, List<String> status,
                                  List<String> comment) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;
        SharedPreferences sPref = getActivity().getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        float min = sPref.getFloat(getString(R.string.MIN), 0);
        float max = sPref.getFloat(getString(R.string.MAX), 0);
        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setBottomBorderColor(IndexedColors.AQUA.getIndex());

        CellStyle cs1 = wb.createCellStyle();
        cs1.setBottomBorderColor(IndexedColors.AQUA.getIndex());
        cs1.setFillPattern(HSSFCellStyle.ALIGN_CENTER);

        CellStyle csR = wb.createCellStyle();
        csR.setFillForegroundColor(HSSFColor.RED.index);
        csR.setBottomBorderColor(IndexedColors.AQUA.getIndex());
        csR.setFillPattern(HSSFCellStyle.ALIGN_CENTER);

        CellStyle csG = wb.createCellStyle();
        csG.setFillForegroundColor(HSSFColor.GREEN.index);
        csG.setBottomBorderColor(IndexedColors.AQUA.getIndex());
        csG.setFillPattern(HSSFCellStyle.ALIGN_CENTER);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Таблица показаний");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Показание");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Дата");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Статус");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Комментарий");
        c.setCellStyle(cs);
        int j = 0;
        for (int i = 1; i <= value.size(); i++) {
            row = sheet1.createRow(i);

            c = row.createCell(0);
            c.setCellValue(value.get(j));
            c.setCellStyle(cs1);

            c = row.createCell(1);
            c.setCellValue(date.get(j));
            c.setCellStyle(cs1);
            if (value.get(j) < min || value.get(j) > max) {
                c = row.createCell(2);
                c.setCellValue(status.get(j));
                c.setCellStyle(csR);
            } else {

                c = row.createCell(2);
                c.setCellValue(status.get(j));
                c.setCellStyle(csG);
            }

            c = row.createCell(3);
            c.setCellValue(comment.get(j));
            c.setCellStyle(cs1);
            j++;
        }

        sheet1.setColumnWidth(0, (15 * 200));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/custodian");
        sdPath.mkdirs();
        File file = new File(sdPath, fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    class WriteFile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            SimpleDateFormat form = new SimpleDateFormat("Дата: dd MMM yyyy Время: kk:mm", new Locale("ru"));
            List<String> lComment = new ArrayList<String>();
            List<String> lStatus = new ArrayList<String>();
            List<String> lDate = new ArrayList<String>();
            List<Double> lValue = new ArrayList<Double>();
            Cursor cur = db.query(CustodianDB.TABLE_NAME, new String[]{CustodianDB.COMMENT,
                            CustodianDB.STATUS, CustodianDB.VALUE, CustodianDB.DATE},
                    null, null, null, null, null);
            while (cur.moveToNext()) {
                String comment = cur.getString(cur.getColumnIndex(CustodianDB.COMMENT));
                String status = cur.getString(cur.getColumnIndex(CustodianDB.STATUS));
                Long date = cur.getLong(cur.getColumnIndex(CustodianDB.DATE));
                double value = cur.getDouble(cur.getColumnIndex(CustodianDB.VALUE));

                lComment.add(comment);
                lStatus.add(status);
                lDate.add(form.format(date));
                lValue.add(value);

            }
            cur.close();
            Context con = getActivity();

            saveExcelFile(con, fname + ".xls", lValue, lDate, lStatus, lComment);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Context con = getActivity();
            Toast.makeText(con, "Файл " + fname + ".xls сохранен", Toast.LENGTH_LONG).show();
            ed_fName.setText("");
        }
    }

}
