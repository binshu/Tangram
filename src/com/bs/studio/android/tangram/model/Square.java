/**
 * 
 */
package com.bs.studio.android.tangram.model;

import com.bs.studio.android.math.Point;

/**
 * @author LemIST
 * 
 */
public class Square extends Piece {

	/**
	 * @param id
	 * @param size
	 */
	public Square(PieceId id, float scale, float size) {
		super(id, scale);
		this.size = size;
		initializeCurrentPoints();
	}

	/**
	 * @return the size
	 */
	public float getSize() {
		return size * this.getScale();
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(float size) {
		this.size = size;
	}

	private float size;

	// /
	// / savedPoints = Point[2][4]
	// / dim 2: rotation, either odd or even
	// / dim 3: 4 points
	// /

	@Override
	protected void initializeCurrentPoints() {

		currentPoints = new Point[4];
		float halfSize = this.getSize() / 2f;
		currentPoints[0] = new Point(-halfSize, -halfSize);
		currentPoints[1] = new Point(halfSize, -halfSize);
		currentPoints[2] = new Point(halfSize, halfSize);
		currentPoints[3] = new Point(-halfSize, halfSize);

	}
}
