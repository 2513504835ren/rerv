package com.linj.camera.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.example.camera.CameraAty;
import com.linj.camera.view.CameraContainer.TakePictureListener;


/** 
 * @ClassName: CameraView 
 * @Description: ������󶨵�SurfaceView ��װ�����շ���
 * @author LinJ
 * @date 2014-12-31 ����9:44:56 
 *  
 */
public class CameraView extends SurfaceView implements CameraOperation{

	public final static String TAG="CameraView";
	/** �͸�View�󶨵�Camera���� */
	public Camera mCamera;

	/** ��ǰ��������ͣ�Ĭ��Ϊ�ر� */ 
	private FlashMode mFlashMode=FlashMode.ON;

	/** ��ǰ���ż���  Ĭ��Ϊ0*/ 
	private int mZoom=0;

	/** �Ƿ��ǰ�����,trueΪǰ��,falseΪ����  */ 
	private boolean mIsFrontCamera;
	
	/**  ������ã���¼��ǰ��¼������¼�������ָ�ԭ���� */ 
	private Camera.Parameters mParameters;
	
	/** ��ǰ��Ļ��ת�Ƕ�*/ 
	private int mOrientation=0;
	
	private CameraAty p;
	
	private PictureCallback pCallback;
	public CameraView(Context context){
		super(context);
		//��ʼ������
		getHolder().addCallback(callback);
		openCamera();
		mIsFrontCamera=false;
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//��ʼ������
		getHolder().addCallback(callback);
		openCamera();
		mIsFrontCamera=false;
	}

