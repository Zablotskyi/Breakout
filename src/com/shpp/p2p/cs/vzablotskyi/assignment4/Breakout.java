package com.shpp.p2p.cs.vzablotskyi.assignment4;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.event.MouseEvent;

public class Breakout extends WindowProgram {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    private static final int PADDLE_Y_OFFSET = 60;
    private static final int SCORE_Y_OFFSET = 20;
    private static final int BALL_RADIUS = 10;
    private static final int BRICK_WIDTH = 40;
    private static final int BRICK_HEIGHT = 10;
    private static final int N_BRICKS_PER_ROW = 10;
    private static final int N_ROWS = 1;
    private static final int DELAY = 10;
    private static final int MAX_TRIES = 3;

    private static int BRICK_COUNT = 0;
    private static int SHUT_BRICKS = 0;
    private static int SCORE = 0;

    private GRect paddle;
    private GOval ball;
    private GRect brick;
    private GLabel score;
    private double vx, vy;
    private int tries;

    public void run() {
        setupGame();
        playGame();
    }

    private void setupGame() {
        setSize(WIDTH, HEIGHT);
        addPaddle();
        addBricks();
        addBall();
        addScore();
        addMouseListeners();
        tries = MAX_TRIES;
    }

    private void playGame() {
        waitForClick();

        while (true) {
            moveBall();
            checkForCollision();
            pause(DELAY);

            if (SHUT_BRICKS == BRICK_COUNT) {
                endGame(); // End game if all bricks are shut
                break;
            }

            if (ball.getY() >= HEIGHT - BALL_RADIUS * 3) {
                // Reduce the number of tries when the ball goes below the paddle
                tries--;
                if (tries == 0) {
                    gameOver(); // Game over if no tries left
                    break;
                } else {
                    resetBall();
                    waitForClick();
                }
            }
        }
    }

    private void addPaddle() {
        paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        add(paddle, (WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
    }

    private void addBall() {
        ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
        ball.setFilled(true);
        add(ball, (WIDTH - BALL_RADIUS * 2) / 2, (HEIGHT - BALL_RADIUS * 2) / 2);
        vy = 3; // Initial vertical velocity
        vx = rgen.nextDouble(1.0, 3.0); // Initial random horizontal velocity
        if (rgen.nextBoolean(0.5)) vx = -vx;
    }

    private void addBricks() {
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_BRICKS_PER_ROW; j++) {
                BRICK_COUNT++;
                brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
                brick.setFilled(true);

                // Змінено розташування цеглин для уникнення виходження за межі екрану
                double x = (WIDTH - N_BRICKS_PER_ROW * (BRICK_WIDTH + 2)) / 2 + j * (BRICK_WIDTH + 2);
                double y = (HEIGHT - N_ROWS * (BRICK_HEIGHT + 2)) / 10 + i * (BRICK_HEIGHT + 2);

                add(brick, x, y);
            }
        }
    }

    private void addScore() {
        score = new GLabel("Score: " + SCORE, 0, 10);
        double x = (WIDTH / 2 - score.getWidth() / 2);
        double y = (HEIGHT - PADDLE_HEIGHT - SCORE_Y_OFFSET) - score.getAscent();
        add(score, x, y);
    }

    private void moveBall() {
        ball.move(vx, vy);
    }

    private void checkForCollision() {
        double x = ball.getX();
        double y = ball.getY();

        if (x <= 0 || x >= WIDTH - BALL_RADIUS * 2) {
            vx = -vx; // Reflect the ball on horizontal walls
        }

        if (y <= 0) {
            vy = -vy; // Reflect the ball on the ceiling
        }

        if (getElementAt(x, y) != null || getElementAt(x + BALL_RADIUS * 2, y) != null ||
                getElementAt(x, y + BALL_RADIUS * 2) != null || getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2) != null) {
            GObject collider = getCollidingObject();
            if (collider != null) {
                if (collider != paddle) {
                    remove(collider); // Remove the brick
                    SHUT_BRICKS++;
                    SCORE += 10;
                }
                vy = -vy; // Reflect the ball off the brick
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (e.getX() >= 0 && e.getX() <= WIDTH - PADDLE_WIDTH) {
            paddle.setLocation(e.getX(), HEIGHT - PADDLE_HEIGHT - PADDLE_Y_OFFSET);
        }
    }

    private GObject getCollidingObject() {
        double x = ball.getX();
        double y = ball.getY();

        if (getElementAt(x, y) != null) {
            return getElementAt(x, y);
        } else if (getElementAt(x + BALL_RADIUS * 2, y) != null) {
            return getElementAt(x + BALL_RADIUS * 2, y);
        } else if (getElementAt(x, y + BALL_RADIUS * 2) != null) {
            return getElementAt(x, y + BALL_RADIUS * 2);
        } else if (getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2) != null) {
            return getElementAt(x + BALL_RADIUS * 2, y + BALL_RADIUS * 2);
        } else {
            return null;
        }
    }

    private void resetBall() {
        ball.setLocation((WIDTH - BALL_RADIUS * 2) / 2, (HEIGHT - BALL_RADIUS * 2) / 2);
        vy = 3;
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5)) vx = -vx;
    }

    private void gameOver() {
        GLabel label = new GLabel("Game Over", WIDTH / 2, HEIGHT / 2);
        label.setFont("Helvetica-24");
        add(label);
        pause(1000);
        System.exit(0);
    }

    private void endGame() {
        GLabel label = new GLabel("You won!", WIDTH / 2, HEIGHT / 2);
        label.setFont("Helvetica-24");
        add(label);
        pause(1000);
        System.exit(0);

    }

    private RandomGenerator rgen = RandomGenerator.getInstance();

    public static void main(String[] args) {
        new Breakout().start(args);
    }
}