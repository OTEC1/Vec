package qwikpay.com.ng.vectis_orbit.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import qwikpay.com.ng.vectis_orbit.R;
import qwikpay.com.ng.vectis_orbit.UTILS.Javautil;

public class Registration extends AppCompatActivity {


    private Button parking, toll, vehicle_raw, vehicle_pass, bus, train, Process;
    private EditText enter_device_serial, merchant_id, terminal_name, terminal_location;

    private Spinner spinner;
    private ArrayAdapter array;
    private List<Object> services_type;


    private String TAG = "Registration", service = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        Inits();
        AddToSpinner(services_type);
        Change(1);


        parking.setOnClickListener(w -> {
            Change(1);
        });
        toll.setOnClickListener(w -> {
            Change(2);
        });
        vehicle_pass.setOnClickListener(w -> {
            Change(3);
        });
        vehicle_raw.setOnClickListener(w -> {
            Change(4);
        });
        bus.setOnClickListener(w -> {
            Change(5);
        });
        train.setOnClickListener(w -> {
            Change(6);
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                service = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Process.setOnClickListener(e -> {

            if (C(enter_device_serial) && C(merchant_id) && C(terminal_name) && C(terminal_location))
                if (service.trim().length() > 0 && !service.trim().contains("Pls")) {
                    new Javautil().init(getApplicationContext()).edit().putString(getString(R.string.SERVICE_TYPE),service).apply();
                    enter_device_serial.setText(""); merchant_id.setText(""); terminal_name.setText("");  terminal_location.setText("");
                    startActivity(new Intent(getApplicationContext(), Verification_page.class));
                }else
                    new Javautil().Message(this, "Pls select service type !");
            else
                new Javautil().Message(this, "Pls fill out all fields !");


        });
    }


    private void AddToSpinner(List<Object> body) {
        array = new ArrayAdapter(getApplicationContext(), R.layout.text_pad, body);
        array.setDropDownViewResource(R.layout.text);
        spinner.setAdapter(array);
    }


    private void Change(int n) {
        new Javautil().init(getApplicationContext()).edit().putString(getString(R.string.DEVICE_TYPE), n == 1 ? "PARKING" : n == 2 ? "TOLL" : n == 3 ? "VECTIS_PASS" : n == 4 ? "REWARD" : n == 5 ? "BUS" : n == 6 ? "TRAIN" : "PARKING").apply();
        parking.setBackgroundResource(n == 1 ? R.color.purple_700 : R.drawable.input_purple_border);
        toll.setBackgroundResource(n == 2 ? R.color.purple_700 : R.drawable.input_purple_border);
        vehicle_pass.setBackgroundResource(n == 3 ? R.color.purple_700 : R.drawable.input_purple_border);
        vehicle_raw.setBackgroundResource(n == 4 ? R.color.purple_700 : R.drawable.input_purple_border);
        bus.setBackgroundResource(n == 5 ? R.color.purple_700 : R.drawable.input_purple_border);
        train.setBackgroundResource(n == 6 ? R.color.purple_700 : R.drawable.input_purple_border);
    }


    private void Inits() {
        parking = findViewById(R.id.parking);
        toll = findViewById(R.id.toll);
        vehicle_raw = findViewById(R.id.vehicle_raw);
        vehicle_pass = findViewById(R.id.vehicle_pass);
        bus = findViewById(R.id.bus);
        train = findViewById(R.id.train);
        Process = findViewById(R.id.Process);
        merchant_id = findViewById(R.id.merchant_id);
        enter_device_serial = findViewById(R.id.enter_device_serial);
        terminal_name = findViewById(R.id.terminal_name);
        terminal_location = findViewById(R.id.terminal_location);
        spinner = findViewById(R.id.spinner);
        services_type = new ArrayList<>();
        services_type.add("Pls select service");
        services_type.add("Premium");
        services_type.add("Standard");


    }


    private boolean C(EditText enter_device_serial) {
        return enter_device_serial.getText().toString().trim().length() > 0;
    }
}