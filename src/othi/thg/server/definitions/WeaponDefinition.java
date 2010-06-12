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

package othi.thg.server.definitions;

/**
 *
 * @author Steve
 */
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import othi.thg.server.definitions.xml.WeaponXML;

public class WeaponDefinition {
    private static final Logger logger = Logger.getLogger(WeaponDefinition.class.getName());
    
    private static Map<String, WeaponDefinition> weapons = new HashMap<String, WeaponDefinition>();
        
    private String name;
    private String filename;
    
    private WeaponDefinition(String name, String filename){
        this.name = name;
        this.filename = filename;
        weapons.put(name, this);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Map<String, WeaponDefinition> getWeapons() {
        if (weapons.isEmpty()) {
            setWeapons();
        }
        return weapons;
    }

    public static void setWeapons() {

        File[] directories = (new File(DefinitionsDefault.getDefinitionsDir())).listFiles();
        for (File directory: directories) {
            if (directory.isDirectory()) {
                WeaponXML weaponXml = null;

                File[] files = directory.listFiles();
                for (File file: files) {
                    if (file.getName().equals(DefinitionsDefault.WEAPON_XML))
                        weaponXml = new WeaponXML(file.getAbsolutePath());
                }

                if (weaponXml != null) {
                    Map <String, Object> weaponProperty = weaponXml.getProperties();
                    Iterator it = weaponProperty.keySet().iterator();
                    while(it.hasNext()) {
                        String name = (String) it.next();
                        Map <String, String> weapon = (Map <String, String>) weaponProperty.get(name);
                        new WeaponDefinition((String) weapon.get("name"), (String) weapon.get("file"));
                    }
                }
            }
        }
    }


    public static void setWeapons(Map<String, WeaponDefinition> weapons) {
        WeaponDefinition.weapons = weapons;
    }

    public static WeaponDefinition getById(String id) {
        return weapons.get(id);
    }    
}
