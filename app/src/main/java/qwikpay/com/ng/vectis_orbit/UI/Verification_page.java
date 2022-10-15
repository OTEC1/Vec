package qwikpay.com.ng.vectis_orbit.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import qwikpay.com.ng.vectis_orbit.R;
import qwikpay.com.ng.vectis_orbit.UTILS.Javautil;

public class Verification_page extends AppCompatActivity {


    private Button verify;
    private EditText enter_device_serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);

        init_widget();


        verify.setOnClickListener(e -> {
            if (C(enter_device_serial)) {
                enter_device_serial.setText("");
                startActivity(new Intent(getApplicationContext(), Complete_setup.class));
            }else
                new Javautil().Message(this,"Pls fill out the field !");
        });

    }

    private void init_widget() {
        verify = findViewById(R.id.verify);
        enter_device_serial = findViewById(R.id.enter_device_serial);

    }


    private boolean C(EditText enter_device_serial) {
        return enter_device_serial.getText().toString().trim().length() > 0;
    }


}