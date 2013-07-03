package com.twicecircled.spritebatcher;

import java.util.ArrayList;
import android.graphics.Rect;
import android.util.Log;

public class SpriteData {
	// A SpriteData contains all of the information needs to make a single
	// GLDRAWELEMENTS call. As you cannot draw using two textures in a single
	// GLDRAWELEMENTS there is a SpriteData for each texture.

	private ArrayList<Float> vertices; // Positions of vertices
	private ArrayList<Short> indices; // Which verts go together to form Ele's
	private ArrayList<Float> textureCoords; // Texture map coordinates

	private int textureWidth;
	private int textureHeight;

	private int argb;

	protected SpriteData(int rgba) {
		vertices = new ArrayList<Float>();
		indices = new ArrayList<Short>();
		textureCoords = new ArrayList<Float>();
		this.argb = rgba;
	}

	protected void setDimensions(int width, int height) {
		textureWidth = width;
		textureHeight = height;
	}

	protected int getARGB() {
		return argb;
	}

	// Add sprite methods
	// DIRECT
	protected void addVertices(float[] f) {
		for (int i = 0; i < f.length; i++) {
			vertices.add(f[i]);
		}
	}

	protected void addIndices(short[] s) {
		for (int i = 0; i < s.length; i++) {
			indices.add(s[i]);
		}
	}

	protected void addTextureCoords(float[] f) {
		for (int i = 0; i < f.length; i++) {
			textureCoords.add(f[i]);
		}
	}

