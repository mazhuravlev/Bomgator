package ru.com.rh.bomgator;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextWatcher, TextToSpeech.OnInitListener {

    private TextView bomgSay;
    private EditText whatBomgirovat;

    private TextToSpeech textToSpeech;

    private boolean canSpeach;
    private boolean isValid;

    private final static String REG_RUS_LETTERS = "^[а-яА-Яё]+$";
    private final static String RU_VOVELS = "ауоыиэяюёе";
    private final static String RU_BOMG = "БОМЖ";

    private final static float VOICE_PITCH = 0.1f;
    private final static float VOICE_RATE = 0.9f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeech = new TextToSpeech(this, this);

        bomgSay = findViewById(R.id.bomgSay);
        bomgSay.setText(R.string.start_message);

        whatBomgirovat = findViewById(R.id.whatBomgirovat);
        whatBomgirovat.addTextChangedListener(this);

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();

        if (text.isEmpty()) {
            bomgSay.setText(R.string.start_message);
            isValid = false;
        } else if (!text.matches(REG_RUS_LETTERS)) {
            whatBomgirovat.setError(getString(R.string.wrong_input_tip));
            bomgSay.setText(R.string.wrong_imput_msg);
            isValid = false;
        } else {
            bomgSay.setText(R.string.click_on_face_msg);
            isValid = true;
        }
    }

    public void onFaceClick(View v) {
        if (isValid) {
            String bomgMessage = bomgirovat(whatBomgirovat.getText().toString());
            bomgSay.setText(bomgMessage);
            if (canSpeach) {
                textToSpeech.speak(bomgMessage, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    private String bomgirovat(String input) {
        int position = getSecondVovelPosition(input);
        return RU_BOMG + input.substring(position).toUpperCase() + "!";
    }

    private int getSecondVovelPosition(String input) {
        int position = 0;
        if (input == null || input.isEmpty()) return position;

        char[] charArray = input.toLowerCase().toCharArray();

        for (int i = 1; i < charArray.length; i++) {
            if (RU_VOVELS.indexOf(charArray[i]) >= 0) {
                position = i;
                break;
            }
        }

        return position;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");

            textToSpeech.setPitch(VOICE_PITCH);
            textToSpeech.setSpeechRate(VOICE_RATE);

            int result = textToSpeech.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
                canSpeach = false;
            } else {
                canSpeach = true;
            }

        } else {
            canSpeach = false;
            Log.e("TTS", "Ошибка!");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // not implemented
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // not implemented
    }


}
