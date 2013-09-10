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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author Serkan ÖZAL
 */
public class SerDeServiceFactory {

	public static final ThreadLocal<Boolean> SERIALIZER_REDIRECT_LOCK = new ThreadLocal<Boolean>(); 
	public static final ThreadLocal<Boolean> DESERIALIZER_REDIRECT_LOCK = new ThreadLocal<Boolean>(); 
	
	private static SerDeService serdeService = new SerDeServiceImpl();
	
	
	private SerDeServiceFactory() {
		
	}
	
	public static SerDeService getSerdeService() {
		return serdeService;
	}
	
	public static void setSerdeService(SerDeService serdeService) {
		SerDeServiceFactory.serdeService = serdeService;
	}
	
	// Used by agent via reflection due to bootstrap classloader challange
	public static void doSerialize(ObjectOutputStream oos, Object obj, OutputStream os) throws IOException {
		serdeService.doSerialize(oos, obj, os);
	}
	
	// Used by agent via reflection due to bootstrap classloader challange
	public static Object doDeserialize(ObjectInputStream ois, InputStream is) throws IOException, ClassNotFoundException {
		return serdeService.doDeserialize(ois, is);
	}
	
	public static boolean isSerializerRedirectLocked() {
		return SERIALIZER_REDIRECT_LOCK.get() != null;
	}
	
	public static void lockSerializerRedirect() {
		SERIALIZER_REDIRECT_LOCK.set(true);
	}
	
	public static void unlockSerializerRedirect() {
		SERIALIZER_REDIRECT_LOCK.remove();
	}
	
	public static boolean isDeserializerRedirectLocked() {
		return DESERIALIZER_REDIRECT_LOCK.get() != null;
	}
	
	public static void lockDeserializerRedirect() {
		DESERIALIZER_REDIRECT_LOCK.set(true);
	}
	
	public static void unlockDeserializerRedirect() {
		DESERIALIZER_REDIRECT_LOCK.remove();
	}
	
}
