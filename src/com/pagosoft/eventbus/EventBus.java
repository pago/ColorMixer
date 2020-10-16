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

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The EventBus is a central part of most non trivial applications.
 *
 * By utilizing this class you'll be able to realize a many-to-many
 * relationship of application-wide events.
 *
 * For example: Many parts may react when a file is to be opened
 * (the editor component needs to display a new editor for it, the
 * recent-files menu needs to be updated, etc.). However, there may
 * be multiple sources of such an event (the filebrowser, some code,
 * drag and drop of a file, etc.).
 *
 * This wouldn't be possible with a traditional event-model as you'd
 * have to register the listeners to any component - but you may not
 * know all of them. Therefore the simple one-to-many model isn't
 * appropriate here.
 *
 * The EventBus offers "topics". A topic is specified by the class of
 * its event. So, to stay with our example, the topic for opened files
 * would be <code>org.simpleedit.event.FileOpeningRequestedEvent.class</code>.
 *
 * There are multiple add-methods to add a listener to a specific topic,
 * as well as appropriate remove-methods.
 *
 * It is also possible to fire events from the EventDispatchThread (Swing)
 * and to run each of them in a thread of their own (note: as they run in
 * a thread of their own you shouldn't call any Swing-stuff
 * in there - except you utilize SwingUtilities or something alike).
 *
 * @author Patrick Gotthardt
 */
public class EventBus {
	private static EventBus instance;

	/**
	 * Lazily creates an EventBus instance (if there is none yet)
	 * and returns it.
	 *
	 * @return the singleton instance
	 */
	public static EventBus getInstance() {
		if(instance == null) {
			instance = new EventBus();
		}
		return instance;
	}

	private Map<Class, List<ApplicationListener>> listeners;
	private EventBus() {
		listeners = new HashMap<Class, List<ApplicationListener>>();
	}

	/**
	 * Adds a listener to a specific topic.
	 *
	 * @param eventClass The topic
	 * @param listener The listener to be notified
	 * @return true if the listener has been added, false otherwise
	 */
	public boolean add(Class eventClass, ApplicationListener listener) {
		List<ApplicationListener> topic = listeners.get(eventClass);
		if(topic == null) {
			topic = new ArrayList<ApplicationListener>();
			listeners.put(eventClass, topic);
		}
		return topic.add(listener);
	}

	/**
	 * Adds the listener to all specified topics.
	 *
	 * @param listener The listener to be notified
	 * @param eventClasses The topics
	 * @return true if the listener has been added to all topics, false otherwise
	 */
	public boolean add(ApplicationListener listener, Class ... eventClasses) {
		boolean result = true;
		for (Class eventClass : eventClasses) {
			result = add(eventClass, listener) && result;
		}
		return result;
	}

	/**
	 * Registers the {@link ApplicationHandler} to all subscribed topics.
	 * Refer to the documentation of {@link ApplicationHandler} to learn
	 * how it works.
	 *
	 * @param listener The listener to be notified
	 * @return true if the listener has been added to all topics, false otherwise
	 */
	public boolean add(ApplicationHandler listener) {
		boolean result = true;
		for(Class<? extends ApplicationEvent> clazz : listener.getObservedEvents()) {
			result = add(clazz, listener) && result;
		}
		return result;
	}

	/**
	 * Removes a listener from a specific topic.
	 *
	 * @param eventClass The topic from which the listener shall be removed
	 * @param listener The listener to be removed
	 * @return true if the listener was removed from the topic
	 */
	public boolean remove(Class eventClass, ApplicationListener listener) {
		List<ApplicationListener> topic = listeners.get(eventClass);
		boolean result = false;
		if(topic != null) {
			result = topic.remove(listener);
			// don't keep empty lists - they're no good
			if(topic.size() == 0) {
				listeners.remove(topic);
				topic = null;
			}
		}
		return result;
	}

	/**
	 * Removes the {@link ApplicationHandler} from all registered topics.
	 *
	 * @param listener The
	 * @return true if the listener has been removed from all
	 * 					specified topics, false otherwise
	 */
	public boolean remove(ApplicationHandler listener) {
		boolean result = true;
		for(Class<? extends ApplicationEvent> clazz : listener.getObservedEvents()) {
			result = remove(clazz, listener) && result;
		}
		return result;
	}

	/**
	 * Removes the complete topic and all of its listeners.
	 * This may be useful for cleanups.
	 *
	 * @param eventClass
	 * @return true if the topic was removed, false otherwise
	 * 					(this includes the case when there
	 * 					was no such topic => no registerd listeners)
	 */
	public boolean remove(Class eventClass) {
		return listeners.remove(eventClass) != null;
	}

	/**
	 * Fire an ApplicationEvent through the bus. This will be
	 * executed in the current thread.
	 * @param event
	 */
	public void fireEvent(ApplicationEvent event) {
		Class eventClass = event.getEventClass();
		List<ApplicationListener> topic = listeners.get(eventClass);
		// anyone interested?
		if(topic == null) {
			return;
		}
		int length = topic.size();
		for(int i = length-1; i >= 0; i--) {
			topic.get(i).handleEvent(event);
		}
	}

	/**
	 * Fire the event from within the EventDispatchThread.
	 * @param event
	 */
	public void fireSwingEvent(final ApplicationEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			fireEvent(event);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					fireEvent(event);
				}
			});
		}
	}

	// asynchronous stuff
	private ThreadPoolExecutor executor;
	private synchronized ThreadPoolExecutor getExecutor() {
		if(executor == null) {
			int threadCount = Runtime.getRuntime().availableProcessors() << 1;
			executor = new ThreadPoolExecutor(threadCount, threadCount << 1,
											  1L, TimeUnit.SECONDS,
											  new LinkedBlockingQueue<Runnable>());
		}
		return executor;
	}

	/**
	 * Execute each listener on a thread of its own at some point in the future.
	 * @param event
	 */
	public void fireAsychronousEvent(final ApplicationEvent event) {
		Class eventClass = event.getEventClass();
		List<ApplicationListener> topic = listeners.get(eventClass);
		// anyone interested?
		if(topic == null) {
			return;
		}
		int length = topic.size();
		for(int i = length-1; i >= 0; i--) {
			ApplicationListener listener = topic.get(i);
			getExecutor().execute(new EventDeliveryService(listener, event));
		}
	}

	private static class EventDeliveryService implements Runnable {
		private ApplicationListener listener;
		private ApplicationEvent event;

		public EventDeliveryService(ApplicationListener listener, ApplicationEvent event) {
			this.listener = listener;
			this.event = event;
		}

		public void run() {
			listener.handleEvent(event);
		}
	}
}
