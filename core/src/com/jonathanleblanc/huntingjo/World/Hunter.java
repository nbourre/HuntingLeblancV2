package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;
import com.jonathanleblanc.huntingjo.Renderers.GameRenderer;
import com.jonathanleblanc.huntingjo.Tasks.BarrelCooldownTask;
import com.jonathanleblanc.huntingjo.Tasks.BarrelPuffTask;
import com.jonathanleblanc.huntingjo.Tasks.ChangeMagazineTask;
import com.jonathanleblanc.huntingjo.Tasks.DryclickReleaseTask;
import com.jonathanleblanc.huntingjo.Tasks.RechamberRoundTask;
import com.jonathanleblanc.huntingjo.Tasks.ReleaseTriggerTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.core.PVector;

//Hunter
//------
//- The simulator's main protagonist
//- Can move around and explore the world
//- Can use his rifle to shoot animals

public class Hunter extends Actor
{
	//Properties
	//----------
	
	//This simulator features one playable rifle:
	//the Remington 700 chambered in .300 Magnum
	final private int MAGAZINE_SIZE = 5;
	final private float MUZZLE_VELOCITY = 990;
	final private float ATTACK_POWER = 0.8f;
	final private float PATTERN_SIZE = 1;
	
	final public float BULLET_DRAG_COEFFICIENT = 0.281f;
	final public float BULLET_FRONTAL_AREA = 0.0745f;
	
	//Action States
	private enum ActionState 
	{
		DRAW,
		MAGCHANGE,
		RELOAD,
		SHOOT,
		CARRYTROPHY,
		WAIT
	}
	
	private Map <ActionState, Integer> states = new HashMap <ActionState, Integer>();
	{
		states.put(ActionState.DRAW, AssetControler.ANIM_HUNTERMAN_DRAW);
		states.put(ActionState.MAGCHANGE, AssetControler.ANIM_HUNTERMAN_MAGCHANGE);
		states.put(ActionState.RELOAD, AssetControler.ANIM_HUNTERMAN_RELOAD);
		states.put(ActionState.SHOOT, AssetControler.ANIM_HUNTERMAN_SHOOT);
		states.put(ActionState.CARRYTROPHY, AssetControler.ANIM_HUNTERMAN_DRAW);
		states.put(ActionState.WAIT, AssetControler.ANIM_HUNTERMAN_DRAW);
	};
	
	protected ArrayList<Bullet> bullets_in_flight;
	
	protected float max_speed;
	protected float friction;
	protected float acceleration_speed;
	protected float direction_angle;
	protected float look_speed;
	protected float look_steerspeed;
	
	protected PVector acceleration;
	protected PVector velocity;
	protected PVector direction;
	protected PVector look_position;
	protected PVector look_velocity;
	protected PVector look_acceleration;
	protected PVector muzzle_position;

	protected boolean trigger_released;
	protected boolean round_chambered;
	protected boolean begun_reload;
	protected boolean begun_rechambering;
	protected int rounds_in_magazine;
	protected boolean barrel_hot;
	protected boolean barrel_smoked;
	protected boolean can_dryclick;
	
	protected int move_animation;
	protected ActionState action_state;
	
	protected float upper_st;
	protected float lower_st;

	//Utility methods
	//---------------

