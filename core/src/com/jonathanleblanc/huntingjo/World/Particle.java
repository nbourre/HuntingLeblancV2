package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;
import com.jonathanleblanc.huntingjo.Tasks.ExpirationTask;
import com.jonathanleblanc.huntingjo.Tasks.FadeOutTask;

import processing.core.PVector;

import static com.jonathanleblanc.huntingjo.World.ParticleEmitter.ParticleType.BLOOD;

//Particle
//- Floats around and gets pushed around by the wind
//- Expires after a given amount of time

public class Particle extends Actor
{
	//Properties
	//----------
	
	private PVector velocity;
	private float transparency;
	private float scale;
	
	private boolean fade_out;
	private float fade_scale;
	
	//Utility methods
	//---------------

	public void fadeOut()
	{
		disposed = true;
	}
	
	//Constructor
	//-----------
	
	public Particle(PVector position, ParticleEmitter.ParticleType type, World world, PVector velocity, float lifespan, float scale)
	{
		super(position, ActorType.WHOCARES, world, false, false);
		
		this.velocity = velocity;
		this.scale = scale;
		transparency = 1;
		fade_scale = 0.99f;

		Timer.schedule(new ExpirationTask(this), lifespan);
		
		if (type != null)
		{
			switch (type)
			{
				case BLOOD:
				{
					texture_index = AssetControler.TEXT_BLOOD;
					transparency = 0.25f;
					break;
				}
				
				case LEAF:
				{
					texture_index = AssetControler.TEXT_LEAF;
					break;
				}
				
				case SMOKE:
				{
					texture_index = AssetControler.TEXT_SMOKE[MathUtils.random(2)];
					break;
				}
				
				default:
				{}
			}
		}
	}
	
	//Update methods
	//--------------
	
	@Override
	public void update(float delta)
	{
		//When you're expired, slowly fade out for a smooth visual transition
		if (fade_out)
		{
			transparency *= fade_scale;
		}
		else if (position.x < 0 || position.y < 0 || position.x > world.getSize().x || position.y > world.getSize().y) dispose();
		
		//Slowly rotate
		rotate_angle += 0.1f;
		
		//Apply wind force
		PVector wind_push = world.getWind().getVelocity();
		wind_push.normalize();
		wind_push.mult(0.1f);
		
		position.add(wind_push);
		position.add(velocity);
	}
	
	@Override
	public void dispose()
	{
		Timer.schedule(new FadeOutTask(this), 10f);
		
		fade_out = true;
	}

	//Draw methods
	//------------
	
	@Override
	public void draw(SpriteBatch batch)
	{
		if (type != null)
		{
			Sprite sprite = new Sprite(AssetControler.textures.get(texture_index));
			sprite.setAlpha(transparency);
			drawSprite(batch, sprite, position.x - AssetControler.textures.get(texture_index).getWidth() / 2, position.y - AssetControler.textures.get(texture_index).getHeight() / 2, scale, rotate_angle);
		}
	}
}
