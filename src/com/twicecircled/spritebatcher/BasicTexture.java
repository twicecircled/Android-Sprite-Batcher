package com.twicecircled.spritebatcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BasicTexture extends Texture {
	// Basic texture for drawing sprites

	public BasicTexture(int bitmapId) {
		this.bitmapId = bitmapId;
	}

	@Override
	protected Bitmap getBitmap(Context context) {
		return BitmapFactory.decodeResource(context.getResources(), bitmapId);
	}
}
