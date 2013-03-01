/* Sprite Batcher V1.1
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;

public class SpriteBatcher implements Renderer {

	// drawer.onDrawFrame(..) gets called by SpriteBatcher each frame
	private Drawer drawer;

	private int width;
	private int height;

	// Holds all the information for our batched GLDRAWELEMENT calls
	private int[] textureIds;
	ArrayList<SpriteData> spriteData;

	// Needed to load bitmaps into OpenGL
	int[] bitmapIds;
	Resources resources;

	public SpriteBatcher(Resources resources, int[] bitmapIds, Drawer drawer) {
		// Temporarily stores for loading textures later:
		this.bitmapIds = bitmapIds;
		this.resources = resources;
		// Create data structure to hold info for draw calls
		textureIds = new int[bitmapIds.length];
		spriteData = new ArrayList<SpriteData>(bitmapIds.length);
		this.drawer = drawer;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Rotate world by 180 around x axis so positive y is down (like canvas)
		gl.glRotatef(-180, 1, 0, 0);

		// START DRAWING
		drawer.onDrawFrame(gl, this);

		// Finally, send off all the draw commands in batches
		batchDraw(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Stores width and height
		this.width = width;
		this.height = height;
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Orthographic mode for 2d
		gl.glOrthof(0, width, -height, 0, -1, 8);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// SETTINGS
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		// DRAWING SETUP
		// NOTES: As we are always drawing with textures and viewing our
		// elements from the same side all the time we can leave all these
		// settings on the whole time
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Telling OpenGL to enable textures.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// Tell OpenGL to enable the use of UV coordinates.
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		// Blending on
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Load textures
		gl.glGenTextures(textureIds.length, textureIds, 0);
		for (int i = 0; i < bitmapIds.length; i++) {
			addTexture(gl,
					BitmapFactory.decodeResource(resources, bitmapIds[i]),
					textureIds[i]);
		}
	}

	public void batchDraw(GL10 gl) {
		// All the draw commands are already batched together for each seperate
		// texture tile. Now we loop through each tile and make the draw calls
		// to OpenGL.
		// NOTE: You can call this method early to send a batch. This gives you
		// more control over layer order of the sprites.
		for (int i = 0; i < textureIds.length; i++) {
			// GRAB SPRITEDATA
			SpriteData thisSpriteData = spriteData.get(i);

			// CONVERT INTO ARRAY
			float[] vertices = thisSpriteData.getVertices();
			short[] indices = thisSpriteData.getIndices();
			float[] textureCoords = thisSpriteData.getTextureCoords();

			// ONLY DRAW IF ALL NOT NULL
			if (vertices != null && indices != null && textureCoords != null) {

				// CREATE BUFFERS - these are just containers for sending the
				// draw information we have already collected to OpenGL

				// Vertex buffer (position information of every draw command)
				ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
				vbb.order(ByteOrder.nativeOrder());
				FloatBuffer vertexBuffer = vbb.asFloatBuffer();
				vertexBuffer.put(vertices);
				vertexBuffer.position(0);

				// Index buffer (which vertices go together to make the
				// elements)
				ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
				ibb.order(ByteOrder.nativeOrder());
				ShortBuffer indexBuffer = ibb.asShortBuffer();
				indexBuffer.put(indices);
				indexBuffer.position(0);

				// How to paste the texture over each element so that the right
				// image is shown
				ByteBuffer tbb = ByteBuffer
						.allocateDirect(textureCoords.length * 4);
				tbb.order(ByteOrder.nativeOrder());
				FloatBuffer textureBuffer = tbb.asFloatBuffer();
				textureBuffer.put(textureCoords);
				textureBuffer.position(0);

				// DRAW COMMAND
				// Tell OpenGL where our texture is located.
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[i]);
				// Telling OpenGL where our textureCoords are.
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
				// Specifies the location and data format of the array of vertex
				// coordinates to use when rendering.
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
				// Draw elements command using indices so it knows which
				// vertices go together to form each element
				gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
						GL10.GL_UNSIGNED_SHORT, indexBuffer);
			}
		}
		// Clear spriteData
		for (int i = 0; i < spriteData.size(); i++) {
			spriteData.get(i).clear();
		}
	}

	private void addTexture(GL10 gl, Bitmap texture, int textureId) {
		// Working with textureId
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// SETTINGS
		// Scale up if the texture is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		// Scale down if the mesh is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		// Clamp to edge behaviour at edge of texture (repeats last pixel)
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// Attach bitmap to current texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);

		// Add dimensional info to spritedata
		spriteData.add(new SpriteData(texture.getWidth(), texture.getHeight()));
	}

	public int getViewWidth() {
		return width;
	}

	public int getViewHeight() {
		return height;
	}

	// ----------- ADD SPRITE METHODS --------------------

	// SIMPLE
	public void draw(GL10 gl, int bitmapId, Rect src, Rect dst) {
		// This is a simple class for doing straight src->dst draws
		// Look up bitmapId
		for (int i = 0; i < bitmapIds.length; i++) {
			if (bitmapId == bitmapIds[i]) {
				spriteData.get(i).addSprite(src, dst);
				return;
			}
		}
		Log.w("SpriteBatcher", "Warning: bitmapId not found");
	}

	public void draw(GL10 gl, int bitmapId, Rect src, Rect dst, int angle) {
		// This is a simple class for doing straight src->dst draws
		// Look up bitmapId
		for (int i = 0; i < bitmapIds.length; i++) {
			if (bitmapId == bitmapIds[i]) {
				spriteData.get(i).addSprite(src, dst, angle);
				return;
			}
		}
		Log.w("SpriteBatcher", "Warning: bitmapId not found");
	}

	// COMPLICATED
	public void draw(GL10 gl, int bitmapId, Rect src, int drawX, int drawY,
			Rect hotRect, int angle, float scale, float alpha) {
		// Just redirects to below with equal scale in x and y
		draw(gl, bitmapId, src, drawX, drawY, hotRect, angle, scale, scale,
				alpha);
	}

	public void draw(GL10 gl, int bitmapId, Rect src, int drawX, int drawY,
			Rect hotRect, int angle, float sizeX, float sizeY, float alpha) {
		// This class allows rotations but needs additional input
		// Look up bitmapId
		for (int i = 0; i < bitmapIds.length; i++) {
			if (bitmapId == bitmapIds[i]) {
				spriteData.get(i).addSprite(src, drawX, drawY, hotRect, angle,
						sizeX, sizeY, alpha);
				return;
			}
		}
		Log.w("SpriteBatcher", "Warning: bitmapId not found");
	}

	// DIRECT
	public void addVertices(int bitmapId, float[] f) {
		// Look up bitmapId
		for (int i = 0; i < bitmapIds.length; i++) {
			if (bitmapId == bitmapIds[i]) {
				spriteData.get(i).addVertices(f);
				return;
			}
		}
	}

	public void addIndices(int bitmapId, short[] s) {
		// Look up bitmapId
		for (int i = 0; i < bitmapIds.length; i++) {
			if (bitmapId == bitmapIds[i]) {
				spriteData.get(i).addIndices(s);
				return;
			}
		}
	}

	public void addTextureCoords(int bitmapId, float[] f) {
		// Look up bitmapId
		for (int i = 0; i < bitmapIds.length; i++) {
			if (bitmapId == bitmapIds[i]) {
				spriteData.get(i).addTextureCoords(f);
				return;
			}
		}
	}

	private class SpriteData {
		// This is a simple a container class to avoid unnecessary code in
		// SpriteBatcher. It holds a whole set of information for a single
		// GLDRAWELEMENTS call:
		private ArrayList<Float> vertices; // Positions of vertices
		private ArrayList<Short> indices; // Which verts go together to form
		// Ele's
		private ArrayList<Float> textureCoords; // Texture map coordinates

		private int textureWidth;
		private int textureHeight;

		public SpriteData(int width, int height) {
			vertices = new ArrayList<Float>();
			indices = new ArrayList<Short>();
			textureCoords = new ArrayList<Float>();
			textureWidth = width;
			textureHeight = height;
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
				int angle, float sizeX, float sizeY, float alpha) {
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
			float[] srcX = { src.left + 0.5f, src.left + 0.5f,
					src.right - 0.5f, src.right - 0.5f };
			float[] srcY = { src.top + 0.5f, src.bottom - 0.5f,
					src.bottom - 0.5f, src.top + 0.5f };
			for (int i = 0; i < 4; i++) {
				textureCoords.add((float) (srcX[i] / textureWidth));
				textureCoords.add((float) (srcY[i] / textureHeight));
			}
			// Log.d("SpriteBatcher", "Left = " + src.left);
			// Log.d("SpriteBatcher", "Top = " + src.top);
			// Log.d("SpriteBatcher", "Right = " + src.right);
			// Log.d("SpriteBatcher", "Bottom = " + src.bottom);
			// Log.d("SpriteBatcher", "LEFT U = "
			// + (float) ((float) src.left + 0.5 / textureWidth));
			// Log.d("SpriteBatcher", "TOP V = "
			// + (float) ((float) src.top + 0.5 / textureHeight));
			// textureCoords
			// .add((float) (((float) src.left + 0.5) / textureWidth));
			// textureCoords
			// .add((float) (((float) src.top + 0.5) / textureHeight));
			// textureCoords
			// .add((float) (((float) src.left + 0.5) / textureWidth));
			// textureCoords
			// .add((float) (((float) src.bottom - 0.5) / textureHeight));
			// textureCoords
			// .add((float) (((float) src.right - 0.5) / textureWidth));
			// textureCoords
			// .add((float) (((float) src.bottom - 0.5) / textureHeight));
			// textureCoords
			// .add((float) (((float) src.right - 0.5) / textureWidth));
			// textureCoords
			// .add((float) (((float) src.top + 0.5) / textureHeight));
		}

		public void clear() {
			vertices.clear();
			indices.clear();
			textureCoords.clear();
		}

		// GETTER/SETTER
		public float[] getVertices() {
			// Convert to float[] before returning
			return convertToPrimitive(vertices.toArray(new Float[vertices
					.size()]));
		}

		public short[] getIndices() {
			// Convert to short[] before returning
			return convertToPrimitive(indices
					.toArray(new Short[indices.size()]));
		}

		public float[] getTextureCoords() {
			// Convert to float[] before returning
			return convertToPrimitive(textureCoords
					.toArray(new Float[textureCoords.size()]));
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
}
