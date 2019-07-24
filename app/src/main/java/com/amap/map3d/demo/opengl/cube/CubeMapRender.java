package com.amap.map3d.demo.opengl.cube;

import android.graphics.PointF;
import android.opengl.Matrix;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CustomRenderer;
import com.amap.api.maps.model.LatLng;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zxy94400 on 2016/3/19.
 */
public class CubeMapRender implements CustomRenderer {

    private float[] translate_vector = new float[4];
    public static float SCALE = 0.005F;// 缩放暂时使用这个

    private LatLng center = new LatLng(39.90403, 116.407525);// 北京市经纬度

    private Cube cube ;

    private AMap aMap;

    float width, height;

    public CubeMapRender(AMap aMap) {
        this.aMap = aMap;

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,15));
    }

    float[] mvp = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {

        if(cube != null) {
            Matrix.setIdentityM(mvp, 0);

            //偏移
            PointF pointF = aMap.getProjection().toOpenGLLocation(center);

            Matrix.multiplyMM(mvp,0, aMap.getProjectionMatrix(),0,aMap.getViewMatrix(),0);

            Matrix.translateM(mvp, 0 , pointF.x , pointF.y  , 0);
            int scale = 1;
            Matrix.scaleM(mvp, 0 , scale, scale, scale);

            cube.drawES20(mvp);
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //创建cube
        cube = new Cube(0.2f,0.2f,0.2f);
        cube.initShader();
    }

    @Override
    public void OnMapReferencechanged() {

    }

}
