/**
 * 
 */
package com.bs.studio.android.tangram.model;

import com.bs.studio.android.math.Point;

/**
 * @author LemIST
 * 
 */
public class Parallelogram extends Piece {
	private float baseSize;

	private float height;

	public Parallelogram(PieceId id, float scale, float baseSize, float height){
		super(id, scale);
		this.baseSize = baseSize;
		this.height = height;
		initializeCurrentPoints();
	}

	/**
	 * @return the baseSize
	 */
	public float getBaseSize() {
		return baseSize * this.getScale();
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height * this.getScale();
	}

	@Override
	protected void initializeCurrentPoints() {
		currentPoints = new Point[4];

		float halfH = this.getHeight() / 2f;
		currentPoints[0] = new Point(-halfH, -halfH);
		currentPoints[1] = new Point(halfH, -halfH * 3);
		currentPoints[2] = new Point(halfH, halfH);
		currentPoints[3] = new Point(-halfH, halfH * 3);
	}
}
