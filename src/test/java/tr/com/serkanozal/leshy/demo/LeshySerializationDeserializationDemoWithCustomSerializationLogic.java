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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import tr.com.serkanozal.leshy.dispatcher.SerDeDispatcher;
import tr.com.serkanozal.leshy.filter.ClassFilter;
import tr.com.serkanozal.leshy.serde.SerDe;
import tr.com.serkanozal.leshy.service.SerDeService;
import tr.com.serkanozal.leshy.service.SerDeServiceFactory;

/**
 * @author Serkan ÖZAL
 */
public class LeshySerializationDeserializationDemoWithCustomSerializationLogic {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		serializeAndDeserializeByUsingCustomSerializationLogicWithCustomSerDe();
	}
	
	public static void serializeAndDeserializeByUsingCustomSerializationLogicWithCustomSerDe() 
			throws IOException, ClassNotFoundException {
		SerDeService serdeService = SerDeServiceFactory.getSerdeService();
		serdeService.
			registerSerDe(new SerDeDispatcher(new ClassFilter(ClassToSerialize.class), new CustomSerDe())).
			setup();
		
		ClassToSerialize serializedObject = new ClassToSerialize().randomize();
		
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
		ClassToSerialize deserializedObject = (ClassToSerialize) ois.readObject();
		
		System.out.println("Deserialized Object:\n====================");
		System.out.println(deserializedObject);
		System.out.println("\n");
		
		System.out.println("As you can see, only byteValue, shortValue and intValue fields are same.");
		System.out.println("Because only only byteValue, shortValue and intValue fields have been serialized and deserialized.");
	}
	
	private static class CustomSerDe implements SerDe {

		@Override
		public void serialize(final Object obj, final OutputStream os) throws IOException {
			System.out.println("This is my CustomSerDe, I am using my simple custom serialization logic :)");
			
			ClassToSerialize classToSerialize = (ClassToSerialize)obj;
			
			// I only serialize byteValue, shortValue and intVlaue in our demo
			
			os.write(classToSerialize.getByteValue());
			
			short shortValue = classToSerialize.getShortValue();
			os.write(shortValue >> 8);
			os.write(shortValue >> 0);
			
			int intValue = classToSerialize.getIntValue();
			os.write(intValue >> 24);
			os.write(intValue >> 16);
			os.write(intValue >> 8);
			os.write(intValue >> 0);
		}

		@Override
		public Object deserialize(final InputStream is) throws IOException, ClassNotFoundException {
			System.out.println("This is my CustomSerDe, I am using my simple custom deserialization logic :)");
			
			ClassToSerialize classToSerialize = new ClassToSerialize();
			
			byte byteValue = (byte) is.read();
			classToSerialize.setByteValue(byteValue);
			
			short shortValue = (short)((is.read() << 8) | (is.read() << 0));
			classToSerialize.setShortValue(shortValue);
			
			int intValue = ((is.read() << 24) | (is.read() << 16) | (is.read() << 8) | (is.read() << 0));
			classToSerialize.setIntValue(intValue);
			
			
			return classToSerialize;
		}
		
	}
	
}
