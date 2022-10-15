package qwikpay.com.ng.vectis_orbit.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import qwikpay.com.ng.vectis_orbit.R;
import qwikpay.com.ng.vectis_orbit.UTILS.Javautil;

public class Complete_setup extends AppCompatActivity {

    private Button verify;


    private  String TAG = "Complete_setup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_setup);
        init_widget();

        verify.setOnClickListener(e->{
            new Javautil().init(getApplicationContext()).edit().putBoolean(getString(R.string.ON_AUTHENTICATED), true).apply();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });

    }

    private void init_widget() {
        verify = findViewById(R.id.verify);
    }
}