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

package othi.thg.server.agents.player;

import java.util.logging.Logger;

import othi.thg.server.ManagedTHGObj;
import othi.thg.server.THGServerDefault;
import othi.thg.server.entities.weapons.Weapon;
import othi.thg.server.entities.weapons.supplies.WeaponSupply;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

/*
 * player's weapon 
 * @author Dong Won Kim
 */
public class ArmedWeapon extends ManagedTHGObj {
    /**
     * Every managed object should declare a serialversionUID
     */
    private static final long serialVersionUID =  1L;

    private static final Logger logger = Logger.getLogger(ArmedWeapon.class.getName());

    private String playerName;    
    
    // Armed weapon
    private ManagedReference<Weapon> armedWeaponRef;
    
    // Weapon supplies
    private ManagedReference<WeaponSupply> weaponSupplyRef;

    private boolean armed = false;
    
    public ArmedWeapon(String name) {
        playerName = name;
    }

/*    
    public Player getPlayer() {
        DataManager dm = AppContext.getDataManager();           
        return (Player) dm.getBinding(THGServerDefault.USERPREFIX + playerName);
    }
*/
    
    public void setArmedWeapon(Weapon weapon) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);    
        armedWeaponRef = dataManager.createReference(weapon);
        armed = true;            
    }

    public void setWeaponSupply(WeaponSupply weaponSupply) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);        
        weaponSupplyRef = dataManager.createReference(weaponSupply);
    }

    public Weapon getArmedWeapon() {
        if (armedWeaponRef == null) return null;
        else return (Weapon) armedWeaponRef.get();
    }

    public WeaponSupply getWeaponSupply() {
        if (weaponSupplyRef == null) return null;
        else return (WeaponSupply) weaponSupplyRef.get();
    }

    public void removeArmedWeapon() {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        armedWeaponRef = null;
        armed = false;
    }

    public void removeWeaponSupply() {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);        
        weaponSupplyRef = null;
    }
    
    public boolean isArmed() {
        return armed;
    }            
}
