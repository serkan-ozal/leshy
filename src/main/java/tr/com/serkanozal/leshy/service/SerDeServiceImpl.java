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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import tr.com.serkanozal.jillegal.Jillegal;
import tr.com.serkanozal.jillegal.instrument.Instrumenter;
import tr.com.serkanozal.jillegal.instrument.domain.model.GeneratedClass;
import tr.com.serkanozal.jillegal.instrument.service.InstrumenterService;
import tr.com.serkanozal.jillegal.instrument.service.InstrumenterServiceFactory;
import tr.com.serkanozal.leshy.dispatcher.SerDeDispatcher;
import tr.com.serkanozal.leshy.serde.SerDe;
import tr.com.serkanozal.leshy.util.IoUtil;
import tr.com.serkanozal.leshy.util.LogUtil;

/**
 * @author Serkan ÖZAL
 */
public class SerDeServiceImpl implements SerDeService {

	private static final Logger logger = LogUtil.getLogger();
	
	private List<SerDeDispatcher> serdeDispatcherList = new ArrayList<SerDeDispatcher>();
	private Map<String, SerDeDispatcher> serdeDispatcherMap = new HashMap<String, SerDeDispatcher>();

	@Override
	public SerDeService registerSerDe(SerDeDispatcher serdeDispatcher) {
		serdeDispatcherList.add(serdeDispatcher);
		serdeDispatcherMap.put(serdeDispatcher.getSerde().getClass().getName(), serdeDispatcher);
		return this;
	}
	
	@Override
	public void setup() {
		try {
            Jillegal.init();

			final String doSerializeCode = 
					IoUtil.getContentOfInputStream(IoUtil.getResourceAsStream("doSerialize.txt"));
			final String doDeserializeCode = 	
					IoUtil.getContentOfInputStream(IoUtil.getResourceAsStream("doDeserialize.txt"));
			
			InstrumenterService instrumenterService = InstrumenterServiceFactory.getInstrumenterService();
			
	        Instrumenter<ObjectOutputStream> objectOutputStreamInstrumenter = 
	        		instrumenterService.getInstrumenter(ObjectOutputStream.class);
	        GeneratedClass<ObjectOutputStream> instrumentedObjectOutputStreamClass = 
	        	objectOutputStreamInstrumenter.
		        	updateMethod(
		        		"writeObject", 
		        		doSerializeCode, 
		        		new Class<?>[] { Object.class }).
		        	build();
	        instrumenterService.redefineClass(instrumentedObjectOutputStreamClass);
	        
	        Instrumenter<ObjectInputStream> objectInputStreamInstrumenter = 
	        		instrumenterService.getInstrumenter(ObjectInputStream.class);
	        GeneratedClass<ObjectInputStream> instrumentedObjectInputStreamClass = 
	            	objectInputStreamInstrumenter.
	    	        	updateMethod(
	    	        		"readObject", 
	    	        		doDeserializeCode, 
	    	        		new Class<?>[] { }).
	    	        	build();
	        instrumenterService.redefineClass(instrumentedObjectInputStreamClass);
		}
		catch (Throwable t) {
			logger.error("Error occured while building SerDe service", t);
			throw new IllegalStateException(t);
		}
	}

	@Override
	public void doSerialize(ObjectOutputStream oos, Object obj, OutputStream os) throws IOException {
		for (SerDeDispatcher sd : serdeDispatcherList) {
			if (sd.useObject(obj)) {
				SerDe serde = sd.getSerde();
				Class<? extends SerDe> serdeClass = serde.getClass();
				String serdeClassName = serdeClass.getName();
				os.write(serdeClassName.length());
				os.write(serdeClassName.getBytes());
				sd.dispatchToSerialize(obj, os);
				return;
			}
		}
		try {
			SerDeServiceFactory.lockSerializerRedirect(); 
			os.write(0);
			oos.writeObject(obj);
		}
		finally {
			SerDeServiceFactory.unlockSerializerRedirect(); 
		}
	}

	@Override
	public Object doDeserialize(ObjectInputStream ois, InputStream is) throws IOException, ClassNotFoundException {
		int serdeClassNameLength = is.read();
		if (serdeClassNameLength > 0) {
			byte[] serdeClassNameArray = new byte[serdeClassNameLength];
			is.read(serdeClassNameArray);
			String serdeClassName = new String(serdeClassNameArray);
			SerDeDispatcher serdeDispatcher = serdeDispatcherMap.get(serdeClassName);
			if (serdeDispatcher != null) {
				return serdeDispatcher.dispatchToDeserialize(is);
			}
			else {
				try {
					SerDeServiceFactory.lockDeserializerRedirect(); 
					return ois.readObject();
				}
				finally {
					SerDeServiceFactory.unlockDeserializerRedirect(); 
				}
			}
		}
		else {
			try {
				SerDeServiceFactory.lockDeserializerRedirect(); 
				return ois.readObject();
			}
			finally {
				SerDeServiceFactory.unlockDeserializerRedirect(); 
			}
		}
	}

}
