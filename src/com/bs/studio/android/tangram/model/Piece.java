/**
 * 
 */
package com.bs.studio.android.tangram.model;

import java.util.Comparator;

import android.graphics.Rect;
import android.util.Log;

import com.bs.studio.android.math.Angle;
import com.bs.studio.android.math.Line;
import com.bs.studio.android.math.Point;

/**
 * @author LemIST The base class for the seven piece.
 */
public abstract class Piece implements Comparable<Piece> {

	public int compareTo(Piece p2) {
		Piece p1 = this;
		if (p1.centerPoint.y < p2.centerPoint.y)
			return -1;
		else if (p1.centerPoint.y > p2.centerPoint.y)
			return 1;
		else {
			if (p1.centerPoint.x < p2.centerPoint.x)
				return -1;
			else if (p1.centerPoint.x > p2.centerPoint.x)
				return 1;
			else
				return 0;
		}
	}

	public static Piece CreatePiece(PieceId id, float scale) {
		switch (id) {
		case LargeTriangle1:
		case LargeTriangle2:
			return new RightAngledTriangle(id, scale, 4f,
					(float) (2 * Math.sqrt(2)));
		case MiddleTriangle:
			return new RightAngledTriangle(id, scale,
					(float) (2 * Math.sqrt(2)), 2f);
		case SmallTriangle1:
		case SmallTriangle2:
			return new RightAngledTriangle(id, scale, 2, (float) (Math.sqrt(2)));
		case Square:
			return new Square(id, scale, (float) Math.sqrt(2));
		case Parallelogram:
			return new Parallelogram(id, scale, 2, 1);
		}
		return null;
	}

	private PieceId id;

	private Point centerPoint;

	private float scale;

	private int rotation = 0;

	protected Point[] currentPoints;

	private Point[] physicalPoints;

	private boolean flipped = false;

	private Rect containingRect;

	public Piece(PieceId id, float scale) {
		this.setId(id);
		this.setScale(scale);
		this.centerPoint = new Point(Float.MIN_VALUE, Float.MIN_VALUE);
	}

	public void autoAdjustPositionTo(Object[] pieces, Point screenSize) {
		// point adjust to points
		Point[] thisPhysicalPoints = this.getPhysicalPoints();
		for (Object pieceObj : pieces) {
			Piece piece = (Piece) pieceObj;
			if (piece == this)
				continue;
			Point[] thatPhysicalPoints = piece.getPhysicalPoints();
			for (Point thisPoint : thisPhysicalPoints) {
				for (Point thatPoint : thatPhysicalPoints) {
					if (Math.abs(thisPoint.x - thatPoint.x) < 5
							&& Math.abs(thisPoint.y - thatPoint.y) < 5) {
						this.moveInScreen(screenSize, new Point(thatPoint.x
								- thisPoint.x, thatPoint.y - thisPoint.y));
						return;
					}
				}
			}
		}

		// point adjust to line
		for (Object pieceObj : pieces) {
			Piece piece = (Piece) pieceObj;
			if (piece == this)
				continue;

			Point[] thatPhysicalPoints = piece.getPhysicalPoints();
			for (Point thisPoint : thisPhysicalPoints) {
				for (int i = 0; i < thatPhysicalPoints.length; i++) {
					Line thatLine = new Line();
					thatLine.defineByTwoPoints(thatPhysicalPoints[i],
							thatPhysicalPoints[(i + 1)
									% thatPhysicalPoints.length]);
					if (thisPoint.nearLine(thatLine)) {
						Point offset = thisPoint
								.getOffsetToMoveToLine(thatLine);
						this.moveInScreen(screenSize, offset);
						Log.v("AutoAdjust", "Move to line because of near "
								+ piece.getId());
						Log.v("AutoAdjust", "\tOffset is " + offset);
						return;
					}
				}
			}
		}

		// line adjust to point
		for (Object pieceObj : pieces) {
			Piece piece = (Piece) pieceObj;
			if (piece == this)
				continue;

			Point[] thatPhysicalPoints = piece.getPhysicalPoints();
			for (Point thatPoint : thatPhysicalPoints) {
				for (int i = 0; i < thisPhysicalPoints.length; i++) {
					Line thisLine = new Line();
					thisLine.defineByTwoPoints(thisPhysicalPoints[i],
							thisPhysicalPoints[(i + 1)
									% thisPhysicalPoints.length]);
					if (thatPoint.nearLine(thisLine)) {
						Point offset = thatPoint
								.getOffsetToMoveToLine(thisLine);
						this.moveInScreen(screenSize, new Point(-offset.x,
								-offset.y));
						Log.v("AutoAdjust", "Move to point because of near "
								+ piece.getId());
						Log.v("AutoAdjust", "\tOffset is " + offset);
						return;
					}
				}
			}
		}
	}

	public boolean containsPoint(Point p) {
		Log.v(this.getId().toString(), "Point p is (" + p.x + ", " + p.y + ")");
		Log.v(this.getId().toString(), "\t" + this);
		double angle = 0;
		p = new Point(p.x - this.getCenterPoint().x, p.y
				- this.getCenterPoint().y);
		for (int i = 0; i < currentPoints.length; i++) {
			double thisAngle = Angle.getValue(p, currentPoints[i],
					currentPoints[(i + 1) % currentPoints.length]);
			Log.v(this.getId().toString(), "\tAngle with point " + i + " and "
					+ (i + 1) % currentPoints.length + " is " + thisAngle
					/ Math.PI * 180 + "^");
			angle += thisAngle;
		}
		Log.v(this.getId().toString(), " Total angle is " + angle + ", 2pi = "
				+ 2 * Math.PI);
		return (Math.abs(angle - 2 * Math.PI) < 0.0001);
	}

