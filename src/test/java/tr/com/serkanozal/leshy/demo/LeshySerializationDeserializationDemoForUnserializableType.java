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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import tr.com.serkanozal.leshy.service.SerDeService;
import tr.com.serkanozal.leshy.service.SerDeServiceFactory;

/**
 * @author Serkan ÖZAL
 */
public class LeshySerializationDeserializationDemoForUnserializableType {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		serializeAndDeserializeUnserializableType();
	}
	
	public static void serializeAndDeserializeUnserializableType() 
			throws IOException, ClassNotFoundException {
		SerDeService serdeService = SerDeServiceFactory.getSerdeService();
		serdeService.serializationOpenForAllTypes();
			
		UnserializableClass serializedObject = new UnserializableClass().randomize();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(serializedObject);
		bos.flush();
		
		System.out.println("Serialized Object:\n====================");
		System.out.println(serializedObject);
		System.out.println("\n");

		byte[] objectContent = bos.toByteArray();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(objectContent);
		ObjectInputStream ois = new ObjectInputStream(bis);
		UnserializableClass deserializedObject = (UnserializableClass) ois.readObject();
		
		System.out.println("Deserialized Object:\n====================");
		System.out.println(deserializedObject);
		System.out.println("\n");
	}

	public static class UnserializableClass {

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

		public UnserializableClass randomize() {
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
	
}
