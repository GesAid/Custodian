package ru.itx.kumov.custodian.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.itx.kumov.custodian.R;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sPref;
    EditText in_name, in_min, in_max;
    Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        in_name = (EditText) findViewById(R.id.et_input_name);
        in_min = (EditText) findViewById(R.id.et_input_min_value);
        in_max = (EditText) findViewById(R.id.et_input_max_value);
        bt_login = (Button) findViewById(R.id.bt_login);

        sPref = getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE);
        String str = sPref.getString(getString(R.string.NAME), "");
        if (str != "") {
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
            finish();
        }

    }

    public void onLogin(View view) {

        sPref = getSharedPreferences(getString(R.string.shared_preference), MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        if (!in_min.getText().toString().isEmpty() && !in_min.getText().toString().isEmpty()
                && !in_max.getText().toString().isEmpty()) {
            editor.putString(getString(R.string.NAME), in_name.getText().toString());
            editor.putFloat(getString(R.string.MIN), Float.parseFloat(in_min.getText().toString()));
            editor.putFloat(getString(R.string.MAX), Float.parseFloat(in_max.getText().toString()));
            editor.commit();
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
        } else
            Toast.makeText(this, "Заполни поля", Toast.LENGTH_LONG).show();

    }
}