	public void flip() {
		flip(0);
		flipped = !flipped;
	}

	// /
	// / centerAxis:
	// / 0 : y axis
	// / 1 : x-y diag
	// / 2 : x axis
	// / 3 : y-x diag
	// /
	private void flip(int centerAxis) {
		for (int i = 0; i < currentPoints.length; i++) {
			currentPoints[i] = getMatchedPoint(centerAxis, currentPoints[i]);
		}
	}

	/**
	 * @return the centerPoint
	 */
	private Point getCenterPoint() {
		if (centerPoint == null)
			centerPoint = new Point(50, 50);
		return centerPoint;
	}

	public Rect getContainingRect() {
		return containingRect;
	}

	public PieceId getId() {
		return id;
	}

	private Point getMatchedPoint(int centerAxis, Point p) {
		if (centerAxis == 0) {
			return new Point(-p.x, p.y);
		} else if (centerAxis == 2) {
			return new Point(p.x, -p.y);
		} else {
			float k = 2 - centerAxis;
			return new Point(k * p.y, k * p.x);
		}
	}

	public Point[] getPhysicalPoints() {
		if (physicalPoints == null) {
			updatePhysicalPoints();
		}
		return physicalPoints;
	}

	public int getRotation() {
		return rotation;
	}

	/**
	 * @return the scale
	 */
	public float getScale() {
		return scale;
	}

	protected abstract void initializeCurrentPoints();

	public boolean isFlipped() {
		return flipped;
	}

	public void move(Point offset) {
		moveInScreen(null, offset);
	}

	public void moveInScreen(Point screenSize, Point offset) {
		centerPoint.x += offset.x;
		centerPoint.y += offset.y;
		if (screenSize != null) {
			for (int i = 0; i < currentPoints.length; i++) {
				float x = currentPoints[i].x + centerPoint.x;
				float y = currentPoints[i].y + centerPoint.y;

				if (x - screenSize.x > 0) {
					centerPoint.x += (screenSize.x - x);
				} else if (x < 0) {
					centerPoint.x -= x;
				}
				if (y - screenSize.y > 0) {
					centerPoint.y += (screenSize.y - y);
				} else if (y < 0) {
					centerPoint.y -= y;
				}
			}
		}
		updatePhysicalPoints();
	}

	public void rotateLeft(Point aroundPoint) {
		for (int i = 0; i < currentPoints.length; i++) {
			currentPoints[i].rotateOneEighth(-1);
		}
		centerPoint.rotateOneEighthAround(aroundPoint, -1);
		rotation = (rotation - 1 + Rotation.length) % Rotation.length;
		updatePhysicalPoints();
	}

	public void rotateRight(Point aroundPoint) {
		for (int i = 0; i < currentPoints.length; i++) {
			currentPoints[i].rotateOneEighth(1);
		}
		centerPoint.rotateOneEighthAround(aroundPoint, 1);
		rotation = (rotation + 1) % Rotation.length;
		updatePhysicalPoints();
	}

	/**
	 * @param centerPoint
	 *            the centerPoint to set
	 */
	public void setCenterPoint(float x, float y) {
		boolean centerPointMoved = true;
		if (this.centerPoint != null) {
			if (centerPoint.x == x && centerPoint.y == y)
				centerPointMoved = false;
			else
				this.centerPoint.set(x, y);
		} else
			this.centerPoint = new Point(x, y);
		if (centerPointMoved)
			updatePhysicalPoints();
	}

	public void setCenterPointByVetexPoint(int vertexIndex, Point position) {
		Point vertexPoint = currentPoints[vertexIndex];
		if (centerPoint == null) {
			centerPoint = new Point(0, 0);
		}
		centerPoint.x = position.x - vertexPoint.x;
		centerPoint.y = position.y - vertexPoint.y;
		updatePhysicalPoints();
	}

	protected void setId(PieceId id) {
		this.id = id;
	}

	public void setRotation(int rotation) {
		while (rotation != this.rotation) {
			rotateRight(this.centerPoint);
		}
	}

	/**
	 * @param scale
	 *            the scale to set
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("[");
		for (Point point : this.getPhysicalPoints()) {
			string.append("(" + point.x + ", " + point.y + ")");
			string.append("; ");
		}
		string.append("]");
		return string.toString();
	}

	private void updatePhysicalPoints() {
		if (physicalPoints == null) {
			physicalPoints = new Point[currentPoints.length];
			for (int i = 0; i < currentPoints.length; i++) {
				physicalPoints[i] = new Point(0, 0);
			}
		}

		float cx = centerPoint.x;
		float cy = centerPoint.y;

		float left = Float.MAX_VALUE;
		float right = Float.MIN_VALUE;
		float top = Float.MAX_VALUE;
		float bottom = Float.MIN_VALUE;

		for (int i = 0; i < currentPoints.length; i++) {
			physicalPoints[i].x = cx + currentPoints[i].x;
			physicalPoints[i].y = cy + currentPoints[i].y;

			if (physicalPoints[i].x < left)
				left = physicalPoints[i].x;
			if (physicalPoints[i].x > right)
				right = physicalPoints[i].x;

			if (physicalPoints[i].y < top)
				top = physicalPoints[i].y;
			if (physicalPoints[i].y > bottom)
				bottom = physicalPoints[i].y;
		}

		if (containingRect == null) {
			containingRect = new Rect();
		}
		containingRect.bottom = (int) (bottom + 5);
		containingRect.top = (int) (top - 5);
		containingRect.left = (int) (left - 5);
		containingRect.right = (int) (right + 5);
	}

}
