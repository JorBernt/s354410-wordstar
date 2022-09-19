package apputvikling.jorber.wordstar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Locale;


public class MainMenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_menu);
        ProgressBar pb = findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);

        Button playButton = findViewById(R.id.playButton);

        Button noLangButton = findViewById(R.id.noLangButton);
        noLangButton.setText(R.string.norwegian);

        noLangButton.setOnClickListener(view -> {
            setLocale(this, "nb");
            playButton.setText(GameManager.instance().isRunning() ? getString(R.string.continue_btn) : getString(R.string.play_btn));
        });

        Button enLangButton = findViewById(R.id.enLangButton);
        enLangButton.setOnClickListener(view -> {
            setLocale(this, "en");
            playButton.setText(GameManager.instance().isRunning() ? getString(R.string.continue_btn) : getString(R.string.play_btn));
        });
        enLangButton.setText(R.string.english);

        playButton.setText(GameManager.instance().isRunning() ? getString(R.string.continue_btn) : getString(R.string.play_btn));
        playButton.setVisibility(View.VISIBLE);
        playButton.setOnClickListener(view -> {
            playButton.setVisibility(View.INVISIBLE);
            noLangButton.setVisibility(View.INVISIBLE);
            enLangButton.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            GameManager.instance().loadWordList(getAssets());
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    // Sets the language of the phone. This function is using some deprecated functions that should be
    // updated.

    //https://stackoverflow.com/questions/56888310/how-to-use-sharedpreferences-to-change-language
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = activity.getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());
    }
}