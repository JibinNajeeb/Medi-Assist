package com.hack.android.medassist.services;

import android.Manifest.permission;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hack.android.medassist.ISpeechAidlInterface;
import com.hack.android.medassist.ISpeechCallbackAidlInterface;
import com.hack.android.medassist.R;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import rx.functions.Action1;

public class SpeechRecognizerService extends Service implements SpeechDelegate, Speech.stopDueToDelay {

  public static SpeechDelegate delegate;
  public final RemoteCallbackList<ISpeechCallbackAidlInterface> mCallbacks = new RemoteCallbackList<ISpeechCallbackAidlInterface>();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    //TODO do something useful
    try {
      if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
        ((AudioManager) Objects.requireNonNull(
          getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    Speech.init(this);
    delegate = this;
    Speech.getInstance().setListener(this);

    if (Speech.getInstance().isListening()) {
      Speech.getInstance().stopListening();
      muteBeepSoundOfRecorder();
    } else {
      System.setProperty("rx.unsafe-disable", "True");
      /*RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
        if (granted) { // Always true pre-M
          try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(null, this);
          } catch (SpeechRecognitionNotAvailable exc) {
            //showSpeechNotSupportedDialog();

          } catch (GoogleVoiceTypingDisabledException exc) {
            //showEnableGoogleVoiceTyping();
          }
        } else {
          Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        }
      });*/
    RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(new Action1<Boolean>() {
        @Override
        public void call(Boolean granted) {
            if (granted) { // Always true pre-M
                try {
                    Speech.getInstance().stopTextToSpeech();
                    Speech.getInstance().startListening(null, SpeechRecognizerService.this);
                } catch (SpeechRecognitionNotAvailable exc) {
                    //showSpeechNotSupportedDialog();

                } catch (GoogleVoiceTypingDisabledException exc) {
                    //showEnableGoogleVoiceTyping();
                }
            } else {
                //Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
            }
        }
    });
      muteBeepSoundOfRecorder();
    }
    return Service.START_STICKY;
  }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return new ISpeechAidlInterface.Stub() {
            @Override
            public String testFunction(String testInput) throws RemoteException {
//              Log.i(TAG, String.format("AddService.add(%d, %d)",ValueFirst, valueSecond));
                int N = mCallbacks.beginBroadcast();
                for (int i = 0; i < 1; i++) {
                    try {
                        mCallbacks.getBroadcastItem(i).valueChanged(testInput);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mCallbacks.finishBroadcast();
                return "test String";
            }

            @Override
            public void registerCallback(ISpeechCallbackAidlInterface cb) throws RemoteException {
                if (cb != null)
                    mCallbacks.register(cb);
            }

            @Override
            public void unregisterCallback(ISpeechCallbackAidlInterface cb) throws RemoteException {
                if (cb != null)
                    mCallbacks.unregister(cb);
            }
        };
    }

  @Override
  public void onStartOfSpeech() {
  }

  @Override
  public void onSpeechRmsChanged(float value) {

  }

  @Override
  public void onSpeechPartialResults(List<String> results) {
    for (String partial : results) {
      Log.d("Result", partial+"");
    }
  }

  @Override
  public void onSpeechResult(String result) {
    Log.d("Result", result+"");
    if (!TextUtils.isEmpty(result)) {
      Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < 1; i++) {
            try {
                mCallbacks.getBroadcastItem(i).valueChanged(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }
  }

  @Override
  public void onSpecifiedCommandPronounced(String event) {
    try {
      if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
        ((AudioManager) Objects.requireNonNull(
          getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (Speech.getInstance().isListening()) {
      muteBeepSoundOfRecorder();
      Speech.getInstance().stopListening();
    } else {
    RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(new Action1<Boolean>() {
        @Override
        public void call(Boolean granted) {
            if (granted) { // Always true pre-M
                try {
                    Speech.getInstance().stopTextToSpeech();
                    Speech.getInstance().startListening(null, SpeechRecognizerService.this);
                } catch (SpeechRecognitionNotAvailable exc) {
                    //showSpeechNotSupportedDialog();

                } catch (GoogleVoiceTypingDisabledException exc) {
                    //showEnableGoogleVoiceTyping();
                }
            } else {
               // Toast.makeText(this,"Please grant the permission to use the microphone", Toast.LENGTH_LONG).show();
            }
        }
    });
      /*RxPermissions.getInstance(this).request(permission.RECORD_AUDIO).subscribe(granted -> {
        if (granted) { // Always true pre-M
          try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(null, this);
          } catch (SpeechRecognitionNotAvailable exc) {
            //showSpeechNotSupportedDialog();

          } catch (GoogleVoiceTypingDisabledException exc) {
            //showEnableGoogleVoiceTyping();
          }
        } else {
          Toast.makeText(this,"Please grant the permission to use the microphone", Toast.LENGTH_LONG).show();
        }
      });*/
      muteBeepSoundOfRecorder();
    }
  }

  /**
   * Function to remove the beep sound of voice recognizer.
   */
  private void muteBeepSoundOfRecorder() {
    AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    if (amanager != null) {
      amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
      amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
      amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
      amanager.setStreamMute(AudioManager.STREAM_RING, true);
      amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
    }
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    //Restarting the service if it is removed.
    PendingIntent service =
      PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
        new Intent(getApplicationContext(), SpeechRecognizerService.class), PendingIntent.FLAG_ONE_SHOT);

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    assert alarmManager != null;
    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    super.onTaskRemoved(rootIntent);
  }
}