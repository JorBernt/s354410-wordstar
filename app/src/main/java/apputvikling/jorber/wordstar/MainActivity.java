package apputvikling.jorber.wordstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;


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
        playButton.setText("Play");
        ProgressBar pb = findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);
        playButton.setOnClickListener(view -> {
            playButton.setVisibility(View.INVISIBLE);
            languageMenu.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            GameManager.instance().loadWordList(getAssets());
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });
    }
}