	private SurfaceHolder.Callback callback=new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if(mCamera==null){
					openCamera();
				}
				setCameraParameters();
				mCamera.setPreviewDisplay(getHolder());
			} catch (Exception e) {
				Toast.makeText(getContext(), "�����ʧ��", Toast.LENGTH_SHORT).show();
				Log.e(TAG,e.getMessage());
			}
			mCamera.startPreview();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			updateCameraOrientation();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				
			}

		}
	};

	/**  
	 *   ת��ǰ�úͺ��������
	 */
	@Override
	public void switchCamera(){
		mIsFrontCamera=!mIsFrontCamera;
		openCamera();
		if(mCamera!=null){
			setCameraParameters();
			updateCameraOrientation();
			try {
				mCamera.setPreviewDisplay(getHolder());
				mCamera.startPreview();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**  
	 *   ���ݵ�ǰ�����״̬(ǰ�û����)���򿪶�Ӧ���
	 */
	private boolean openCamera()  {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		if(mIsFrontCamera){
			Camera.CameraInfo cameraInfo=new CameraInfo();
			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if(cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
					try {
						mCamera=Camera.open(i);
					} catch (Exception e) {
						mCamera =null;
						return false;
					}

				}
			}
		}else {
			try {
				mCamera=Camera.open();
			} catch (Exception e) {
				mCamera =null;
				return false;
			}

		}
		return true;
	}

	/**  
	 *  ��ȡ��ǰ���������
	 *  @return   
	 */
	@Override
	public FlashMode getFlashMode() {
		return mFlashMode;
	}

	/**  
	 *  �������������
	 *  @param flashMode   
	 */
	@Override
	public void setFlashMode(FlashMode flashMode) {
		if(mCamera==null) return;
		mFlashMode = flashMode;
		
		Camera.Parameters parameters = null;
		try {
			parameters = mCamera.getParameters();
			switch (flashMode) {
			case ON:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
				break;
			case AUTO:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				break;
			case TORCH:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				break;
			default:
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				break;
			}
			mCamera.setParameters(parameters);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	@Override
	public void takePicture(PictureCallback pCallback,TakePictureListener listener){
		this.pCallback = pCallback;
		if(!mIsFrontCamera){
			mCamera.autoFocus(autoFocusCallback);
		}else{
			mCamera.takePicture(null, null, this.pCallback);
		}
		
	}
	
	public final AutoFocusCallback autoFocusCallback=new AutoFocusCallback() {

		@SuppressWarnings("static-access")
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			//�۽�֮����ݽ���޸�ͼƬ
			if (success) {
				mCamera.takePicture(null, null, pCallback);
			}else {
				//�۽�ʧ��
				Toast.makeText(getContext(), "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
				
				openCamera();
				if(mCamera!=null){
					try {
						setCameraParameters();
						mCamera.setPreviewDisplay(getHolder());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getContext(), "�����ʧ��", Toast.LENGTH_SHORT).show();
						Log.e(TAG,e.getMessage());
					}
					mCamera.startPreview();
					updateCameraOrientation();
					if(p.mCameraShutterButton.VISIBLE!=View.GONE){
						p.mCameraShutterButton.setClickable(true);
					}
				}
			}
		} 
	};
	
	public void click(Activity p) {
		// TODO Auto-generated method stub
		this.p = (CameraAty) p;
	}
	
	/**  
	 * �ֶ��۽� 
	 *  @param point ��������
	 */
	protected void onFocus(Point point,AutoFocusCallback callback){
		Camera.Parameters parameters=mCamera.getParameters();
		//��֧�������Զ���۽�����ʹ���Զ��۽�������
		if (parameters.getMaxNumFocusAreas()<=0) {
			mCamera.autoFocus(callback);
			return;
		}
		List<Area> areas=new ArrayList<Camera.Area>();
		int left=point.x-300;
		int top=point.y-300;
		int right=point.x+300;
		int bottom=point.y+300;
		left=left<-1000?-1000:left;
		top=top<-1000?-1000:top;
		right=right>1000?1000:right;
		bottom=bottom>1000?1000:bottom;
		areas.add(new Area(new Rect(left,top,right,bottom), 100));
		parameters.setFocusAreas(areas);
		try {
			//����ʹ�õ�С���ֻ������þ۽������ʱ�򾭳�����쳣������־�����ǿ�ܲ���ַ���תint��ʱ������ˣ�
			//Ŀ����С���޸��˿�ܲ���뵼�£��ڴ�try������ʵ�ʾ۽�Ч��ûӰ��
			mCamera.setParameters(parameters);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		mCamera.autoFocus(callback);
	}

	/**  
	 *  ��ȡ������ż������Ϊ40
	 *  @return   
	 */
	@Override
	public int getMaxZoom(){
		if(mCamera==null) return -1;		
		Camera.Parameters parameters=mCamera.getParameters();
		if(!parameters.isZoomSupported()) return -1;
		return parameters.getMaxZoom()>40?40:parameters.getMaxZoom();
	}
	/**  
	 *  ����������ż���
	 *  @param zoom   
	 */
	@Override
	public void setZoom(int zoom){
		if(mCamera==null) return;
		Camera.Parameters parameters;
		//ע��˴�Ϊ¼��ģʽ�µ�setZoom��ʽ����Camera.unlock֮�󣬵���getParameters����������android��ܵײ���쳣
		//stackoverflow�Ͽ����Ľ��������ڶ��߳�ͬʱ����Camera���µĳ�ͻ�������ڴ�ʹ��¼��ǰ�����mParameters��
		if(mParameters!=null)
			parameters=mParameters;
		else {
			parameters=mCamera.getParameters();
		}

		if(!parameters.isZoomSupported()) return;
		parameters.setZoom(zoom);
		mCamera.setParameters(parameters);
		mZoom=zoom;
	}
	@Override
	public int getZoom(){
		return mZoom;
	}

	/**
	 * �������������
	 */
	private void setCameraParameters(){
		Camera.Parameters parameters = mCamera.getParameters();
		// ѡ����ʵ�Ԥ���ߴ�   
		List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
		if (sizeList.size()>0) {
			Size cameraSize=sizeList.get(0);
			//Ԥ��ͼƬ��С
			parameters.setPreviewSize(cameraSize.width, cameraSize.height);
		}

		//�������ɵ�ͼƬ��С
		sizeList = parameters.getSupportedPictureSizes();
		if (sizeList.size()>0) {
			Size cameraSize=sizeList.get(0);
			for (Size size : sizeList) {
				//С��100W����
				if (size.width*size.height<100*10000) {
					cameraSize=size;
					break;
				}
			}
			parameters.setPictureSize(cameraSize.width, cameraSize.height);
		}
		//����ͼƬ��ʽ
		parameters.setPictureFormat(ImageFormat.JPEG);       
		parameters.setJpegQuality(100);
		parameters.setJpegThumbnailQuality(100);
		//�Զ��۽�ģʽ
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		mCamera.setParameters(parameters);
		//���������ģʽ���˴���Ҫ������������ݻٺ����ؽ�������֮ǰ��״̬
		//setFlashMode(mFlashMode);
		//�������ż���
		setZoom(mZoom);
		//������Ļ�������
	}


	/**  
	 *   ���ݵ�ǰ�����޸ı���ͼƬ����ת�Ƕ�
	 */
	private void updateCameraOrientation(){
		if(mCamera!=null){
			Camera.Parameters parameters = mCamera.getParameters();
			//rotation����Ϊ 0��90��180��270��ˮƽ����Ϊ0��
			int rotation=90+mOrientation==360?0:90+mOrientation;
			//ǰ������ͷ��Ҫ�Դ�ֱ�������任��������Ƭ�ǵߵ���
			if(mIsFrontCamera){
				if(rotation==90) rotation=270;
				else if (rotation==270) rotation=90;
			}
			parameters.setRotation(rotation);//���ɵ�ͼƬת90��
			//Ԥ��ͼƬ��ת90��
			mCamera.setDisplayOrientation(90);//Ԥ��ת90��
			mCamera.setParameters(parameters);
		}
	}
	
	
	/** 
	 * @Description: ���������ö�� Ĭ��Ϊ�ر�
	 */
	public enum FlashMode{
		/** ON:����ʱ�������   */ 
		ON,
		/** OFF�����������  */ 
		OFF,
		/** AUTO��ϵͳ�����Ƿ�������  */ 
		AUTO,
		/** TORCH��һֱ�������  */ 
		TORCH
	}

}