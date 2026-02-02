package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private int screenWidth;
    private int screenHeight;
    private static final int PADDLE_WIDTH = 20;
    private static final int PADDLE_HEIGHT = 100;
    private static final float PADDLE_OFFSET = 50;
    private static final int BALL_SIZE = 15;
    private static final float PADDLE_SPEED = 300f;
    private static final float AI_PADDLE_SPEED = 250f;
    private static final float BALL_BASE_SPEED = 300f;
    private static final float SPEED_INCREASE_INTERVAL = 3.0f;
    private static final float SPEED_MULTIPLIER = 1.15f;
    private static final float MAX_SPEED_MULTIPLIER = 2.5f;
    private static final int WINNING_SCORE = 21;
    private float leftPaddleY;
    private float rightPaddleY;
    private float ballX;
    private float ballY;
    private float ballVelocityX;
    private float ballVelocityY;
    private float currentSpeedMultiplier;
    private int playerScore;
    private int aiScore;
    private boolean gameOver;
    private String winnerText;
    private float rallyTimer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(3);

        shapeRenderer = new ShapeRenderer();

        // Screen dimensions
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        // Element positions
        leftPaddleY = screenHeight / 2 - PADDLE_HEIGHT / 2;
        rightPaddleY = screenHeight / 2 - PADDLE_HEIGHT / 2;

        playerScore = 0;
        aiScore = 0;
        gameOver = false;

        resetBall();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (!gameOver) {
            if (rallyTimer >= SPEED_INCREASE_INTERVAL && currentSpeedMultiplier < MAX_SPEED_MULTIPLIER) {
                increaseSpeed();
                rallyTimer = 0;
            }

            // Handle touch input for player paddle
            handleInput(deltaTime);

            // AI controls right paddle
            updateAI(deltaTime);

            // Update ball position
            updateBall(deltaTime);
        } else {
            // Check for touch to restart game
            if (Gdx.input.isTouched()) {
                resetGame();
            }
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Elements
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);

        // Midfield line
        drawDashedLine();

        // Draw left paddle
        shapeRenderer.rect(PADDLE_OFFSET, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw right paddle
        shapeRenderer.rect(screenWidth - PADDLE_OFFSET - PADDLE_WIDTH, rightPaddleY,
            PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        shapeRenderer.circle(ballX + BALL_SIZE / 2, ballY + BALL_SIZE / 2, BALL_SIZE / 2);

        shapeRenderer.end();

        // Score
        batch.begin();
        String playerScoreText = String.valueOf(playerScore);
        font.draw(batch, playerScoreText, screenWidth / 4, screenHeight - 50);
        String aiScoreText = String.valueOf(aiScore);
        font.draw(batch, aiScoreText, screenWidth * 3 / 4, screenHeight - 50);

        if (gameOver) {
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
    private void drawDashedLine() {
        int dashHeight = 20;
        int dashGap = 15;
        int lineWidth = 5;
        int centerX = screenWidth / 2 - lineWidth / 2;

        for (int y = 0; y < screenHeight; y += dashHeight + dashGap) {
            shapeRenderer.rect(centerX, y, lineWidth, dashHeight);
        }
    }

    private void increaseSpeed() {
        currentSpeedMultiplier = Math.min(currentSpeedMultiplier * SPEED_MULTIPLIER, MAX_SPEED_MULTIPLIER);

        float currentSpeed = (float) Math.sqrt(ballVelocityX * ballVelocityX + ballVelocityY * ballVelocityY);
        float newSpeed = BALL_BASE_SPEED * currentSpeedMultiplier;
        float speedRatio = newSpeed / currentSpeed;

        ballVelocityX *= speedRatio;
        ballVelocityY *= speedRatio;
    }
    private void resetBall() {
        ballX = screenWidth / 2 - BALL_SIZE / 2;
        ballY = screenHeight / 2 - BALL_SIZE / 2;

        currentSpeedMultiplier = 1.0f;
        rallyTimer = 0;

        float speed = BALL_BASE_SPEED * currentSpeedMultiplier;
        ballVelocityX = speed * (Math.random() > 0.5 ? 1 : -1);
        ballVelocityY = speed * (Math.random() > 0.5 ? 1 : -1) * 0.5f;
    }
    private void updateBall(float deltaTime) {
        // Update ball position
        ballX += ballVelocityX * deltaTime;
        ballY += ballVelocityY * deltaTime;

        // Ball collision with top and bottom walls
        if (ballY <= 0 || ballY + BALL_SIZE >= screenHeight) {
            ballVelocityY = -ballVelocityY;
            ballY = Math.max(0, Math.min(screenHeight - BALL_SIZE, ballY));
        }

        // Ball collision with left paddle
        if (ballX <= PADDLE_OFFSET + PADDLE_WIDTH &&
            ballX >= PADDLE_OFFSET &&
            ballY + BALL_SIZE >= leftPaddleY &&
            ballY <= leftPaddleY + PADDLE_HEIGHT) {
            ballVelocityX = Math.abs(ballVelocityX);
            ballX = PADDLE_OFFSET + PADDLE_WIDTH;

            // Ball collision depending on which part of the paddle makes contact
            float paddleCenter = leftPaddleY + PADDLE_HEIGHT / 2;
            float ballCenter = ballY + BALL_SIZE / 2;
            float hitOffset = (ballCenter - paddleCenter) / (PADDLE_HEIGHT / 2);

            boolean hitEdge = Math.abs(hitOffset) > 0.6f;
            if (hitEdge && currentSpeedMultiplier < MAX_SPEED_MULTIPLIER) {
                increaseSpeed();
                rallyTimer = 0;
            }
            ballVelocityY = hitOffset * BALL_BASE_SPEED * currentSpeedMultiplier * 0.75f;
        }

        // Ball collision with right paddle
        if (ballX + BALL_SIZE >= screenWidth - PADDLE_OFFSET - PADDLE_WIDTH &&
            ballX + BALL_SIZE <= screenWidth - PADDLE_OFFSET &&
            ballY + BALL_SIZE >= rightPaddleY &&
            ballY <= rightPaddleY + PADDLE_HEIGHT) {
            ballVelocityX = -Math.abs(ballVelocityX);
            ballX = screenWidth - PADDLE_OFFSET - PADDLE_WIDTH - BALL_SIZE;

            float paddleCenter = rightPaddleY + PADDLE_HEIGHT / 2;
            float ballCenter = ballY + BALL_SIZE / 2;
            float hitOffset = (ballCenter - paddleCenter) / (PADDLE_HEIGHT / 2);

            boolean hitEdge = Math.abs(hitOffset) > 0.6f;
            if (hitEdge && currentSpeedMultiplier < MAX_SPEED_MULTIPLIER) {
                increaseSpeed();
                rallyTimer = 0;
            }
            ballVelocityY = hitOffset * BALL_BASE_SPEED * currentSpeedMultiplier * 0.75f;
        }

        // Check for goals, update score
        if (ballX < 0) {
            // AI scores
            aiScore++;
            checkWinCondition();
            if (!gameOver) {
                resetBall();
            }
        } else if (ballX > screenWidth) {
            // Player scores
            playerScore++;
            checkWinCondition();
            if (!gameOver) {
                resetBall();
            }
        }
    }
    private void handleInput(float deltaTime){
        if (Gdx.input.isTouched()) {
            // Get touch position (Y coordinate)
            int touchY = Gdx.input.getY();
            float targetY = screenHeight - touchY - PADDLE_HEIGHT / 2;

            // Move paddle toward touch position
            float difference = targetY - leftPaddleY;
            if (Math.abs(difference) > PADDLE_SPEED * deltaTime) {
                leftPaddleY += Math.signum(difference) * PADDLE_SPEED * deltaTime;
            } else {
                leftPaddleY = targetY;
            }

            leftPaddleY = Math.max(0, Math.min(screenHeight - PADDLE_HEIGHT, leftPaddleY));
        }
    }
    private void updateAI(float deltaTime) {
        // AI tracks the ball's Y position
        float ballCenterY = ballY + BALL_SIZE / 2;
        float paddleCenterY = rightPaddleY + PADDLE_HEIGHT / 2;

        // Only move AI if ball is moving toward it
        if (ballVelocityX > 0) {
            if (ballCenterY > paddleCenterY + 10) {
                rightPaddleY += AI_PADDLE_SPEED * deltaTime;
            } else if (ballCenterY < paddleCenterY - 10) {
                rightPaddleY -= AI_PADDLE_SPEED * deltaTime;
            }
        }

        // Keep paddle within screen bounds
        rightPaddleY = Math.max(0, Math.min(screenHeight - PADDLE_HEIGHT, rightPaddleY));
    }
    private void checkWinCondition() {
        if (playerScore >= WINNING_SCORE) {
            gameOver = true;
            winnerText = "You Win!";
        } else if (aiScore >= WINNING_SCORE) {
            gameOver = true;
            winnerText = "AI Wins!";
        }
    }
    private void resetGame() {
        playerScore = 0;
        aiScore = 0;
        gameOver = false;
        leftPaddleY = screenHeight / 2 - PADDLE_HEIGHT / 2;
        rightPaddleY = screenHeight / 2 - PADDLE_HEIGHT / 2;
        resetBall();
    }
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
