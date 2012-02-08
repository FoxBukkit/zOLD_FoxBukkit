package de.doridian.yiffbukkit.main;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class StateContainer {
	@Retention(RetentionPolicy.RUNTIME) protected @interface Loader { String[] value(); }
	@Retention(RetentionPolicy.RUNTIME) protected @interface Saver { String[] value(); }

	// For the lack of a better name...
	class Closure {
		StateContainer instance;
		Method method;

		public Closure(StateContainer instance, Method method) {
			super();
			this.instance = instance;
			this.method = method;
		}

		public String toString() {
			return method.getDeclaringClass().getName()+"."+method.getName()+"()";
		}

		public void invoke() {
			try {
				method.invoke(instance);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private static Map<String, Closure> loadersByName = new HashMap<String, Closure>();
	private static Map<String, Closure> saversByName = new HashMap<String, Closure>();
	private static Set<Closure> loaders = new HashSet<Closure>();
	private static Set<Closure> savers = new HashSet<Closure>();

	public StateContainer() {
		for (Method method : getClass().getMethods()) {
			final Loader loaderAnnotation = method.getAnnotation(Loader.class);
			final Saver saverAnnotation = method.getAnnotation(Saver.class);
			if (loaderAnnotation != null) {
				final Closure closure = new Closure(this, method);

				loaders.add(closure);
				for (String name : loaderAnnotation.value()) {
					loadersByName.put(name, closure);
				}
			}
			if (saverAnnotation != null) {
				final Closure closure = new Closure(this, method);

				savers.add(closure);
				for (String name : saverAnnotation.value()) {
					saversByName.put(name, closure);
				}
			}
		}
	}

	public static void loadAll() {
		for (Entry<String, Closure> entry : loadersByName.entrySet()) {
			Closure closure = entry.getValue();

			closure.invoke();
		}
	}

	public static boolean loadSingle(String loaderName) {
		final Closure closure = loadersByName.get(loaderName);
		if (closure == null)
			return false;

		closure.invoke();
		return true;
	}

	public static void saveAll() {
		for (Entry<String, Closure> entry : saversByName.entrySet()) {
			Closure closure = entry.getValue();

			closure.invoke();
		}
	}

	public static boolean saveSingle(String saverName) {
		final Closure closure = saversByName.get(saverName);
		if (closure == null)
			return false;
		
		closure.invoke();
		return true;
	}
}
