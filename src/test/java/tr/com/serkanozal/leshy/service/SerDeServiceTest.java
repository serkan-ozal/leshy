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
import java.io.OutputStream;

import junit.framework.Assert;

import org.junit.Test;

import tr.com.serkanozal.leshy.dispatcher.SerDeDispatcher;
import tr.com.serkanozal.leshy.filter.ClassFilter;
import tr.com.serkanozal.leshy.serde.SerDe;

/**
 * @author Serkan ÖZAL
 */
public class SerDeServiceTest {

	private static class CustomSerDeForSerialization implements SerDe {
		@Override
		public void serialize(Object obj, OutputStream os) throws IOException {
			os.write(((Integer)obj).intValue());
		}
		
		@Override
		public Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
			return null;
		}
	}
	
	@Test
	public void objectSerializedWithSpecifiedSerDe() throws IOException {
		final Integer i = new Integer(1);
		
		SerDeServiceFactory.turnToDefaultSerDeService();
		SerDeService serdeService = SerDeServiceFactory.getSerdeService();
		serdeService.
			registerSerDe(new SerDeDispatcher(new ClassFilter(Integer.class), new CustomSerDeForSerialization())).
			setup();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		serdeService.doSerialize(null, i, bos);
		bos.flush();

		byte[] array = bos.toByteArray();
		
		byte classNameLength = array[0];
		Assert.assertEquals(CustomSerDeForSerialization.class.getName().length(), classNameLength);
		
		String className = new String(array, 1, classNameLength);
		Assert.assertEquals(CustomSerDeForSerialization.class.getName(), className);
		
		Assert.assertEquals(1, array[1 + classNameLength]);
	}
	
	private static class CustomSerDeForDeserialization implements SerDe {
		@Override
		public void serialize(Object obj, OutputStream os) throws IOException {
			
		}
		
		@Override
		public Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
			return new Integer(is.read());
		}
	}
	
	@Test
	public void objectDeserializedWithSpecifiedSerDe() throws IOException, ClassNotFoundException {
		final Integer i = new Integer(1);
		
		SerDeServiceFactory.turnToDefaultSerDeService();
		SerDeService serdeService = SerDeServiceFactory.getSerdeService();
		serdeService.
			registerSerDe(new SerDeDispatcher(new ClassFilter(Integer.class), new CustomSerDeForDeserialization())).
			setup();
		
		String className = CustomSerDeForDeserialization.class.getName();
		int classNameLength = className.length();
		byte[] array = new byte[1 + classNameLength + 1];
		array[0] = (byte)classNameLength;
		System.arraycopy(className.getBytes(), 0, array, 1, classNameLength);
		array[classNameLength + 1] = 1;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(array);
		Integer deserializedI = (Integer)serdeService.doDeserialize(null, bis);

		Assert.assertEquals(i, deserializedI);
	}
	
}
