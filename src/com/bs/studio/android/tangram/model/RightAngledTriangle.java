/**
 * 
 */
package com.bs.studio.android.tangram.model;

import com.bs.studio.android.math.Point;

/**
 * @author LemIST
 * 
 */
public class RightAngledTriangle extends Piece {
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param baseLineSize
	 * @param rightAngleSideSize
	 */
	public RightAngledTriangle(PieceId id, float scale, float baseLineSize,
			float rightAngleSideSize) {
		super(id, scale);
		this.baseLineSize = baseLineSize;
		this.rightAngleSideSize = rightAngleSideSize;
		initializeCurrentPoints();
	}

	/**
	 * @return the rightAngleSideSize
	 */
	public float getRightAngleSideSize() {
		return rightAngleSideSize * this.getScale();
	}

	/**
	 * @param rightAngleSideSize
	 *            the rightAngleSideSize to set
	 */
	public void setRightAngleSideSize(float rightAngleSideSize) {
		this.rightAngleSideSize = rightAngleSideSize;
	}

	/**
	 * @return the baseLineSize
	 */
	public float getBaseLineSize() {
		return baseLineSize * this.getScale();
	}

	/**
	 * @param baseLineSize
	 *            the baseLineSize to set
	 */
	public void setBaseLineSize(float baseLineSize) {
		this.baseLineSize = baseLineSize;
	}
	
	private float rightAngleSideSize;
	private float baseLineSize;
	
	@Override 
	protected void initializeCurrentPoints(){
		currentPoints = new Point[3];
		currentPoints[0] = new Point(0, 0);
		currentPoints[1] = new Point(0, -this.getRightAngleSideSize());
		currentPoints[2] = new Point(this.getRightAngleSideSize(), 0);
	}
}
