package ige.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;

public class Notifier {

	// thread-safe singleton
	private static Notifier instance = new Notifier();
	
	public static Notifier getInstance() {
		return instance;
	}
	
	HashMap<String, Vector<PropertyChangeListener>> propertyChangeListeners = new HashMap<String,Vector<PropertyChangeListener>>();
	
	private Notifier() {
		// prevent implicit public,no-arg c'tor
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		Vector<PropertyChangeListener> vector = getVectorForPropertyName(propertyName);
		if (vector==null) {
			vector = new Vector<PropertyChangeListener>();
			vector.add(listener);
			propertyChangeListeners.put(propertyName, vector);
		}
		else if (!vector.contains(listener)) {
			vector.add(listener);
		}
	}
	
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		Vector<PropertyChangeListener> vector = getVectorForPropertyName(propertyName);
		if (vector==null || !vector.contains(listener)) {
			return;
		}
		else {
			vector.remove(listener);
			if (vector.size()==0) {
				vector=null;
				propertyChangeListeners.remove(propertyName);
			}
		}
	}

	public void firePropertyChange(Object src, String propertyName, Object newValue) {
		Vector<PropertyChangeListener> vector = getVectorForPropertyName(propertyName);
		if (vector==null || vector.size()==0) {
			return;
		}
		PropertyChangeEvent e = new PropertyChangeEvent(src, propertyName, null, newValue);
		if (vector!=null) {
			for (PropertyChangeListener listener : vector) {
				listener.propertyChange(e);
			}
		}
	}
	
	protected Vector<PropertyChangeListener> getVectorForPropertyName(String propertyName) {
		for (String key : propertyChangeListeners.keySet()) {
			if (key.equals(propertyName)) {
				return propertyChangeListeners.get(key);
			}
		}
		return null;
	}
}
