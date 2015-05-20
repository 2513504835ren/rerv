package com.linj.camera.view;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.example.camera.R;
import com.linj.camera.view.CameraView.FlashMode;


/** 
 * @ClassName: CameraContainer 
 * @Description:  ������������ ��������󶨵�surfaceview�����պ����ʱͼƬView�;۽�View 
 * @author LinJ
 * @date 2014-12-31 ����9:38:52 
 *  
 */
public class CameraContainer extends RelativeLayout implements CameraOperation{

	public final static String TAG="CameraContainer";
	
	private boolean IsOnPause = false;

	/** ����󶨵�SurfaceView  */ 
	private CameraView mCameraView;

	/** ������Ļʱ��ʾ�ľ۽�ͼ��  */ 
	private FocusImageView mFocusImageView;

	/** ��Ƭ�ֽ���������  */ 
	private DataHandler mDataHandler;

	/** ���ռ����ӿڣ����������տ�ʼ�ͽ�����ִ����Ӧ����  */ 
	private TakePictureListener mListener;

	/** ���ż����϶��� */ 
	private SeekBar mZoomSeekBar;
	
	private float Height;
	
	/** ����ִ�ж�ʱ�����Handler����*/
	private Handler mHandler;
	
	public CameraContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		mHandler=new Handler();
		setOnTouchListener(new TouchListener());
		Height = context.getResources().getDisplayMetrics().heightPixels;
	}

	/**  
	 *  ��ʼ���ӿؼ�
	 *  @param context   
	 */
	private void initView(Context context) {
		inflate(context, R.layout.cameracontainer, this);
		mCameraView=(CameraView) findViewById(R.id.cameraView);

		mFocusImageView=(FocusImageView) findViewById(R.id.focusImageView);

		mZoomSeekBar=(SeekBar) findViewById(R.id.zoomSeekBar);
		//��ȡ��ǰ�����֧�ֵ�������ż���ֵС��0��ʾ��֧�����š���֧������ʱ�������϶�����
		int maxZoom=mCameraView.getMaxZoom();
		
		if(maxZoom>0){
			mZoomSeekBar.setMax(maxZoom);
			mZoomSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
		}
	}

	public boolean Act(boolean a){
		IsOnPause = a;
		return IsOnPause;
	}
	
	/**
	 * �Զ��۽�����
	 */
	public void autoF(final TakePictureListener t){
		this.mListener = t;
		final Handler handler = new Handler();  
	    Runnable runnable = new Runnable(){  
	         @Override  
	         public void run() {  
	             // TODO Auto-generated method stub  
	             // �ڴ˴����ִ�еĴ���  
	     		try {
	     			java.lang.System.out.println("�۽�");
	     			if(IsOnPause == false){
	     				mCameraView.takePicture(pictureCallback,t);
	     			}
	     			//����ʹ�õ�С���ֻ������þ۽������ʱ�򾭳�����쳣������־�����ǿ�ܲ���ַ���תint��ʱ������ˣ�
	     			//Ŀ����С���޸��˿�ܲ���뵼�£��ڴ�try������ʵ�ʾ۽�Ч��ûӰ��
	     		} catch (Exception e) {
	     			// TODO: handle exception
	     			e.printStackTrace();
	     		}
	     		if(IsOnPause == false){
	   	    	 handler.postDelayed(this, 5500);// �򿪶�ʱ����ִ�в���  
	     		}
	         }   
	     };  
	     if(IsOnPause == false){
	    	 handler.postDelayed(runnable, 5500);// �򿪶�ʱ����ִ�в���  
	     }

	}

	/**  
	 *   ǰ�á���������ͷת��
	 */
	@Override
	public void switchCamera(){
		mCameraView.switchCamera();
	}
	
	/**  
	 *  ��ȡ��ǰ���������
	 *  @return  mCameraView.getFlashMode()
	 */
	@Override
	public FlashMode getFlashMode() {
		return mCameraView.getFlashMode();
	}

	/**  
	 *  �������������
	 *  @param flashMode   
	 */
	@Override
	public void setFlashMode(FlashMode flashMode) {
		mCameraView.setFlashMode(flashMode);
	}

	/**
	 * ���շ���
	 * @param callback
	 */
	public void takePicture(){
		takePicture(pictureCallback,mListener);
	}

	/**  
	 * @Description: ���շ���
	 * @param @param listener ���ռ����ӿ�
	 * @return void    
	 * @throws 
	 */
	public void takePicture(TakePictureListener listener){
		this.mListener=listener;
		takePicture(pictureCallback, mListener);
		
	}


	@Override
	public void takePicture(PictureCallback callback,
			TakePictureListener listener) {
		mCameraView.takePicture(callback,listener);
	}
	
	
	public void clclik(Activity p){
		mCameraView.click(p);
		
	}

	@Override
	public int getMaxZoom() {
		// TODO Auto-generated method stub
		return mCameraView.getMaxZoom();
	}

	@Override
	public void setZoom(int zoom) {
		// TODO Auto-generated method stub
		mCameraView.setZoom(zoom);
	}

	@Override
	public int getZoom() {
		// TODO Auto-generated method stub
		return mCameraView.getZoom();
	} 

	private final OnSeekBarChangeListener onSeekBarChangeListener=new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			mCameraView.setZoom(progress);
			mHandler.removeCallbacksAndMessages(mZoomSeekBar);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};


	private final class TouchListener implements OnTouchListener {

		/** ��¼��������Ƭģʽ���ǷŴ���С��Ƭģʽ */

		private static final int MODE_INIT = 0;
		/** �Ŵ���С��Ƭģʽ */
		private static final int MODE_ZOOM = 1;
		private int mode = MODE_INIT;// ��ʼ״̬ 

		/** ���ڼ�¼����ͼƬ�ƶ�������λ�� */

		private float startDis;


		@Override
		public boolean onTouch(View v, MotionEvent event) {
			/** ͨ�������㱣������λ MotionEvent.ACTION_MASK = 255 */
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// ��ָѹ����Ļ
			case MotionEvent.ACTION_DOWN:
				mode = MODE_INIT;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				//���mZoomSeekBarΪnull ��ʾ���豸��֧������ ֱ����������mode Moveָ��Ҳ�޷�ִ��
				if(mZoomSeekBar==null) return true;
				//�Ƴ�token����ΪmZoomSeekBar����ʱ����
				mHandler.removeCallbacksAndMessages(mZoomSeekBar);
				mZoomSeekBar.setVisibility(View.VISIBLE);

				mode = MODE_ZOOM;
				/** ����������ָ��ľ��� */
				startDis = distance(event);
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == MODE_ZOOM) {
					//ֻ��ͬʱ�����������ʱ���ִ��
					if(event.getPointerCount()<2) return true;
					float endDis = distance(event);// ��������
					//ÿ�仯10f zoom��1
					int scale=(int) ((endDis-startDis)/10f);
					if(scale>=1||scale<=-1){
						int zoom=mCameraView.getZoom()+scale;
						//zoom���ܳ�����Χ
						if(zoom>mCameraView.getMaxZoom()) zoom=mCameraView.getMaxZoom();
						if(zoom<0) zoom=0;
						mCameraView.setZoom(zoom);
						mZoomSeekBar.setProgress(zoom);
						//�����һ�εľ�����Ϊ��ǰ����
						startDis=endDis;
					}
				}
				break;
				// ��ָ�뿪��Ļ
			case MotionEvent.ACTION_UP:
				if(mode!=MODE_ZOOM){
					//���þ۽�
					if(event.getY()<=1*Height/3){
						Point point=new Point((int)event.getX(), (int)event.getY());
						mCameraView.onFocus(point,autoFocusCallback);
						mFocusImageView.startFocus(point);
					}
				}else {
					
				}
				break;
			}
			return true;
		}
		/** ����������ָ��ľ��� */
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** ʹ�ù��ɶ���������֮��ľ��� */
			return (float) Math.sqrt(dx * dx + dy * dy);
		}

	}
	
	
	private final AutoFocusCallback autoFocusCallback=new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			//�۽�֮����ݽ���޸�ͼƬ
			if (success) {
				mFocusImageView.onFocusSuccess();
			}else {
				//�۽�ʧ����ʾ��ͼƬ������δ�ҵ����ʵ���Դ����������ʾͬһ��ͼƬ
				mFocusImageView.onFocusFailed();

			}
		}
	};

	
	private final PictureCallback pictureCallback=new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if(mDataHandler==null) mDataHandler=new DataHandler();	
			mDataHandler.setMaxSize(200);
			Bitmap bm = null;
			if(mDataHandler.save(data)!=null){
				bm=mDataHandler.save(data);
			}
			//���´�Ԥ��ͼ��������һ�ε�����׼��
			camera.startPreview();
			if(mListener!=null) mListener.onTakePictureEnd(bm);
			
		}
	};

	/**
	 * ���շ��ص�byte���ݴ�����
	 * @author linj
	 *
	 */
	private final class DataHandler{
		/** ѹ�����ͼƬ���ֵ ��λKB*/
		private int maxSize=200;

		/**
		 * ����ͼƬ
		 * @param ������ص��ļ���
		 * @return ���������ɵ�����ͼ
		 */
		public Bitmap save(byte[] data){
			
			Bitmap bm;
			if(data!=null){
				//��������������ص�ͼƬ
				
				try{
					bm=BitmapFactory.decodeByteArray(data, 0, data.length);
					
					return bm; 
				}catch(Exception e){
					Log.e(TAG, e.toString());
					Toast.makeText(getContext(), "�������������ʧ��", Toast.LENGTH_SHORT).show();
					return null;
				}
			}else{
				Toast.makeText(getContext(), "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
				return null;
			}
		}

		/**
		 * ͼƬѹ������
		 * @param bitmap ͼƬ�ļ�
		 * @param max �ļ���С���ֵ
		 * @return ѹ������ֽ���
		 * @throws Exception
		 */
		@SuppressWarnings("unused")
		public ByteArrayOutputStream compress(Bitmap bitmap){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
			int options = 99;
			while ( baos.toByteArray().length / 1024 > maxSize) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
				options -= 3;// ÿ�ζ�����10
				//ѹ����С��0������ѹ��
				if (options<0) {
					break;
				}
				Log.i(TAG,baos.toByteArray().length / 1024+"");
				baos.reset();// ����baos�����baos
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			}
			return baos;
		}

		public void setMaxSize(int maxSize) {
			this.maxSize = maxSize;
		}
	}

	/** 
	 * @ClassName: TakePictureListener 
	 * @Description:  ���ռ����ӿڣ����������տ�ʼ�ͽ�����ִ����Ӧ����
	 * @author LinJ
	 * @date 2014-12-31 ����9:50:33 
	 *  
	 */
	public static interface TakePictureListener{		
		/**  
		 *���ս���ִ�еĶ������÷�������onPictureTaken����ִ�к󴥷�
		 *  @param bm �������ɵ�ͼƬ 
		 */
		public void onTakePictureEnd(Bitmap bm);

		/**  ��ʱͼƬ���������󴥷�
		 * @param bm �������ɵ�ͼƬ 
		 * @param isVideo true����ǰΪ¼������ͼ false:Ϊ��������ͼ
		 * */
		public void onAnimtionEnd(Bitmap bm,boolean isVideo);
	}


}