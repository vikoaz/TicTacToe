package com.example.tic_tac_toe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Random rnd = new Random();

    String xPlayer, oPlayer;
    TextView tvXPlayer, tvOPlayer, tvTurn;
    Button[] arrayOfButtons = new Button[9];
    private final ArrayList <int[]> winPoints = new ArrayList<>();
    Button btNewGame, btBack;
    Intent startIntent;
    int counter = 0, player = 0;
    boolean bot, turn = false;
    int[] virtualPositions = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    static int counterX = 0;
    static int counterO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        /*winPositions {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}}*/
        winPoints.add(new int[] {0, 1, 2});
        winPoints.add(new int[] {3, 4, 5});
        winPoints.add(new int[] {6, 7, 8});
        winPoints.add(new int[] {0, 3, 6});
        winPoints.add(new int[] {1, 4, 7});
        winPoints.add(new int[] {2, 5, 8});
        winPoints.add(new int[] {0, 4, 8});
        winPoints.add(new int[] {2, 4, 6});

        tvXPlayer = findViewById(R.id.tvXPlayer);
        tvOPlayer = findViewById(R.id.tvOPlayer);
        tvTurn = findViewById(R.id.tvTurn);

        arrayOfButtons[0] = (Button) findViewById(R.id.btTTT0);
        arrayOfButtons[1] = (Button) findViewById(R.id.btTTT1);
        arrayOfButtons[2] = (Button) findViewById(R.id.btTTT2);
        arrayOfButtons[3] = (Button) findViewById(R.id.btTTT3);
        arrayOfButtons[4] = (Button) findViewById(R.id.btTTT4);
        arrayOfButtons[5] = (Button) findViewById(R.id.btTTT5);
        arrayOfButtons[6] = (Button) findViewById(R.id.btTTT6);
        arrayOfButtons[7] = (Button) findViewById(R.id.btTTT7);
        arrayOfButtons[8] = (Button) findViewById(R.id.btTTT8);


        btNewGame = findViewById(R.id.btNewGame);
        btBack = findViewById(R.id.btBack);


        for (Button arrayOfButton : arrayOfButtons) {
            arrayOfButton.setOnClickListener(this);
            arrayOfButton.setTextColor(Color.WHITE);
        }
        btNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder newGameDialog = new AlertDialog.Builder(MainActivity.this);

                newGameDialog.setMessage("Are you sure that you want start new game");
                newGameDialog.setCancelable(false);

                newGameDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Starting new game", Toast.LENGTH_SHORT).show();

                        resetGame();
                    }
                });
                newGameDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Ok stay!", Toast.LENGTH_SHORT).show();
                    }
                });

                newGameDialog.show();
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(MainActivity.this, secondActivity.class);

                backIntent.putExtra("key_x_player", xPlayer);
                backIntent.putExtra("key_o_player", oPlayer);
                backIntent.putExtra("key_human_vs_human", startIntent.getBooleanExtra("key_human_vs_human", false));
                backIntent.putExtra("key_human_vs_bot", startIntent.getBooleanExtra("key_human_vs_bot", false));

                AlertDialog.Builder backDialog = new AlertDialog.Builder(MainActivity.this);

                backDialog.setMessage("Are you sure that you want back");
                backDialog.setCancelable(false);

                backDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Ok go back!", Toast.LENGTH_SHORT).show();
                        counterX = 0;
                        counterO = 0;
                        startActivity(backIntent);
                        finish();
                    }
                });
                backDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Ok stay!", Toast.LENGTH_SHORT).show();
                    }
                });

                backDialog.show();
            }
        });

        initButtons();

        startIntent = getIntent();

        if (startIntent.getExtras() != null){
            xPlayer = startIntent.getStringExtra("key_x_player");
            tvXPlayer.setText("Player X: " + xPlayer + ":" + counterX);
            oPlayer = startIntent.getStringExtra("key_o_player");
            tvOPlayer.setText("Player O: " +oPlayer + ":" + counterO);

            bot = startIntent.getBooleanExtra("key_bot", false);
        }
        if(tvXPlayer.equals("")){
            tvXPlayer.setHint("Player X: no name");
        }
        if(tvOPlayer.equals("")){
            tvOPlayer.setHint("Player O: no name");
        }
    }

    @Override
    public void onClick(View view) {
        Handler handler = new Handler();
        Button b = (Button) view;
        counter++;

        if (!turn) {
            b.setText("X");
            tvTurn.setText("Turn: O");
            b.setTextColor(Color.parseColor("#45E3FF"));
            player = 0;
            turn = true;
            b.setClickable(false);
            virtualPositions[buttonPlace(b)] = player;
        } else if(!bot && turn) {
            b.setText("O");
            tvTurn.setText("Turn: X");
            b.setTextColor(Color.parseColor("#F7A400"));
            player = 1;
            turn = false;
            b.setClickable(false);
            virtualPositions[buttonPlace(b)] = player;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                while (counter < 9 && bot && turn && !checkIfSomeoneWin(virtualPositions,player) && ifTwo()) {
            /*int rand = rnd.nextInt(9);
            if (virtualPositions[rand] == 2) {
                arrayOfButtons[rand].setText("O");
                tvTurn.setText("Turn: X");
                arrayOfButtons[rand].setTextColor(Color.parseColor("#F7A400"));
                player = 1;
                virtualPositions[buttonPlace(arrayOfButtons[rand])] = player;

                turn = false;
                arrayOfButtons[rand].setClickable(false);
                break;
            } else rand = rnd.nextInt(9);*/
                    int nextMove = bestMove(virtualPositions);
                    if (virtualPositions[nextMove] == 2) {
                        arrayOfButtons[nextMove].setText("O");
                        tvTurn.setText("Turn: X");
                        arrayOfButtons[nextMove].setTextColor(Color.parseColor("#F7A400"));
                        player = 1;
                        virtualPositions[buttonPlace(arrayOfButtons[nextMove])] = player;

                        turn = false;
                        arrayOfButtons[nextMove].setClickable(false);
                        break;
                    } else nextMove = bestMove(virtualPositions);
                }
                // check if someone wins
                if (checkIfSomeoneWin(virtualPositions, player)) {
                    setClickableFalse();
                    dialogOfWinner();
                }/* check if tie */ else if (!checkIfSomeoneWin(virtualPositions,player) && !ifTwo()) {
                    setClickableFalse();
                    dialogOfWinner();
                }
            }
        }, 200);
    }

    private Button convertIntToButton(int button) {
        switch (button) {
            case 0:
                return arrayOfButtons[0];
            case 1:
                return arrayOfButtons[1];
            case 2:
                return arrayOfButtons[2];
            case 3:
                return arrayOfButtons[3];
            case 4:
                return arrayOfButtons[4];
            case 5:
                return arrayOfButtons[5];
            case 6:
                return arrayOfButtons[6];
            case 7:
                return arrayOfButtons[7];
            case 8:
                return arrayOfButtons[8];
        }
        return null;
    }

    private int buttonPlace(Button button){
        if (arrayOfButtons[0].equals(button)) {
            return 0;
        } else if (arrayOfButtons[1].equals(button)) {
            return 1;
        } else if (arrayOfButtons[2].equals(button)) {
            return 2;
        } else if (arrayOfButtons[3].equals(button)) {
            return 3;
        } else if (arrayOfButtons[4].equals(button)) {
            return 4;
        } else if (arrayOfButtons[5].equals(button)) {
            return 5;
        } else if (arrayOfButtons[6].equals(button)) {
            return 6;
        } else if (arrayOfButtons[7].equals(button)) {
            return 7;
        } else if (arrayOfButtons[8].equals(button)) {
            return 8;
        }else{
            return 0;
        }
    }

    private void makeGlowX(int b1, int b2, int b3){
        convertIntToButton(b1).setTextColor(Color.GREEN);
        convertIntToButton(b2).setTextColor(Color.GREEN);
        convertIntToButton(b3).setTextColor(Color.GREEN);
    }

    private void makeGlowO(int b1, int b2, int b3){
        convertIntToButton(b1).setTextColor(Color.RED);
        convertIntToButton(b2).setTextColor(Color.RED);
        convertIntToButton(b3).setTextColor(Color.RED);
    }

    private void initButtons(){
        for (int i = 0; i < arrayOfButtons.length; i++){
            arrayOfButtons[i].setText("");
        }
    }

    private boolean checkIfSomeoneWin(int[] board, int player) {
        boolean ifWin = false;
        for (int i = 0; i < winPoints.size(); i++) {
            int [] winnigPoint = winPoints.get(i);

            if(board[winnigPoint[0]] == player &&
                    board[winnigPoint[1]] == player &&
                    board[winnigPoint[2]] == player) {
                ifWin = true;

                if(!turn) {
                    makeGlowO(winnigPoint[0], winnigPoint[1], winnigPoint[2]);
                }

                if(turn) {
                    makeGlowX(winnigPoint[0], winnigPoint[1], winnigPoint[2]);
                }
            }
        }
        return ifWin;
    }

    private boolean ifTwo(){
        for(int i = 0; i < virtualPositions.length; i++) {
            if (virtualPositions[i] == 2) {
                return true;
            }
        }
        return false;
    }

    private void setClickableFalse(){
        for (Button arrayOfButton : arrayOfButtons) {
            arrayOfButton.setClickable(false);
        }
    }

    private void resetGame(){
        startIntent.putExtra("key_bot",bot);
        startActivity(startIntent);
    }

    private void dialogOfWinner() {
        String winner = "";
        if (!checkIfSomeoneWin(virtualPositions,player) && !ifTwo()) {
            winner = "Tie";
        } else if (checkIfSomeoneWin(virtualPositions,player)) {
            if(!turn) {
                winner = startIntent.getStringExtra("key_o_player") + " won";
                counterO++;
            }

            if(turn) {
                winner = startIntent.getStringExtra("key_x_player") + " won";
                counterX++;
            }
        }
        AlertDialog.Builder newGameDialog = new AlertDialog.Builder(MainActivity.this);

        newGameDialog.setMessage(winner + ", Do you want start new game?");
        newGameDialog.setCancelable(false);
        newGameDialog.setPositiveButton("Yes, start new Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Starting new game", Toast.LENGTH_SHORT).show();
                resetGame();
            }
        });
        newGameDialog.setNegativeButton("No, back to menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Ok See you in the next game", Toast.LENGTH_SHORT).show();

                Intent backIntent = new Intent(MainActivity.this, secondActivity.class);

                backIntent.putExtra("key_x_player", xPlayer);
                backIntent.putExtra("key_o_player", oPlayer);
                backIntent.putExtra("key_human_vs_human", startIntent.getBooleanExtra("key_human_vs_human", false));
                backIntent.putExtra("key_human_vs_bot", startIntent.getBooleanExtra("key_human_vs_bot", false));
                counterX = 0;
                counterO = 0;
                startActivity(backIntent);
                finish();
            }
        });

        newGameDialog.show();
        tvTurn.setText(winner);
    }

    private int minimax(int[] virtualPositions, int player) {
        int winner = checkForWin(virtualPositions);
        if (winner == 1) {
            return 1;
        } else if (winner == 0) {
            return -1;
        } else if (isBoardFull(virtualPositions)) {
            return 0;
        }

        int bestScore = (player == 1) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < virtualPositions.length; i++) {
            if (virtualPositions[i] == 2) {
                virtualPositions[i] = player;
                int score = minimax(virtualPositions, (player == 1) ? 0 : 1);
                virtualPositions[i] = 2;

                if (player == 1) {
                    bestScore = Math.max(score, bestScore);
                } else {
                    bestScore = Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    private int bestMove(int[] virtualPositions) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;
        for (int i = 0; i < virtualPositions.length; i++) {
            if (virtualPositions[i] == 2) {
                virtualPositions[i] = 1;
                int score = minimax(virtualPositions, 0);
                virtualPositions[i] = 2;

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    private int checkForWin(int[] virtualPositions) {
        for (int[] winPoint : winPoints) {
            if (virtualPositions[winPoint[0]] == virtualPositions[winPoint[1]] &&
                    virtualPositions[winPoint[1]] == virtualPositions[winPoint[2]] &&
                    virtualPositions[winPoint[0]] != 2) {
                return virtualPositions[winPoint[0]];
            }
        }
        return 2;
    }

    private boolean isBoardFull(int[] virtualPositions) {
        for (int i : virtualPositions) {
            if (i == 2) {
                return false;
            }
        }
        return true;
    }
}