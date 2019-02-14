package com.hack.android.medassist;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hack.android.alexa.VoiceHelper;
import com.hack.android.alexa.data.Directive;
import com.hack.android.alexa.interfaces.CustomJsonResponse;
import com.hack.android.medassist.actions.BaseListenerFragment;
import com.hack.android.medassist.chatBot.adapter.MessageAdapter;
import com.hack.android.medassist.chatBot.model.ResponseMessage;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ee.ioc.phon.android.speechutils.Log;

public class ChatFragment extends BaseListenerFragment {
    //extends AppCompatActivity {

    EditText userInput;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;
    CustomJsonResponse customResponseGetter = new CustomJsonResponse() {
        @Override
        public void jsonText(final Directive directive) {
//            Toast.makeText(getContext(), "jsonText", Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    String str = "";
                    String msg = directive.getPayload().getTextField();
                    String imgUrl = directive.getPayload().getImage().getSources().get(0).getUrl();
                    ResponseMessage responseMessage2 = new ResponseMessage(msg, false, imgUrl);
                    responseMessageList.add(responseMessage2);
                    messageAdapter.notifyDataSetChanged();
                    if (!isLastVisible())
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
            });
        }
    };
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        userInput = (EditText) findViewById(R.id.userInput);
        recyclerView = (RecyclerView) findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(messageAdapter);

        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    ResponseMessage responseMessage = new ResponseMessage(userInput.getText().toString(), true);
                    responseMessageList.add(responseMessage);
                   *//* ResponseMessage responseMessage2 = new ResponseMessage(userInput.getText().toString(), false);
                    responseMessageList.add(responseMessage2);*//*
                    messageAdapter.notifyDataSetChanged();
                    if (!isLastVisible())
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
                return false;
            }
        });
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        VoiceHelper.getInstance(getContext());
        return inflater.inflate(R.layout.activity_chat_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        alexaManager.setAVSResponseGetter(customResponseGetter);
        userInput = (EditText) view.findViewById(R.id.userInput);
        recyclerView = (RecyclerView) view.findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(messageAdapter);


        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    String text = userInput.getText().toString();
                    ResponseMessage responseMessage = new ResponseMessage(text, true);
                    responseMessageList.add(responseMessage);

                    /*AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

                    int current_volume =mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    //If you want to player is mute ,then set_volume variable is zero.Otherwise you may supply some value.
                    int set_volume=0;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,set_volume, 0);*/

                    alexaManager.sendTextRequest("Ask Medi Assist "+text, getRequestCallback());

                    messageAdapter.notifyDataSetChanged();
                    if (!isLastVisible())
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
                return false;
            }
        });
        ResponseMessage responseMessage = new ResponseMessage("What can I do for you", false);
        responseMessageList.add(responseMessage);
        messageAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDetach() {
//        customResponseGetter = null;
        super.onDetach();
    }

    @Override
    public void startListening() {
        /*search.setText("");
        search.requestFocus();*/
    }


    @Override
    protected String getTitle() {
        return getString(R.string.fragment_action_send_text);
    }

    @Override
    protected int getRawCode() {
        return R.raw.code_text;
    }

    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems);
    }

}

