package com.matimdev.extras;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.matimdev.manager.ResourcesManager;

public class LevelCompleteWindow extends Sprite
{
	private Sprite star1;
	private Sprite star2;
	private Sprite star3;
	
	
	
	public enum StarsCount
	{
		ONE,
		TWO,
		THREE
	}
	
	public LevelCompleteWindow(VertexBufferObjectManager pSpriteVertexBufferObject)
	{
		super(0, 0, 347, 373, ResourcesManager.getInstance().complete_window_region, pSpriteVertexBufferObject);
		attachStars(pSpriteVertexBufferObject);
	}
	
	private void attachStars(VertexBufferObjectManager pSpriteVertexBufferObject)
	{
		star1 = new Sprite(100, 150, ResourcesManager.getInstance().coin_region, pSpriteVertexBufferObject);
		star2 = new Sprite(170, 150, ResourcesManager.getInstance().coin_region, pSpriteVertexBufferObject);
		star3 = new Sprite(240, 150, ResourcesManager.getInstance().coin_region, pSpriteVertexBufferObject);
		
		attachChild(star1);
		attachChild(star2);
		attachChild(star3);
	}
	
	/**
	 * Change star`s tile index, depends on stars count.
	 * @param starsCount
	 */
	public void display(StarsCount starsCount, Scene scene, Camera camera)
	{
		// Change stars tile index, based on stars count (1-3)
		switch (starsCount)
		{
			case ONE:
				star1.setZIndex(0);
				star2.setZIndex(1);
				star3.setZIndex(1);
				break;
			case TWO:
				star1.setZIndex(0);
				star2.setZIndex(0);
				star3.setZIndex(1);
				break;
			case THREE:
				camera.getHUD().setVisible(true);
				camera.getHUD().setPosition(170, 150);
				break;
		}
		
		
		
		// Hide HUD
		
		
		// Disable camera chase entity
		camera.setChaseEntity(null);
		
		// Attach our level complete panel in the middle of camera
		setPosition(camera.getCenterX(), camera.getCenterY());
		scene.attachChild(this);
	}
}