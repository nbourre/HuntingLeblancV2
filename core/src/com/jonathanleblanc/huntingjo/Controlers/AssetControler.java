package com.jonathanleblanc.huntingjo.Controlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

//Asset Controller
//----------------
//- Loads all the resources required to run the game
//- Disposes the resources when game ends

public class AssetControler
{
	//Properties
	//----------
	
	//Animation Index
	public static final int ANIM_HUNTERMAN_DRAW = 0;
	public static final int ANIM_HUNTERMAN_MAGCHANGE = 1;
	public static final int ANIM_HUNTERMAN_RELOAD = 2;
	public static final int ANIM_HUNTERMAN_SHOOT = 3;
	public static final int ANIM_HUNTERMAN_FORWARD = 4;
	public static final int ANIM_CARIBOU_EAT = 5;
	public static final int ANIM_CARIBOU_DEATH = 6;
	public static final int ANIM_CARIBOU_ROT = 7;
	public static final int ANIM_WOLF_WAGTAIL = 8;
	
	//Texture Index
	public static final int TEXT_ROCK = 0;
	public static final int TEXT_BLOOD = 1;
	public static final int TEXT_LEAF = 2;
	public static final int TEXT_FLOWERPETAL = 3;
	public static final int TEXT_FLOWERSTALK = 4;
	public static final int TEXT_TREECANOPY = 5;
	public static final int TEXT_TREETRUNK = 6;
	public static final int TEXT_BULLET = 7;
	public static final int TEXT_CAMPFIRE = 8;
	public static final int[] TEXT_SMOKE = {9, 10, 11};
	public static final int TEXT_CAMERAEDGE = 12;
	public static final int TEXT_HELP = 13;
	
	//Sound Index
	public static final int SOUND_DRAWGUN = 0;
	public static final int SOUND_RELOADGUN = 1;
	public static final int SOUND_MAGCHANGE = 2;
	public static final int SOUND_GUNSHOT = 3;
	public static final int SOUND_DRYCLICKGUN = 4;
	public static final int[] SOUND_HITFLESH = {5, 6, 7};
	public static final int[] SOUND_RICOCHET = {8, 9};
	public static final int SOUND_WOLFBITE = 10;
	public static final int[] SOUND_FOOTSTEP = {11, 12, 13, 14};
	
	//Initialize Lists
	public static ArrayList<Texture> textures;
	public static ArrayList<Animation> animations;
	public static ArrayList<Sound> sounds;
	public static Music music;
	
	//Load animations
	private static void loadAnimations()
	{
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/hunterman_draw.atlas")).getRegions()));
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/hunterman_magchange.atlas")).getRegions()));
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/hunterman_reload.atlas")).getRegions()));
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/hunterman_shoot.atlas")).getRegions()));
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/hunterman_forward.atlas")).getRegions()));
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/caribou.atlas")).getRegions()));
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/caribou_death.atlas")).getRegions()));
		animations.add(new Animation(1/5f, new TextureAtlas(Gdx.files.internal("animations/caribou_rot.atlas")).getRegions()));
		animations.add(new Animation(1/30f, new TextureAtlas(Gdx.files.internal("animations/wolf.atlas")).getRegions()));
	}
	
	//Load textures
	private static void loadTextures()
	{
		textures.add(new Texture(Gdx.files.internal("textures/rock.png")));
		textures.add(new Texture(Gdx.files.internal("textures/blood.png")));
		textures.add(new Texture(Gdx.files.internal("textures/leaf.png")));
		textures.add(new Texture(Gdx.files.internal("textures/flowerpetal.png")));
		textures.add(new Texture(Gdx.files.internal("textures/flowerstalk.png")));
		textures.add(new Texture(Gdx.files.internal("textures/treecanopy.png")));
		textures.add(new Texture(Gdx.files.internal("textures/treetrunk.png")));
		textures.add(new Texture(Gdx.files.internal("textures/bullet.png")));
		textures.add(new Texture(Gdx.files.internal("textures/firepit.png")));
		textures.add(new Texture(Gdx.files.internal("textures/smoke1.png")));
		textures.add(new Texture(Gdx.files.internal("textures/smoke2.png")));
		textures.add(new Texture(Gdx.files.internal("textures/smoke3.png")));
		textures.add(new Texture(Gdx.files.internal("textures/cameraEdge.png")));
		textures.add(new Texture(Gdx.files.internal("textures/help.png")));
	}
	
	//Load sounds
	private static void loadSounds()
	{
		sounds = new ArrayList<Sound> ();
		
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/draw_gun.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/reload_gun.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/magchange.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/gunshot.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/dryclick_gun.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/bullet_hitflesh1.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/bullet_hitflesh2.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/bullet_hitflesh3.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/ricochet1.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/ricochet2.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/wolfbite.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/footstep1.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/footstep2.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/footstep3.mp3")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/footstep4.mp3")));
	}
	
	//Load all resources
	public static void loadAssets()
	{
		textures = new ArrayList<Texture>();
		animations = new ArrayList<Animation>();
		
		loadAnimations();
		loadTextures();
		loadSounds();
	}

	//Dispose all resources
	//(no dispose() method for TextureAtlas)
	public static void dispose()
	{
		for (Texture texture : textures)
		{
			texture.dispose();
		}
		
		for (Sound sound : sounds)
		{
			sound.dispose();
		}
	}	
}
