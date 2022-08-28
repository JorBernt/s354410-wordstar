package apputvikling.jorber.wordstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main_menu);
        Button playButton = findViewById(R.id.playButton);
        playButton.setText("Play");
        ProgressBar pb = findViewById(R.id.loadingBar);
        pb.setVisibility(View.INVISIBLE);
        playButton.setOnClickListener(view -> {
            playButton.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            GameManager.instance().loadWordList(getAssets());
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });
    }
}