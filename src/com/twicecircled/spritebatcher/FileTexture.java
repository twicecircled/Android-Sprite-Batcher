package com.twicecircled.spritebatcher;

import java.io.IOException;
import java.io.InputStream;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class FileTexture extends Texture {
	// Texture that gets its bitmap from a filepath

	private String filepath;
	private int apkExpansionVersionMain;
	private int apkExpansionVersionPatch;

	public FileTexture(String filepath, int apkExpansionVersionMain,
			int apkExpansionVersionPatch) {
		this.filepath = filepath;
		this.apkExpansionVersionMain = apkExpansionVersionMain;
		this.apkExpansionVersionPatch = apkExpansionVersionPatch;
	}

	@Override
	protected Bitmap getBitmap(Context context) {
		// Get a ZipResourceFile representing a merger of both the main and
		// patch files
		ZipResourceFile expansionFile;
		try {
			expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context,
					apkExpansionVersionMain, apkExpansionVersionPatch);
		} catch (IOException e) {
			Log.e(SpriteBatcher.TAG, "Error finding expansion apk file", e);
			return null;
		}

		// Get an input stream for a known file inside the expansion file ZIPs
		InputStream fileStream;
		try {
			fileStream = expansionFile.getInputStream(filepath);
		} catch (IOException e) {
			Log.e(SpriteBatcher.TAG, "Error finding file at: " + filepath, e);
			return null;
		}
		return BitmapFactory.decodeStream(fileStream);
	}
}
