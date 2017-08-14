package com.matimdev.scene;

import java.io.IOException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.matimdev.base.BaseScene;
import com.matimdev.extras.LevelCompleteWindow;
import com.matimdev.extras.LevelCompleteWindow.StarsCount;
import com.matimdev.manager.ResourcesManager;
import com.matimdev.manager.SceneManager;
import com.matimdev.manager.SceneManager.SceneType;
import com.matimdev.object.Player;


/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class GameScene extends BaseScene implements IOnMenuItemClickListener
{
	private int score = 0;
	
	private HUD gameHUD;
	private Text scoreText;
	private Text gameOverText;
	private PhysicsWorld physicsWorld;
	private LevelCompleteWindow levelCompleteWindow;
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
    private MenuScene menuChildScene;
	
	private final int ROPE = 0;
	private final int ABUBBLE = 1;
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BUBBLE = "bubble";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";
	
	private Player player;
	
	
	
	
	private boolean gameOverDisplayed = false;
	SpriteMenuItem rope;
	SpriteMenuItem Abubble;
	
	
	@Override
	public void createScene()
	{
		createBackground();
        
  		createHUD();
  		createGameChildScene();
        
// createPhysics() causes application termination!!!
  		createPhysics();
		loadLevel(1);
		createGameOverText();
		
		levelCompleteWindow = new LevelCompleteWindow(vbom);
		
		

	}
	
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
			case ROPE:
				ResourcesManager.getInstance().Cut.play();
				rope.setVisible(false);
				player.setfalling();
				
				return true;
			case ABUBBLE:
				
				
				return true;	
			
			default:
				return false;
		}
	}
	private void createGameChildScene()
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		
		
		rope=new SpriteMenuItem(ROPE, resourcesManager.rope_region, vbom);
		final IMenuItem ROPEItem = new ScaleMenuItemDecorator(rope, 1f, 1);
		
		
		
		
		menuChildScene.addMenuItem(ROPEItem);
		
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
//		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 10);
		ROPEItem.setPosition(-155, ROPEItem.getY()+120);
		
		
		menuChildScene.setOnMenuItemClickListener((IOnMenuItemClickListener) this);
		
		setChildScene(menuChildScene);
	}

	

	@Override
	public void onBackKeyPressed()
	{
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene()
	{
		camera.setHUD(null);
		camera.setChaseEntity(null); //TODO
		camera.setCenter(240,400);
		
		// TODO code responsible for disposing scene
		// removing all game scene objects.
	}
	
	
	private void loadLevel(int levelID)
	{
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
			{
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
				
				camera.setBounds(0, 0, width, height); // here we set camera bounds
				camera.setBoundsEnabled(true);

				return GameScene.this;
			}
		});
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
			{
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
				
				final Sprite levelObject;
				
				if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1))
				{
					levelObject = new Sprite(x, y, resourcesManager.platform1_region, vbom);
					PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF).setUserData("platform1");
				} 
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2))
				{
					levelObject = new Sprite(x, y, resourcesManager.platform2_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform2");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3))
				{
					levelObject = new Sprite(x, y, resourcesManager.platform3_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform3");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN))
				{
					levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom)
					{
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) 
						{
							super.onManagedUpdate(pSecondsElapsed);

							if (player.collidesWith(this))
							{
								addToScore(10);
								ResourcesManager.getInstance().Leaf.play();
								this.setVisible(false);
								this.setIgnoreUpdate(true);
							}
						}
					};
					levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BUBBLE))
				{
					levelObject = new Sprite(x, y, resourcesManager.bubble_region, vbom)
					{
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) 
						{
							super.onManagedUpdate(pSecondsElapsed);

							if (player.collidesWith(this))
							{
								
								
								this.setVisible(false);
								this.setIgnoreUpdate(true);
							}
						}
					};
					
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER))
				{
					player = new Player(x,y, vbom, camera, physicsWorld)
					{
						@Override
						public void onDie()
						{
							if (!gameOverDisplayed)
							{
								displayGameOverText();
							}
						}
					};
					levelObject = player;
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE))
				{
					levelObject = new Sprite(x, y, resourcesManager.complete_stars_region, vbom)
					{
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) 
						{
							super.onManagedUpdate(pSecondsElapsed);

							if (player.collidesWith(this))
							{
								ResourcesManager.getInstance().Eat.play();
								levelCompleteWindow.display(StarsCount.THREE, GameScene.this, camera);
								this.setVisible(false);
								this.setIgnoreUpdate(true);
								player.setVisible(false);
								SceneManager.getInstance().loadGame2Scene(engine);
								
							}
						}
					};
					
				}	
				else
				{
					throw new IllegalArgumentException();
				}

				levelObject.setCullingEnabled(true);

				return levelObject;
			}
		});

		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	}
	
	private void createGameOverText()
	{
		gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);
	}
	
	private void displayGameOverText()
	{
		camera.setChaseEntity(null);
		gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(gameOverText);
		gameOverDisplayed = true;
	}
	
	private void createHUD()
	{
		gameHUD = new HUD();
		
		scoreText = new Text(20, 720, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setAnchorCenter(0, 0);	
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);
		
		camera.setHUD(gameHUD);
	}
	
	private void createBackground()
	{
		ParallaxBackground background = new ParallaxBackground(0, 0, 0);
	    background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(240,400, resourcesManager.game_background_region, vbom)));
	    setBackground(background);
	}
	
	private void addToScore(int i)
	{
		score += i;
		scoreText.setText("Score: " + score);
	}
	
	private void createPhysics()
	{
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false); 
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	// ---------------------------------------------
	// INTERNAL CLASSES
	// ---------------------------------------------

	private ContactListener contactListener()
	{
		ContactListener contactListener = new ContactListener()
		{
			public void beginContact(Contact contact)
			{
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
				{
					if (x2.getBody().getUserData().equals("player"))
					{
						player.increaseFootContacts();
					}
					
					if (x1.getBody().getUserData().equals("platform2") && x2.getBody().getUserData().equals("player"))
					{
						engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback()
						{									
						    public void onTimePassed(final TimerHandler pTimerHandler)
						    {
						    	pTimerHandler.reset();
						    	engine.unregisterUpdateHandler(pTimerHandler);
						    	x1.getBody().setType(BodyType.DynamicBody);
						    }
						}));
					}
					
					if (x1.getBody().getUserData().equals("platform3") && x2.getBody().getUserData().equals("player"))
					{
// the following line was commented by HK Chiang, 04/28/2014
//						x1.getBody().setType(BodyType.DynamicBody);

// the following 8 lines was added by HK Chiang, 04/28/2014						
						engine.registerUpdateHandler(new TimerHandler(0.05f, new ITimerCallback()
						{									
						    public void onTimePassed(final TimerHandler pTimerHandler)
						    {
						    	pTimerHandler.reset();
						    	engine.unregisterUpdateHandler(pTimerHandler);
						    	x1.getBody().setType(BodyType.DynamicBody);
						    }
						}));
// end; HK Chiang
					}
				}
			}

			public void endContact(Contact contact)
			{
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
				{
					if (x2.getBody().getUserData().equals("player"))
					{
						player.decreaseFootContacts();
					}
				}
			}

			public void preSolve(Contact contact, Manifold oldManifold)
			{

			}

			public void postSolve(Contact contact, ContactImpulse impulse)
			{

			}
		};
		return contactListener;
	}
	
}