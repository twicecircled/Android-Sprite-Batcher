package com.twicecircled.spritebatcher;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.SparseArray;

public abstract class Texture {
	// Abstract texture class, holds everything needed to draw this texture's
	// batch of sprites

	// Shared fields
	protected SparseArray<SpriteData> spriteDatas = new SparseArray<SpriteData>();
	protected int textureId;
	protected int bitmapId;
	protected int width;
	protected int height;

	protected final static int DEFAULT_ARGB = 0xffffffff;

	// SHARED METHODS:
	// Return SpriteData class
	public SparseArray<SpriteData> getSpriteData() {
		return spriteDatas;
	}

	// Return unique texture id associated with this texture
	public int getTextureId() {
		return textureId;
	}

	// Set unique texture id associated with this texture
	public void setTextureId(int id) {
		textureId = id;
	}

	// Passes dimensional data over to spritedata
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void addSprite(Rect src, Rect dst) {
		// No rgba value defined so use default
		getDefaultSpriteData().addSprite(src, dst);
	}

	public void addSprite(Rect src, Rect dst, int angle) {
		getDefaultSpriteData().addSprite(src, dst, angle);
	}

	public void addSprite(Rect src, int drawX, int drawY, Rect hotRect,
			int angle, float sizeX, float sizeY) {
		getDefaultSpriteData().addSprite(src, drawX, drawY, hotRect, angle,
				sizeX, sizeY);
	}

	protected SpriteData getDefaultSpriteData(){
		SpriteData spriteData = spriteDatas.get(DEFAULT_ARGB);
		if (spriteData==null){
			// Create it
			spriteData = new SpriteData(DEFAULT_ARGB);
			spriteDatas.put(DEFAULT_ARGB, spriteData);
			spriteData.setDimensions(width, height);
		}
		return spriteData;
	}
	
	protected SpriteData getARGBSpriteData(int argb){
		SpriteData spriteData = spriteDatas.get(argb);
		if (spriteData==null){
			// Create it
			spriteData = new SpriteData(argb);
			spriteDatas.put(argb, spriteData);
			spriteData.setDimensions(width, height);
		}
		return spriteData;
	}

	// REQUIRED METHODS

	// The constructor needs to be specfied in the subclass

	// Get bitmap object. Note this must decoded on the fly and recycled so that
	// it is not ever held in memory
	public abstract Bitmap getBitmap(Resources resources);

}
