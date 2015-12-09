package simulations.pong;

public class World {
	public interface WorldListener {
		public void bump();
	}

	public static final float WORLD_WIDTH = 48;
	public static final float WORLD_HEIGHT = 32;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_GAME_END = 1;

	public final int BALLSPEED = 20;
	private final double MAXBOUNCEANGLE = Math.toRadians(75); // 75 degrees

	public final Paddle paddleP1;
	public final Paddle paddleP2;
	public final Ball ball;
//	public final WorldListener listener;

	public int scoreP1;
	public int scoreP2;
	public int state;
	
	public float accelP1 = 0;
	public float accelP2 = 0;
	
	public long time;

	public World(WorldListener listener) {
		this.ball = new Ball(10, 10);
		this.paddleP1 = new Paddle(1, 16);
		this.paddleP2 = new Paddle(47, 16);
		this.scoreP1 = 0;
		this.scoreP2 = 0;
//		this.listener = listener;
		this.state = WORLD_STATE_RUNNING;
		time = System.nanoTime();

	}

	public void update(float deltaTime) {
		if (this.state == WORLD_STATE_RUNNING) {
			updateBall(deltaTime);
			updatePaddles(deltaTime, accelP1, accelP2);
			checkCollisions();
			checkGameOver();
		}
	}

	public void updateBall(float deltaTime) {
		ball.update(deltaTime);
		if (ball.position.y >= WORLD_HEIGHT && ball.velocity.y > 0)
			ball.velocity.y = ball.velocity.y * -1;
		if (ball.position.y <= 0 && ball.velocity.y < 0)
			ball.velocity.y = ball.velocity.y * -1;
		if (ball.position.x >= WORLD_WIDTH) {
			scoreP1++;
			ball.score();
		}
		if (ball.position.x <= 0) {
			scoreP2++;
			ball.score();
		}

	}

	public void updatePaddles(float deltaTime, float accelP1, float accelP2) {
		paddleP1.velocity.y = accelP1;
		paddleP2.velocity.y = accelP2;
		paddleP1.update(deltaTime);
		paddleP2.update(deltaTime);
	}

	public void checkCollisions() {
		if (OverlapTester.overlapRectangles(paddleP1.bounds, ball.bounds) && ball.velocity.x < 0) {
			float relInter = paddleP1.position.y - ball.position.y + ball.height / 2;
			float normalRelInter = relInter / paddleP1.height;
			float bounceAngle = (float) (normalRelInter * MAXBOUNCEANGLE);

			ball.velocity.x = (float) (BALLSPEED * Math.cos(bounceAngle));
			ball.velocity.y = (float) -(BALLSPEED * Math.sin(bounceAngle));

			// ball.velocity.x = ball.velocity.x * -1;
			// ball.velocity.x = ball.velocity.x * 1.1f;
			// ball.velocity.y = ball.velocity.y * 1.1f;
//			listener.bump();
		}
		if (OverlapTester.overlapRectangles(paddleP2.bounds, ball.bounds) && ball.velocity.x > 0) {
			float relInter = paddleP2.position.y - ball.position.y + ball.height / 2;
			float normalRelInter = relInter / paddleP2.height;
			float bounceAngle = (float) (normalRelInter * MAXBOUNCEANGLE);

			ball.velocity.x = (float) -(BALLSPEED * Math.cos(bounceAngle));
			ball.velocity.y = (float) -(BALLSPEED * Math.sin(bounceAngle));
			// ball.velocity.x = ball.velocity.x * -1;
			// ball.velocity.x = ball.velocity.x * 1.1f;
			// ball.velocity.y = ball.velocity.y * 1.1f;
//			listener.bump();
		}
	}

	public boolean checkGameOver() {
		if (scoreP1 > 0 || scoreP2 > 0 || System.nanoTime() - time > 500000000.0) { //0.5 seconds
			state = WORLD_STATE_GAME_END;
			return true;
		}
		
		return false;
	}

}
