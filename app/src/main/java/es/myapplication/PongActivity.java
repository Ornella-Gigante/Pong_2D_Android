package es.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PongActivity extends AppCompatActivity {

    private GameThread mGameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pong);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final PongTable table = (PongTable) findViewById(R.id.pongTable);
        table.setmScoreOpponent((TextView) findViewById(R.id.tvScoreOpponent));
        table.setmScorePlayer((TextView) findViewById(R.id.tvScorePlayer));
        table.setmStatus((TextView) findViewById(R.id.tvStatus));

        mGameThread = table.getGame();
    }
}