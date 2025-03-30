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

        // Retrieve the PongTable instance from the layout using its ID.
        // This table serves as the main game surface.
        final PongTable table = (PongTable) findViewById(R.id.pongTable);

        // Link the TextView for the opponent's score to the PongTable.
        // This allows the table to update and display the opponent's score dynamically.
        table.setmScoreOpponent((TextView) findViewById(R.id.tvScoreOpponent));

        // Link the TextView for the player's score to the PongTable.
        // This allows the table to update and display the player's score dynamically.
        table.setmScorePlayer((TextView) findViewById(R.id.tvScorePlayer));

        // Link the TextView for the game status to the PongTable.
        // This allows the table to update and display game status messages dynamically.
        table.setmStatus((TextView) findViewById(R.id.tvStatus));

        // Retrieve the GameThread instance from the PongTable.
        // This thread manages game logic, including updates and rendering.
        mGameThread = table.getGame();
    }
}