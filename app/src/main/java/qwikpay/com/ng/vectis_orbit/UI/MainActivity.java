package qwikpay.com.ng.vectis_orbit.UI;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import qwikpay.com.ng.vectis_orbit.R;
import qwikpay.com.ng.vectis_orbit.UTILS.Javautil;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private Button qrcode, nfc, plan_based,mainback2;
    private TextView payload, process, time_stamp;
    private ImageView logo;
    private AlertDialog alertDialog;
    private LayoutInflater layoutInflater;
    private RelativeLayout qscan_section, mainback;
    private ProgressBar on_scan;
    private View v;


    private UsbManager usbManager;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;
    Handler h = new Handler();


    public final String ACTION_USB_PERMISSION = "qwikpay.com.ng.vectis.UI.USB_PERMISSION";
    private String TAG = "MainActivity";
    private int n = 0, MY_CAMERA_REQUEST_CODE = 100;
    private boolean write_permission = false;

    @Override
    protected void onResume() {
        super.onResume();
        askPermissions();
    }

    private void askPermissions() {
        int permissionCheckStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheckStorage == PackageManager.PERMISSION_GRANTED)
            write_permission = true;
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You need to give permission to access storage in order to work this feature.");
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Outside();
                    }
                });
                builder.show();
            } else
                Outside();
        }
    }

    private void Outside() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_widget();
        CHECK_DEVICE_STATE();
        askPermissions();



        if (hasInternet()) {
            SetUpUsb();
            CheckUsb();

            qrcode.setOnClickListener(e -> {
                if (hasInternet()) {
                    LoadUI(R.layout.qscan_model);
                    alertDialog.show();
                    Update_time(3000);
                } else
                    new Javautil().Message(this, "No internet connection detected");

            });


            nfc.setOnClickListener(e -> {
                if (hasInternet()) {
                    n = 1;
                    LoadUI(R.layout.nfc_model);
                    alertDialog.show();
                } else
                    new Javautil().Message(this, "No internet connection detected");

            });


            plan_based.setOnClickListener(e -> {
                if (hasInternet()) {
                    if (write_permission)
                        startActivity(new Intent(getApplicationContext(), Screen.class));
                    else
                        askPermissions();
                } else
                    new Javautil().Message(this, "No internet connection detected");

            });


            mainback.setOnClickListener(e->{
                h.removeCallbacksAndMessages(null);
                Log.d(TAG, "onCreate: ");
            });

            
        } else
            new Javautil().Message(this, "No internet connection detected !");


    }


    private boolean hasInternet() {
        @SuppressLint("WrongConstant")
        NetworkInfo info = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable()) {
            return false;
        }
        return true;
    }


    //Model widget
    private void init_widget() {
        qrcode = findViewById(R.id.qrcode);
        nfc = findViewById(R.id.nfc);
        time_stamp = findViewById(R.id.time_stamp);
        plan_based = findViewById(R.id.plan_based);
        mainback = findViewById(R.id.main_background);
        logo = findViewById(R.id.logo);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        Update_time(1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
        }
    }


    //Validating device business logic
    private void CHECK_DEVICE_STATE() {
        if (!new Javautil().init(getApplicationContext()).getBoolean(getString(R.string.ON_AUTHENTICATED), false))
            startActivity(new Intent(getApplicationContext(), SetupPage.class));
        else {
            if (new Javautil().init(getApplicationContext()).getString(getString(R.string.DEVICE_TYPE), null).contains(getString(R.string.DEVICE_TYPE_CHOOSE))) {
                mainback.setBackgroundResource(R.mipmap.main_bkgrd);
                if (new Javautil().init(getApplicationContext()).getString(getString(R.string.SERVICE_TYPE), null).contains(getString(R.string.SERVICE_TYPE_CHOOSE)))
                    plan_based.setVisibility(View.VISIBLE);
            } else {
                mainback.setBackgroundResource(R.mipmap.main_screen_backgrd);
            }
        }
    }


    //RFID bussines logic ---------------START----------------------------
    UsbSerialInterface.UsbReadCallback mCallback = bytes -> {
        String data;
        try {
            data = new String(bytes, "UTF-8");
            data.concat("\n");
            Dumps(data);
        } catch (Exception e) {
            new Javautil().Message(this, e.getLocalizedMessage());
        }
    };


    private void Dumps(String data) {
        if (n == 1) {
            final TextView ftv = payload;
            final CharSequence ftext = data;
            API_CALL(data);

            runOnUiThread((() -> {
                ftv.setText(ftext);
                on_scan.setVisibility(View.INVISIBLE);
            }));
            n++;
        }

    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {

                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);

                    if (serialPort != null) {
                        if (serialPort.open()) {
                            serialPort.setBaudRate(115200);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                        } else
                            message("Post not open");

                    } else
                        message("Post is null");
                } else
                    message("Permission not granted !");
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                Log.d(TAG, "onReceive: Port attached");
                CheckUsb();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))
                message("Port is closed !");
        }
    };

    private void message(String post_not_open) {
        new Javautil().Message(this, post_not_open);
    }


    private void CheckUsb() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                //Log.d(TAG, "CheckUsb: "+deviceVID);
                if (deviceVID == 6790) {
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        } else
            new Javautil().Message(this, "OTG port is empty");

    }


    private void SetUpUsb() {
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }


    //QR_SCAN -----------------------------START----------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (String.valueOf(data).contains("Intent {  }") || String.valueOf(data).contains("null"))
            Log.d(TAG, "none");
        else {
            if (data.getAction().contains("android.SCAN")) {
                IntentResult res = IntentIntegrator.parseActivityResult(resultCode, data);
                if (res.getContents() != null) {
                    API_CALL(res.getContents());
                    qscan_section.setBackgroundColor(Color.WHITE);
                    process.append("Processing...");
                    process.append("\n" + res.getContents());
                    process.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void API_CALL(String data) {
        Log.d(TAG, "API  payload: " + data);
    }


    //-----------------------UI BUILD---------------------------------
    private void LoadUI(int nfc_model) {
        layoutInflater = LayoutInflater.from(this);
        v = layoutInflater.inflate(nfc_model, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setView(v);
        payload = v.findViewById(R.id.payload);
        qscan_section = v.findViewById(R.id.qscan_section);
        process = v.findViewById(R.id.process);
        on_scan = v.findViewById(R.id.on_scan);
        mainback2 = v.findViewById(R.id.activate);
        alertDialog = alBuilder.create();
        alertDialog.setCancelable(false);


        mainback2.setOnClickListener(e->{
            h.removeCallbacksAndMessages(null);
            alertDialog.hide();
            Log.d(TAG, "LoadUI: ");
        });
    }




    private void Update_time(int i) {
        Log.d(TAG, "Update_time: "+i);
        if (i == 1) {
            TimerTask task = new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        GET_TIME();
                    });
                }
            };
            Timer time = new Timer();
            time.schedule(task, 0, 10000);
        } else {
            h.postDelayed(this::Scan_qr_code, i);
    }
    }

    private void Scan_qr_code() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void GET_TIME() {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        time_stamp.setText(date);
    }


}



