	//Gunshot event
	private void dischargeWeapon()
	{
		if (action_state != ActionState.WAIT)
		{
			rounds_in_magazine --;
			
			//Spawn bullet
			PVector pattern = new PVector(MathUtils.random(-PATTERN_SIZE, PATTERN_SIZE), MathUtils.random(-PATTERN_SIZE, PATTERN_SIZE));
			float angle = (MathUtils.atan2((world.getRenderer().getMouse().y + pattern.y) - position.y, (world.getRenderer().getMouse().x + pattern.x) - position.x)); //MathUtils.radiansToDegrees *
			
			PVector muzzle_velocity = PVector.fromAngle(angle);
			muzzle_velocity.mult(MUZZLE_VELOCITY / 10);
			
			Bullet shot = new Bullet(this, world, position.get(), muzzle_velocity, world.getWind().getDragForce(muzzle_velocity, BULLET_FRONTAL_AREA, BULLET_DRAG_COEFFICIENT), ATTACK_POWER);
			bullets_in_flight.add(shot);
			
			//Emit noise
			emitAlert(ActorType.NOISE, 1000, 10, AssetControler.SOUND_GUNSHOT);
			
			//Particle effects
			world.getParticleEmitters().add(new ParticleEmitter(muzzle_position, world, 2+ MathUtils.random(2), ParticleEmitter.ParticleType.SMOKE, 2/3f));
			
			if (!barrel_hot)
			{
				Timer.schedule(new BarrelCooldownTask(this), 3);
				barrel_hot = true;
			}
		}
	}
	
	//Begin rechambering a round
	private void rechamberRound()
	{
		begun_rechambering = true;
		action_state = ActionState.RELOAD;
		
		Timer.schedule(new RechamberRoundTask(this), 1.5f);
		
		upper_st = 0;

		emitAlert(ActorType.NOISE, 250, 3, AssetControler.SOUND_RELOADGUN);
	}
	
	//Rechambering round event
	public void chamberRound()
	{
		if (action_state == ActionState.RELOAD)
		{	
			upper_st = 30;
			begun_rechambering = false;
			action_state = ActionState.DRAW;
			round_chambered = true;
		}
	}
	
	//Release the trigger event
	public void releaseTrigger()
	{
		if (action_state == ActionState.SHOOT)
		{
			action_state = ActionState.DRAW;
			upper_st = 30;
			
			if (rounds_in_magazine == 0)
			{
				do_reload();
			}
			else if (!round_chambered)
			{
				rechamberRound();
			}
			
			trigger_released = true;
		}
	}
	
	//Magazine change event
	public void changeMagazine()
	{
		if (action_state == ActionState.MAGCHANGE)
		{
			upper_st = 30;
			action_state = ActionState.DRAW;
			begun_reload = false;
			
			if (!round_chambered)
			{
				rechamberRound();
			}
	
			rounds_in_magazine = MAGAZINE_SIZE;
		}
	}
	
	//Barrel cooldown event
	public void cooldownBarrel()
	{
		barrel_hot = false;
	}
	
	//Barrel smoke event
	public void puffBarrel()
	{
		barrel_smoked = false;
	}
	
	//Dry click event
	public void canDryClick()
	{
		can_dryclick = true;
	}
	
	//Getting the direction angle for drawing the leg animation
	protected float getDirectionAngle()
	{
	    float angle = MathUtils.radiansToDegrees * (MathUtils.atan2((position.y + velocity.y) - position.y, (position.x + velocity.x) - position.x));
		if (angle < 0) angle = 360 + angle;
		return angle;
	}
	
	//Getter/Setter methods
	//---------------------
	
	public ArrayList<Bullet> getBulletsInFlight()
	{
		return bullets_in_flight;
	}
	
	public PVector getLookFocus()
	{
		return look_position;
	}
	
	//Constructor
	//-----------
	
	public Hunter(PVector position, World world) 
	{
		super(position, ActorType.HUNTER, world, true, true);		
		
		bullets_in_flight = new ArrayList<Bullet> ();
		
		acceleration = new PVector();
		velocity = new PVector();
		direction = new PVector();
		look_position = position.get();
		look_velocity = new PVector();
		look_acceleration = new PVector();
		muzzle_position = new PVector();
		
		trigger_released = true;
		round_chambered = true;
		rounds_in_magazine = MAGAZINE_SIZE;
		begun_reload = false;
		begun_rechambering = false;
		can_dryclick = true;
		
		action_state = ActionState.DRAW;
		move_animation = AssetControler.ANIM_HUNTERMAN_FORWARD;
				
		direction = new PVector(0, 0, 0);
		friction = 0.1f;
		max_speed = 1.7f;
		acceleration_speed = 0.3f;
		mass = 50;
		look_speed = 10;
		look_steerspeed = 0.06f;
		
		type = ActorType.HUNTER;
		
		upper_st = 0;
		lower_st = 0;
	}
	
