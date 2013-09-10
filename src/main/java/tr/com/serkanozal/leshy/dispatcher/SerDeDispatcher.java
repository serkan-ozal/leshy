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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tr.com.serkanozal.leshy.filter.Filter;
import tr.com.serkanozal.leshy.serde.SerDe;

/**
 * @author Serkan ÖZAL
 */
public class SerDeDispatcher implements Filter {

	protected Filter filter;
	protected SerDe serde;
	
	public SerDeDispatcher(Filter filter, SerDe serde) {
		this.filter = filter;
		this.serde = serde;
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	public SerDe getSerde() {
		return serde;
	}
	
	@Override
	public boolean useObject(Object o) {
		return filter.useObject(o);
	}
	
	public void dispatchToSerialize(Object obj, OutputStream os) throws IOException {
		serde.serialize(obj, os);
	}
	
	public Object dispatchToDeserialize(InputStream is) throws IOException, ClassNotFoundException {
		return serde.deserialize(is);
	}

}
