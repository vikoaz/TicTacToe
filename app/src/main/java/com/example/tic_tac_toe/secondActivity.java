package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class secondActivity extends AppCompatActivity implements View.OnClickListener {

    String xPlayer, oPlayer;
    RadioGroup rgMode;
    RadioButton rbHumanVsHuman,rbHumanVsBot;
    EditText etXPlayer, etOPlayer;
    Button btStart, btAbout;

    boolean bot = false;

    private final String FILE_NAME =  "details";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getSupportActionBar().hide();

        rgMode = findViewById(R.id.rgMode);
        rbHumanVsHuman = findViewById(R.id.rbHumanVsHuman);
        rbHumanVsBot = findViewById(R.id.rbHumanVsBot);
        etXPlayer = findViewById(R.id.etXPlayer);
        etOPlayer = findViewById(R.id.etOPlayer);
        btStart = findViewById(R.id.btStart);
        btAbout = findViewById(R.id.btAbout);

        btStart.setOnClickListener(this);
        btAbout.setOnClickListener(this);

        etXPlayer.setTextColor(Color.WHITE);
        etOPlayer.setTextColor(Color.WHITE);

        LoadDetails();

        rbHumanVsBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etOPlayer.setText("Bot");
                etOPlayer.setEnabled(false);
                rbHumanVsBot.setClickable(false);
                rbHumanVsHuman.setClickable(true);
            }
        });
        rbHumanVsHuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etOPlayer.getText().toString().equals("Bot")) {
                    etOPlayer.setText("");
                }
                etOPlayer.setEnabled(true);
                rbHumanVsHuman.setClickable(false);
                rbHumanVsBot.setClickable(true);
            }
        });

        Intent backIntent = getIntent();

        if (backIntent.getExtras() != null){
            xPlayer = backIntent.getStringExtra("key_x_player");
            oPlayer = backIntent.getStringExtra("key_o_player");

            etXPlayer.setText(xPlayer);
            etOPlayer.setText(oPlayer);

            if (backIntent.getBooleanExtra("key_human_vs_human", false)) {
                rbHumanVsHuman.toggle();
            } else if (backIntent.getBooleanExtra("key_human_vs_bot", false)) {
                rbHumanVsBot.toggle();
                etOPlayer.setTextColor(Color.WHITE);
                etOPlayer.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btAbout){
            Toast.makeText(this, "Viko's Tic Tac Toe", Toast.LENGTH_SHORT).show();
        }
        if (view == btStart){
            if (etXPlayer.getText().toString().equals("")) {
                Toast.makeText(this, "Can't start the game please enter name.", Toast.LENGTH_SHORT).show();
            } else if (etOPlayer.getText().toString().equals("")) {
                Toast.makeText(this, "Can't start the game please enter name.", Toast.LENGTH_SHORT).show();
            } else if (!rbHumanVsHuman.isChecked() && !rbHumanVsBot.isChecked()) {
                Toast.makeText(this, "Can't start the game please choose mode.", Toast.LENGTH_SHORT).show();
            } else {
                Intent startIntent = new Intent(secondActivity.this, MainActivity.class);

                startIntent.putExtra("key_x_player", etXPlayer.getText().toString());
                startIntent.putExtra("key_o_player", etOPlayer.getText().toString());
                startIntent.putExtra("key_human_vs_human", rbHumanVsHuman.isChecked());
                startIntent.putExtra("key_human_vs_bot", rbHumanVsBot.isChecked());
                if (rbHumanVsBot.isChecked()) {
                    bot = true;
                }

                else if (rbHumanVsBot.isChecked())
                    bot = false;

                startIntent.putExtra("key_bot", bot);

                startActivity(startIntent);
                finish();
                SharedPreferences sp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();

                edit.putString("Key_x_player", etXPlayer.getText().toString());
                edit.putString("Key_o_player", etOPlayer.getText().toString());

                edit.putBoolean("Key_bot", bot);

                edit.apply();
            }
        }
    }

    private void LoadDetails() {
        SharedPreferences sp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        etXPlayer.setText(sp.getString("Key_x_player", ""));
        etOPlayer.setText(sp.getString("Key_o_player", ""));

        if(!sp.getBoolean("Key_bot", false))
            rbHumanVsHuman.toggle();
        else
            rbHumanVsBot.toggle();
    }
}
