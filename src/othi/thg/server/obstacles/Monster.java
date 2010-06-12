/**
 * Copyright (c) 2010, The Project OTHI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 *
 *    * Redistributions of source code must retain the above copyright 
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright 
 *      notice, this list of conditions and the following disclaimer in 
 *      the documentation and/or other materials provided with the 
 *      distribution.
 *    * Neither the name of the OTHI nor the names of its contributors 
 *      may be used to endorse or promote products derived from this 
 *      software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package othi.thg.server.obstacles;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import othi.thg.common.Commands;
import othi.thg.server.*;
import othi.thg.server.agents.player.PlayerCompetence;
import othi.thg.server.agents.player.Player;
import othi.thg.server.entities.foods.Food;
import othi.thg.server.pathFinding.DJKPathFinder;
import othi.thg.server.pathFinding.Path;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

public class Monster extends NonPlayable<MonsterTask> {

	private static final long serialVersionUID = 20070719071413L;

	private static final Logger logger = Logger.getLogger(Monster.class.getName());

	/** The number of frames for resurrection */
	private static final int TOMBROUNDS = 10;

	/** RP (Run away point) indicates when the monster runs away from the player's attack. */	
	private int rp;

	/** check if the monster is waiting for a food at home. */
	private boolean loitering = true;

	/** check if the monster has found the food. */	
	private boolean foodFound = false;

	/** check if the monster is under attack. */	
	private boolean underAttack = false;

	private ManagedReference<Food> foodRef = null;
	private ManagedReference<MonsterTomb> monsterTombRef = null;

	/** x,y for a food for monster to get. */
	private float foodPosX;
	private float foodPosY;    

	/** x,y for home of monster to be back. */
	private float homePosX;
	private float homePosY;

	private String attackerName = null;

	/**
	 * Constructor for a new monster at a given map position
	 * 
	 * @param GameBoard 
	 * @param Place 
	 * @param name the monster's name 
	 * @param id 
	 * @param bx the monster's x position on the map
	 * @param by the monster's y position on the map
	 */
	public Monster(int id, String name, int placeId, String placeName, int bx, int by, String imgRef) {
		this.id = id;		
		this.name = name;
		this.placeId = placeId;
		this.placeName = placeName;
		homePosX = bx;
		homePosY = by;
		posX = bx;
		posY = by;
		imageRef = imgRef;
		pathFinder = new DJKPathFinder(getTerrainMap().getTerrainWithBlock());
	}

	public void tickMove() {
		/*	       
        if (this.getId() == 1001) {
            logger.log(Level.INFO, "loitering? " + isLoitering());
        }
		 */	

		if (isLoitering ()) {
			moveRandomly();
		} else {
			Path aPath = getPath();
			if (aPath != null && aPath.getSize() > 1) {

				//logger.log(Level.INFO, name + " Monster Action => tickMove, path size: " + path.getSize() );
				int nextPosX = aPath.getX(getPathIndex());
				int nextPosY = aPath.getY(getPathIndex());

				walk(nextPosX, nextPosY);	

	            if (getPathIndex() >= aPath.getSize()) setPath(null);	
			}
		}

		if (isUnderAttack()) {
			fight();
			if (getHP() <= rp) {
				setPathToRunAway();
			} else {
				setPathToAttacker();
			}			
		} else {
			if (isFoodFound()) {
				if (foodReached()) {
					eatFood();			
				} else {
					Food food = getFood();
					if (food != null && food.isRegenerating()) {
						setFood(null);
						setFoodFound(false);	
						setPathToHome();					
					}
				}
			} else {			
				if (homeReached()) {
					setLoitering(true);
				}						
				searchFood();
			}

			watchOut();
		}

	}

	public MonsterTomb getMonsterTomb() {
		if (monsterTombRef == null) {
			return null;
		} 
		return monsterTombRef.get();
	}

	public void setMonsterTomb(MonsterTomb monsterTomb) {
		DataManager dm = AppContext.getDataManager();	
		dm.markForUpdate(this);
		
		if (monsterTomb == null) {
			monsterTomb = null;
			return;
		}		
		
		monsterTombRef = dm.createReference(monsterTomb);
	}

	private Food getFood(){
		if (foodRef == null) {
			return null;
		}

		return foodRef.get();
	}

	private void setFood(Food food){		
		DataManager dm = AppContext.getDataManager();	
		dm.markForUpdate(this);		
		
		if (food == null) {
			foodRef = null;
			return;
		}

		foodRef = dm.createReference(food);
		foodPosX = food.getPosX();
		foodPosY = food.getPosY();		
	}	
	
	private void watchOut(){		
		if (isUnderAttack()) {
			if (attackerName == null) {
				setUnderAttack(false);				
			}
		} else {
			if (attackerName != null) {
				setUnderAttack(true);
			}
		}
	}

	public void setAttackerName(String name) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);
		
		if (name == null) {
			this.attackerName = null;
			return;
		}

		this.attackerName = name;
	}

	public String getAttackerName() {
		return attackerName;
	}

	public Player getAttacker() {
		return getPlayer(getAttackerName());
	}
	
	public void setRp(int rp) {
		AppContext.getDataManager().markForUpdate(this);		
		this.rp = rp;
	}

	public int getRp() {
		return rp;
	}

	public boolean isLoitering() {
		return loitering;
	}

	public void setLoitering(boolean loitering) {
		AppContext.getDataManager().markForUpdate(this);				
		this.loitering = loitering;
	}

	public boolean isFoodFound() {		
		return foodFound;
	}

	public void setFoodFound(boolean foodFound) {
		AppContext.getDataManager().markForUpdate(this);				
		this.foodFound = foodFound;
	}


	public boolean isUnderAttack() {
		return underAttack;
	}

	public void setUnderAttack(boolean underAttack) {
		AppContext.getDataManager().markForUpdate(this);				
		this.underAttack = underAttack;
	}

	private void searchFood() {
		Food food = getMostAttractiveFood();
		if (food != null) { 
			setFood(food);                   
			setPathToFood(food);                   
			setLoitering(false);    
			setFoodFound(true);
		}
	}

	private void fight() {
		if (getHP() > rp) {
			goingAttack();
		} else {
			runAway();
		}        
	}

	private boolean homeReached() {
		return (homePosX == posX && homePosY == posY);
	}

	private boolean foodReached() {        
		return (foodPosX == posX && foodPosY == posY);
	}

	private Food getMostAttractiveFood() {
		Food food = null;

		float highAttractionRate = 0;

		DataManager dm = AppContext.getDataManager();
		String binding = THGServerDefault.FOOD + getPlaceId() + ":";
		String bindingName = dm.nextBoundName(binding);
		
		while (bindingName != null && bindingName.startsWith(binding)) {			
			Food candidate = (Food) dm.getBinding(bindingName);
			
			if (!candidate.isRegenerating()) {
				float attractionRate = getAttractionRate(candidate);
				if (attractionRate > highAttractionRate) {
					food = candidate;
					highAttractionRate = attractionRate;
				}
			}
			
			bindingName = dm.nextBoundName(bindingName);
		}
		return food;
	}

	private float getAttractionRate(Food food) {
		float foodX = 0;
		float foodY = 0;
		float dx = food.getPosX() - getPosX();
		float dy = food.getPosY() - getPosY();
		if (dx != 0) {
			foodX = food.getAttractionPoint() / dx;
		}
		if (dy != 0) {
			foodY = food.getAttractionPoint() / dy;
		}
		return Math.abs(foodX) + Math.abs(foodY);        
	}

	private void moveRandomly() {		
		float[]	newPos = decideRandomPosition();
		if (!isBlockedOrProtected(newPos[0], newPos[1])) {	
			walk((int) newPos[0], (int) newPos[1]);
		}
	}

	private boolean isBlockedOrProtected(float bx, float by) {
		DataManager dm = AppContext.getDataManager();
		TerrainMap terrainMap = (TerrainMap) dm.getBinding(THGServerDefault.TERRAIN_MAP + getPlaceId());
		return terrainMap.isBlocked((int) bx, (int) by) || terrainMap.isProtected((int) bx, (int) by);
	}
	
	private void goingAttack() {
		Player player = getAttacker();

		float attackerX = player.getPosX();
		float attackerY = player.getPosX();

		if (isInRangeToAttack(attackerX, attackerY)) {
			attack();
		}
	}

	public boolean isNearBy(float attackerX, float attackerY) {
		float dx = attackerX - getPosX();
		float dy = attackerY - getPosY();

		return (Math.abs(dx) < 2 && Math.abs(dy) < 2);
	}
	
    public boolean isInRangeToAttack(float attackerX, float attackerY) {
        return isNearBy(attackerX, attackerY);
    }

	public void attack() {
		int attackPower = getPower() * -1;
		Player player = getAttacker();
		PlayerCompetence cp = player.getCompetence(getAttackerName());
		
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(cp);
		cp.addHP(attackPower);

		getManagedTHGTask().addMove(Commands.hpCommand(player.getId(), cp.getMyHP()));
	}

	private void runAway() {
		Player player = getAttacker();
		float attackerX = player.getPosX();
		float attackerY = player.getPosX();

		if (!player.getArmedWeapon(getAttackerName()).getArmedWeapon().isInRangeToAttack(attackerX, attackerY, posX, posY)) {
			clearBrainForAttacker();
		}
	}

	private void eatFood() {
		Food food = (Food) foodRef.getForUpdate();
		getRecovery(food);
		getManagedTHGTask().addMove(Commands.removeFoodCommand(food.getId(), food.getPosX(), food.getPosY()));
		food.setRegenerating(true);
		food.setRegenIssueTime(System.currentTimeMillis());
		setFood(null);
		setFoodFound(false);
		setPathToHome();		
	}

	private void getRecovery(Food food) {
		//Need to implement!
	}

	private void setPathToRunAway() {
		Player player = getAttacker();
		float attackerX = player.getPosX();
		float attackerY = player.getPosX();

		float[] newPos = decideRunAwayPosition(attackerX, attackerY);
		setPath((int) newPos[0], (int) newPos[1]);
	}

	private void setPathToAttacker() {
		Player player = getAttacker();
		float attackerX = player.getPosX();
		float attackerY = player.getPosX();

		float[] newPos = decideAttackPosition(attackerX, attackerY);
		setPath((int) newPos[0], (int) newPos[1]);
	}

	private void setPathToFood(Food food) {
		setPath((int) food.getPosX(), (int) food.getPosY());
	}

	private void setPathToHome() {
		setPath((int) homePosX, (int) homePosY);
	}

	private void setPath(int dx, int dy) {
		setDestPosX(dx);
		setDestPosY(dy);		

		//pathFinder.setTerrain(getTerrainMap().getTerrainWithBlock());
		
		pathFinder.reset();
		
		Path aPath = pathFinder.findPath((int) getPosX(), (int) getPosY(), dx, dy, THGServerDefault.PATH_SEARCH_DEPTH);
		if (aPath != null) {
			setPath(aPath);
			setPathIndex(0);
		}
	}

	private float[] decideRandomPosition() {

		float[] newPos = new float[2];

		int wayToGo = new Random().nextInt(4);

		switch (wayToGo) {
		case 0:
			newPos[0] = getPosX() + getSpeed();
			newPos[1] = getPosY();
			break;
		case 1:
			newPos[0] = getPosX() - getSpeed();
			newPos[1] = getPosY();
			break;
		case 2:
			newPos[0] = getPosX();
			newPos[1] = getPosY() + getSpeed();
			break;
		case 3:
			newPos[0] = getPosX();
			newPos[1] = getPosY() - getSpeed();
			break;
		}

		return newPos;
	}

	private float[] decideAttackPosition(float attackerX, float attackerY) {
		float dx = attackerX - getPosX();
		float dy = attackerY - getPosY();

		float[] newPos = new float[2];

		if (dx >= 0) { // right 
			newPos[0] = getPosX() - 1;
		} else {
			newPos[0] = getPosX() + 1;
		}

		if (dy >= 0) {
			newPos[1] = getPosY() - 1;
		} else {
			newPos[1] = getPosY() + 1;
		}

		return newPos;
	}

	private float[] decideRunAwayPosition(float attackerX, float attackerY) {
		float dx = attackerX - getPosX();
		float dy = attackerY - getPosY();

		float[] newPos = new float[2];

		if (dx >= 0) { // right
			newPos[0] = getPosX() + 1;
		} else {
			newPos[0] = getPosX() - 1;
		}

		if (dy >= 0) {
			newPos[1] = getPosY() + 1;
		} else {
			newPos[1] = getPosY() - 1;
		}

		return newPos;
	}

	private void clearBrainForAttacker() {
		AppContext.getDataManager().markForUpdate(this);		
		attackerName = null;
	}

	public void remove() {
		removeMonster();
	}

	private void removeMonster() {
		getManagedTHGTask().getTaskHandle().cancel();	
		AppContext.getDataManager().removeObject(this);
	}

	private void removeBinding() {
		DataManager dm = AppContext.getDataManager();
		dm.removeBinding(THGServerDefault.MONSTER + placeId + ":" + id);
	}

	public void kill() {
		logger.info(getName() + " : Monster killed!");
		removeBinding();

		MonsterTomb monsterTomb = getMonsterTomb();

		if (monsterTomb != null) {
			/** use its MonsterTomb object if the monster has been hunted before.         */
			monsterTomb.setXY(getPosX(), getPosY());
			monsterTomb.setCountdown(TOMBROUNDS);
		} else {
			/** create its MonsterTomb o		lastTimestamp = currentTimestamp;
bject if the monster has never been hunted before. */
			monsterTomb = new MonsterTomb(TOMBROUNDS, getPosX(), getPosY());
			setMonsterTomb(monsterTomb);			
		}

		getManagedTHGTask().addMove(Commands.addMonsterTomb(-1, monsterTomb.getPosX(), monsterTomb.getPosY()));
		getManagedTHGTask().setAlive(false);
	}	
}