package com.matimdev.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.matimdev.manager.ResourcesManager;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public abstract class AppleB extends AnimatedSprite
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	
	private Body body;
	
	private boolean canRun = false;
	private int blowx=0;
	private int footContacts = 0;
	
	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------
	
	public AppleB(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
		//camera.setChaseEntity(this);
	}
	
	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));

		body.setUserData("player");
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				
				if ((getY() <= 0)||(getX()<=0)||(getY() >= 800)||(getX() >= 480))
				{					
					onDie();
				}
				
				if (canRun)
				{	
					body.setLinearVelocity(new Vector2(0, body.getLinearVelocity().y)); 
				}
	        }
		});
	}
	public void setfalling(){
		body.setType(BodyType.DynamicBody);
	}
	
	
	public void setblow(){
		blowx-=3;
		body.setLinearVelocity(new Vector2(blowx, body.getLinearVelocity().y));
	}
	
	public void setRunning()
	{
		canRun = false;
		
		final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100 };
		
		animate(PLAYER_ANIMATE, 0, 2, false);
	}
	
	public void jump()
	{
		if (footContacts < 1) 
		{
			return; 
		}
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 12)); 
	}
	
	public void increaseFootContacts()
	{
		footContacts++;
	}
	
	public void decreaseFootContacts()
	{
		footContacts--;
	}
	
	public abstract void onDie();
}
