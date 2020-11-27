package fr.isep.ii3510.assignment4;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    Double targetRate = null, baseRate = null;
    private EditText inputEditText;
    private TextView outputTextView;
    private Button btnConvert;
    private Spinner spinner1, spinner2;
    private ArrayAdapter<String> dataAdapter;
    private ImageView imageView;

    private List<String> currencyList;
    private CurrencyExchangeRate ExchangeRatesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = findViewById(R.id.inputCurrency);
        outputTextView = findViewById(R.id.outputCurrency);
        btnConvert = findViewById(R.id.convertButton);
        spinner1 = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.image);

        spinner1.setOnItemSelectedListener(this);

        ExchangeRatesList = getCurrencyList();

        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        spinner2.setAdapter(dataAdapter);
        //Set selection to 2nd element so both spinners don't have the same currency selected
        spinner2.setSelection(1);
    }


    //Creates a list of currencies by reading data from API
    //Also saves Exchange rates with EUR as the base
    public CurrencyExchangeRate getCurrencyList() {
        String url = "https://api.exchangeratesapi.io/latest";
        currencyList = new ArrayList<>();
        currencyList.add("EUR");
        final CurrencyExchangeRate eur = new CurrencyExchangeRate("EUR");
        Currency currency = new Currency("EUR", 1.0);
        eur.addCurrencyRate(currency);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("rates");

                            //REFERENCE: https://stackoverflow.com/a/25977001/11031957
                            for (int i = 0; i < object.names().length(); i++) {
                                //Extract data
                                String code = object.names().getString(i);
                                Double rate = (Double) object.get(object.names().getString(i));

                                //Add currency codes to the list(names)
                                currencyList.add(code);

                                //Create Currency with rate in relation to "EUR"
                                Currency currency = new Currency(code, rate);
                                //Add new Currency to the list
                                eur.addCurrencyRate(currency);
                                //Log.d("Volley Currency: ", list.get(i).toString() + " added to list. Size = " + list.size());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley response", "An error occurred.\n" + error);
            }
        });
        queue.add(request);

        return eur;
    }

    //Returns a list with the specified currency removed
    //REMOVED - Conflict with swapping
    public ArrayList<String> getUpdatedCurrencyList(final String selectedCurrency) {
        final ArrayList list = new ArrayList<String>();
        for (int i = 0; i < currencyList.size(); i++) {
            String currency = currencyList.get(i);
            //Ignore selected string
            if (!currency.equals(selectedCurrency)) {
                list.add(currency);
                //Log.d("Volley", currency + " added to new list. Size = " + list.size());
            }
        }
        return list;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    /* REMOVED - Conflict with swapping
        String baseCurrency = parent.getItemAtPosition(position).toString();
        int pos = 0;
        //Toast.makeText(parent.getContext(), "Selected: " + baseCurrency, Toast.LENGTH_LONG).show();

        ArrayList<String> newList = getUpdatedCurrencyList(baseCurrency);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, newList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(spinner2.isSelected()) {
            String currency2 = spinner2.getSelectedItem().toString();
            pos = dataAdapter.getPosition(currency2);
            spinner2.setSelection(pos);
            Log.d("Volley response", "Setting " + currency2 + " to position " + pos);
        }

        //Update list to ignore selected currency in spinner1
        spinner2.setAdapter(dataAdapter);

        */
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onClick(View v) {
        final String baseCurrency = spinner1.getSelectedItem().toString();
        final String targetCurrency = spinner2.getSelectedItem().toString();
        final Double inputValue = Double.parseDouble(inputEditText.getText().toString());
        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            Toast.makeText(this, "ERROR! Please select both currency options before converting ", Toast.LENGTH_LONG).show();
        } else {
            baseRate = ExchangeRatesList.getRate(baseCurrency);
            targetRate = ExchangeRatesList.getRate(targetCurrency);
            Log.d("Volley response", "Calculating " + inputValue + " " + baseCurrency + " to " + targetCurrency + " " + targetRate);
            if (targetRate == null || baseRate == null) {
                Toast.makeText(this, "ERROR! Unable to calculate " + inputValue + " " + baseCurrency + " to " + targetCurrency, Toast.LENGTH_LONG).show();
            } else {
                Double result = inputValue * targetRate / baseRate;
                outputTextView.setText(String.format("%.5f", result));
            }
        }
    }

    public void switchCurrencies(View v) {
        String currency1 = spinner1.getSelectedItem().toString();
        String currency2 = spinner2.getSelectedItem().toString();
        Log.d("Volley response", "Swapping " + currency1 + " & " + currency2);
        int pos1 = dataAdapter.getPosition(currency1);
        int pos2 = dataAdapter.getPosition(currency2);

        Log.d("Volley response", "Position " + pos1 + " for " + currency1);
        Log.d("Volley response", "Position " + pos2 + " for " + currency2);

        spinner1.setSelection(pos2);
        spinner2.setSelection(pos1);
    }
}