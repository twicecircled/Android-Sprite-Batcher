/* Sprite Batcher V1.31
   Copyright (c) 2013 Tim Wicksteed <tim@twicecircled.com>
   http:/www.twicecircled.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.twicecircled.spritebatcher;

import android.content.Context;
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
	protected SparseArray<SpriteData> getSpriteData() {
		return spriteDatas;
	}

	// Return unique texture id associated with this texture
	protected int getTextureId() {
		return textureId;
	}

	// Set unique texture id associated with this texture
	protected void setTextureId(int id) {
		textureId = id;
	}

	// Passes dimensional data over to spritedata
	protected void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected void addSprite(Rect src, Rect dst) {
		// No rgba value defined so use default
		getDefaultSpriteData().addSprite(src, dst);
	}

	protected void addSprite(Rect src, Rect dst, int angle) {
		getDefaultSpriteData().addSprite(src, dst, angle);
	}

	protected void addSprite(Rect src, Rect dst, int angle, int argb) {
		getARGBSpriteData(argb).addSprite(src, dst, angle);
	}

	protected void addSprite(Rect src, int drawX, int drawY, Rect hotRect,
			int angle, float sizeX, float sizeY) {
		getDefaultSpriteData().addSprite(src, drawX, drawY, hotRect, angle,
				sizeX, sizeY);
	}

	protected void addSprite(Rect src, int drawX, int drawY, Rect hotRect,
			int angle, float sizeX, float sizeY, int argb) {
		getARGBSpriteData(argb).addSprite(src, drawX, drawY, hotRect, angle,
				sizeX, sizeY);
	}

	protected void drawLine(Rect src, int x1, int y1, int x2, int y2, int width) {
		getDefaultSpriteData().drawLine(src, x1, y1, x2, y2, width);
	}

	protected void drawTile(Rect dst, int offsetX, int offsetY, float scale) {
		getDefaultSpriteData().drawTile(dst, offsetX, offsetY, scale);
	}

	protected SpriteData getDefaultSpriteData() {
		SpriteData spriteData = spriteDatas.get(DEFAULT_ARGB);
		if (spriteData == null) {
			// Create it
			spriteData = new SpriteData(DEFAULT_ARGB);
			spriteDatas.put(DEFAULT_ARGB, spriteData);
			spriteData.setDimensions(width, height);
		}
		return spriteData;
	}

	protected SpriteData getARGBSpriteData(int argb) {
		SpriteData spriteData = spriteDatas.get(argb);
		if (spriteData == null) {
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
	protected abstract Bitmap getBitmap(Context context);

}
