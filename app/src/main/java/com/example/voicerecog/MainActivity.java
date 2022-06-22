package com.example.voicerecog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.voicerecog.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private SpeechRecognizer speechRecognizer;
    private int count = 0;
    private String one, two;
    private int a, b;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        activityMainBinding.refresh.setOnClickListener(view -> {
            Random random = new Random();
            a = random.nextInt(9) + 1;
            activityMainBinding.one.setText(String.valueOf(a));

            b = random.nextInt(9) + 1;
            activityMainBinding.two.setText(String.valueOf(b));
        });

        textToSpeech = new TextToSpeech(this, i -> {
            if (i == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("TextToSpeech", "Language Not Supported");
                } else activityMainBinding.read.setEnabled(true);
            }
        });

        activityMainBinding.read.setOnClickListener(view -> {
            one = activityMainBinding.one.getText().toString();
            two = activityMainBinding.two.getText().toString();
            read(one + "into" + two); //function for reading
        });

        //User input
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        activityMainBinding.imgbutton.setOnClickListener(view -> {
                if (count == 0) {
                    activityMainBinding.imgbutton.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.mic_off));
                    activityMainBinding.text.setText("Done Answering ? Switch off the mic!");
                    count = 1;
                    speechRecognizer.startListening(intent);

                } else {
                    activityMainBinding.imgbutton.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this,R.drawable.mic_on));
                    activityMainBinding.text.setText("Want to say the answer? Switch on the mic!");
                    count = 0;
                    speechRecognizer.stopListening();
                }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (Objects.equals(data.get(0), String.valueOf(a * b)))
                    read("Yay! correct answer");
                else
                    read("Oops! Wrong answer");
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "This App requires permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void read(String text) {
        textToSpeech.setSpeechRate(0.6f);
        textToSpeech.setPitch(0.9f);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}