package com.pong.model;

public class Paddle {
    private static final float PADDLE_SPEED = 300f;
    private static final float AI_PADDLE_SPEED = 250f;
    public static final int PADDLE_WIDTH = 20;
    public static final int PADDLE_HEIGHT = 100;
    public static final float PADDLE_OFFSET = 50;

    float paddleY;
    float paddleX;
    float paddleSpeed;
    public Paddle (boolean isLeft, int screenHeight, int screenWidth) {
        paddleY = screenHeight / 2 - PADDLE_HEIGHT / 2;

        if (isLeft) {
            paddleX = PADDLE_OFFSET;
            paddleSpeed = PADDLE_SPEED;
        }
        else {
            paddleX = screenWidth - PADDLE_OFFSET - PADDLE_WIDTH;
            paddleSpeed = AI_PADDLE_SPEED;
        }

    }
    public float getPaddleY() {
        return paddleY;
    }
    public float getPaddleX() {
        return paddleX;
    }
    public float getPaddleSpeed() {
        return paddleSpeed;
    }
    public void reset(int screenHeight) {
        paddleY = screenHeight / 2 - PADDLE_HEIGHT / 2;
    }


    public void update(float newPaddleY) {
        paddleY = newPaddleY;
    }


    public void updateSum(float increase) {
        paddleY += increase;
    }
}
