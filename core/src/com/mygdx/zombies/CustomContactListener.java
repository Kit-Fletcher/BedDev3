package com.mygdx.zombies;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.zombies.items.PowerUp;
import com.mygdx.zombies.items.Projectile;
import com.mygdx.zombies.items.Weapon;
import com.mygdx.zombies.states.StateManager;

/**
 * Class for handling Box2D collisions and collision events
 */
public class CustomContactListener implements ContactListener {
	
	/*
	 * Collision event method called when Box2D objects collide
	 */
	@Override
	public void beginContact(Contact contact) {
		
		//Get the Box2D bodies that collided
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		//Extract extra data from the bodies
		InfoContainer a = (InfoContainer)bodyA.getUserData();
		InfoContainer b = (InfoContainer)bodyB.getUserData();
			
		//There should never be a situation where a and b are null,
		//Only walls are null and they do not collide with each other.
		InfoContainer.BodyID aType = a == null ? InfoContainer.BodyID.WALL : a.getType();
		InfoContainer.BodyID bType = b == null ? InfoContainer.BodyID.WALL : b.getType();
		
		//Sorted alphabetically so aType before bType
		if (aType.name().compareTo(bType.name()) >= 0) {
			InfoContainer.BodyID tempType = aType;
			aType = bType;
			bType = tempType;		
			InfoContainer tempInfoContainer = a;
			a = b;
			b = tempInfoContainer;
		}
		
		//Switch statement to allow different collision events for different object collisions.
		//Remember that a has been sorted to be before b alphabetically, so objects will only occur in a specific order
		switch(aType) {
		
			case WALL:
				if (bType == InfoContainer.BodyID.ZOMBIE) {
					Enemy zombie = (Enemy)b.getObj();
					//zombie.reverseVelocity();
					System.out.println("Collision between zombie and wall");
				}
				break;
				
			case GATE:
				if(bType == InfoContainer.BodyID.PLAYER) {
					Gate gate = (Gate)a.getObj();
					StateManager.loadState(gate.getDestination(), gate.getEntryID());
					System.out.println("Player has contacted gate");
				}
				break;
				
			case PROJECTILE:
				if (bType == InfoContainer.BodyID.ZOMBIE) {
					Projectile projectile = (Projectile)a.getObj();
					projectile.getInfo().flagForDeletion();
					Enemy zombie = (Enemy)b.getObj();
					zombie.setHealth(zombie.getHealth()-1);			
					System.out.println("Zombie has been damaged");
				}
				else if (bType == InfoContainer.BodyID.WALL) {
					Projectile projectile = (Projectile)a.getObj();
					projectile.getInfo().flagForDeletion();
					System.out.println("Bullet has hit wall");
				}
				break;
				
			case PLAYER:
				if (bType == InfoContainer.BodyID.ZOMBIE) {
					Player player = (Player)a.getObj();
					if (player.isSwinging()) {
						player.setHealth(player.getHealth()-player.getDamage());
						Enemy zombie = (Enemy)b.getObj();
						zombie.setHealth(zombie.getHealth()-3);
					}
					else
						player.setHealth(player.getHealth()-(player.getDamage()));			
					System.out.println("Player has contacted zombie");
				}
				else if (bType == InfoContainer.BodyID.WEAPON) {
					Player player = (Player)a.getObj();
					PickUp weaponPickUp = (PickUp)b.getObj();
					player.setWeapon((Weapon)weaponPickUp.getContainedItem());
					weaponPickUp.getInfo().flagForDeletion();
					System.out.println("Player has picked up weapon");
				}
				break;
				
			case PICKUP:
				if (bType == InfoContainer.BodyID.PLAYER) {
					PickUp powerUpPickUp = (PickUp)a.getObj();
					Player player = (Player)b.getObj();
					player.setPowerUp((PowerUp)powerUpPickUp.getContainedItem());
					powerUpPickUp.getInfo().flagForDeletion();
					System.out.println("Player has picked up item");
				}
				break;
				
			case NPC:
				if (bType == InfoContainer.BodyID.ZOMBIE) {
					NPC npc = (NPC)a.getObj();
					npc.setHealth(npc.getHealth()-1);
					System.out.println("NPC has contacted zombie");
				}
				break;
				
			default:
				System.err.println("Error: Unrecognised collision event");
				break;
		}		
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
