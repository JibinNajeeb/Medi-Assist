package com.hack.android.medassist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hack.android.medassist.actions.ActionsFragment;
import com.hack.android.medassist.chatBot.adapter.MessageAdapter;
import com.hack.android.medassist.chatBot.model.ResponseMessage;

import java.util.ArrayList;
import java.util.List;

import ee.ioc.phon.android.speechutils.Log;

import static com.hack.android.medassist.R.id.customframe;

public class CustomAlexaActivity extends BaseActivity  implements ActionsFragment.ActionFragmentInterface, FragmentManager.OnBackStackChangedListener{

    EditText userInput;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;
    private final static String TAG_FRAGMENT = "CurrentFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        ChatFragment fragment = new ChatFragment();
        loadFragment(fragment, false);
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(customframe, fragment, TAG_FRAGMENT);
        if(addToBackStack){
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    @Override
    protected void startListening() {
        Log.i("CustomAlexa","startListening");
        Toast.makeText(this, "startListening", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void stateListening() {
        Log.i("CustomAlexa","stateListening");
        Toast.makeText(this, "stateListening", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void stateProcessing() {
        Log.i("CustomAlexa","stateProcessing");
        Toast.makeText(this, "stateProcessing", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void stateSpeaking() {
        Log.i("CustomAlexa","stateSpeaking");
        Toast.makeText(this, "stateSpeaking", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void stateFinished() {
        Log.i("CustomAlexa","stateFinished");
        Toast.makeText(this, "stateFinished", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void statePrompting() {
        Log.i("CustomAlexa","statePrompting");
        Toast.makeText(this, "statePrompting", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void stateNone() {
        Log.i("CustomAlexa","stateNone");
        Toast.makeText(this, "stateNone", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackStackChanged() {
        Log.i("CustomAlexa","onBackStackChanged");
        Toast.makeText(this, "onBackStackChanged", Toast.LENGTH_SHORT).show();
    }
}


