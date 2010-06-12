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

/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package othi.thg.common;

public class TimeoutConf {
	/** This indicate when we stop to wait on the socket. The lower the slower. */
	final public static int SOCKET_TIMEOUT = 10;

	/**
	 * This indicate how many time we wait for a message to arrive. The lower
	 * the slower.
	 */
	final public static int GAMESERVER_MESSAGE_GET_TIMEOUT = 1000;

	/** This indicate when the client remove the incomplete packet from its queue */
	final public static int CLIENT_MESSAGE_DROPPED_TIMEOUT = 60000;

	/** Indicate how many packets can be read from network before returing */
	final public static int CLIENT_NETWORK_NUM_READ = 20;

	/**
	 * This indicate that the player is totally dead and must be removed. Should
	 * be related to Turn Duration, around 4-10 times bigger at least.
	 */
	final public static int GAMESERVER_PLAYER_TIMEOUT = 30000;

	/** This indicate that how often the player is stored on database. */
	final public static int GAMESERVER_PLAYER_STORE_LAPSUS = 3600000;

	/** Maximum size of bytes on a message (256KB) */
	final public static int MAX_BYTE_ARRAY_ELEMENTS = 256 * 1024;

	/** Maximum size of elements on a array (256K) */
	final public static int MAX_ARRAY_ELEMENTS = 256 * 1024;
}
