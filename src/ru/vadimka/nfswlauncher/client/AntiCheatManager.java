package ru.vadimka.nfswlauncher.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import ru.vadimka.nfswlauncher.Log;

abstract public class AntiCheatManager {
	public static AntiCheat create(String path) {
		try {
			Class<?> c = Class.forName("ru.vadimka.nfswlauncher.anticheat.RWAC");
			Class[] argTypes = new Class[1];
			argTypes[0] = String.class;
			Constructor<?> ct = c.getConstructor(argTypes);
			
			Object[] args = new Object[1];
			args[0] = path;
			return (AntiCheat)ct.newInstance(args);
		} catch (ClassNotFoundException e) {
			Log.getLogger().log(Level.WARNING,"RWAC not founded",e);
		} catch (NoSuchMethodException e) {
			Log.getLogger().log(Level.WARNING,"RWAC method not founded",e);
		} catch (SecurityException | InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
			Log.getLogger().log(Level.WARNING,"RWAC error",e);
		}
		return null;
	}
}
