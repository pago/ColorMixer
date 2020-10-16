/*
 * Copyright 2005 Patrick Gotthardt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pagosoft.eventbus;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Patrick Gotthardt
 */
public class ApplicationHandler implements ApplicationListener {
	private Map<Class<? extends ApplicationEvent>, Method> cache;
	private Object source;

	public ApplicationHandler() {
		source = this;
		buildCache();
	}

	public ApplicationHandler(Object source) {
		this.source = source;
		buildCache();
	}

	private final void buildCache() {
		cache = new HashMap<Class<? extends ApplicationEvent>, Method>();
		Method[] methods = source.getClass().getMethods();
		for(Method method : methods) {
			if(method.isAnnotationPresent(EventHandler.class)) {
				cache.put(method.getAnnotation(EventHandler.class).value(), method);
			}
		}
	}

	public Set<Class<? extends ApplicationEvent>> getObservedEvents() {
		return cache.keySet();
	}

	public void handleEvent(ApplicationEvent event) {
		Method m = cache.get(event.getEventClass());
		if(m != null) {
			try {
				m.invoke(source, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
