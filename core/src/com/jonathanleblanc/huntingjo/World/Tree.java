package com.jonathanleblanc.huntingjo.World;

//Tree
//----
//- A noble oak standing in the forest
//- Its limbs sway in the wind

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;

import java.util.ArrayList;

import processing.core.PVector;

public class Tree extends Actor 
{
	//Properties
	//----------
	
	protected ArrayList<TreeLimb> limbs;

	//Constructor
	//-----------
	
	public Tree(PVector position, World world, int numOfLimbs) 
	{
		super(position, ActorType.TREE, world, false, false);
		
		material_type = MaterialType.WOOD;
		
		mass = 500;
		density_scale = 1/6f;
		texture_index = AssetControler.TEXT_TREETRUNK;
		limbs = new ArrayList<TreeLimb> ();
		is_cover = true;
		
		actor_collisions(world.getActors());
		
		for (int i = 0; i < numOfLimbs; i ++)
		{
			PVector pos = PVector.random2D();
			pos.mult(5 + MathUtils.random(10));
			pos.add(position);
			limbs.add(new TreeLimb(pos, world));
		}
	}
	
	//Update methods
	//--------------
	
	@Override
	public void update(float delta)
	{
		//Update your branches
		for (TreeLimb limb : limbs)
		{	
			limb.update(delta);
		}
		
		actor_collisions(world.getActors());
	}

	//Draw methods
	//------------
	
	@Override
	public void draw(SpriteBatch batch)
	{
		drawSprite(batch, new Sprite(AssetControler.textures.get(texture_index)), position.x - AssetControler.textures.get(texture_index).getWidth() / 2, position.y - AssetControler.textures.get(texture_index).getHeight() / 2, 0.5f, rotate_angle);
	}
	
	//Draw all of your branches
	public void draw_limbs(SpriteBatch batch)
	{
		for (TreeLimb limb : limbs)
		{
			limb.draw(batch);
		}
	}
}