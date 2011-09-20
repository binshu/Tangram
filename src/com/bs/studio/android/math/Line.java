/**
 * 
 */
package com.bs.studio.android.math;


/**
 * @author LemIST
 * 
 */
public class Line {

	/**
	 * Definition of line
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public Line defineByTwoPoints(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;

		a = p2.y - p1.y;
		b = p1.x - p2.x;
		c = p2.x * p1.y - p1.x * p2.y;

		return this;
	}

	public Point getP1() {
		return p1;
	}

	public Point getP2() {
		return p2;
	}

	public Point getMiddleOfTwoPoints() {
		if (middle == null)
			middle = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
		return middle;
	}

	public Point getPointOnLineWithPercentage(float percentage) {
		return new Point((p2.x - p1.x) * percentage + p1.x, (p2.y - p1.y)
				* percentage + p1.y);
	}

	private Point p1;
	private Point p2;
	private Point middle;

	/**
	 * line form ax + by + c = 0
	 */
	private float a;
	private float b;
	private float c;

	public float getA() {
		return a;
	}

	public float getB() {
		return b;
	}

	public float getC() {
		return c;
	}
}
