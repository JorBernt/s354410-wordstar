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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageMenu.setAdapter(adapter);

        Button playButton = findViewById(R.id.playButton);
        playButton.setText(GameManager.instance().isRunning() ? "Continue" : "Play");
        ProgressBar pb = findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
        languageMenu.setVisibility(View.VISIBLE);
        playButton.setOnClickListener(view -> {
            if(languageMenu.getSelectedItem().equals("Norsk")) {
                setLocale(this, "nb_NO");
            }
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
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}