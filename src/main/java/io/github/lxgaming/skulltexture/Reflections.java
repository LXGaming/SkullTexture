package io.github.lxgaming.skulltexture;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public final class Reflections {
	
    private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
    private static String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
	private static Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");
	
	private Reflections() {
	}
	
	private static String expandVariables(String name) {
		StringBuffer output = new StringBuffer();
		Matcher matcher = MATCH_VARIABLE.matcher(name);
		
		while(matcher.find()) {
			String variable = matcher.group(1);
			String replacement;
			
			if ("nms".equalsIgnoreCase(variable))  {
				replacement = NMS_PREFIX;
			} else if ("obc".equalsIgnoreCase(variable)) {
				replacement = OBC_PREFIX;
			} else if ("version".equalsIgnoreCase(variable)) {
				replacement = VERSION;
			} else {
				throw new IllegalArgumentException("Unknown variable: " + variable);
			}
				
			if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.') {
				replacement += ".";
			}
			matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(output);
		return output.toString();
	}
	
	private static Class<?> getCanonicalClass(String canonicalName) {
		try {
			return Class.forName(canonicalName);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Cannot find " + canonicalName, ex);
		}
	}
	
	public static Class<?> getClass(String lookupName) {
		return getCanonicalClass(expandVariables(lookupName));
	}
	
	public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
		return getField(target, name, fieldType, 0);
	}
	
	public static <T> FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
		return getField(getClass(className), name, fieldType, 0);
	}
	
	public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
		return getField(target, null, fieldType, index);
	}
	
	public static <T> FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
		return getField(getClass(className), fieldType, index);
	}
	
	
	private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<?> fieldType, int index) {
		for (final Field field : target.getDeclaredFields()) {
			if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
				field.setAccessible(true);
				
				return new FieldAccessor<T>() {
					@SuppressWarnings("unchecked")
					@Override
					public T get(Object target) {
						try {
							return (T) field.get(target);
						} catch (IllegalAccessException ex) {
							throw new RuntimeException("Cannot access reflection.", ex);
						}
					}
					
					@Override
					public void set(Object target, Object value) {
						try {
							field.set(target, value);
						} catch (IllegalAccessException ex) {
							throw new RuntimeException("Cannot access reflection.", ex);
						}
					}
					
					@Override
					public boolean hasField(Object target) {
						return field.getDeclaringClass().isAssignableFrom(target.getClass());
					}
				};
			}
		}
		
		if (target.getSuperclass() != null) {
			return getField(target.getSuperclass(), name, fieldType, index);
		}
		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}
	
	public interface FieldAccessor<T> {
		public T get(Object target);
		public void set(Object target, Object value);
		public boolean hasField(Object target);
	}
}