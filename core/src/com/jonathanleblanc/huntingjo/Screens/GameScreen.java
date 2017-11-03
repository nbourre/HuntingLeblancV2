package com.jonathanleblanc.huntingjo.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;
import com.jonathanleblanc.huntingjo.Controlers.HunterControler;
import com.jonathanleblanc.huntingjo.Renderers.GameRenderer;
import com.jonathanleblanc.huntingjo.World.World;


import processing.core.PVector;

//Game Screen
//-----------

public class GameScreen implements Screen, InputProcessor
{
	//Properties
	//----------
	
	private World world;
	private GameRenderer renderer;
	private HunterControler controller;
	
	//Key Index
	public static int KEY_RIGHT = Keys.D;
	public static int KEY_UP = Keys.W;
	public static int KEY_LEFT = Keys.A;
	public static int KEY_DOWN = Keys.S;
	public static int KEY_STEALTH = Keys.SHIFT_LEFT;
	public static int KEY_RELOAD = Keys.R;
	public static int KEY_HELP = 104;
	public static int KEY_EXIT = 27;
	
	//Utility methods
	//---------------

	private void exit_game()
	{
		AssetControler.dispose();
		Gdx.app.exit();
	}
	
	//Getter/Setter methods
	//---------------------
	
	public GameRenderer getRenderer()
	{
		return renderer;
	}
	
	//Screen methods
	//--------------
	
	@Override
	public void show() 
	{
		//Create new world
		world = new World(this, new PVector(2000, 2000));
		
		//Set up the renderer
		renderer = new GameRenderer(world);
		
		//Set up the hunter controls
		controller = new HunterControler(world);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) 
	{
		//Clear the gl buffers and draw background
		Gdx.gl.glClearColor(0.29f, 0.49f, 0.09f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
	    //Update the mouse
		renderer.update_mouse(Gdx.input.getX(), Gdx.input.getY());
	    
		//Process user input
		controller.update(delta);
		
		//Update the world
		world.update(delta);
		
		//Display the world on the screen
		renderer.render(delta);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void resize(int width, int height) 
	{
		renderer.setSize(width, height);
	}

	@Override
	public void hide() 
	{
		Gdx.input.setInputProcessor(null);
	}
	
	@Override
	public boolean keyDown(int keycode) 
	{
		if (keycode == KEY_RIGHT)
			controller.rightPressed();
		if (keycode == KEY_UP)
			controller.upPressed();
		if (keycode == KEY_LEFT)
			controller.leftPressed();
		if (keycode == KEY_DOWN)
			controller.downPressed();
		if (keycode == KEY_STEALTH)
			controller.stealthPressed();
		if (keycode == KEY_RELOAD)
			controller.reloadPressed();
		
		return true;
	}

	//Keyboard input
	//--------------
	
	@Override
	public boolean keyUp(int keycode) 
	{
		if (keycode == KEY_RIGHT)
			controller.rightReleased();
		if (keycode == KEY_UP)
			controller.upReleased();
		if (keycode == KEY_LEFT)
			controller.leftReleased();
		if (keycode == KEY_DOWN)
			controller.downReleased();
		if (keycode == KEY_STEALTH)
			controller.stealthReleased();
		if (keycode == KEY_RELOAD)
			controller.reloadReleased();
		
		return true;
	}
	
	@Override
	public boolean keyTyped(char character) 
	{
		if (character == KEY_HELP) world.toggle_help();
		if (character == KEY_EXIT) exit_game();
			
		return false;
	}

	@Override
	public void dispose() 
	{
		Gdx.input.setInputProcessor(null);
	}

	//Mouse input
	//-----------
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) 
	{
		controller.firePressed();
		
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) 
	{
		controller.fireReleased();
		
		return true;
	}
	
	//----------------------------------------------
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) 
	{
		return false;
	}
	
	@Override
	public void pause() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	

}