	//Controller methods
	//-------------------------

	public void move_right()
	{
		direction.add(new PVector(1, 0));
	}
	
	public void move_up()
	{
		direction.add(new PVector(0, 1));
	}
	
	public void move_left()
	{
		direction.add(new PVector(-1, 0));
	}
	
	public void move_down()
	{
		direction.add(new PVector(0, -1));
	}
	
	public void do_reload()
	{
		if (!begun_rechambering && !begun_reload && trigger_released && rounds_in_magazine < MAGAZINE_SIZE)
		{
			begun_reload = true;
			upper_st = 0;
			action_state = ActionState.MAGCHANGE;

			emitAlert(ActorType.NOISE, 250, 3, AssetControler.SOUND_MAGCHANGE);
			
			Timer.schedule(new ChangeMagazineTask(this), 2);
		}
	}
	
	public void do_fire()
	{
		if (rounds_in_magazine > 0)
		{
			if (trigger_released && round_chambered)
			{
				upper_st = 0;
				action_state = ActionState.SHOOT;
				trigger_released = false;
				round_chambered = false;
	
				dischargeWeapon();
				
				Timer.schedule(new ReleaseTriggerTask(this), 1 + MathUtils.random(0.2f));
			}
			
		}
		else
		{
			if (can_dryclick)
			{
				emitAlert(ActorType.NOISE, 250, 3, AssetControler.SOUND_DRYCLICKGUN);
				
				Timer.schedule(new DryclickReleaseTask(this), 1/3f);
				can_dryclick = false;
			}
			
		}
	}
	
	//Update methods
	//--------------
	
	@Override
	public void update(float delta)
	{
		update_direction();
		update_bullets(delta);
		update_animations(delta);
		update_movement();
		update_look();
		
		limits();
		actor_collisions(world.getActors());
	}
	
	//Having processed the user input
	//	Normalize the direction vector and add it to the acceleration
	private void update_direction()
	{
		direction.setMag(acceleration_speed);
		
		if (action_state != ActionState.WAIT)
		{
			acceleration.add(direction);
		}
		
		direction = new PVector();
	}
	
	//Update the bullets still in flight
	private void update_bullets(float delta)
	{
		ArrayList<Bullet> expired_bullets = new ArrayList<Bullet> ();
		for (Bullet bullet : bullets_in_flight)
		{
			bullet.update(delta);
			if (bullet.isDisposed()) expired_bullets.add(bullet);
		}
		
		//Get rid of expired bullets
		for (Bullet expired_bullet : expired_bullets)
		{
			bullets_in_flight.remove(expired_bullet);
		}
	}
	
	//Update the Hunter's state time for upper and lower layer animations
	private void update_animations(float delta)
	{
		//Lower animation (running legs)
		if (velocity.mag() >= 0.1)
		{
			lower_st += delta * (velocity.mag() / max_speed);
			if (lower_st > 40/30f) lower_st = 0;
			
			//If a foot hits the ground emit movement noise
			if ((int) (lower_st * 10) == 3 || (int) (lower_st * 10) == 10)
			{
				if (!footfall) 
				{
					movementNoise();
					footfall = true;
				}
			}
			else footfall = false;
		}
		
		//Upper animation (gun-wielding arms)
		if (action_state == ActionState.CARRYTROPHY || action_state == ActionState.WAIT) upper_st = 0;
		else if (upper_st < 30) upper_st += delta;
		else upper_st = 30;
	}
	
