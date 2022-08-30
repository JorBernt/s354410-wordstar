package apputvikling.jorber.wordstar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_menu);

        Spinner languageMenu = findViewById(R.id.languageSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        languageMenu.setAdapter(adapter);

        Button playButton = findViewById(R.id.playButton);
        playButton.setText(GameManager.instance().isRunning() ? "Continue" : "Play");
        ProgressBar pb = findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
        languageMenu.setVisibility(View.VISIBLE);
        playButton.setOnClickListener(view -> {
            setLocale(this, languageMenu.getSelectedItem().equals("Norsk") ? "nb" : "en");
            playButton.setVisibility(View.INVISIBLE);
            languageMenu.setVisibility(View.INVISIBLE);
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

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = activity.getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());
    }
}