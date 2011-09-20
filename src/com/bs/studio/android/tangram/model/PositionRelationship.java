/**
 * 
 */
package com.bs.studio.android.tangram.model;

import java.util.Vector;

import android.util.Log;

import com.bs.studio.android.math.Line;
import com.bs.studio.android.math.Point;

/**
 * @author LemIST
 * 
 */
public class PositionRelationship {
	public PositionRelationship(){
		
	}
	
	private PositionRelationship(Piece thePiece, Piece relativeToPiece) {
		ids = new PieceId[] { thePiece.getId(), relativeToPiece.getId() };
		rotations = new int[] { thePiece.getRotation(),
				relativeToPiece.getRotation() };
		flips = new boolean[] { thePiece.isFlipped(),
				relativeToPiece.isFlipped() };
	}

	public static PositionRelationship getPositionRelationshipByMatchPoint(
			Piece thePiece, Piece relativeToPiece) {
		Point[] thePhysicalPositions = thePiece.getPhysicalPoints();
		Point[] relativePhysicalPositions = relativeToPiece.getPhysicalPoints();

		// test match points
		for (int i = 0; i < thePhysicalPositions.length; i++) {
			for (int j = 0; j < relativePhysicalPositions.length; j++) {
				if (thePhysicalPositions[i]
						.matches(relativePhysicalPositions[j])) {
					PositionRelationship relative = new PositionRelationship(
							thePiece, relativeToPiece);
					relative.type = PositionRelationshipType.ByMatchPoint;
					relative.pointMatch = new int[] { i, j };
					return relative;
				}
			}
		}
		return null;
	}

