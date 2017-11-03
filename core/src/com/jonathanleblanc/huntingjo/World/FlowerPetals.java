package com.jonathanleblanc.huntingjo.World;


import com.jonathanleblanc.huntingjo.Controlers.AssetControler;

import processing.core.PVector;

//Flower Petals
//- Little blue buttercups fluttering in the breeze

public class FlowerPetals extends TreeLimb
{
	//Utility methods
	//---------------
	
	//Whereas a tree limb is above ground and hides the Hunter if he is underneath
		//	A flower is underneath the Hunter and doesn't need to become transparent
	@Override
	protected void check_for_player()
	{}
	
	//Constructor
	//-----------
	
	public FlowerPetals(PVector position, World world) 
	{
		super(position, world);
		
		texture_index = AssetControler.TEXT_FLOWERPETAL;
		fluffiness = -0.2f;
		mass = mass / 5;
	}
}