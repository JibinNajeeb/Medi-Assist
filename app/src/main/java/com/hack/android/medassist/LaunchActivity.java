package com.hack.android.medassist;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hack.android.medassist.interfaces.HttpResponseInterface;
import com.hack.android.medassist.models.PrescriptionModel;
import com.hack.android.medassist.services.SpeechRecognizerService;
import com.hack.android.medassist.utility.RestApiBuilder;

import okhttp3.OkHttpClient;

public class LaunchActivity extends AppCompatActivity {

    private Button btStartService;
    private ISpeechAidlInterface service;
    private TextView recordStatusText;
    private EditText prescriptionText;
    private StringBuilder prescriptionValue;
    private RestApiBuilder restApiBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restApiBuilder = new RestApiBuilder(new OkHttpClient(), httpResponseInterface);
        setContentView(R.layout.activity_launch);
        btStartService = (Button) findViewById(R.id.micButton);
        recordStatusText = (TextView) findViewById(R.id.recordStatus);
        prescriptionText = (EditText) findViewById(R.id.prescriptionText);
        prescriptionValue = new StringBuilder("");
        enableAutoStart();
        if (checkServiceRunning()) {
            btStartService.setText(getString(R.string.stop_service));
            recordStatusText.setVisibility(View.VISIBLE);
        }
    }

    public void onBtnClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this,"ssss", Toast.LENGTH_LONG);
    }

    public void onChatBtnClick(View view) {
        Intent intent = new Intent(this, CustomAlexaActivity.class);
        startActivity(intent);
    }

    public void onMicBtnClicked(View view) {
        Intent intent = new Intent(LaunchActivity.this, SpeechRecognizerService.class);
        if (btStartService.getText().toString().equalsIgnoreCase(getString(R.string.start_service))) {
            startService(intent);
            bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder boundService) {
                    Toast.makeText(LaunchActivity.this, "ssss", Toast.LENGTH_SHORT).show();
                    service = ISpeechAidlInterface.Stub.asInterface((IBinder) boundService);
                    try {
                        service.registerCallback(mCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    try {
                        service.unregisterCallback(mCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, Context.BIND_AUTO_CREATE);
            btStartService.setText(getString(R.string.stop_service));
            recordStatusText.setVisibility(View.VISIBLE);
        } else {
            stopService(intent);
            btStartService.setText(getString(R.string.start_service));
            recordStatusText.setVisibility(View.GONE);
        }
    }

    public void onMedAssistClicked(View view) {
        restApiBuilder.execute("http://54.205.152.174:8000/prescription/get");
       /* String sample = " {prescription: [\n" +
                "        {\n" +
                "            model: \"polls.prescription\",\n" +
                "                    pk: 120,\n" +
                "                fields: {\n" +
                "            medicine: \"paracetamol\",\n" +
                "                    dosage: \"5 mg\",\n" +
                "                    frequency: \"3 times\",\n" +
                "                    duration: \"5 days\"\n" +
                "        }\n" +
                "        },\n" +
                "        {\n" +
                "            model: \"polls.prescription\",\n" +
                "                    pk: 121,\n" +
                "                fields: {\n" +
                "            medicine: \"tylenol\",\n" +
                "                    dosage: null,\n" +
                "                    frequency: null,\n" +
                "                    duration: \"6 days\"\n" +
                "        }\n" +
                "        }\n" +
                "        ]}";

        Intent intent = new Intent(this, PrescriptionActivity.class);
        intent.putExtra("prescription", sample);
        startActivity(intent);*/
        /*[
        {
            model: "polls.prescription",
                    pk: 120,
                fields: {
            medicine: "paracetamol",
                    dosage: "5 mg",
                    frequency: null,
                    duration: "5 days"
        }
        },
        {
            model: "polls.prescription",
                    pk: 121,
                fields: {
            medicine: "tylenol",
                    dosage: null,
                    frequency: null,
                    duration: "6 days"
        }
        }
        ]*/
    }

    public boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if (getString(R.string.my_service_name).equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void enableAutoStart() {

        //For new devices
   /* for (Intent intent : Constants.AUTO_START_INTENTS) {
      if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
        new Builder(this).title(R.string.enable_autostart)
          .content(R.string.ask_permission)
          .theme(Theme.LIGHT)
          .positiveText(getString(R.string.allow))
          .onPositive((dialog, which) -> {
            try {
              for (Intent intent1 : Constants.AUTO_START_INTENTS)
                if (getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY)
                  != null) {
                  startActivity(intent1);
                  break;
                }
            } catch (Exception e) {
              e.printStackTrace();
            }
          })
          .show();
        break;
      }
    }*/
    }
    private ISpeechCallbackAidlInterface mCallback = new ISpeechCallbackAidlInterface.Stub() {
        /**
         * This is called by the remote service regularly to tell us about
         * new values.  Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */
        public void valueChanged(final String value) {
            //mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
//            Toast.makeText(LaunchActivity.this, "callback = "+value, Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prescriptionValue.append(" "+value);
                    prescriptionText.setText(prescriptionValue.toString().trim());
                }
            });
        }
    };

    private HttpResponseInterface httpResponseInterface = new HttpResponseInterface() {
        @Override
        public void getResponseBody(final String responseBody) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    PrescriptionModel prescriptionModel = gson.fromJson(responseBody, PrescriptionModel.class);
                    prescriptionText.setText(responseBody);

                }
            });
            Log.d("LaunchActivity", responseBody);
//            return null;
        }
    };
}
