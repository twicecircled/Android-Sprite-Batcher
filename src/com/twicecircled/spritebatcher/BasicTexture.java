package com.twicecircled.spritebatcher;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BasicTexture extends Texture {
	// Basic texture for drawing sprites

	public BasicTexture(int bitmapId) {
		this.bitmapId = bitmapId;
	}

	@Override
	public Bitmap getBitmap(Resources resources) {
		return BitmapFactory.decodeResource(resources, bitmapId);
	}
}
