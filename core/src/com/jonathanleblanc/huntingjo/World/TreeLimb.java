package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;

import processing.core.PVector;

//Tree Limb
//---------
//- Sways in the breeze

public class TreeLimb extends Actor
{
	//Properties
	//----------
	
	protected float length;
	protected float maxspeed;
	protected float transparency;
	protected float fluffiness;
	
	protected PVector original_position;
	protected PVector acceleration;
	protected PVector velocity;
	
	//Utility methods
	//---------------

	public void applyForce(PVector force)
	{
		force.div(mass);
		acceleration.add(force);
	}
	
	//Constructor
	//-----------
	
	public TreeLimb(PVector position, World world) 
	{
		super(position, ActorType.WHOCARES, world, false, false);
		
		texture_index = AssetControler.TEXT_TREECANOPY;
		
		original_position = position.get();
		acceleration = new PVector();
		velocity = new PVector();
		
		length = 10 + MathUtils.random(10);
		mass = 50 + MathUtils.random(20);
		maxspeed = 0.2f;
		transparency = 1;
		fluffiness = MathUtils.random(0.3f);
	}
	
	//Update methods
	//--------------
	
	@Override
	public void update(float delta)
	{
		check_for_player();
		wind_push();
		elastic_pull();
		movement();
	}
	
	//Become transparent if the Hunter is underneath you
	protected void check_for_player()
	{
		if (PVector.dist(position, world.getHunter().getPosition()) > 40) transparency = 1;
		else transparency = 0.2f;
	}
	
	//Apply the wind force
	private void wind_push()
	{
		PVector wind_push = world.getWind().getVelocity().get();
		wind_push.normalize();
		
		if (PVector.dist(position, original_position) < length)
		{
			applyForce(wind_push);
		}
		else 
		{
			PVector pull = PVector.sub(original_position, position);
			pull.normalize();
			pull.mult(length);
			pull.add(position);
			
			position = pull;
		}
	}
	
	//The elastic force bringing you back to your original position
	private void elastic_pull()
	{
		PVector pull = PVector.sub(original_position, position);
		pull.normalize();
		pull.mult(1.1f);
		
		applyForce(pull);
	}
	
	private void movement()
	{
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		
		position.add(velocity);
		
		acceleration.mult(0);
	}
	
	//Draw methods
	//------------
	
	@Override
	public void draw(SpriteBatch batch)
	{
		drawSprite(batch, new Sprite(AssetControler.textures.get(texture_index)), position.x - AssetControler.textures.get(texture_index).getWidth() / 2, position.y - AssetControler.textures.get(texture_index).getHeight() / 2, 1 + fluffiness, rotate_angle);
	}
}
