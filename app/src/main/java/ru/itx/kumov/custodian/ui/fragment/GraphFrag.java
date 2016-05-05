package ru.itx.kumov.custodian.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.itx.kumov.custodian.CustodianDB;
import ru.itx.kumov.custodian.R;
import ru.itx.kumov.custodian.ui.MainActivity;

/**
 * Created by kumov on 29.04.16.
 */
public class GraphFrag extends Fragment{
    View view;
    private View mChart;
    CustodianDB custodianDB;
    SQLiteDatabase db;
    SharedPreferences sPref;
    float min;
    float max;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        custodianDB = new CustodianDB(context, "custodian.db", null, 1);
        db = custodianDB.getWritableDatabase();
        sPref = getActivity().getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE);
        min = sPref.getFloat(getString(R.string.MIN), 0);
        max = sPref.getFloat(getString(R.string.MAX), 0);

        view = inflater.inflate(R.layout.f_graph, container, false);
        DbAsync async = new DbAsync(context, view);
        async.execute();
        return view;
    }

    class DbAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        XYMultipleSeriesDataset dataset;
        XYMultipleSeriesRenderer multiRenderer;
        int masssSize;
        String name;
        String[] dateString;
        double maxY;

private Context mContext;
private View rootView;

public DbAsync(Context context, View rootView){
    this.mContext=context;
    this.rootView=rootView;
}


        @Override
        protected Void doInBackground(Void... params) {


            List<Long> longList = new ArrayList<Long>();
            List<Double> doubleList = new ArrayList<Double>();
            long firstDate =0;
            double temp=0;
            Cursor cur = db.query(ru.itx.kumov.custodian.CustodianDB.TABLE_NAME,
                    new String[]{ru.itx.kumov.custodian.CustodianDB.VALUE,
                            ru.itx.kumov.custodian.CustodianDB.DATE},
                    null, null, null, null, null);
            while (cur.moveToNext()) {
                double value = cur.getDouble(cur.getColumnIndex(ru.itx.kumov.custodian.CustodianDB.VALUE));
                long ldate = cur.getLong(cur.getColumnIndex(ru.itx.kumov.custodian.CustodianDB.DATE));
                maxY = value;
                if (maxY > temp){
                    temp = maxY;
                }
                if (firstDate==0){
                    firstDate = ldate;
                }
                doubleList.add(value);
                longList.add(ldate);
            }
            maxY = temp;
            cur.close();
            masssSize = doubleList.size();
            dateString = new String[masssSize];
            double[] dateValue = new double[masssSize];
            long[] dateLong = new long[masssSize];
            SimpleDateFormat form = new SimpleDateFormat("dd MMM \"kk:mm\"", new Locale("ru"));
            for (int i = 0; i < masssSize; i++) {
                dateLong[i] = longList.get(i);
                dateValue[i] = doubleList.get(i);
                dateString[i] = form.format(new Date(dateLong[i]));
            }
            XYSeries valSeries = new XYSeries("VALUE");
            XYSeries minSeries = new XYSeries("MIN");
            XYSeries maxSeries = new XYSeries("MAX");
            for (int i = 0; i < masssSize; i++) {
                valSeries.add(i, dateValue[i]);
                minSeries.add(i, min);
                maxSeries.add(i, max);

            }
            dataset = new XYMultipleSeriesDataset();
            dataset.addSeries(valSeries);
            dataset.addSeries(minSeries);
            dataset.addSeries(maxSeries);
            XYSeriesRenderer valueRenderer = new XYSeriesRenderer();
            valueRenderer.setColor(Color.GREEN);
            valueRenderer.setFillPoints(true);
            valueRenderer.setLineWidth(2f);
            valueRenderer.setDisplayChartValues(true);
            valueRenderer.setPointStyle(PointStyle.DIAMOND);
            valueRenderer.setStroke(BasicStroke.SOLID);

            XYSeriesRenderer minRenderer = new XYSeriesRenderer();
            minRenderer.setColor(Color.RED);
            minRenderer.setFillPoints(true);
            minRenderer.setLineWidth(2f);
            minRenderer.setDisplayChartValues(true);
            minRenderer.setPointStyle(PointStyle.CIRCLE);
            minRenderer.setStroke(BasicStroke.DOTTED);

            XYSeriesRenderer maxRenderer = new XYSeriesRenderer();
            maxRenderer.setColor(Color.RED);
            maxRenderer.setFillPoints(true);
            maxRenderer.setLineWidth(2f);
            maxRenderer.setDisplayChartValues(true);
            maxRenderer.setPointStyle(PointStyle.CIRCLE);
            maxRenderer.setStroke(BasicStroke.DOTTED);



            multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setXLabels(0);
            multiRenderer.setChartTitle("ГТТ ");
            multiRenderer.setXTitle("Дата показания");

            multiRenderer.setYTitle("Показания " );
            multiRenderer.setChartTitleTextSize(20);
            multiRenderer.setAxisTitleTextSize(20);
            multiRenderer.setLabelsTextSize(20);
            multiRenderer.setZoomButtonsVisible(true);
            multiRenderer.setPanEnabled(true, true);
            multiRenderer.setClickEnabled(false);
            multiRenderer.setZoomEnabled(true, true);
            multiRenderer.setShowGridY(true);
            multiRenderer.setShowGridX(true);
            multiRenderer.setFitLegend(true);
            multiRenderer.setShowGrid(true);
            multiRenderer.setZoomEnabled(false);
            multiRenderer.setExternalZoomEnabled(false);
            multiRenderer.setAntialiasing(true);
            multiRenderer.setInScroll(false);
            multiRenderer.setLegendHeight(20);
            multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
            multiRenderer.setYLabelsAlign(Paint.Align.LEFT);
            multiRenderer.setTextTypeface("sans_serif", Typeface.BOLD_ITALIC);
            multiRenderer.setYLabels(10);
            multiRenderer.setYAxisMax(maxY+3);
            multiRenderer.setXAxisMin(-0.5);
            multiRenderer.setXAxisMax(masssSize+1);
            multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
            multiRenderer.setApplyBackgroundColor(true);
            multiRenderer.setScale(2f);
            multiRenderer.setPointSize(4f);
            multiRenderer.setMargins(new int[]{30, 30, 30, 30});
            for (int i = 0; i < masssSize; i++) {
                multiRenderer.addXTextLabel(i, dateString[i]);
            }
            multiRenderer.addSeriesRenderer(valueRenderer);
            multiRenderer.addSeriesRenderer(maxRenderer);
            multiRenderer.addSeriesRenderer(minRenderer);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mChart = ChartFactory.getLineChartView(getActivity(), dataset, multiRenderer);
             LinearLayout chartContainer = (LinearLayout) rootView.findViewById(R.id.chart);
            chartContainer.addView(mChart);
        }
    }
}

