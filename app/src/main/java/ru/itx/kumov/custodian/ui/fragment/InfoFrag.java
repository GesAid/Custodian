package ru.itx.kumov.custodian.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;
import ru.itx.kumov.custodian.R;

/**
 * Created by kumov on 29.04.16.
 */
public class InfoFrag extends Fragment{

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_info,container, false);
        Button bt_licence = (Button) view.findViewById(R.id.bt_license);
        bt_licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context con = getActivity();
                new LicensesDialog.Builder(con)
                        .setNotices(R.raw.licence)
                        .build().showAppCompat();
            }
        });
        return view;
    }
}
