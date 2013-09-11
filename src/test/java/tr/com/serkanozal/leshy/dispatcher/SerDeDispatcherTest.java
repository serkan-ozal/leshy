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

package tr.com.serkanozal.leshy.dispatcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;

import tr.com.serkanozal.leshy.filter.Filter;
import tr.com.serkanozal.leshy.serde.SerDe;

/**
 * @author Serkan ÖZAL
 */
public class SerDeDispatcherTest {
	
	@Test
	public void specifiedFilterUsed() {
		Integer i = new Integer(1);
		Filter mockFilter = EasyMock.createMock(Filter.class);
		EasyMock.expect(mockFilter.useObject(i)).andReturn(true);
		EasyMock.replay(mockFilter);
		Assert.assertTrue(new SerDeDispatcher(mockFilter, null).useObject(i));
	}
	
	@Test
	public void specifiedSerializerUsed() throws IOException {
		Integer i = new Integer(1);
		
		Filter mockFilter = EasyMock.createMock(Filter.class);
		EasyMock.expect(mockFilter.useObject(i)).andReturn(true);
		EasyMock.replay(mockFilter);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SerDe serDe = new SerDe() {
			@Override
			public void serialize(Object obj, OutputStream os) throws IOException {
				os.write(1);
			}
			
			@Override
			public Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
				return null;
			}
		};
		
		new SerDeDispatcher(mockFilter, serDe).dispatchToSerialize(i, bos);

		bos.flush();
		
		byte[] array = bos.toByteArray();
		Assert.assertEquals(1, array.length);
		Assert.assertEquals(1, array[0]);
	}
	
	@Test
	public void specifiedDeserializerUsed() throws IOException, ClassNotFoundException {
		final Integer i = new Integer(1);
		
		Filter mockFilter = EasyMock.createMock(Filter.class);
		EasyMock.expect(mockFilter.useObject(i)).andReturn(true);
		EasyMock.replay(mockFilter);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(new byte[1]);
		SerDe serDe = new SerDe() {
			@Override
			public void serialize(Object obj, OutputStream os) throws IOException {
				
			}
			
			@Override
			public Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
				return i;
			}
		};
		
		Integer deserializedI = (Integer)new SerDeDispatcher(mockFilter, serDe).dispatchToDeserialize(bis);

		Assert.assertEquals(i, deserializedI);
	}
	
}
