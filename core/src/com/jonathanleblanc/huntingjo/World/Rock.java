package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;

import processing.core.PVector;

//Rock
//----
//- A very bland rock formation

public class Rock extends Actor
{
	//Constructor
	//-----------
	
	public Rock(PVector position, World world) 
	{
		super(position, ActorType.ROCK, world, false, false);

		material_type = MaterialType.STONE;
		
		texture_index = AssetControler.TEXT_ROCK;
		
		mass = 1000;
		size_radius = 30;
		body.get(0).setSizeRadius(size_radius);
		is_cover = true;
	}

	//Draw methods
	//------------
	
	@Override
	public void draw(SpriteBatch batch)
	{
		drawSprite(batch, new Sprite(AssetControler.textures.get(texture_index)), position.x - AssetControler.textures.get(texture_index).getWidth() / 2, position.y - AssetControler.textures.get(texture_index).getHeight() / 2, 1.5f, rotate_angle);
	}
}
