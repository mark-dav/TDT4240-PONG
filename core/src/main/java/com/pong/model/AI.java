package com.pong.model;

public class AI {
    public float update(float rightPaddleY, float paddleSpeed, float deltaTime, float ballX, float ballY, float ballVelocityX, int ballSize, int paddleHeight, int screenHeight) {
        float ballCenterY = ballY + ballSize / 2;
        float paddleCenterY = rightPaddleY + paddleHeight / 2;

        // Only move AI if ball is moving toward it
        if (ballVelocityX > 0) {
            if (ballCenterY > paddleCenterY + 10) {
                rightPaddleY += paddleSpeed * deltaTime;
            } else if (ballCenterY < paddleCenterY - 10) {
                rightPaddleY -= paddleSpeed * deltaTime;
            }
        }

        // Keep paddle within screen bounds
        rightPaddleY = Math.max(0, Math.min(screenHeight - paddleHeight, rightPaddleY));
        return rightPaddleY;
    }
}
