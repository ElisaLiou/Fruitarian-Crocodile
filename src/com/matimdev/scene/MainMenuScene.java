package com.matimdev.scene;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.matimdev.base.BaseScene;
import com.matimdev.manager.ResourcesManager;
import com.matimdev.manager.SceneManager;
import com.matimdev.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener
{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	
	private MenuScene menuChildScene;
	
	private final int MENU_PLAY = 0;
	
	
	//---------------------------------------------
	// METHODS FROM SUPERCLASS
	//---------------------------------------------

	@Override
	public void createScene()
	{
		creatMusic();
		createBackground();
		createMenuChildScene();
		
		
		
	}
	
	
	
	public void creatMusic(){
		ResourcesManager.getInstance().BgMusic.play();
	}

	@Override
	public void onBackKeyPressed()
	{
		System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_MENU;
	}
	

	@Override
	public void disposeScene()
	{
		// TODO Auto-generated method stub
	}
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
			case MENU_PLAY:
				//Load Game Scene!
				
				SceneManager.getInstance().loadGameScene(engine);
				return true;
			
			default:
				return false;
		}
	}
	
	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------
	
	private void createBackground()
	{
		attachChild(new Sprite(240,400, resourcesManager.menu_background_region, vbom)
		{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
		});
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
		
		
		menuChildScene.addMenuItem(playMenuItem);
		
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
//		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() + 10);
		playMenuItem.setPosition(-170, playMenuItem.getY() - 300);

		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
}