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

package tr.com.serkanozal.leshy.filter;

/**
 * @author Serkan ÖZAL
 */
public class PackageFilter implements Filter {

	private String packagePrefix;
	
	public PackageFilter(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}
	
	@Override
	public boolean useObject(Object obj) {
		if (obj == null) {
			return false;
		}
		Package pck = obj.getClass().getPackage();
		if (pck == null) {
			return false;
		}
		String pckName = pck.getName();
		if (pckName.startsWith(packagePrefix)) {
			return true;
		}
		else {
			return false;
		}
	}

}
