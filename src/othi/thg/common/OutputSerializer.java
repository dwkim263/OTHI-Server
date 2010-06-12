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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * OutputSerializer is used to serialize classes that implement the Serializable
 * interface into a OutputStream.
 */
public class OutputSerializer {
	OutputStream out;

	/**
	 * Constructor that defines a specified OutputStream for the serializer
	 * 
	 * @param out
	 *            the OutputStream to which objects are serialized
	 */
	public OutputSerializer(OutputStream out) {
		this.out = out;
	}

	/**
	 * Add the Object to the serializer, if it implements the
	 * othi.thg.common.Serializable interface
	 * 
	 * @param obj
	 *            the object to serialize
  * @throws java.io.IOException 
	 */
	public void write(othi.thg.common.Serializer obj) throws IOException {
		obj.writeObject(this);
	}

	/**
	 * Add the byte to the serializer
	 * 
	 * @param a
	 *            the byte to serialize
  * @throws java.io.IOException 
	 */
	public void write(byte a) throws IOException {
		out.write(a);
	}

	/**
	 * Add the byte array to the serializer
	 * 
	 * @param a
	 *            the byte array to serialize
  * @throws java.io.IOException 
	 */
	public void write(byte[] a) throws IOException {
		write(a.length);
		out.write(a);
	}

	/**
	 * Add a byte array whose size is smaller than 255 to the serializer
	 * 
	 * @param a
	 *            the byte array to serialize
  * @throws java.io.IOException 
	 */
	public void write255LongArray(byte[] a) throws IOException {
		if (a.length > Byte.MAX_VALUE) {
			throw new IOException();
		}

		write((byte) a.length);
		out.write(a);
	}

	public void write65536LongArray(byte[] a) throws IOException {
		if (a.length > Short.MAX_VALUE) {
			throw new IOException();
		}

		write((short) a.length);
		out.write(a);
	}

	/**
	 * Add the short to the serializer
	 * 
	 * @param a
	 *            the short to serialize
  * @throws java.io.IOException 
	 */
	public void write(short a) throws IOException {
		int tmp;

		tmp = a & 0xFF;
		out.write(tmp);
		tmp = (a >> 8) & 0xFF;
		out.write(tmp);
	}

	/**
	 * Add the int to the serializer
	 * 
	 * @param a
	 *            the int to serialize
  * @throws java.io.IOException 
	 */
	public void write(int a) throws IOException {
		int tmp;

		tmp = a & 0xFF;
		out.write(tmp);
		tmp = (a >> 8) & 0xFF;
		out.write(tmp);
		tmp = (a >> 16) & 0xFF;
		out.write(tmp);
		tmp = (a >> 24) & 0xFF;
		out.write(tmp);
	}

	/**
	 * Add the float to the serializer
	 * 
	 * @param a
	 *            the int to serialize
  * @throws java.io.IOException 
	 */
	public void write(float a) throws IOException {
		int tmp;
		int bits = Float.floatToIntBits(a);

		tmp = bits & 0xFF;
		out.write(tmp);
		tmp = (bits >> 8) & 0xFF;
		out.write(tmp);
		tmp = (bits >> 16) & 0xFF;
		out.write(tmp);
		tmp = (bits >> 24) & 0xFF;
		out.write(tmp);
	}

	/**
	 * Add the String to the serializer, using UTF-8 encoding
	 * 
	 * @param a
	 *            the String to serialize
  * @throws java.io.IOException 
  * @throws java.io.UnsupportedEncodingException 
	 */
	public void write(String a) throws IOException,
			UnsupportedEncodingException {
		write(a.getBytes("UTF-8"));
	}

	/**
	 * Add a short string to the serializer, using UTF-8 encoding
	 * 
	 * @param a
	 *            the String to serialize
  * @throws java.io.IOException 
  * @throws java.io.UnsupportedEncodingException 
	 */
	public void write255LongString(String a) throws IOException,
			UnsupportedEncodingException {
		write255LongArray(a.getBytes("UTF-8"));
	}

	public void write65536LongString(String a) throws IOException,
			UnsupportedEncodingException {
		write65536LongArray(a.getBytes("UTF-8"));
	}

	/**
	 * Add the String array to the serializer, using UTF-8 encoding
	 * 
	 * @param a
	 *            the String array to serialize
  * @throws java.io.IOException 
	 */
	public void write(String[] a) throws IOException {
		write(a.length);
		for (int i = 0; i < a.length; i++) {
			write(a[i]);
		}
	}
};
