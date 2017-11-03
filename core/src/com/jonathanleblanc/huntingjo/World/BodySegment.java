package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.math.Circle;

import processing.core.PVector;

//Body Segment
//- Physical unit making up all Actors
//- Required for collision-checking and damage modeling

public class BodySegment
{
	//Properties
	//----------
	
	//Body Segment Types
	public static enum BodySegmentType
	{
		HEAD,
		CHEST,
		ARSE
	}
	
	private Actor owner;
	private PVector position;
	private float damage_model;
	private float size_radius;
	private float offset;
	
	//Utility methods
	//---------------
	
	//Checks if it collides with another segment
	public boolean collidesWith(BodySegment other)
	{
		return (position.dist(other.getPosition()) <= size_radius + other.getSizeRadius());
	}
	
	//Checks if a given position is within its bounds
	public boolean containsPoint(PVector point)
	{
		return (point.dist(position) <= size_radius);
	}

	//Getter/Setter methods
	//---------------------

	public void setPosition(PVector position)
	{
		this.position = position;
	}
	
	public PVector getPosition()
	{
		return position;
	}
	
	public float getSizeRadius()
	{
		return size_radius;
	}
	
	public Circle getCircle()
	{
		return new Circle(position.x, position.y, size_radius);
	}
	
	public Actor getOwner()
	{
		return owner;
	}
	
	public void setSizeRadius(float size_radius)
	{
		this.size_radius = size_radius;
	}
	
	public float getOffset()
	{
		return offset;
	}
	
	public float getDamageModel()
	{
		return damage_model;
	}
	
	//Constructors
	//------------
	
	public BodySegment(Actor owner, PVector position, float size_radius)
	{
		this.owner = owner;
		this.position = position;
		this.size_radius = size_radius;
		offset = 0;
		damage_model = 1;
	}
	
	public BodySegment(Actor owner, PVector position, float size_radius, float offset, float damage_model)
	{
		this.owner = owner;
		this.position = position;
		this.size_radius = size_radius;
		this.offset = offset;
		this.damage_model = damage_model;
	}
}