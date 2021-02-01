    package fr.yoannarn.currencyconverter;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Spinner;
    import android.widget.TextView;

    import androidx.appcompat.app.AppCompatActivity;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonObjectRequest;
    import com.android.volley.toolbox.Volley;
    import com.fasterxml.jackson.databind.ObjectMapper;

    import org.json.JSONObject;

    import java.io.IOException;
    import java.util.ArrayList;

    public class CurrencyConverterActivity extends AppCompatActivity {

        private Spinner currencyRef, currencyDest;
        private Button swapButton, converterButton;
        private TextView resultView;
        private EditText inputNumber;
        private SharedPreferences sharedPref;
        private Rates rates;

        private static String URL_API = "https://api.exchangeratesapi.io/latest";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_currency_converter);

            currencyRef = findViewById(R.id.currency1);
            currencyDest = findViewById(R.id.currency2);
            swapButton = findViewById(R.id.swapButton);
            converterButton = findViewById(R.id.converterButton);
            resultView = findViewById(R.id.resultView);
            inputNumber = findViewById(R.id.inputNumber);

            sharedPref = getPreferences(Context.MODE_PRIVATE);

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_API, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                rates = mapper.readValue(response.toString(), Rates.class);
                                String ratesSaved = mapper.writeValueAsString(rates);

                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("last_rates", ratesSaved);
                                editor.apply();

                                initConverter();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String ratesSaved = sharedPref.getString("last_rates", null);
                    if (ratesSaved != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            rates = mapper.readValue(ratesSaved, Rates.class);
                            initConverter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            queue.add(request);

        }


        public void initConverter() {
            ArrayAdapter currencyAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item,
                    new ArrayList(rates.getCurrencies()));

            currencyRef.setAdapter(currencyAdapter);
            currencyDest.setAdapter(currencyAdapter);
            converterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double result = rates.getCurrentyConversion(currencyRef.getSelectedItem().toString(), currencyDest.getSelectedItem().toString(), Double.valueOf(inputNumber.getText().toString()));
                    result = Math.round(result * 100.0) / 100.0;
                    resultView.setText(String.valueOf(result));
                }
            });


            swapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int posCurrencyRef = currencyRef.getSelectedItemPosition();
                    int posCurrencyDest = currencyDest.getSelectedItemPosition();


                    currencyRef.setSelection(posCurrencyDest);
                    currencyDest.setSelection(posCurrencyRef);
                }
            });
        }

    }