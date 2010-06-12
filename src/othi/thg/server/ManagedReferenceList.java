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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server;

import com.sun.sgs.app.*;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
/**
 *
 * @author DongWon Kim
 */
public class ManagedReferenceList<T extends ManagedObject> implements ManagedObject, Serializable
{
	private static final long serialVersionUID = -3046371286919636590L;

	protected List<ManagedReference<?>> repository;

	public ManagedReferenceList() {
		this(new ArrayList<ManagedReference<?>>());
	}

	public ManagedReferenceList(List<ManagedReference<?>> repository) {	
		this.repository = repository;
	}

	public void add(T obj) {
		DataManager dm = AppContext.getDataManager();
		dm.markForUpdate(this);
		repository.add(dm.createReference(obj));
	}

	public void remove(T obj) {
		DataManager dm = AppContext.getDataManager();
		dm.markForUpdate(this);
		repository.remove(dm.createReference(obj));
	}

	public int size() {
		return repository.size();
	}

	public void clear() {
		repository.clear();
	}

	@SuppressWarnings("unchecked")
	public T get(int ix) {
		return (T) repository.get(ix).get();
	}

	public int indexOf (T obj) {
		DataManager dm = AppContext.getDataManager();
		return repository.indexOf(dm.createReference(obj));
	}

	public boolean contains (T obj) {
		DataManager dm = AppContext.getDataManager();
		return repository.contains(dm.createReference(obj));
	}
}
