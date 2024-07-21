package v4lpt.vpt.f005.rsd;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView dice1, dice2;
    private TextView[] resultViews;
    private int[] diceDrawables;
    private Random random;
    private MediaPlayer rollSound;
    private long lastClickTime = 0;
    private static final long CLICK_TIME_INTERVAL = 727;
    private TextView startText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        findViewById(R.id.startAppButton).setOnClickListener(v -> startDiceScreen());
        findViewById(R.id.infoButton).setOnClickListener(v ->  openInfoFragment());
    }

    private void startDiceScreen() {
        setContentView(R.layout.dice_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        initializeViews();
        setupClickListener();
    }
    private void openInfoFragment() {
        InfoFragment infoFragment = new InfoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, infoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void closeInfoFragment() {
        getSupportFragmentManager().popBackStack();
    }

    private void initializeViews() {
        dice1 = findViewById(R.id.dice1);
        dice2 = findViewById(R.id.dice2);
        resultViews = new TextView[]{
                findViewById(R.id.resultNorth),
                findViewById(R.id.resultSouth),
                findViewById(R.id.resultEast),
                findViewById(R.id.resultWest)
        };
        startText = findViewById(R.id.startText);

        diceDrawables = new int[]{
                R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3,
                R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6
        };

        random = new Random();
        rollSound = MediaPlayer.create(this, R.raw.click_001);

        // Initially hide dice and results
        dice1.setVisibility(View.INVISIBLE);
        dice2.setVisibility(View.INVISIBLE);
        for (TextView view : resultViews) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void setupClickListener() {
        findViewById(R.id.diceContainer).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                long clickTime = SystemClock.elapsedRealtime();
                if (clickTime - lastClickTime < CLICK_TIME_INTERVAL) {
                    return true;
                }
                lastClickTime = clickTime;

                if (startText.getVisibility() == View.VISIBLE) {
                    startText.setVisibility(View.GONE);
                    showDiceAndResults();
                } else {
                    rollDice();
                }

                return true;
            }
            return false;
        });
    }

    private void showDiceAndResults() {
        dice1.setVisibility(View.VISIBLE);
        dice2.setVisibility(View.VISIBLE);
        for (TextView view : resultViews) {
            view.setVisibility(View.VISIBLE);
        }
        rollDice();
    }

    private void rollDice() {
        int roll1 = random.nextInt(6) + 1;
        int roll2 = random.nextInt(6) + 1;
        int total = roll1 + roll2;

        dice1.setImageResource(diceDrawables[roll1 - 1]);
        dice2.setImageResource(diceDrawables[roll2 - 1]);

        String resultText = String.valueOf(total);
        if (total == 6 || total == 9) {
            resultText += ".";
        }

        for (TextView view : resultViews) {
            view.setText(resultText);
        }

        if (rollSound != null) {
            rollSound.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rollSound != null) {
            rollSound.release();
            rollSound = null;
        }
    }
}