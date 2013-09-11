/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tr.com.serkanozal.leshy.demo;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * @author Serkan ÖZAL
 */
@SuppressWarnings({"serial"})
public class ClassToSerialize implements Serializable {

	private static final Random RANDOM = new SecureRandom();
	
	private byte byteValue = 1;
	private boolean booleanValue = true;
	private char charValue = 'X';
	private short shortValue = 10;
	private int intValue = 100;
	private float floatValue = 200.0F;
	private long longValue = 1000;
	private double doubleValue = 2000.0;
	private String stringValue = "str";

	public byte getByteValue() {
		return byteValue;
	}

	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public char getCharValue() {
		return charValue;
	}

	public void setCharValue(char charValue) {
		this.charValue = charValue;
	}

	public short getShortValue() {
		return shortValue;
	}

	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}

	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public ClassToSerialize randomize() {
		byteValue = (byte) RANDOM.nextInt(256);
		booleanValue = RANDOM.nextBoolean();
		charValue = (char) RANDOM.nextInt(65536);
		shortValue = (short) RANDOM.nextInt(65536);
		intValue = RANDOM.nextInt();
		floatValue = RANDOM.nextFloat();
		longValue = RANDOM.nextLong();
		doubleValue = RANDOM.nextDouble();
		stringValue = UUID.randomUUID().toString();
		
		return this;
	}	
	
	@Override
	public String toString() {
		return
			"byteValue    : " + byteValue 		+ "\n" +
			"booleanValue : " + booleanValue 	+ "\n" +
			"charValue    : " + charValue 		+ "\n" +
			"shortValue   : " + shortValue 		+ "\n" +
			"intValue     : " + intValue 		+ "\n" +
			"floatValue   : " + floatValue 		+ "\n" +
			"longValue    : " + longValue 		+ "\n" +
			"doubleValue  : " + doubleValue 	+ "\n" +
			"stringValue  : " + stringValue;
	}
	
}
