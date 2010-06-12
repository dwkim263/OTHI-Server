/*
 * LearningObject.java
 * 
 * Created on 2007. 7. 2, 5:30:53
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package othi.thg.server.information;

/**
 *
 * @author Dong Won Kim
 */
import othi.thg.server.ManagedTHGEntity;

public class Topic extends ManagedTHGEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7250087991811191135L;

	public Topic(String name){
        this.name = name;
    }
}
