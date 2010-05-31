package org.cocos2d.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class FloatBufferProvider {
	// 64k is big enough for most objects
	private static final int ALLOCATION_SIZE = 64 * 1024;
	private ByteBuffer currentBuffer = null;
	private static FloatBufferProvider global_synced = new FloatBufferProvider();
	
	public ByteBuffer allocate(int size)
	{
		if(size >= ALLOCATION_SIZE)
			return ByteBuffer.allocateDirect(size);

		if(currentBuffer == null || size > currentBuffer.remaining())
			currentBuffer = ByteBuffer.allocateDirect(ALLOCATION_SIZE);

		currentBuffer.limit(currentBuffer.position() + size);
		ByteBuffer result = currentBuffer.slice();

		currentBuffer.position(currentBuffer.limit());
		currentBuffer.limit(currentBuffer.capacity());
		return result;
	}

	public static ByteBuffer allocateDirect(int size)
	{
		synchronized(global_synced)
		{
			return global_synced.allocate(size);
		}
	}
	
    public static void drawQuads(GL10 gl, FloatBuffer fbVert, FloatBuffer fbCoord) {
        fbVert.position(0);
        fbCoord.position(0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fbVert);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, fbCoord);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    }

    public static void fillFloatBuffer(FloatBuffer fb, float[] arr) {
        fb.position(0);
        fb.put(arr);
    }

    public static FloatBuffer makeFloatBuffer(float[] arr) {
        ByteBuffer bb = FloatBufferProvider.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        return fb;
    }

    public static FloatBuffer createFloatBuffer(int arrayElementCount) {
        ByteBuffer temp = FloatBufferProvider.allocateDirect(4 * arrayElementCount);
        temp.order(ByteOrder.nativeOrder());
        
        return temp.asFloatBuffer();
    }
}
