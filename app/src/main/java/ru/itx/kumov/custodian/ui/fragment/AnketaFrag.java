package ru.itx.kumov.custodian.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.itx.kumov.custodian.R;

/**
 * Created by kumov on 29.04.16.
 */
public class AnketaFrag extends Fragment {
    View view;
    protected static int RESULT_LOAD_IMAGE = 1;
    SharedPreferences sPref;
    float min;
    float max;
    String name;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_anketa, container, false);
        ImageView buttonLoadImage = (ImageView) view.findViewById(R.id.img);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        sPref = getActivity().getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        min = sPref.getFloat(getString(R.string.MIN), 0);
        max = sPref.getFloat(getString(R.string.MAX), 0);
        name = sPref.getString(getString(R.string.NAME), "");
        ImageView imageView = (ImageView) view.findViewById(R.id.img);
        imageView.setImageBitmap(BitmapFactory.decodeFile(sPref.getString("img", "")));
        TextView tv_name = (TextView) view.findViewById(R.id.tv_Name);
        TextView tv_min = (TextView) view.findViewById(R.id.tv_min_value);
        TextView tv_max = (TextView) view.findViewById(R.id.tv_max_value);
        tv_name.setText(name);
        tv_min.setText(min + " - минимальный показатель нормы");
        tv_max.setText(max + " - максимальный показатель нормы");
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == AnketaFrag.RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Context con = getActivity();
                Cursor cursor = con.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                ImageView imageView = (ImageView) view.findViewById(R.id.img);
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString("img", picturePath);
                editor.commit();
            }

        } catch (SecurityException e) {
            Context con = getActivity();
            Toast.makeText(con, "Нет доступа к файлу.", Toast.LENGTH_SHORT);
        }
    }

}
