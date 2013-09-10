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

package tr.com.serkanozal.leshy.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.junit.Test;

import tr.com.serkanozal.leshy.dispatcher.SerDeDispatcher;
import tr.com.serkanozal.leshy.filter.ClassFilter;
import tr.com.serkanozal.leshy.serde.SerDe;

/**
 * @author Serkan ÖZAL
 */
public class SerDeServiceTest {

	@Test
	public void serializeAndDeserializeSuccessfully() throws IOException {
		try{
			SerDeService serdeService = SerDeServiceFactory.getSerdeService();
			serdeService.
				registerSerDe(new SerDeDispatcher(new ClassFilter(ClassToSerialize.class), new CustomSerDe())).
				setup();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(new ClassToSerialize());
			bos.flush();

			byte[] objectContent = bos.toByteArray();
			
			ByteArrayInputStream bis = new ByteArrayInputStream(objectContent);
			ObjectInputStream ois = new ObjectInputStream(bis);
			ClassToSerialize obj = (ClassToSerialize) ois.readObject();
			
			System.out.println(obj);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private static class CustomSerDe implements SerDe {

		@Override
		public void serialize(Object obj, OutputStream os) throws IOException {
			System.out.println("This is my CustomSerDe, but I am using ObjectOutputStream class of Java to serialize :)");
			try {
				SerDeServiceFactory.lockSerializerRedirect();
				new ObjectOutputStream(os).writeObject(obj);
			}
			finally {
				SerDeServiceFactory.unlockSerializerRedirect();
			}
		}

		@Override
		public Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
			System.out.println("This is my CustomSerDe, but I am using ObjectInputStream class of Java to deserialize :)");
			try {
				SerDeServiceFactory.lockDeserializerRedirect();
				return new ObjectInputStream(is).readObject();
			}
			finally {
				SerDeServiceFactory.unlockDeserializerRedirect();
			}
		}
		
	}
	
}
