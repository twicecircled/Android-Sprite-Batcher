package com.twicecircled.spritebatcher;

import java.util.ArrayList;
import android.graphics.Rect;

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

	public SpriteData(int rgba) {
		vertices = new ArrayList<Float>();
		indices = new ArrayList<Short>();
		textureCoords = new ArrayList<Float>();
		this.argb = rgba;
	}

	public void setDimensions(int width, int height) {
		textureWidth = width;
		textureHeight = height;
	}

	public int getARGB() {
		return argb;
	}

	// Add sprite methods
	// DIRECT
	public void addVertices(float[] f) {
		for (int i = 0; i < f.length; i++) {
			vertices.add(f[i]);
		}
	}

	public void addIndices(short[] s) {
		for (int i = 0; i < s.length; i++) {
			indices.add(s[i]);
		}
	}

	public void addTextureCoords(float[] f) {
		for (int i = 0; i < f.length; i++) {
			textureCoords.add(f[i]);
		}
	}

	// SIMPLE
	public void addSprite(Rect src, Rect dst) {
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

	public void addSprite(Rect src, Rect dst, int angle) {
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
	public void addSprite(Rect src, int drawX, int drawY, Rect hotRect,
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

	public void clear() {
		vertices.clear();
		indices.clear();
		textureCoords.clear();
	}

	// GETTER/SETTER
	public float[] getVertices() {
		// Convert to float[] before returning
		return convertToPrimitive(vertices.toArray(new Float[vertices.size()]));
	}

	public short[] getIndices() {
		// Convert to short[] before returning
		return convertToPrimitive(indices.toArray(new Short[indices.size()]));
	}

	public float[] getTextureCoords() {
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
