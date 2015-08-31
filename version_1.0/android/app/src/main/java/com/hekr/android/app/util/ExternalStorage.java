package com.hekr.android.app.util;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;

/**
 * Utility class for managing external storage.
 * 
 */
public class ExternalStorage {
	
	@SuppressWarnings("unused")
	private static final String TAG = "ExternalStorage";
	
	// Convention for external storage path used by Android 2.2.
	private static final String EXT_STORAGE_ROOT_PREFIX = "/Android/data/";
	private static final String EXT_STORAGE_ROOT_SUFFIX = "/files/";

	private static StringBuilder sStoragePath = new StringBuilder();

	/**
	 * Likely places where we could find an external SD card mounted.
	 * This list was determined empirically, by looking at various devices.
	 * It is not known to be complete.
	 */ 
	private static final String ALTERNATE_SDCARD_MOUNTS[] = {
		"/emmc",				// Internal storage on Droid Incredible, Nook Color/CyanogenMod, some other devices
		"/sdcard/ext_sd",		// Newer (2011) HTC devices (Flyer, Rezound)
		"/sdcard-ext",			// Some Motorola devices (RAZR)
		"/sdcard/sd",			// Older Samsung Galaxy S (Captivate)
		"/sdcard/sdcard"		// Archos tablets
	};

	public static File getSDCacheDir( Context context, String dirName ) {
		File cacheDir = null;
		
		// Check to see if SD Card is mounted and read/write accessible
		if ( android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState()) ) {
			// Get the directory on the SD card to store content
			// Attempt to use getExternalFilesDir() if we are on Android 2.2 or newer
			// Data stored in this location will auto-delete with app uninstall
			Method getExternalFilesDirMethod = null;
			try {
				getExternalFilesDirMethod = Context.class.getMethod( "getExternalFilesDir", String.class );
				cacheDir = (File) getExternalFilesDirMethod.invoke( context, dirName );
			} catch (NoSuchMethodException e) {
				// Android 2.1 and earlier - use old APIs
				cacheDir = buildCacheDirPath( context, android.os.Environment.getExternalStorageDirectory(), dirName );
			} catch (IllegalArgumentException e) {
				cacheDir = buildCacheDirPath( context, android.os.Environment.getExternalStorageDirectory(), dirName );
			} catch (IllegalAccessException e) {
				cacheDir = buildCacheDirPath( context, android.os.Environment.getExternalStorageDirectory(), dirName );
			} catch (InvocationTargetException e) {
				cacheDir = buildCacheDirPath( context, android.os.Environment.getExternalStorageDirectory(), dirName );
			}
		}
		
		if ( cacheDir == null ) {
			// Attempting to find the default external storage was a failure.
			// Look for another suitable external filesystem where we can store our crap
			for ( int i = 0; i < ALTERNATE_SDCARD_MOUNTS.length; i++ ) {
				File alternateDir = new File( ALTERNATE_SDCARD_MOUNTS[i] );
				if ( alternateDir.exists() && alternateDir.isDirectory() && 
						alternateDir.canRead() && alternateDir.canWrite() ) {
					cacheDir = buildCacheDirPath( context, alternateDir, dirName );
					break;
				}	
			}
		}

		// Attempt to create folder on external storage if it does not exist
		if ( cacheDir != null && !cacheDir.exists() ) {
			if ( !cacheDir.mkdirs() ) {
				cacheDir = null;		// Failed to create folder
			}
		}

		// Fall back on internal cache as a last resort
		if ( cacheDir == null ) {
			cacheDir = new File( context.getCacheDir() + File.separator + dirName );
			cacheDir.mkdirs();
		}
		
		return cacheDir;		
	}
	
	/**
	 * Clear files from SD cache.
	 * 
	 * @param context
	 */
	public static void clearSDCache( Context context, String dirName ) {
		File cacheDir = getSDCacheDir( context, dirName );
		File[] files = cacheDir.listFiles();
		for (File f : files) {
			f.delete();
		}
		cacheDir.delete();
	}
	
	/**
	 * Use older Android APIs to put data in the same relative directory location
	 * as the 2.2 API.
	 * 
	 * When device upgrades to 2.2, data will auto-delete with app uninstall.
	 * 
	 * @param mountPoint
	 * @return
	 */
	private static File buildCacheDirPath( Context context, File mountPoint, String dirName ) {
		sStoragePath.setLength(0);
		sStoragePath.append(EXT_STORAGE_ROOT_PREFIX);
		sStoragePath.append(context.getPackageName());
		sStoragePath.append(EXT_STORAGE_ROOT_SUFFIX);
		sStoragePath.append(dirName);
		return new File( mountPoint, sStoragePath.toString());
	}

}