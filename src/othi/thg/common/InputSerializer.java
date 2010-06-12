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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * InputSerializer is used to serialize classes that implement the Serializable
 * interface from a InputStream.
 */
public class InputSerializer {
	private InputStream in;

	/**
	 * Constructor that pass the InputStream to the serializer
	 * 
	 * @param in
	 *            the InputStream
	 */
	public InputSerializer(InputStream in) {
		this.in = in;
	}

	/**
	 * This method serialize an object that implements the interface
	 * Serializable allowing to implement this behaviour in several classes
	 * 
	 * @param obj
	 *            the object were we will serialize the data
	 * @return the object serialized, just for interface coherence
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public Object readObject(othi.thg.common.Serializer obj)
			throws IOException, java.lang.ClassNotFoundException {
		obj.readObject(this);
		return obj;
	}

	/**
	 * This method read a byte from the Serializer
	 * 
	 * @return the byte serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public byte readByte() throws IOException, java.lang.ClassNotFoundException {
		int result = in.read();

		if (result < 0) {
			throw new IOException();
		}
		return (byte) result;
	}

	/**
	 * This method read a byte array from the Serializer
	 * 
	 * @return the byte array serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public byte[] readByteArray() throws IOException,
			java.lang.ClassNotFoundException {
		int size = readInt();

		if (size > TimeoutConf.MAX_BYTE_ARRAY_ELEMENTS) {
			throw new IOException("Ilegal request of an array of " + size
					+ " size");
		}

		byte[] buffer = new byte[size];
		int bytes_read_total = 0;
		int bytes_read = 0;

		while ((bytes_read_total < size)
				&& (bytes_read = in.read(buffer, bytes_read_total, size
						- bytes_read_total)) != -1) {
			bytes_read_total += bytes_read;
		}
		if (bytes_read_total != size) {
			throw new IOException("Declared array size=" + size
					+ " is not equal to actually read bytes count("
					+ bytes_read_total + ")!");
		}
		return buffer;
	}

	/**
	 * This method read a byte array from the Serializer
	 * 
	 * @return the byte array serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public byte[] read255LongByteArray() throws IOException,
			java.lang.ClassNotFoundException {
		int size = readByte();

		if (size > Byte.MAX_VALUE) {
			throw new IOException("Ilegal request of an array of " + size
					+ " size");
		}

		byte[] buffer = new byte[size];
		int bytes_read_total = 0;
		int bytes_read = 0;

		while ((bytes_read_total < size)
				&& (bytes_read = in.read(buffer, bytes_read_total, size
						- bytes_read_total)) != -1) {
			bytes_read_total += bytes_read;
		}
		if (bytes_read_total != size) {
			throw new IOException("Declared array size=" + size
					+ " is not equal to actually read bytes count("
					+ bytes_read_total + ")!");
		}
		return buffer;
	}

	public byte[] read65536LongByteArray() throws IOException,
			java.lang.ClassNotFoundException {
		int size = readShort();

		if (size > Short.MAX_VALUE) {
			throw new IOException("Ilegal request of an array of " + size
					+ " size");
		}

		byte[] buffer = new byte[size];
		int bytes_read_total = 0;
		int bytes_read = 0;

		while ((bytes_read_total < size)
				&& (bytes_read = in.read(buffer, bytes_read_total, size
						- bytes_read_total)) != -1) {
			bytes_read_total += bytes_read;
		}
		if (bytes_read_total != size) {
			throw new IOException("Declared array size=" + size
					+ " is not equal to actually read bytes count("
					+ bytes_read_total + ")!");
		}
		return buffer;
	}

	/**
	 * This method read a short from the Serializer
	 * 
	 * @return the short serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public short readShort() throws IOException,
			java.lang.ClassNotFoundException {
		int size = 2;
		byte[] data = new byte[size];
		int bytes_read_total = 0;
		int bytes_read = 0;

		while ((bytes_read_total < size)
				&& (bytes_read = in.read(data, bytes_read_total, size
						- bytes_read_total)) != -1) {
			bytes_read_total += bytes_read;
		}
		if (bytes_read_total != size) {
			throw new IOException("Declared array size=" + size
					+ " is not equal to actually read bytes count("
					+ bytes_read_total + ")!");
		}

		int result = data[0] & 0xFF;

		result += (data[1] & 0xFF) << 8;
		return (short) result;
	}

	/**
	 * This method read a int from the Serializer
	 * 
	 * @return the int serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public int readInt() throws IOException, java.lang.ClassNotFoundException {
		int size = 4;
		byte[] data = new byte[size];
		int bytes_read_total = 0;
		int bytes_read = 0;

		while ((bytes_read_total < size)
				&& (bytes_read = in.read(data, bytes_read_total, size
						- bytes_read_total)) != -1) {
			bytes_read_total += bytes_read;
		}
		if (bytes_read_total != size) {
			throw new IOException("Declared array size=" + size
					+ " is not equal to actually read bytes count("
					+ bytes_read_total + ")!");
		}

		int result = data[0] & 0xFF;

		result += (data[1] & 0xFF) << 8;
		result += (data[2] & 0xFF) << 16;
		result += (data[3] & 0xFF) << 24;
		return result;
	}

	/**
	 * This method read a float from the Serializer
	 * 
	 * @return the float serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public float readFloat() throws IOException,
			java.lang.ClassNotFoundException {
		int size = 4;
		byte[] data = new byte[size];
		int bytes_read_total = 0;
		int bytes_read = 0;

		while ((bytes_read_total < size)
				&& (bytes_read = in.read(data, bytes_read_total, size
						- bytes_read_total)) != -1) {
			bytes_read_total += bytes_read;
		}
		if (bytes_read_total != size) {
			throw new IOException("Declared array size=" + size
					+ " is not equal to actually read bytes count("
					+ bytes_read_total + ")!");
		}

		int result = data[0] & 0xFF;

		result += (data[1] & 0xFF) << 8;
		result += (data[2] & 0xFF) << 16;
		result += (data[3] & 0xFF) << 24;
		return Float.intBitsToFloat(result);
	}

	/**
	 * This method read a String from the Serializer
	 * 
	 * @return the String serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
  * @throws java.io.UnsupportedEncodingException 
	 */
	public String readString() throws IOException,
			java.lang.ClassNotFoundException, UnsupportedEncodingException {
		return new String(readByteArray(), "UTF-8");
	}

	/**
	 * This method read a short string ( whose size is smaller than 255 chars
	 * long ) from the Serializer
	 * 
	 * @return the String serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
  * @throws java.io.UnsupportedEncodingException 
	 */
	public String read255LongString() throws IOException,
			java.lang.ClassNotFoundException, UnsupportedEncodingException {
		return new String(read255LongByteArray(), "UTF-8");
	}

	public String read65536LongString() throws IOException,
			java.lang.ClassNotFoundException, UnsupportedEncodingException {
		return new String(read65536LongByteArray(), "UTF-8");
	}

	/**
	 * This method read a String array from the Serializer
	 * 
	 * @return the String array serialized
	 * @throws java.io.IOException
	 *             if there is an IO error
	 * @throws java.lang.ClassNotFoundException
	 *             if the class to serialize doesn't exist.
	 */
	public String[] readStringArray() throws IOException,
			java.lang.ClassNotFoundException {
		int size = readInt();

		if (size > TimeoutConf.MAX_ARRAY_ELEMENTS) {
			throw new IOException("Ilegal request of an array of "
					+ String.valueOf(size) + " size");
		}

		String[] buffer = new String[size];

		for (int i = 0; i < size; i++) {
			buffer[i] = readString();
		}
		return buffer;
	}
}