	public static PositionRelationship getPositionRelationshipByPointOnLine(
			Piece thePiece, Piece relativeToPiece) {
		Point[] thePiecePhysicalPoints = thePiece.getPhysicalPoints();
		Point[] relativePiecePhysicalPoints = relativeToPiece
				.getPhysicalPoints();

		// test the point of relative piece on the line of this piece

		for (int k = 0; k < 2; k++) {
			boolean pointIsOfThisPiece = k % 2 == 0;
			Point[] pointPhysicalPoints = pointIsOfThisPiece ? thePiecePhysicalPoints
					: relativePiecePhysicalPoints;
			Point[] linePhysicalPositions = pointIsOfThisPiece ? relativePiecePhysicalPoints
					: thePiecePhysicalPoints;

			for (int i = 0; i < pointPhysicalPoints.length; i++) {
				for (int j = 0; j < linePhysicalPositions.length; j++) {
					Line line = new Line();
					line.defineByTwoPoints(linePhysicalPositions[j],
							linePhysicalPositions[(j + 1)
									% linePhysicalPositions.length]);

					if (pointPhysicalPoints[i].isOnLine(line)) {
						PositionRelationship relative = new PositionRelationship(
								thePiece, relativeToPiece);
						relative.type = PositionRelationshipType.ByPointOnLine;
						relative.pointInLine = relative.new PointInLine();
						relative.pointInLine.pointIndex = i;
						relative.pointInLine.lineIndexes = new int[] { j,
								(j + 1) % linePhysicalPositions.length };
						relative.pointInLine.percentage = pointPhysicalPoints[i]
								.onLinePercentage(line);
						relative.pointInLine.pointIsOfThisPiece = pointIsOfThisPiece;
						return relative;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Pre condition: piece is not in knownPieces.
	 * 
	 * @param piece
	 * @param knownPieces
	 * @return
	 */
	public boolean restore(Piece piece, Vector<Piece> knownPieces) {
		if (pieceKindOf(piece) == PieceKind.ThePiece) {
			for (Piece relativeToPiece : knownPieces) {
				if (pieceKindOf(relativeToPiece) == PieceKind.RelativeToPiece) {
					piece.setRotation(rotations[0]);
					if (piece.isFlipped() != flips[0]) {
						piece.flip();
					}
					if (type == PositionRelationshipType.ByMatchPoint)
						restoreByMatchPoints(piece, relativeToPiece);
					else if (type == PositionRelationshipType.ByPointOnLine)
						restoreByPointOnLine(piece, relativeToPiece);
					return true;
				}
			}
		}
		return false;
	}

	public void restoreByMatchPoints(Piece thePiece, Piece relativeToPiece) {
		thePiece.setCenterPointByVetexPoint(pointMatch[0],
				relativeToPiece.getPhysicalPoints()[pointMatch[1]]);
	}

	private void restoreByPointOnLine(Piece thePiece, Piece relativeToPiece) {
		if (this.pointInLine.pointIsOfThisPiece) {
			Point[] relativePhysicalPoints = relativeToPiece
					.getPhysicalPoints();
			Line line = new Line();
			line.defineByTwoPoints(
					relativePhysicalPoints[pointInLine.lineIndexes[0]],
					relativePhysicalPoints[pointInLine.lineIndexes[1]]);

			thePiece.setCenterPointByVetexPoint(pointInLine.pointIndex,
					line.getPointOnLineWithPercentage(pointInLine.percentage));
		} else {
			Point[] thisPhysicalPoints = thePiece
					.getPhysicalPoints();
			Line line = new Line();
			if (thisPhysicalPoints == null){
				Log.v("AndroidRuntime", "point in line not restored.");
			}else{
				Log.v("AndroidRuntime", "it is ok.");
			}
			line.defineByTwoPoints(
					thisPhysicalPoints[pointInLine.lineIndexes[0]],
					thisPhysicalPoints[pointInLine.lineIndexes[1]]);
			
			Point currentMatchPoint = line.getPointOnLineWithPercentage(pointInLine.percentage);
			Point shouldBePoint = relativeToPiece.getPhysicalPoints()[pointInLine.pointIndex];
			Point offset = new Point(shouldBePoint.x - currentMatchPoint.x, 
					shouldBePoint.y - currentMatchPoint.y);
			
			thePiece.move(offset);
		}
	}

	private PieceKind pieceKindOf(Piece piece) {
		if (piece.getId() == ids[0])
			return PieceKind.ThePiece;
		if (piece.getId() == ids[1])
			return PieceKind.RelativeToPiece;
		return PieceKind.Unknown;
	}

	private enum PieceKind {
		ThePiece, RelativeToPiece, Unknown
	}

	/**
	 * Description of pieces.
	 */
	private PieceId[] ids = null;
	private int[] rotations = null;
	private boolean[] flips = null;

	public Piece getRelativeToPiece(Piece[] pieces) {
		for (Piece piece : pieces) {
			if (pieceKindOf(piece) == PieceKind.RelativeToPiece) {
				piece.setRotation(rotations[1]);
				if (piece.isFlipped() != flips[1]) {
					piece.flip();
				}
				return piece;
			}
		}
		return null;
	}

	/**
	 * Relative positions, from highest privilege to lower
	 */
	private PositionRelationshipType type;
	private int[] pointMatch;
	private PointInLine pointInLine;

	public class PointInLine {
		public PointInLine() {

		}

		private int pointIndex;
		private int[] lineIndexes;
		private float percentage;
		private boolean pointIsOfThisPiece;

		@Override
		public String toString() {
			return pointIsOfThisPiece + ", " + pointIndex + ", "
					+ lineIndexes[0] + ", " + lineIndexes[1] + ", "
					+ percentage;
		}
		
		public PointInLine fromString(String string){
			String[] elements = string.split(",");
			this.pointIsOfThisPiece = Boolean.parseBoolean(elements[0].trim());
			this.pointIndex = Integer.parseInt(elements[1].trim());
			this.lineIndexes = new int[2];
			this.lineIndexes[0] = Integer.parseInt(elements[2].trim());
			this.lineIndexes[1] = Integer.parseInt(elements[3].trim());
			this.percentage = Float.parseFloat(elements[4].trim());
			return this;
		}
	}

	@Override
	public String toString() {
		return  ids[0]
				+ ", "
				+ ids[1]
				+ ", "
				+ rotations[0]
				+ ", "
				+ rotations[1]
				+ ","
				+ flips[0]
				+ ", "
				+ flips[1]
				+ ", "
				+ type
				+ ", value("
				+ (type == PositionRelationshipType.ByMatchPoint ? (
						+ pointMatch[0] + ", " + pointMatch[1])
						: pointInLine) + ")";
	}
	
	public PositionRelationship fromString(String string){
		
		String[] elements = string.split(",");
		ids = new PieceId[2];
		ids[0] = PieceId.valueOf(elements[0].trim());
		ids[1] = PieceId.valueOf(elements[1].trim());
		rotations = new int[2];
		rotations[0] = Integer.parseInt(elements[2].trim());
		rotations[1] = Integer.parseInt(elements[3].trim());
		flips = new boolean[2];
		flips[0] = Boolean.parseBoolean(elements[4].trim());
		flips[1] = Boolean.parseBoolean(elements[5].trim());
		type = PositionRelationshipType.valueOf(elements[6].trim());
		
		String valueSubString = string.substring(string.indexOf("value(") + "value(".length(),
				string.length() -1);
		if (this.type == PositionRelationshipType.ByMatchPoint){
			String[] valueElements = valueSubString.split(",");
			pointMatch = new int[2];
			pointMatch[0] = Integer.parseInt(valueElements[0].trim());
			pointMatch[1] = Integer.parseInt(valueElements[1].trim());
		}else{
			pointInLine = new PointInLine();
			pointInLine.fromString(valueSubString);
		}
		return this;
	}

}