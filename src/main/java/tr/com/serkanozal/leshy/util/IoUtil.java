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

package tr.com.serkanozal.leshy.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * @author Serkan OZAL
 */
public class IoUtil {

	private static final Logger logger = LogUtil.getLogger();
	
	private IoUtil() {
		
	}
	
	public static InputStream getResourceAsStream(String resourcePath) {
		try {
			if (resourcePath.startsWith("/") == false) {
				resourcePath = "/" + resourcePath;
			}
			InputStream is = IoUtil.class.getResourceAsStream(resourcePath);
			if  (is == null) {
				is = getCallerClass().getResourceAsStream(resourcePath);
			}
			return is;
		}
		catch (Throwable t) {
			logger.error("Unable to get resource " + "(" + resourcePath + ")" + " as stream", t);
			return null;
		}
	}
	
	public static File getResourceAsFile(String resourcePath) {
		try {
			if (resourcePath.startsWith("/") == false) {
				resourcePath = "/" + resourcePath;
			}
			URL url = IoUtil.class.getResource(resourcePath);
			if (url == null) {
				url = getCallerClass().getResource(resourcePath);
			}
			return new File(url.toURI());
		} 
		catch (Throwable t) {
			logger.error("Unable to get resource " + "(" + resourcePath + ")" + " as file", t);
			return null;
		}
	}
	
	public static String getContentOfInputStream(InputStream is) {
		return new Scanner(is).useDelimiter("\\Z").next();
	}
	
	private static Class<?> getCallerClass() {
		try {
			return Class.forName(Thread.currentThread().getStackTrace()[3].getClassName());
		} 
		catch (ClassNotFoundException e) {
			logger.error("Unable to get caller class", e);
			return null;
		}
	}
	
}