	// SIMPLE
	protected void addSprite(Rect src, Rect dst) {
		// This is a simple class for doing straight src->dst draws

		// VERTICES
		vertices.add((float) dst.left);
		vertices.add((float) dst.top);
		vertices.add(0f);
		vertices.add((float) dst.left);
		vertices.add((float) dst.bottom);
		vertices.add(0f);
		vertices.add((float) dst.right);
		vertices.add((float) dst.bottom);
		vertices.add(0f);
		vertices.add((float) dst.right);
		vertices.add((float) dst.top);
		vertices.add(0f);

		// INDICES - increment from last value
		short lastValue;
		if (!indices.isEmpty()) {
			// If not empty, find last value
			lastValue = indices.get(indices.size() - 1);
		} else
			lastValue = -1;
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 2));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 4));

		// TEXTURE COORDS
		float[] srcX = { src.left, src.left, src.right, src.right };
		float[] srcY = { src.top, src.bottom, src.bottom, src.top };
		for (int i = 0; i < 4; i++) {
			textureCoords.add((float) (srcX[i] / textureWidth));
			textureCoords.add((float) (srcY[i] / textureHeight));
		}
	}

	protected void addSprite(Rect src, Rect dst, int angle) {
		// This is a simple class for doing straight src->dst draws
		// It automatically rotates the images by angle about its centre

		// VERTICES
		// Trig
		double cos = Math.cos((double) angle / 180 * Math.PI);
		double sin = Math.sin((double) angle / 180 * Math.PI);

		// Width and height
		float halfWidth = (dst.right - dst.left) / 2;
		float halfHeight = (dst.top - dst.bottom) / 2;

		// Coordinates before rotation
		float[] hotX = { -halfWidth, -halfWidth, halfWidth, halfWidth };
		float[] hotY = { halfHeight, -halfHeight, -halfHeight, halfHeight };
		for (int i = 0; i < 4; i++) {
			// Coordinates after rotation
			float transformedX = (float) (cos * hotX[i] - sin * hotY[i]);
			float transformedY = (float) (sin * hotX[i] + cos * hotY[i]);
			// Pan by draw coordinates
			transformedX += dst.left + halfWidth;
			transformedY += dst.bottom + halfHeight;
			// Add to vertices array
			vertices.add(transformedX);
			vertices.add(transformedY);
			vertices.add(0f);
		}

		// INDICES - increment from last value
		short lastValue;
		if (!indices.isEmpty()) {
			// If not empty, find last value
			lastValue = indices.get(indices.size() - 1);
		} else
			lastValue = -1;
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 2));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 4));

		// TEXTURE COORDS
		float[] srcX = { src.left, src.left, src.right, src.right };
		float[] srcY = { src.top, src.bottom, src.bottom, src.top };
		for (int i = 0; i < 4; i++) {
			textureCoords.add((float) (srcX[i] / textureWidth));
			textureCoords.add((float) (srcY[i] / textureHeight));
		}
	}

	// COMPLICATED
	protected void addSprite(Rect src, int drawX, int drawY, Rect hotRect,
			int angle, float sizeX, float sizeY) {
		// This class allows rotations but needs additional input
		// hotRect defines the corner coordinates from drawX and drawY
		// drawX and drawY is the draw point and centre of rotation

		// VERTICES
		// Trig
		double cos = Math.cos((double) angle / 180 * Math.PI);
		double sin = Math.sin((double) angle / 180 * Math.PI);

		// Coordinates before rotation
		float[] hotX = { hotRect.left, hotRect.left, hotRect.right,
				hotRect.right };
		float[] hotY = { hotRect.top, hotRect.bottom, hotRect.bottom,
				hotRect.top };
		for (int i = 0; i < 4; i++) {
			// Apply scale before rotation
			float x = hotX[i] * sizeX;
			float y = hotY[i] * sizeY;
			// Coordinates after rotation
			float transformedX = (float) (cos * x - sin * y);
			float transformedY = (float) (sin * x + cos * y);
			// Pan by draw coordinates
			transformedX += drawX;
			transformedY += drawY;
			// Add to vertices array
			vertices.add(transformedX);
			vertices.add(transformedY);
			vertices.add(0f);
		}

		// INDICES - increment from last value
		short lastValue;
		if (!indices.isEmpty()) {
			// If not empty, find last value
			lastValue = indices.get(indices.size() - 1);
		} else
			lastValue = -1;
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 2));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 4));

		// TEXTURE COORDS
		float[] srcX = { src.left + 0.5f, src.left + 0.5f, src.right - 0.5f,
				src.right - 0.5f };
		float[] srcY = { src.top + 0.5f, src.bottom - 0.5f, src.bottom - 0.5f,
				src.top + 0.5f };
		for (int i = 0; i < 4; i++) {
			textureCoords.add((float) (srcX[i] / textureWidth));
			textureCoords.add((float) (srcY[i] / textureHeight));
		}

	}

	protected void drawLine(Rect src, int x1, int y1, int x2, int y2, int width) {
		// Get angle between p1 and p2
		double angle = Math.atan2(y2 - y1, x2 - x1);
		int sinAngleOffset = (int) (Math.sin(angle) * width / 2);
		int cosAngleOffset = (int) (Math.cos(angle) * width / 2);

		// VERTICES
		vertices.add((float) (x1 + sinAngleOffset));
		vertices.add((float) (y1 - cosAngleOffset));
		vertices.add(0f);
		vertices.add((float) (x1 - sinAngleOffset));
		vertices.add((float) (y1 + cosAngleOffset));
		vertices.add(0f);
		vertices.add((float) (x2 - sinAngleOffset));
		vertices.add((float) (y2 + cosAngleOffset));
		vertices.add(0f);
		vertices.add((float) (x2 + sinAngleOffset));
		vertices.add((float) (y2 - cosAngleOffset));
		vertices.add(0f);

		// INDICES - increment from last value
		short lastValue;
		if (!indices.isEmpty()) {
			// If not empty, find last value
			lastValue = indices.get(indices.size() - 1);
		} else
			lastValue = -1;
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 2));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 4));

		// Get length of line
		int length = (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1)
				* (y2 - y1));
		// Get number of times texture should 'wrap'
		int textureLength = src.right - src.left;
		int nWrap = length / textureLength;

		// TEXTURE COORDS
		float[] u = { 0, 0, nWrap, nWrap };
		float[] srcY = { src.top, src.bottom, src.bottom, src.top };
		for (int i = 0; i < 4; i++) {
			textureCoords.add((float) (u[i]));
			textureCoords.add((float) (srcY[i] / textureHeight));
		}
	}

	public void drawTile(Rect dst, int offsetX, int offsetY, float scale) {
		// VERTICES
		vertices.add((float) dst.left);
		vertices.add((float) dst.top);
		vertices.add(0f);
		vertices.add((float) dst.left);
		vertices.add((float) dst.bottom);
		vertices.add(0f);
		vertices.add((float) dst.right);
		vertices.add((float) dst.bottom);
		vertices.add(0f);
		vertices.add((float) dst.right);
		vertices.add((float) dst.top);
		vertices.add(0f);

		// INDICES - increment from last value
		short lastValue;
		if (!indices.isEmpty()) {
			// If not empty, find last value
			lastValue = indices.get(indices.size() - 1);
		} else
			lastValue = -1;
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 2));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 1));
		indices.add((short) (lastValue + 3));
		indices.add((short) (lastValue + 4));

		// Get number of times texture should 'wrap'
		int drawWidth = dst.right - dst.left;
		int drawHeight = dst.bottom - dst.top;
		float xWrap = (float) drawWidth / textureWidth / scale;
		float yWrap = (float) drawHeight / textureHeight / scale;

		// Offsets
		float offsetU = (float) offsetX / textureWidth;
		float offsetV = (float) offsetY / textureHeight;

		Log.d(SpriteBatcher.TAG, "offsetU = " + offsetU);

		// TEXTURE COORDS
		float[] u = { offsetU, offsetU, xWrap + offsetU, xWrap + offsetU };
		float[] v = { offsetV, yWrap + offsetV, yWrap + offsetV, offsetV };
		for (int i = 0; i < 4; i++) {
			textureCoords.add((float) (u[i]));
			textureCoords.add((float) (v[i]));
		}

	}

	protected void clear() {
		vertices.clear();
		indices.clear();
		textureCoords.clear();
	}

	// GETTER/SETTER
	protected float[] getVertices() {
		// Convert to float[] before returning
		return convertToPrimitive(vertices.toArray(new Float[vertices.size()]));
	}

	protected short[] getIndices() {
		// Convert to short[] before returning
		return convertToPrimitive(indices.toArray(new Short[indices.size()]));
	}

	protected float[] getTextureCoords() {
		// Convert to float[] before returning
		return convertToPrimitive(textureCoords.toArray(new Float[textureCoords
				.size()]));
	}

	private float[] convertToPrimitive(Float[] objectArray) {
		if (objectArray == null) {
			return null;
		} else if (objectArray.length == 0) {
			return null;
		}
		final float[] primitiveArray = new float[objectArray.length];
		for (int i = 0; i < objectArray.length; i++) {
			primitiveArray[i] = objectArray[i].floatValue();
		}
		return primitiveArray;
	}

	private short[] convertToPrimitive(Short[] objectArray) {
		if (objectArray == null) {
			return null;
		} else if (objectArray.length == 0) {
			return null;
		}
		final short[] primitiveArray = new short[objectArray.length];
		for (int i = 0; i < objectArray.length; i++) {
			primitiveArray[i] = objectArray[i].shortValue();
		}
		return primitiveArray;
	}
}
