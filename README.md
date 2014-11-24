Android-Sprite-Batcher
======================
By Tim Wicksteed

Sprite Batcher is a tool to help draw 2D sprites in Android using OpenGL. It handles all the interaction with the OpenGL client and offers you a set of simple draw() methods to handle all your drawing needs.

**Version Log**

v1.31 - Added license to every source file.

v1.3 - Added drawLine(), drawTile() and ARGB parameters. These features are beta and may not be bug free. Use at your own risk.

v1.2 - Added text support using a font atlas.

**Features**

1. Draw sprites with OpenGL with less than 10 lines of code
2. Batches sprites together to optimise performance
3. Range of draw methods including rotation and scaling options
4. ARGB colour transformations (Warning: Reduces efficiency)
5. Generate sprite atlas from font for drawing text
6. Draw point to point lines with repeated patterns (1D)
7. Draw tiled texture with repeated pattern (2D) \*\***EXPERIMENTAL**\*\*

**Getting SpriteBatcher**

Get a copy of the repo by either:

- Forking/cloning (all you GitHub afficianados will already know how to do this!)
- Downloading directly as a .zip file using the button near the top of the screen

**Adding SpriteBatcher to your project**

Using a library project:

* Import the entire Android-Sprite-Batcher project folder into Eclipse using File -> Import -> Existing Android Code Into Workspace. 
* Right click on the newly imported project and click Properties -> Android -> check 'Is Library'
* Right click on your application that you want to use Sprite Batcher in and click Properties -> Android -> Under libraries click 'add' and select the Android-Sprite-Batcher.

Using a JAR:

* Simply copy the Android-Sprite-Batcher-vX.X.jar from the releases folder into the lib folder of your Android project.
* *Note: you will not have access to the javadoc markup by going this route, whereas if you use the library project you will.*

**Using SpriteBatcher**

Watch the videos below to find out how to use SpriteBatcher.

[Tutorial pt 1 - The Basics](http://www.youtube.com/watch?v=Pv9GwRVbODE "How to use SpriteBatcher 1")

[Tutorial pt 2 - Ordering Sprites](http://www.youtube.com/watch?v=43_j-rapMug "How to use SpriteBatcher 2")

[Tutorial pt 3 - Drawing Text](http://www.youtube.com/watch?v=smzddiexk94 "How to use SpriteBatcher 3")

... more coming soon, I'm trying to release 1 per week.

**Hints and Tips**

Like all implementations of openGL some devices don't like it if you provide non-power 2 textures ie 256x256 512x512 1024x1024 etc. If your sprites are being drawn as white squares then:

* Make sure you are using power 2 textures (they don't have to be square ie 256x512 is fine)
* Make sure your textures are in the drawable-nodpi folder, so Android does not scale them

If you run into "java.lang.IllegalArgumentExcep­tion: No config chosen" then try the following code before called setRenderer():
```java
glSurfaceView.setEGLConfigChoo­ser(8, 8, 8, 8, 16, 0); 
```