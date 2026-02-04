package com.pong.model;

public class Score {
    private static final int WINNING_SCORE = 21;
    int aiScore;
    int playerScore;
    public int getPlayerScore() {
        return playerScore;
    }
    public int getAIScore() {
        return aiScore;
    }
    public boolean update (int goalscorer) {
        if (goalscorer == 1) {
            playerScore++;
            return checkWinCondition(playerScore);
        }
        else {
            aiScore++;
            return checkWinCondition(aiScore);
        }
    }
    public boolean checkWinCondition (int score) {
        return score >= WINNING_SCORE;
    }

    public void reset() {
        aiScore = 0;
        playerScore = 0;
    }


}
