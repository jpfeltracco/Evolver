package simulations.pong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;
import simulations.pong.World.WorldListener;

public class GameScreen extends Screen implements InputProcessor{
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_WON = 2;
	
	int state;
	OrthographicCamera guiCam;
//	Vector3 touchPoint;
	SpriteBatch batcher;
	World world;
	WorldListener worldListener;
	WorldRenderer renderer;
	int player1Score;
	int player2Score;
	String player1ScoreString;
	String player2ScoreString;
	Controller c1;
	Controller c2;
//	FPSLogger fpslogger;
	
	public GameScreen(Game game, Controller c1, Controller c2){
		super(game);
		this.c1 = c1;
		this.c2 = c2;
		state = GAME_READY;
		guiCam = new OrthographicCamera(480,320);
		guiCam.position.set(480 / 2, 320 / 2, 0);
//		touchPoint = new Vector3();
		batcher = new SpriteBatch();
		worldListener = new WorldListener(){
			@Override
			public void bump() {
				Assets.playSound(Assets.bounceSound);
			}
		};
		world = new World(worldListener);
		renderer = new WorldRenderer(batcher, world);
		player1Score = 0;
		player2Score = 0;
		player1ScoreString = "0";
		player2ScoreString = "0";
//		fpslogger = new FPSLogger();
	}

	@Override
	public void update(float deltaTime) {
		if (deltaTime > 0.1f) deltaTime = 0.1f;
		
		switch (state){
		case GAME_READY:
			updateReady();
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_WON:
			updateWon();
			break;
		}
	}
	
	public void updateReady(){
		state = GAME_RUNNING;
	}
	
	private void updateRunning (float deltaTime){
		
		float accel1 = 0;
		float accel2 = 0;
		accel1 = (float) (20 * c1.calculate(
				normPos(world.paddleP1.position.y, World.WORLD_HEIGHT),
				normPos(world.ball.position.x, World.WORLD_WIDTH),
				normPos(world.ball.position.y, World.WORLD_HEIGHT),
				normNeg(world.ball.velocity.x, world.BALLSPEED),
				normNeg(world.ball.velocity.y, world.BALLSPEED),
				normPos(world.paddleP2.position.y, World.WORLD_HEIGHT))[0]);
		accel1 = MathUtils.clamp(accel1, -20, 20);
		accel2 = (float) (20 * c2.calculate(
				normPos(world.paddleP2.position.y, World.WORLD_HEIGHT),
				normPos(world.ball.position.x, World.WORLD_WIDTH),
				normPos(world.ball.position.y, World.WORLD_HEIGHT),
				normNeg(world.ball.velocity.x, world.BALLSPEED),
				normNeg(world.ball.velocity.y, world.BALLSPEED),
				normPos(world.paddleP1.position.y, World.WORLD_HEIGHT))[0]);
		accel2 = MathUtils.clamp(accel2, -20, 20);
		world.accelP1 = accel1;
		world.accelP2 = accel2;
//		float accel1 = 0;
//		float accel2 = 0;
//		if(Gdx.input.isKeyPressed(Keys.DPAD_UP))
//			accel2 = 20;
//		if(Gdx.input.isKeyPressed(Keys.DPAD_DOWN))
//			accel2 = -20;
//		if(Gdx.input.isKeyPressed(Keys.W))
//			accel1 = 20;
//		if(Gdx.input.isKeyPressed(Keys.S))
//			accel1 = -20;
		world.update(deltaTime);
		if (world.scoreP1 != player1Score || world.scoreP2 != player2Score){
			player1Score = world.scoreP1;
			player2Score = world.scoreP2;
			player1ScoreString = "" + player1Score;
			player2ScoreString = "" + player2Score;
		}
		
//		fpslogger.log();
		
	}
	
	private double normPos(double val, double maxVal) {
		 // normalizes -1 to 1 for positive
			return 2 * (val / maxVal) - 1;
		 }
		 
		 private double normNeg(double val, double maxVal) {
			 return val / maxVal;
		 }
	
	private void updateWon(){
		
	}

	@Override
	public void present(float deltaTime) {
		GL20 gl = Gdx.gl;
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL20.GL_TEXTURE_2D);
		
		renderer.render();
		
		guiCam.update();
		batcher.setProjectionMatrix(guiCam.combined);
		batcher.enableBlending();
		batcher.begin();
		Gdx.input.setInputProcessor(this);
		switch (state){
		case GAME_READY:
			presentReady();
			break;
		case GAME_RUNNING:
			presentRunning();
			break;
		case GAME_WON:
			presentWon();
			break;
		}
		batcher.end();
	}
	
	public void presentReady(){
		//waits for a click or touchscreen, doesn't render anything
	}
	
	public void presentRunning(){
//		Assets.font.draw(batcher, player1ScoreString, 36, 300);
//		Assets.font.draw(batcher, player2ScoreString, 420, 300);
	}
	
	public void presentWon(){
	}

	@Override
	public void pause() {
	
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
	
	}

	@Override
	public boolean keyDown(int arg0) {
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		return false;
	}

}