	//Update positions and velocity
	private void update_movement()
	{
		float actual_maxspeed = max_speed;
		if (!noisy) actual_maxspeed -= 0.7f;
		
		//Movement
		velocity.add(acceleration);
	    velocity.limit(actual_maxspeed);

	    //Apply friction
	    PVector friction_vector = velocity.get();
	    friction_vector.mult(-friction);
	    velocity.add(friction_vector);
	    if (velocity.mag() < 0.0001) velocity.mult(0);
	    
	    //Update angle
	    direction_angle = getDirectionAngle();
	    
	    position.add(velocity);
	    acceleration.mult(0);
	    
	    //Update gun barrel muzzle position
		muzzle_position = position.get();
		PVector dir = GameRenderer.vector3toProcessing( world.getRenderer().getMouse());
		dir.sub(position);
		dir.normalize();
		dir.mult(50);
		muzzle_position.add(dir);
		
		//Emit smoke if barrel is hot
		if (barrel_hot)
		{
			if (!barrel_smoked)
			{
				world.getParticleEmitters().add(new ParticleEmitter(muzzle_position, world, 1, ParticleEmitter.ParticleType.SMOKE, 1/6f));
				
				Timer.schedule(new BarrelPuffTask(this), 0.05f);
				barrel_smoked = true;
			}
		}
	}
	
	//Cause the GameRenderer's camera to follow the mouse cursor
	private void update_look()
	{
		//Get camera velocity
		PVector mouse = new PVector(world.getRenderer().getMouse().x, world.getRenderer().getMouse().y);
		PVector dir = PVector.sub(mouse, look_position);
		dir.normalize();
		dir.mult(PVector.dist(mouse, look_position) * 0.02f);
		dir.limit(10);
		
		//Apply velocity
		look_position.add(dir);
		
		//Limit the camera's movement
		if (PVector.dist(position, look_position) > (world.getRenderer().getCameraSize().y / 2) - 10)
		{
			PVector dir2 = PVector.sub(look_position, position);
			dir2.normalize();
			dir2.mult((world.getRenderer().getCameraSize().y / 2) - 10);
			
			look_position = position.get();
			look_position.add(dir2);
		}

		//Update the rotate angle for drawing the arms with the gun
		PVector mouse_position = new PVector(world.getRenderer().getMouse().x, world.getRenderer().getMouse().y);
		float angle;
	    angle = MathUtils.radiansToDegrees * (MathUtils.atan2(mouse_position.y - position.y, mouse_position.x - position.x));
	    if (angle < 0) angle = 360 + angle;
		rotate_angle = angle;
	}
	
	//Make sure the Hunter can't go outside the world bounds
	private void limits()
	{
		if (position.x < body.get(0).getSizeRadius()) position.x = body.get(0).getSizeRadius();
		if (position.y < body.get(0).getSizeRadius()) position.y = body.get(0).getSizeRadius();
		if (position.x > world.getSize().x - body.get(0).getSizeRadius()) position.x = world.getSize().x - body.get(0).getSizeRadius();
		if (position.y > world.getSize().y - body.get(0).getSizeRadius()) position.y = world.getSize().y - body.get(0).getSizeRadius();
	}

	//Draw methods
	//------------
	
	@Override
	public void draw(SpriteBatch batch)
	{

		Sprite sprite = new Sprite((TextureRegion) AssetControler.animations.get(move_animation).getKeyFrame(lower_st, true));
		drawSprite(batch, sprite, (position.x) - sprite.getRegionWidth()/2, (position.y) - sprite.getRegionHeight()/2, 0.5f, direction_angle);
		drawSprite(batch, new Sprite((TextureRegion) AssetControler.animations.get(states.get(action_state)).getKeyFrame(upper_st, false)), position.x - 40, position.y - 65, 0.5f, 40, 65, rotate_angle);
		
		//Bullets
		for (Bullet bullet : bullets_in_flight)
		{
			bullet.draw(batch);
		}
	}
}