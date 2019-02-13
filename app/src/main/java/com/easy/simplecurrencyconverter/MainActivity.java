package com.easy.simplecurrencyconverter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.lang.ref.WeakReference;
import java.util.Currency;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vas.com.currencyconverter.CurrencyConverter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<Currency> clist;
    @BindView(R.id.input_currency)
    TextInputEditText inputCurrency;
    @BindView(R.id.from_short)
    TextView fromShort;
    @BindView(R.id.from_Spinner)
    SearchableSpinner fromSpinner;
    @BindView(R.id.destination_Spinner)
    SearchableSpinner destinationSpinner;
    @BindView(R.id.convert)
    Button convert;
    @BindView(R.id.result)
    TextView result;
    @BindView(R.id.result_short)
    TextView resultShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        clist = CurrencyConverter.getCurrencyList();
        String[] arr = new String[clist.size()];
        for (int c = 0; c < arr.length; c++) {
            arr[c] = clist.get(c).toString() + " - " + clist.get(c).getDisplayName();
        }
        for (Currency c : clist) {
            Log.d("list", c.getCurrencyCode() + c.getDisplayName() + c.getSymbol() + c.getDefaultFractionDigits());
            Log.d("ok", c.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        destinationSpinner.setAdapter(adapter);
        fromSpinner.setOnItemSelectedListener(this);
        destinationSpinner.setOnItemSelectedListener(this);
        fromSpinner.setTitle(getString(R.string.from_string));
        destinationSpinner.setTitle(getString(R.string.to_string));
    }

    @OnClick({R.id.convert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.convert:
                try {
                    if (fromSpinner.getSelectedItem()!=null){
                        if (destinationSpinner.getSelectedItem()!=null){
                            if (!TextUtils.isEmpty(inputCurrency.getText().toString())&&TextUtils.isDigitsOnly(inputCurrency.getText().toString())){
                                new process(MainActivity.this,Integer.parseInt(inputCurrency.getText().toString()),clist.get(fromSpinner.getSelectedItemPosition()).getCurrencyCode(),clist.get(destinationSpinner.getSelectedItemPosition()).getCurrencyCode(),clist.get(destinationSpinner.getSelectedItemPosition()).getCurrencyCode()).execute("my string parameter");
                                //process(Integer.parseInt(inputCurrency.getText().toString()),clist.get(fromSpinner.getSelectedItemPosition()).getCurrencyCode(),clist.get(destinationSpinner.getSelectedItemPosition()).getCurrencyCode(),clist.get(destinationSpinner.getSelectedItemPosition()).getCurrencyCode());
                            }else {
                                Toast.makeText(getApplicationContext(),getString(R.string.amount_string), Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),getString(R.string.to_string), Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(getApplicationContext(),getString(R.string.from_string), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.d("Error Line Number", Log.getStackTraceString(e));
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.from_Spinner) {
            fromShort.setText(clist.get(fromSpinner.getSelectedItemPosition()).getCurrencyCode());
        } else if (parent.getId() == R.id.destination_Spinner) {
            resultShort.setText(clist.get(destinationSpinner.getSelectedItemPosition()).getCurrencyCode());

        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {

    }
    public static class process extends AsyncTask<String, Integer, String> {

        private WeakReference<MainActivity> activityReference;
        MainActivity activity;
        ProgressDialog loading;
        int val;
        String value,desire,code;

        process(MainActivity context,int val, String value, String desire,String code){
            this.val=val;
            this.value=value;
            this.desire=desire;
            this.code=code;

            activityReference = new WeakReference<>(context);
            activity=activityReference.get();
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Do something like display a progress bar
            loading = ProgressDialog.show(activity,"Processing","Please Wait!");

            loading.setCanceledOnTouchOutside(false);
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            try {
                CurrencyConverter.calculate(val, value, desire, new CurrencyConverter.Callback() {
                    @Override
                    public void onValueCalculated(Double value, Exception e) {
                        if (e != null) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }else{
                            activity.result.setText(CurrencyConverter.formatCurrencyValue(code, value));
                        }
                    }
                });
            } catch (Exception e) {
                Log.d("Error Line Number", Log.getStackTraceString(e));
            }
            return "this string is passed to onPostExecute";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Do things like hide the progress bar or change a TextView
            // get a reference to the activity if it is still there
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            loading.dismiss();
        }
    }
}
