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

import junit.framework.Assert;

import org.junit.Test;

import tr.com.serkanozal.leshy.dispatcher.SerDeDispatcher;

/**
 * @author Serkan ÖZAL
 */
public class SerDeServiceFactoryTest {
	
	@Test
	public void objectSerializedWithSpecifiedSerializer() throws IOException {
		Integer i = new Integer(1);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SerDeService serdeService = new SerDeService() {
			
			@Override
			public void setup() {
				
			}

			@Override
			public void remove() {
				
			}

			@Override
			public SerDeService registerSerDe(SerDeDispatcher serdeDispatcher) {
				return null;
			}

			@Override
			public void doSerialize(ObjectOutputStream oos, Object obj, OutputStream os) throws IOException {
				os.write(1);
			}

			@Override
			public Object doDeserialize(ObjectInputStream ois, InputStream is) throws IOException, ClassNotFoundException {
				return null;
			}

			@Override
			public void runInSandbox(SerializationSandbox serializationSandbox) {
				
			}

			@Override
			public Object runInSandbox(DeserializationSandbox deserializationSandbox) {
				return null;
			}
			
		};
		
		SerDeServiceFactory.setSerdeService(serdeService);
		
		SerDeServiceFactory.doSerialize(null, i, bos);
		
		bos.flush();
		
		byte[] array = bos.toByteArray();
		Assert.assertEquals(1, array.length);
		Assert.assertEquals(1, array[0]);
	}
	
	@Test
	public void objectDeserializedWithSpecifiedDeserializer() throws IOException, ClassNotFoundException {
		final Integer i = new Integer(1);
		SerDeService serdeService = new SerDeService() {
			
			@Override
			public void setup() {
				
			}

			@Override
			public void remove() {
				
			}

			@Override
			public SerDeService registerSerDe(SerDeDispatcher serdeDispatcher) {
				return null;
			}

			@Override
			public void doSerialize(ObjectOutputStream oos, Object obj, OutputStream os) throws IOException {
				
			}

			@Override
			public Object doDeserialize(ObjectInputStream ois, InputStream is) throws IOException, ClassNotFoundException {
				return i;
			}

			@Override
			public void runInSandbox(SerializationSandbox serializationSandbox) {
				
			}

			@Override
			public Object runInSandbox(DeserializationSandbox deserializationSandbox) {
				return null;
			}
			
		};
		
		SerDeServiceFactory.setSerdeService(serdeService);
		
		Integer deserializedI  = (Integer)SerDeServiceFactory.doDeserialize(null, new ByteArrayInputStream(new byte[1]));
		
		Assert.assertEquals(i, deserializedI);
	}
	
	@Test
	public void serializerRedirectLockManagedSuccessfully() {
		Assert.assertFalse(SerDeServiceFactory.isSerializerRedirectLocked());
		
		SerDeServiceFactory.lockSerializerRedirect();
		
		Assert.assertTrue(SerDeServiceFactory.isSerializerRedirectLocked());
		
		SerDeServiceFactory.unlockSerializerRedirect();
		
		Assert.assertFalse(SerDeServiceFactory.isSerializerRedirectLocked());
	}
	
	@Test
	public void deserializerRedirectLockManagedSuccessfully() {
		Assert.assertFalse(SerDeServiceFactory.isDeserializerRedirectLocked());
		
		SerDeServiceFactory.lockDeserializerRedirect();
		
		Assert.assertTrue(SerDeServiceFactory.isDeserializerRedirectLocked());
		
		SerDeServiceFactory.unlockDeserializerRedirect();
		
		Assert.assertFalse(SerDeServiceFactory.isDeserializerRedirectLocked());
	}
	
}
