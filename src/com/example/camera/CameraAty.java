package com.example.camera;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraContainer.TakePictureListener;
import com.linj.camera.view.CameraView.FlashMode;


/** 
 * @ClassName: CameraAty 
 * @Description:  �Զ����������
 * @author LinJ
 * @date 2014-12-31 ����9:44:25 
 *  
 */
public class CameraAty extends Activity implements View.OnClickListener,TakePictureListener{
	
	/**
	 * ʶ����
	 */
	private static String ocrResult;
	
	/**
	 * ʶ�������Դ·��
	 */
	private static final String TESSBASE_PATH = Environment.getExternalStorageDirectory().getPath()+"/tesseract/";
	/**
	 * ʶ�������Դ
	 */
	private static final String DEFAULT_LANGUAGE = "eng";
	
	/**
	 * ͼƬ�洢·��
	 */
	private static String path1=Environment.getExternalStorageDirectory().getPath()+"/";
	
	/**
	 * �ж�Activity�Ƿ�����
	 */
	private static boolean IsOnPouse=false;
	
	private static boolean IsGray=true;

	/**
	 * �������
	 */
	private CameraContainer mContainer;
	
	public ImageButton mCameraShutterButton;

	private ImageView mFlashView;
	private ImageView mSwitchCameraView;
	private ImageView mSettingView;
	private ImageView mBackview;
	
	private Button mSave;
	private Button mCancel;
	private Button mDelete;
	private Button m1;
	private Button m2;
	private Button m3;
	private Button m4;
	private Button m5;
	private Button m6;
	private Button m7;
	private Button m8;
	private Button m9;
	private Button m0;
	
	private Switch mMode;
	private Switch mAuto;
	
	private LinearLayout key;
	
	private ViewfinderView mViewf;
	private EditText mNumber;
	
	private RelativeLayout l1;
	
	private Bitmap dis;
	
