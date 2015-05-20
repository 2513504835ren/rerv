/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
public final class ViewfinderView extends View {
	/**
	 * ˢ�½����ʱ��
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * �ĸ���ɫ�߽Ƕ�Ӧ�ĳ���
	 */
	private int ScreenRate;
	
	/**
	 * �ĸ���ɫ�߽Ƕ�Ӧ�Ŀ��
	 */
	private static final int CORNER_WIDTH = 10/2;
	/**
	 * ɨ����е��м��ߵĿ��
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;
	
	/**
	 * ɨ����е��м��ߵ���ɨ������ҵļ�϶
	 */
	private static final int MIDDLE_LINE_PADDING = 5;
	
	/**
	 * �м�������ÿ��ˢ���ƶ��ľ���
	 */
	private static final int SPEEN_DISTANCE = 5;
	
	/**
	 * �ֻ�����Ļ�ܶ�
	 */
	private static float density;
	/**
	 * �ֻ�����Ļx
	 */
	private static int X;
	/**
	 * �ֻ�����Ļy
	 */
	private static int Y;
	/**
	 * �����С
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * �������ɨ�������ľ���
	 */
	//private static final int TEXT_PADDING_TOP = 30;
	
	/**
	 * ���ʶ��������
	 */
	private Paint paint;
	
	/**
	 * �м们���ߵ�����λ��
	 */
	private int slideLeft;
	
	private Bitmap resultBitmap;
	private Bitmap bm = null;
	private final int maskColor;
	private final int resultColor;
	
	boolean isFirst;
	
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		density = context.getResources().getDisplayMetrics().density;
		X = context.getResources().getDisplayMetrics().widthPixels;
		Y = context.getResources().getDisplayMetrics().heightPixels;
		//������ת����dp
		ScreenRate = (int)(3*(20 * density)/4);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		//�м��ɨ�����Ҫ�޸�ɨ���Ĵ�С��ȥCameraManager�����޸�
		Rect frame = new Rect();
		frame.left=(X/25);
		frame.top=(Y/8);
		frame.right=(24*X/25);
		frame.bottom=((Y/8)+(Y/12));
		
		//��ʼ���м��߻��������ϱߺ����±�
		if(!isFirst){
			isFirst = true;
			slideLeft = frame.left;
		}
		
		//��ȡ��Ļ�Ŀ�͸�
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		
		//����ɨ����������Ӱ���֣����ĸ����֣�ɨ�������浽��Ļ���棬ɨ�������浽��Ļ����
		//ɨ��������浽��Ļ��ߣ�ɨ�����ұߵ���Ļ�ұ�
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);
		
		

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			//��ɨ�����ϵĽǣ��ܹ�8������
			paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top
					+ ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
					+ ScreenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate,
					frame.left + CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH,
					frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate,
					frame.right, frame.bottom, paint);

			
			//�����м����,ÿ��ˢ�½��棬�м���������ƶ�SPEEN_DISTANCE
			slideLeft += SPEEN_DISTANCE;
			if(slideLeft >= frame.right){
				slideLeft = frame.left;
			}
			canvas.drawRect(slideLeft - MIDDLE_LINE_WIDTH/2, frame.top + MIDDLE_LINE_PADDING,slideLeft + MIDDLE_LINE_WIDTH/2, frame.bottom - MIDDLE_LINE_PADDING, paint);
			
			
			//��ɨ����������
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			paint.setAlpha(0x40);
			paint.setTypeface(Typeface.create("System", Typeface.BOLD));
			canvas.drawText(getResources().getString(R.string.camera_scan), frame.left, (float) (frame.bottom + 2*frame.left  ), paint);
			
			//ֻˢ��ɨ�������ݣ������ط���ˢ��
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);
		}
		if(bm != null){
			paint.setAlpha(150);
			frame.top = (frame.top + 3*frame.left+Y/12);
			frame.bottom = (frame.bottom + 3*frame.left+Y/12);
			canvas.drawBitmap(bm, null, frame, paint);;
		}
	}
	
	public Bitmap SetBit(Bitmap bm){
		this.bm = bm;
		this.invalidate();
		return bm;
		
	}

}
