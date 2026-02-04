package com.pong.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pong.model.AI;
import com.pong.model.Paddle;
import com.pong.model.Ball;
import com.pong.model.Score;
import com.pong.view.PongScreen;

public class Controller {
    private static Controller singleInstance;
    private float rallyTimer;
    private Ball ball;
    private Score score;
    private PongScreen screen;
    private AI ai;
    private Paddle leftPaddle;
    private Paddle rightPaddle;
    private int screenWidth;
    private int screenHeight;
    private String winnerText;
    private boolean gameOver;

    private Controller() {
        rallyTimer=0;
        screen = new PongScreen();
        score = new Score();
        screenHeight = screen.getHeight();
        screenWidth = screen.getWidth();
        ball = new Ball();
        ball.reset(screenWidth, screenHeight);
        leftPaddle = new Paddle(true, screenHeight, screenWidth);
        rightPaddle = new Paddle(false, screenHeight, screenWidth);
        ai = new AI();
    }

    public Paddle getLeftPaddle() {
        return leftPaddle;
    }
    public Paddle getRightPaddle() {
        return rightPaddle;
    }
    public Ball getBall() {
        return ball;
    }
    public Score getScore() {
        return score;
    }
    public boolean gameIsOver() {
        return gameOver;
    }
    public static Controller getSingleInstance() {
        if (singleInstance == null)
            singleInstance = new Controller();
        return singleInstance;
    }

    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (!gameOver) {
            if (ball.speedShouldBeIncreased(rallyTimer)) {
                ball.increaseSpeed();
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
            if (screen.isTouched()) {
                resetGame();
            }
        }
        screen.render(winnerText);
    }
    private void handleInput(float deltaTime){
        if (screen.isTouched()) {
            // Get touch position (Y coordinate)
            int touchY = screen.getTouchY();
            float targetY = screenHeight - touchY - Paddle.PADDLE_HEIGHT / 2;

            // Move paddle toward touch position
            float difference = targetY - leftPaddle.getPaddleY();
            if (Math.abs(difference) > leftPaddle.getPaddleSpeed() * deltaTime) {
                leftPaddle.updateSum(Math.signum(difference) * leftPaddle.getPaddleSpeed() * deltaTime);
            } else {
                leftPaddle.update(targetY);
            }

            leftPaddle.update(Math.max(0, Math.min(screenHeight - Paddle.PADDLE_HEIGHT, leftPaddle.getPaddleY())));
        }
    }
    private void updateBall(float deltaTime) {
        // Update ball position
        ball.updateBall(deltaTime);

        // Ball collision with top and bottom walls
        ball.wallCollision(screenHeight);

        // Ball collision with left paddle
        paddleCollision(leftPaddle);

        // Ball collision with right paddle
        paddleCollision(rightPaddle);

        // Checks whether player 1 or player 2 have scored. Returns 0 if neither have scored.
        int goalscorer = ball.checkGoal(screenWidth);

        // Update scores
        if (goalscorer != 0) {
            // Update score and check if Win Condition is met.
            if (score.update(goalscorer)) {
                winnerText = ("Player " + goalscorer + " Wins!");
                resetGame();
            }
            ball.reset(screenWidth, screenHeight);
            rallyTimer = 0;
        }
    }

    private void paddleCollision (Paddle paddle) {
        float ballX = ball.getBallX();
        float ballY = ball.getBallY();
        float ballSize = Ball.BALL_SIZE;

        float paddleY = paddle.getPaddleY();
        float paddleOffset = Paddle.PADDLE_OFFSET;
        float paddleWidth = Paddle.PADDLE_WIDTH;
        float paddleHeight = Paddle.PADDLE_HEIGHT;

        // Left paddle
        if (ballX <= paddleOffset + paddleWidth &&
            ballX >= paddleOffset &&
            ballY + ballSize >= paddleY &&
            ballY <= paddleY + paddleHeight) {

            // Ball collision depending on which part of the paddle makes contact
            float hitOffset = calculateHitOffset(paddleY, paddleHeight, ballY, ballSize);
            ball.paddleCollision(true, screenWidth, paddleOffset, paddleWidth, hitOffset);
        }

        // Right paddle
        else if (ballX + ballSize >= screenWidth - paddleOffset - paddleWidth &&
            ballX + ballSize <= screenWidth - paddleOffset &&
            ballY + ballSize >= paddleY &&
            ballY <= paddleY + paddleHeight) {

            // Ball collision depending on which part of the paddle makes contact
            float hitOffset = calculateHitOffset(paddleY, paddleHeight, ballY, ballSize);
            ball.paddleCollision(false, screenWidth, paddleOffset, paddleWidth, hitOffset);
        }
    }
    private float calculateHitOffset (float paddleY, float paddleHeight, float ballY, float ballSize) {
        float paddleCenter = paddleY + paddleHeight / 2;
        float ballCenter = ballY + ballSize / 2;
        float hitOffset = (ballCenter - paddleCenter) / (paddleHeight / 2);

        boolean hitEdge = Math.abs(hitOffset) > 0.6f;
        if (ball.edgeBallSpeed(hitEdge))
            rallyTimer = 0;
        return hitOffset;
    }

    private void resetGame() {
        gameOver = false;
        score.reset();
        leftPaddle.reset(screenHeight);
        rightPaddle.reset(screenHeight);
        ball.reset(screenWidth, screenHeight);
    }
    private void updateAI(float deltaTime) {
        // AI tracks the ball's Y position
        float newPaddleY = ai.update(rightPaddle.getPaddleY(), rightPaddle.getPaddleSpeed(), deltaTime, ball.getBallX(), ball.getBallY(), ball.getVelocityX(), Ball.BALL_SIZE, Paddle.PADDLE_HEIGHT, screenHeight);
        rightPaddle.update(newPaddleY);
    }

    public void dispose() {
        screen.dispose();
    }



}