	private float sWidth;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.camera);
		//����ؼ�
		initView();
		
		bm();
		//�󶨼�����
		listener();
		//�Զ��۽�
		//mContainer.autoF();
		//���������
		mContainer.setFlashMode(FlashMode.OFF);
	}
	
	/**
	 * ����ؼ�
	 */
	private void initView(){
		mContainer=(CameraContainer)findViewById(R.id.container);
		
		mCameraShutterButton=(ImageButton)findViewById(R.id.btn_shutter_camera);
		mSwitchCameraView=(ImageView)findViewById(R.id.btn_switch_camera);
		mFlashView=(ImageView)findViewById(R.id.btn_flash_mode);
		mSettingView=(ImageView)findViewById(R.id.btn_other_setting);
		mBackview=(ImageView)findViewById(R.id.header_bar_back_cam);
		mNumber=(EditText) findViewById(R.id.number1);
		mViewf=(ViewfinderView)findViewById(R.id.viewfinder_view);
		l1=(RelativeLayout) findViewById(R.id.l1);
		mSave=(Button)findViewById(R.id.save);
		mCancel=(Button)findViewById(R.id.cancel);
		mDelete=(Button)findViewById(R.id.delete);
		m1=(Button)findViewById(R.id.button1);
		m2=(Button)findViewById(R.id.button2);
		m3=(Button)findViewById(R.id.button3);
		m4=(Button)findViewById(R.id.button4);
		m5=(Button)findViewById(R.id.button5);
		m6=(Button)findViewById(R.id.button6);
		m7=(Button)findViewById(R.id.button7);
		m8=(Button)findViewById(R.id.button8);
		m9=(Button)findViewById(R.id.button9);
		m0=(Button)findViewById(R.id.button0);
		key=(LinearLayout) findViewById(R.id.l3);
		mMode=(Switch) findViewById(R.id.switch1);
		mAuto=(Switch) findViewById(R.id.switch2);
		
		mNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
		mNumber.setFocusable(true);
		mNumber.setFocusableInTouchMode(true);
		mNumber.requestFocus();
		mNumber.requestFocusFromTouch();
//		���ü��̲��Զ�����
		getWindow().setSoftInputMode( WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
//		���ü�������		
		mNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		disableShowSoftInput();
//		menuShowContent.setSelection(contentFromTitle.length());

	}
	
	/**
    * ��ȡ��Ļ��С
    * @author ren
    */
	protected void bm (){
   	 	DisplayMetrics Dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(Dm);
		sWidth = Dm.widthPixels;		//��ȡ��
		
		
		LayoutParams para1;
        para1 = l1.getLayoutParams();   
        para1.width = (int) (23*sWidth/25);  
        l1.setLayoutParams(para1);
	}

	/**
	 *@author ren
	 *�󶨼�����
	 */
	@SuppressLint("NewApi")
	private void listener(){
		mCameraShutterButton.setOnClickListener(this);
		mFlashView.setOnClickListener(this);
		mSwitchCameraView.setOnClickListener(this);
		mSettingView.setOnClickListener(this);
		mBackview.setOnClickListener(this);
		mNumber.setOnClickListener(this);
		mSave.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		m1.setOnClickListener(this);
		m2.setOnClickListener(this);
		m3.setOnClickListener(this);
		m4.setOnClickListener(this);
		m5.setOnClickListener(this);
		m6.setOnClickListener(this);
		m7.setOnClickListener(this);
		m8.setOnClickListener(this);
		m9.setOnClickListener(this);
		m0.setOnClickListener(this);
		
		//��ɫ��ڰ�ģʽ�л�
		mMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
			  
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {  
					IsGray=false;
                } else {  
                	IsGray=true;

                }  
			}  
        });  
		
		//�Զ���
		mAuto.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
			  
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {  
					IsOnPouse = false;
					mContainer.Act(IsOnPouse);
					mContainer.clclik(activity());
					mContainer.autoF(lis());
					mCameraShutterButton.setVisibility(View.GONE);
                } else {
                	IsOnPouse = true;
            		mContainer.Act(IsOnPouse);
                	mCameraShutterButton.setVisibility(View.VISIBLE);
                	
                }  
			}  
        });  
	}

	private void setNum(int i){
		
		int w = mNumber.getSelectionStart();
		Editable e = mNumber.getText();
		if(!e.equals(null)){
			String s = i+"";
			if(e.length()<=14){
				e = e.insert(w, s);
				mNumber.setText(e);
				e = mNumber.getText();
				if (e instanceof Spannable) {
	        	     Spannable spanText = (Spannable) e;
	        	     Selection.setSelection(spanText, w+1);
	        	}
			}else{
				Toast.makeText(activity(), "�绰��̫���ˣ��׼Ҽǲ�ס", Toast.LENGTH_LONG).show();
			}
			
		}else{
			mNumber.setText(i);
		}
		
	}
	
	private void deNum(){
		int w = mNumber.getSelectionStart();
		Editable e = mNumber.getText();
		if(!e.equals(null)){
			if(w!=0){
				e = e.delete(w-1, w);
				mNumber.setText(e);
				e = mNumber.getText();
				if (e instanceof Spannable) {
			        Spannable spanText = (Spannable) e;
			        Selection.setSelection(spanText, w-1);
				}
			}
			
		}else{
			Toast.makeText(activity(), "�绰��̫���ˣ��׼Ҽǲ�ס", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_shutter_camera:
			mCameraShutterButton.setClickable(false);
			mContainer.clclik(this);
			mContainer.takePicture(this);
			mNumber.setText("");
			key.setVisibility(View.GONE);
			break;
		case R.id.number1:

			key.setVisibility(View.VISIBLE);
			mDelete.setVisibility(View.VISIBLE);
			break;
		case R.id.header_bar_back_cam:
			this.finish();
			break;
		case R.id.save:
			key.setVisibility(View.GONE);
			mDelete.setVisibility(View.GONE);
			//Editable s= mNumber.getText();
			mNumber.setText("");
			break;
		case R.id.cancel:
			key.setVisibility(View.GONE);
			mDelete.setVisibility(View.GONE);
			mNumber.setText("");
			break;
		case R.id.delete:
			deNum();
			break;
		case R.id.button1:
			setNum(1);
			break;
		case R.id.button2:
			setNum(2);
			break;
		case R.id.button3:
			setNum(3);
			break;
		case R.id.button4:
			setNum(4);
			break;
		case R.id.button5:
			setNum(5);
			break;
		case R.id.button6:
			setNum(6);
			break;
		case R.id.button7:
			setNum(7);
			break;
		case R.id.button8:
			setNum(8);
			break;
		case R.id.button9:
			setNum(9);
			break;
		case R.id.button0:
			setNum(0);
			break;
		case R.id.btn_flash_mode:
			key.setVisibility(View.GONE);
			mDelete.setVisibility(View.GONE);
			if(mContainer.getFlashMode()==FlashMode.ON){
				mContainer.setFlashMode(FlashMode.OFF);
				mFlashView.setImageResource(R.drawable.btn_flash_off);
			}else if (mContainer.getFlashMode()==FlashMode.OFF) {
				mContainer.setFlashMode(FlashMode.AUTO);
				mFlashView.setImageResource(R.drawable.btn_flash_auto);
			}
			else if (mContainer.getFlashMode()==FlashMode.AUTO) {
				mContainer.setFlashMode(FlashMode.TORCH);
				mFlashView.setImageResource(R.drawable.btn_flash_torch);
			}
			else if (mContainer.getFlashMode()==FlashMode.TORCH) {
				mContainer.setFlashMode(FlashMode.ON);
				mFlashView.setImageResource(R.drawable.btn_flash_on);
			}
			break;
		case R.id.btn_switch_camera:
			mContainer.switchCamera();
			break;
		case R.id.btn_other_setting:
			//sGetandSaveCurrentImage();
			break;
			
		default:
			break;
		}
	}
	
	
//	private void GetandSaveCurrentImage(){  
//	    //1.����Bitmap  
//	    WindowManager windowManager = getWindowManager();  
//	    Display display = windowManager.getDefaultDisplay();  
//	    int w = display.getWidth();  
//	    int h = display.getHeight();  
//	    
//	    String SavePATH = Environment.getExternalStorageDirectory().getPath()+"/Shit";
//	      
//	    Bitmap Bmp = Bitmap.createBitmap( w, h, Config.ARGB_8888 );      
//	      
//	    //2.��ȡ��Ļ  
//	    View decorview = this.getWindow().getDecorView();   
//	    decorview.setDrawingCacheEnabled(true);   
//	    Bmp = decorview.getDrawingCache();   
//	  
//	    //3.����Bitmap   
//	    try {  
//	        File path = new File(SavePATH);  
//	        //�ļ�  
//	        String filepath = SavePATH + "/Screen_1.png";  
//	        File file = new File(filepath);  
//	        if(!path.exists()){  
//	            path.mkdirs();  
//	        }  
//	        if (!file.exists()) {  
//	            file.createNewFile();  
//	        }  
//	          
//	        FileOutputStream fos = null;  
//	        fos = new FileOutputStream(file);  
//	        if (null != fos) {  
//	            Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);  
//	            fos.flush();  
//	            fos.close();    
//	              
//	            Toast.makeText(this, "�����ļ��ѱ�����SDCard/ADASiteMaps/ScreenImage/��", Toast.LENGTH_LONG).show();  
//	        }  
//	  
//	    } catch (Exception e) {  
//	        e.printStackTrace();  
//	    }  
//	}

	@Override
	public void onTakePictureEnd(Bitmap bm) {
		if(bm!=null){
			dis = DistBit.shoot(this, path1+"cut.png",bm);
			find();
			mViewf.SetBit(dis);
			
		}	
		
		mCameraShutterButton.setClickable(true);
	}
	
	@Override
	public void onAnimtionEnd(Bitmap bm,boolean isVideo) {
		if(bm!=null){
		}
	}
	
	
	/********************************************************
	 * 
	 * ����ʶ����
	 * @author li
	 * 
	 ********************************************************/
	
	/**
	 * ����ʶ�����ݲ���������ص�handler
	 */
	private void find(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (IsGray) {
					dis = ImageProgress.converyToGrayImg(dis);
				} else {
					dis = ImageProgress.doPretreatment(dis);
				}
				
				try {
					ocrResult = doOcr(dis);
				} catch (Exception e) {
					Toast.makeText(activity(), "ʶ�����������һ��", Toast.LENGTH_LONG).show();
				}
				Message msg = new Message();
				msg.what = 1;
				msg.getData().putString("text", ocrResult);
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * ��ͼƬ����ʶ��ķ���
	 */
	public String doOcr(Bitmap bitmap) {
		
		TessBaseAPI baseApi = new TessBaseAPI();
		// ��ʼ��baseApi
		baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
		// ����Ӵ��У�tess-twoҪ��BMP����Ϊ������
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		baseApi.setImage(bitmap);
		// text��Ϊʶ��Ľ��
		String text = baseApi.getUTF8Text();
		baseApi.clear();
		baseApi.end();
		return text;
	}
	
	/**
	 * ɸѡʶ�������е�����
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String ocrString = msg.getData().getString("text").trim();
				// ��ȥ ʶ������� ���� ֻ��ʾ ����
				if (!TextUtils.isEmpty(ocrString)) {
					try {
						ocrResult = OcrResultCheck
								.ocrFiltration(ocrString);
						if(ocrResult==null){
							Toast.makeText(activity(), "ʶ��ʧ�ܣ�������һ��", Toast.LENGTH_LONG).show();
						}
						mNumber.setText(ocrResult);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						Toast.makeText(activity(), "ʶ��ʧ�ܣ�������һ��", Toast.LENGTH_LONG).show();
						mNumber.setText(ocrResult);
					}
				}else {
					Toast.makeText(activity(), "ʶ��ʧ�ܣ�������һ��", Toast.LENGTH_LONG).show();
					mNumber.setText(ocrResult);
				}
				
				
				break;

			default:
				break;
			}
		}

	};
	
	/**
	 * ��ֹEdittext��������̣������Ȼ������ʾ��
	 */
	public void disableShowSoftInput()
	{
		if (android.os.Build.VERSION.SDK_INT <= 10) 
		{
				mNumber.setInputType(InputType.TYPE_NULL);  
        } 
		else {  
                Class<EditText> cls = EditText.class;  
                Method method;
	            try { 
	            	System.out.println("===============");
	                method = cls.getMethod("setShowSoftInputOnFocus",boolean.class);  
	                method.setAccessible(true);  
	                method.invoke(mNumber, false);  
	            }catch (Exception e) {
					// TODO: handle exception
				}
	            
	            try { 
	                method = cls.getMethod("setSoftInputShownOnFocus",boolean.class);  
	                method.setAccessible(true);  
	                method.invoke(mNumber, false);  
	            }catch (Exception e) {
					// TODO: handle exception
				}
        } 
	}
	
	
	/**
	 * �õ�Activity
	 * @return {@link CameraAty}
	 */
	private Activity activity(){
		return this;
	}
	
	
	
	/**
	 * �õ�TakePictureListener
	 * @return this
	 */
	private TakePictureListener lis(){
		return this;
	}
	

	@Override
	protected void onResume() {		
		super.onResume();
		IsOnPouse = false;
		mContainer.Act(IsOnPouse);
		//mContainer.autoF();
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		IsOnPouse = true;
		mContainer.Act(IsOnPouse);
	}

}