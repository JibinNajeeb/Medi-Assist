package com.hack.android.medassist.actions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.hack.android.alexa.VoiceHelper;
import com.hack.android.alexa.data.Directive;
import com.hack.android.alexa.interfaces.CustomJsonResponse;
import com.hack.android.alexa.requestbody.DataRequestBody;
import com.hack.android.medassist.R;
import com.hack.android.medassist.chatBot.model.ResponseMessage;
import com.hack.android.recorderview.RecorderView;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import ee.ioc.phon.android.speechutils.RawAudioRecorder;
import okio.BufferedSink;


/**
 * @author will on 5/30/2016.
 */

public class SendAudioActionFragment extends BaseListenerFragment {

    private static final String TAG = "SendAudioActionFragment";

    private final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int AUDIO_RATE = 16000;
    private RawAudioRecorder recorder;
    private RecorderView recorderView;
    private VoiceHelper mVoiceHelper;
    EditText search;
    View button;
    ImageView alexaSpeechImage;
    CustomJsonResponse customResponseGetter = new CustomJsonResponse() {
        @Override
        public void jsonText(final Directive directive) {
//            Toast.makeText(getContext(), "jsonText", Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    String str = "";
                    String msg = directive.getPayload().getTextField();
                    String imgUrl = directive.getPayload().getImage().getSources().get(0).getUrl();
                    Picasso.with(getContext()).load(imgUrl).resize(200,250).into(alexaSpeechImage);
                }
            });
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVoiceHelper =  VoiceHelper.getInstance(getContext(), mInitListener);

        return inflater.inflate(R.layout.fragment_action_audio, container, false);
    }

    private TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if(status == TextToSpeech.SUCCESS){
                mVoiceHelper.setInialized(true);
                alexaManager.sendTextRequest("Open Medi Assist ",getRequestCallback());
            }else{
                new IllegalStateException("Unable to initialize Text to Speech engine").printStackTrace();
            }
        }
    };
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        alexaManager.setAVSResponseGetter(customResponseGetter);
        super.onViewCreated(view, savedInstanceState);
        if(mVoiceHelper.getInialized())
            alexaManager.sendTextRequest("Open Medi Assist ",getRequestCallback());

        recorderView = (RecorderView) view.findViewById(R.id.recorder);
        alexaSpeechImage = (ImageView) view.findViewById(R.id.alexaSpeechImage);
        recorderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder == null) {
                    startListening();
                }else{
                    stopListening();
                }
            }
        });

        search = (EditText) view.findViewById(R.id.searchEditText);
        button = view.findViewById(R.id.buttonSearchAlexa);

        search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    search();
                    return true;
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void search(){
        String text = search.getText().toString();
        alexaManager.sendTextRequest(text, getRequestCallback());
    }



    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void onDetach() {
//        customResponseGetter = null;
        super.onDetach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                }
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //tear down our recorder on stop
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public void startListening() {
        if(recorder == null){
            recorder = new RawAudioRecorder(AUDIO_RATE);
        }
        recorder.start();
        alexaManager.sendAudioRequest(requestBody, getRequestCallback());
    }

    private DataRequestBody requestBody = new DataRequestBody() {
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            while (recorder != null && !recorder.isPausing()) {
                if(recorder != null) {
                    final float rmsdb = recorder.getRmsdb();
                    if(recorderView != null) {
                        recorderView.post(new Runnable() {
                            @Override
                            public void run() {
                                recorderView.setRmsdbLevel(rmsdb);
                            }
                        });
                    }
                    if(sink != null && recorder != null) {
                        sink.write(recorder.consumeRecording());
                    }
                        Log.i(TAG, "Received audio");
                        Log.i(TAG, "RMSDB: " + rmsdb);

                }

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopListening();
        }

    };

    private void stopListening(){
        if(recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    protected String getTitle() {
        return getString(R.string.fragment_action_send_audio);
    }

    @Override
    protected int getRawCode() {
        return R.raw.code_audio;
    }


}
