package simulations.pong;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;
import simulations.Renderable;
import simulations.Simulation;
import simulations.pong.World.WorldListener;
import ui.Builder.MenuItems;

public class PongSim extends Simulation implements Renderable {
	float spf = 1 / 60f;

	@Override
	public void simulate(Controller[] c) {
		// c[0] will be our left paddle
		// c[1] will be our right paddle

		World world = new World(new WorldListener() {
			@Override
			public void bump() {
			}
		});

		while (!world.checkGameOver()) {
			world.update(.1f);
			float accel1 = 0;
			float accel2 = 0;
			accel1 = (float) (20 * c[0].calculate(
					normPos(world.paddleP1.position.y, World.WORLD_HEIGHT),
					normPos(world.ball.position.x, World.WORLD_WIDTH),
					normPos(world.ball.position.y, World.WORLD_HEIGHT),
					normNeg(world.ball.velocity.x, world.BALLSPEED),
					normNeg(world.ball.velocity.y, world.BALLSPEED),
					normPos(world.paddleP2.position.y, World.WORLD_HEIGHT))[0]);
			accel1 = MathUtils.clamp(accel1, -20, 20);
			accel2 = (float) (20 * c[0].calculate(
					normPos(world.paddleP2.position.y, World.WORLD_HEIGHT),
					normPos(world.ball.position.x, World.WORLD_WIDTH),
					normPos(world.ball.position.y, World.WORLD_HEIGHT),
					normNeg(world.ball.velocity.x, world.BALLSPEED),
					normNeg(world.ball.velocity.y, world.BALLSPEED),
					normPos(world.paddleP1.position.y, World.WORLD_HEIGHT))[0]);
			accel2 = MathUtils.clamp(accel2, -20, 20);
			world.accelP1 = accel1;
			world.accelP2 = accel2;

		}

		c[0].addFitness(world.scoreP1);
		c[1].addFitness(world.scoreP2);
	}

	private double normPos(double val, double maxVal) {
		 // normalizes -1 to 1 for positive
			return 2 * (val / maxVal) - 1;
		 }
		 
		 private double normNeg(double val, double maxVal) {
			 return val / maxVal;
		 }

	@Override
	public int getNumInputs() {
		return 6;
	}

	@Override
	public int getNumOutputs() {
		return 1;
	}

	@Override
	public int getControlPerSim() {
		return 2;
	}

	@Override
	public Simulation copy() {
		return new PongSim();
	}

	@Override
	public String toString() {
		return "Pong Simulation";
	}

	@Override
	public void menuInit(MenuItems inputF) {
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public void render(Controller[] c) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Awesome";
		config.width = 700;
		config.height = 500;
		config.resizable = false;
		config.forceExit = false;
		Pong p = new Pong(c[0], c[1]);

		new LwjglApplication(p, config);

		GameScreen s = (GameScreen) p.getStartScreen();

		while (!s.world.checkGameOver()) {
			float accel1 = 0;
			float accel2 = 0;
			accel1 = (float) (20 * c[0].calculate(s.world.paddleP1.y, s.world.ball.x, s.world.ball.y,
					s.world.ball.velocity.x, s.world.ball.velocity.y, s.world.paddleP2.y)[0]);
			accel1 = MathUtils.clamp(accel1, -20, 20);
			accel2 = (float) (20 * c[1].calculate(s.world.paddleP1.y, s.world.ball.x, s.world.ball.y,
					s.world.ball.velocity.x, s.world.ball.velocity.y, s.world.paddleP2.y)[0]);
			accel2 = MathUtils.clamp(accel2, -20, 20);

			s.world.update(spf);
		}
	}

}
