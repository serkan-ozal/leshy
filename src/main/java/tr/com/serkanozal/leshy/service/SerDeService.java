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

import tr.com.serkanozal.leshy.dispatcher.SerDeDispatcher;

/**
 * @author Serkan ÖZAL
 */
public interface SerDeService {

	void setup();
	void remove();
	
	void serializationOpenForAllTypes();
	void serializationOpenOnlyForSerializableTypes();
	
	SerDeService registerSerDe(SerDeDispatcher serdeDispatcher);

	void doSerialize(ObjectOutputStream oos, Object obj, OutputStream os) throws IOException;
	Object doDeserialize(ObjectInputStream ois, InputStream is) throws IOException, ClassNotFoundException;
	
	void runInSandbox(SerializationSandbox serializationSandbox);
	Object runInSandbox(DeserializationSandbox deserializationSandbox);
	
}
