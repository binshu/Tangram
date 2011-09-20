/**
 * 
 */
package com.bs.studio.android.math;

import android.util.Log;


/**
 * @author LemIST
 * 
 */
public class Point {
	public float x;
	public float y;
	
	public static int objectCount = 0; 
	public static final Point OriginPoint = new Point(0,0);
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
		
		objectCount ++;
		if (objectCount %1000 == 0){
			Log.v("dalvikvm", objectCount + "Points created.");
		}
	}
	
	public void set(float x, float y){
		this.x = x;
		this.y = y;
	}

	public double distanceToLine(Line line){
		float a = line.getA();
		float b = line.getB();
		float c = line.getC();
		return (Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b
				* b));
	}
	
	public boolean isOnLine(Line line){
		return distanceToLine(line) < 0.0001 && withinSection(line);
	}
	
	/**
	 * from line p1 to line p2
	 * @param line
	 * @return
	 */
	public float onLinePercentage(Line line){
		double distanceToP1 = this.distanceToPoint(line.getP1());
		double sectionLength = line.getP1().distanceToPoint(line.getP2());
		return (float)(distanceToP1 / sectionLength);
	}
	
	public boolean withinSection(Line line){
		Point lineP1 = line.getP1();
		Point lineP2 = line.getP2();
		
		return Angle.getValue(lineP1, this, lineP2) <= Math.PI / 2
		&& Angle.getValue(lineP2, this, lineP1) <= Math.PI / 2;
	}
	
	public boolean nearLine(Line line) {
		return distanceToLine(line) < 5d && withinSection(line);
	}

	public Point getOffsetToMoveToLine(Line line) {
		float a = line.getA();
		float b = line.getB();
		float c = line.getC();
		
		float cx = line.getMiddleOfTwoPoints().x;
		float cy = line.getMiddleOfTwoPoints().y;
		
		if (Math.abs(cx - x) < 3d && Math.abs(cy - y) < 3d){
			return new Point(cx - x, cy - y);
		}
		
		if (a == 0)
			return new Point(0, -c / b - y);
		if (b == 0)
			return new Point(-c / a - x, 0);
		if (a != 0 && b != 0)
			return new Point(0, -(a * x + c) / b - y);
		return new Point(0, 0);
	}
	
	public boolean matches(Point anotherPoint){
		return x == anotherPoint.x && y == anotherPoint.y;
	}
	
	public double distanceToPoint(Point anotherPoint){
		float dx = x - anotherPoint.x;
		float dy = y - anotherPoint.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	private Point rotate(Point aroundPoint, float angle){
		if (aroundPoint == this)
			return this;
		
		set(x - aroundPoint.x, y - aroundPoint.y);
		
		set(x * (float) Math.cos(angle) - y
				* (float) Math.sin(angle),x * (float) Math.sin(angle)
				+ y * (float) Math.cos(angle));
		
		set(x + aroundPoint.x, y + aroundPoint.y);
		return this;
	}

	public Point rotateOneEighth(int direction) {
		return rotate(OriginPoint, 0.25f * (float) Math.PI * direction);
	}
	
	public Point rotateOneEighthAround(Point aroundPoint, int direction) {
		return rotate(aroundPoint, 0.25f * (float) Math.PI * direction);
	}
	
	public boolean equals(Point anotherPoint){
		return anotherPoint.x == x && anotherPoint.y == y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
