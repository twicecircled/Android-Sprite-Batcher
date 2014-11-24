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

import javax.microedition.khronos.opengles.GL10;

public interface Drawer {

	// An object implementing this interface must be handed to SpriteBatcher
	// when it is created. The onDrawFrame method of SpriteBatcher automatically
	// calls the method onDrawFrame() of the object implementing Drawer.

	// All draw calls onto SpriteBatcher should be made from within this method.

	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher);

}
