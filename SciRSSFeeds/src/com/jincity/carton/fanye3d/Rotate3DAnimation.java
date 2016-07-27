package com.jincity.carton.fanye3d;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3DAnimation extends Animation {

	private final float mFormDegress;

	private final float mToDegress;

	private final float mCenterX;

	private final float mCenterY;

	/**
	 * ����z���һ������ֵ,��Ҫ�ǿ��ƶ�������������
	 */
	private final float mDepthz;

	/**
	 * ����z���������ƶ����������ƶ�,�Ӷ�ʵ������Ч��
	 */
	private final boolean mReverse;

	private Camera mCamera;

	public Rotate3DAnimation(float formDegress, float toDegress, float centerX, float centerY, float depthz, boolean reverse) {
		super();
		this.mFormDegress = formDegress;
		this.mToDegress = toDegress;
		this.mCenterX = centerX;
		this.mCenterY = centerY;
		this.mDepthz = depthz;
		this.mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	/**
	 * interpolatedTime ȡֵ��Χ��0-1֮�䵱ÿ�Σ��������������ϵͳ�᲻ͣ�ĵ���applyTransformation������
	 * ���ı�interpolatedTime��ֵ
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float formDegress = mFormDegress;
		// ͨ�����ֵ�����ת��ĽǶ�
		float degress = formDegress + ((mToDegress - formDegress) * interpolatedTime);
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		// �õ���ǰ����
		Matrix matrix = t.getMatrix();
		// ����ǰ��Ļ��״̬
		camera.save();
		// �ж��ǽ�������
		if (mReverse) {
			// ����ı�Z��Ƕ�
			camera.translate(0.0f, 0.0f, mDepthz * interpolatedTime);
		} else {
			// ����ı�Z��Ƕ�
			camera.translate(0.0f, 0.0f, mDepthz * (1.0f - interpolatedTime));
		}
		// ��תY��Ƕ�
		camera.rotateY(degress);
		// �ѵ�ǰ�ı��ľ�����Ϣ���Ƹ�Transformation��Matrix
		camera.getMatrix(matrix);
		// ���ݸı��ľ�����Ϣ���»ָ���Ļ
		camera.restore();

		// �ö�������Ļ�м�����
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}