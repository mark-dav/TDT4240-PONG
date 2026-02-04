package com.pong.model;

public class Ball {
    public static final int BALL_SIZE = 15;
    private static final float BALL_BASE_SPEED = 300f;
    private static final float SPEED_INCREASE_INTERVAL = 3.0f;
    private static final float SPEED_MULTIPLIER = 1.15f;
    private static final float MAX_SPEED_MULTIPLIER = 2.5f;
    private float ballX;
    private float ballY;
    private float ballVelocityX;
    private float ballVelocityY;
    private float currentSpeedMultiplier;
    public Ball () {
        ballX = 0;
        ballY = 0;
        ballVelocityX = 0;
        ballVelocityY = 0;
        currentSpeedMultiplier = 0;
    }

    public float getBallX() {
        return ballX;
    }
    public float getBallY() {
        return ballY;
    }
    public float getVelocityX() {
        return ballVelocityX;
    }
    public void increaseSpeed() {
        currentSpeedMultiplier = Math.min(currentSpeedMultiplier * SPEED_MULTIPLIER, MAX_SPEED_MULTIPLIER);

        float currentSpeed = (float) Math.sqrt(ballVelocityX * ballVelocityX + ballVelocityY * ballVelocityY);
        float newSpeed = BALL_BASE_SPEED * currentSpeedMultiplier;
        float speedRatio = newSpeed / currentSpeed;

        ballVelocityX *= speedRatio;
        ballVelocityY *= speedRatio;
    }
    public boolean edgeBallSpeed(boolean hitEdge) {
        if (hitEdge && currentSpeedMultiplier < MAX_SPEED_MULTIPLIER) {
            increaseSpeed();
            return true;
        }
        return false;
    }
    public void paddleCollision (boolean isLeft, float screenWidth, float paddleOffset, float paddleWidth, float hitOffset) {
        if (isLeft) {
            ballVelocityX = Math.abs(ballVelocityX);
            ballX = paddleOffset + paddleWidth;
        }
        else {
            ballVelocityX = -Math.abs(ballVelocityX);
            ballX = screenWidth - paddleOffset - paddleWidth - BALL_SIZE;
        }
        ballVelocityY = hitOffset * BALL_BASE_SPEED * currentSpeedMultiplier * 0.75f;
    }
    public void updateBall(float deltaTime) {
        ballX += ballVelocityX * deltaTime;
        ballY += ballVelocityY * deltaTime;
    }
    public void wallCollision (int screenHeight) {
        if (ballY <= 0 || ballY + BALL_SIZE >= screenHeight) {
            ballVelocityY = -ballVelocityY;
            ballY = Math.max(0, Math.min(screenHeight - BALL_SIZE, ballY));
        }
    }
    public int checkGoal(int screenWidth) {
        // Player 2 has scored
        if (ballX < 0) {
            return 2;
        }
        // Player 1 has scored
        else if (ballX > screenWidth) {
            return 1;
        }
        return 0;
    }
    public void reset(int screenWidth, int screenHeight) {
        ballX = screenWidth / 2 - BALL_SIZE / 2;
        ballY = screenHeight / 2 - BALL_SIZE / 2;

        currentSpeedMultiplier = 1.0f;

        float speed = BALL_BASE_SPEED * currentSpeedMultiplier;
        ballVelocityX = speed * (Math.random() > 0.5 ? 1 : -1);
        ballVelocityY = speed * (Math.random() > 0.5 ? 1 : -1) * 0.5f;
    }

    public boolean speedShouldBeIncreased(float rallyTimer) {
        return rallyTimer >= SPEED_INCREASE_INTERVAL && currentSpeedMultiplier < MAX_SPEED_MULTIPLIER;
    }
}
