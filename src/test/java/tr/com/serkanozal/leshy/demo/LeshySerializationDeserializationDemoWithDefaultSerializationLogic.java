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
import tr.com.serkanozal.leshy.service.DeserializationSandbox;
import tr.com.serkanozal.leshy.service.SerDeService;
import tr.com.serkanozal.leshy.service.SerDeServiceFactory;
import tr.com.serkanozal.leshy.service.SerializationSandbox;

/**
 * @author Serkan ÖZAL
 */
public class LeshySerializationDeserializationDemoWithDefaultSerializationLogic {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		serializeAndDeserializeByUsingDefaultSerializationLogicWithCustomSerDe();
	}
	
	public static void serializeAndDeserializeByUsingDefaultSerializationLogicWithCustomSerDe() 
			throws IOException, ClassNotFoundException {
		SerDeService serdeService = SerDeServiceFactory.getSerdeService();
		serdeService.
			registerSerDe(new SerDeDispatcher(new ClassFilter(ClassToSerialize.class), new CustomSerDe(serdeService))).
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
	
	private static class CustomSerDe implements SerDe {

		private SerDeService serdeService;
		
		private CustomSerDe(SerDeService serdeService) {
			this.serdeService = serdeService;
		}
		
		@Override
		public void serialize(final Object obj, final OutputStream os) throws IOException {
			System.out.println("This is my CustomSerDe, but I am using ObjectOutputStream class of Java to serialize :)");
			serdeService.runInSandbox(
				new SerializationSandbox() {
					@Override
					public void runInSandbox() {
						try {
							new ObjectOutputStream(os).writeObject(obj);
						}
						catch (Throwable t) {
							t.printStackTrace();
							throw new RuntimeException(t);
						}
					}
				}
			);
		}

		@Override
		public Object deserialize(final InputStream is) throws IOException, ClassNotFoundException {
			System.out.println("This is my CustomSerDe, but I am using ObjectInputStream class of Java to deserialize :)");
			return 
				serdeService.runInSandbox(
					new DeserializationSandbox() {
						@Override
						public Object runInSandbox() {
							try {
								return new ObjectInputStream(is).readObject();
							} 
							catch (Throwable t) {
								t.printStackTrace();
								throw new RuntimeException(t);
							}
						}
					}
				);
		}
		
	}
	
}
