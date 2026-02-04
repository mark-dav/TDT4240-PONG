package com.pong.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pong.controller.Controller;
import com.pong.model.Ball;
import com.pong.model.Paddle;
import com.pong.model.Score;

import javax.naming.ldap.Control;

public class PongScreen {
    private int screenHeight;
    private int screenWidth;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    public PongScreen () {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(3);

        shapeRenderer = new ShapeRenderer();
    }

    public int getHeight() {
        return screenHeight;
    }

    public int getWidth() {
        return screenWidth;
    }
    public boolean isTouched() {
        return Gdx.input.isTouched();
    }
    public int getTouchY() {
        return Gdx.input.getY();
    }

    public void render(String winnerText) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);

        // Midfield line
        drawDashedLine();

        // Draw paddles
        drawPaddle(Controller.getSingleInstance().getLeftPaddle());
        drawPaddle(Controller.getSingleInstance().getRightPaddle());

        // Draw ball
        drawBall(Controller.getSingleInstance().getBall());

        shapeRenderer.end();

        // Score
        batch.begin();
        drawScore();
        if (Controller.getSingleInstance().gameIsOver()) {
            font.getData().setScale(4);
            float textWidth = font.getRegion().getRegionWidth() * winnerText.length() / 4;
            font.draw(batch, winnerText, screenWidth / 2 - textWidth, screenHeight / 2 + 50);

            font.getData().setScale(2);
            String tapText = "Tap to play again";
            float tapTextWidth = font.getRegion().getRegionWidth() * tapText.length() / 8;
            font.draw(batch, tapText, screenWidth / 2 - tapTextWidth, screenHeight / 2 - 50);

            font.getData().setScale(3); // Reset scale
        }

        batch.end();
    }

    private void drawScore() {
        Score score = Controller.getSingleInstance().getScore();
        String playerScoreText = String.valueOf(score.getPlayerScore());
        font.draw(batch, playerScoreText, screenWidth / 4, screenHeight - 50);
        String aiScoreText = String.valueOf(score.getAIScore());
        font.draw(batch, aiScoreText, screenWidth * 3 / 4, screenHeight - 50);
    }

    private void drawBall(Ball ball) {
        shapeRenderer.circle(ball.getBallX() + Ball.BALL_SIZE / 2, ball.getBallY() + Ball.BALL_SIZE / 2, Ball.BALL_SIZE / 2);
    }

    private void drawPaddle(Paddle paddle) {
        shapeRenderer.rect(paddle.getPaddleX(), paddle.getPaddleY(), Paddle.PADDLE_WIDTH, Paddle.PADDLE_HEIGHT);
    }

    private void drawDashedLine() {
        int dashHeight = 20;
        int dashGap = 15;
        int lineWidth = 5;
        int centerX = screenWidth / 2 - lineWidth / 2;

        for (int y = 0; y < screenHeight; y += dashHeight + dashGap) {
            shapeRenderer.rect(centerX, y, lineWidth, dashHeight);
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
