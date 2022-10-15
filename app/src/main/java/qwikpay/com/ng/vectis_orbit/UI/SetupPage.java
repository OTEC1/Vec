package qwikpay.com.ng.vectis_orbit.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import qwikpay.com.ng.vectis_orbit.R;
import qwikpay.com.ng.vectis_orbit.UTILS.Javautil;


public class SetupPage extends AppCompatActivity implements TextToSpeech.OnInitListener {


    private Button commence, submit;
    private TextToSpeech tts;
    private EditText installation_code;
    private TextView tB, wU;
    private LayoutInflater layoutInflater;
    private AlertDialog alertDialog;
    private View v;


    private String TAG = "SetupPage";
    private String speak = "Welcome to vectis era";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_page);
        if (hasInternet()) {
            init_variables();

            commence.setOnClickListener(e -> {
                    alertDialog.show();
            });


            submit.setOnClickListener(e -> {
                if (C(installation_code)) {
                    installation_code.setText("");
                    startActivity(new Intent(getApplicationContext(), Registration.class));
                }
            });
        } else
            new Javautil().Message(this, "No internet connection detected !");


    }

    private boolean validate_engineer_id(String toString) {
        return false;
    }


    private void init_variables() {
        commence = findViewById(R.id.submit);
        tts = new TextToSpeech(this, this);
        layoutInflater = LayoutInflater.from(this);
        v = layoutInflater.inflate(R.layout.setup_model, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setView(v);
        alertDialog = alBuilder.create();
        tB = findViewById(R.id.top_banner);
        wU = findViewById(R.id.write_up);
        installation_code = v.findViewById(R.id.input4);
        submit = v.findViewById(R.id.activate);
        new Javautil().FontChange(tB, "fonts/nunitosans-semibold.ttf", getApplicationContext());
        new Javautil().FontChange(wU, "fonts/nunitosans-semibold.ttf", getApplicationContext());
    }


    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                tts.speak("Unsupported text format", TextToSpeech.QUEUE_FLUSH, null);
            else
                tts.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    private boolean hasInternet() {
        @SuppressLint("WrongConstant")
        NetworkInfo info = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable()) {
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    private boolean C(EditText enter_device_serial) {
        return enter_device_serial.getText().toString().trim().length() > 0;
    }


}