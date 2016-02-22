package com.lbriceno.cardboardtest;

import android.opengl.GLES20;
import android.util.FloatMath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by luisbriceno on 11/5/15.
 */
public class Triangle {
    private final int mProgram;
    public FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;   // Buffer for color-array (NEW)
    private ShortBuffer drawListBuffer;
    private float animation =  0.0f;

    private float[] colors = { // Colors for the vertices (NEW)
            1.0f, 0.0f, 0.0f, 1.0f, // Red (NEW)
            0.0f, 1.0f, 0.0f, 1.0f, // Green (NEW)
            0.0f, 0.0f, 1.0f, 1.0f  // Blue (NEW)
    };

    private int mPositionHandle;
    private int mColorHandle;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 4;


    static float triangleCoords[] = {   // in counterclockwise order:
            -0.1f,  -0.1f, 0.0f, 1.0f, // top
             0.1f,  -0.1f, 0.0f, 1.0f, // bottom left
             0.0f,   0.1f, 0.0f, 1.0f, // bottom right
    };

    final float[] color = {
            // X, Y, Z,
            // R, G, B, A
            1.0f, 0.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 1.0f, 1.0f,

            0.0f, 1.0f, 0.0f, 1.0f
    };

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =""+
            "uniform vec2 translate;"+
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition + vec4(translate.x, translate.y , 0.0, 0.0);" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
  public Triangle(){

    // initialize vertex byte buffer for shape coordinates
      ByteBuffer bb = ByteBuffer.allocateDirect(
              // (number of coordinate values * 4 bytes per float)
              triangleCoords.length * 4);
      // use the device hardware's native byte order
      bb.order(ByteOrder.nativeOrder());

      // create a floating point buffer from the ByteBuffer
      vertexBuffer = bb.asFloatBuffer();
      // add the coordinates to the FloatBuffer
      vertexBuffer.put(triangleCoords);
      // set the buffer to read the first coordinate
      vertexBuffer.position(0);


      ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
      cbb.order(ByteOrder.nativeOrder()); // Use native byte order (NEW)
      colorBuffer = cbb.asFloatBuffer();  // Convert byte buffer to float (NEW)
      colorBuffer.put(colors);            // Copy data into buffer (NEW)
      colorBuffer.position(0);


      //drawing the triangle
      int vertexShader = ShaderRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
              vertexShaderCode);
      int fragmentShader = ShaderRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
              fragmentShaderCode);

      // create empty OpenGL ES Program
      mProgram = GLES20.glCreateProgram();

      // add the vertex shader to program
      GLES20.glAttachShader(mProgram, vertexShader);

      // add the fragment shader to program
      GLES20.glAttachShader(mProgram, fragmentShader);

      // creates OpenGL ES program executables
      GLES20.glLinkProgram(mProgram);
  }

    public void draw() {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        float translateX = (float) Math.sin(animation);
        float translatey = (float) Math.cos(animation);
        animation += 0.01f;

        GLES20.glUniform2f(GLES20.glGetUniformLocation(mProgram, "translate"),translateX,translatey);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, colors, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);


    }
}
