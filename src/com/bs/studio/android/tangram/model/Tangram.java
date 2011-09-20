/**
 * 
 */
package com.bs.studio.android.tangram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.util.Log;

import com.bs.studio.android.math.Point;

/**
 * @author LemIST Stores the relative position among pieces that forms a
 *         tangram.
 */
public class Tangram {
	private final static String Tag = "Tangram";
	public Tangram(){
		this.name = "Unknown";
		this.category = "Default";
	}
	/**
	 * calculate the position, and save it
	 */
	public static Tangram createTangram(Piece[] pieces) {
		Tangram tangram = new Tangram();
		tangram.relationships = new Vector<PositionRelationship>();
		tangram.knownPieces = new Vector<Piece>();
		tangram.knownPieces.add(pieces[0]);
		Log.v(Tag, "First known pieces is "
				+ tangram.knownPieces.get(0).getId());

		List<Piece> pieceList = new ArrayList<Piece>();
		for (Piece piece : pieces){
			pieceList.add(piece);
		}
		Collections.sort(pieceList);
		
		PositionRelationshipType relationType = PositionRelationshipType.ByMatchPoint;
		boolean hasNewRelationship = true;
		while (hasNewRelationship && tangram.knownPieces.size() < pieceList.size()) {
			hasNewRelationship = false;
			for (Piece thisPiece : pieceList) {
				// check if this piece has already been known
				boolean isThisPieceKnown = false;
				for (Piece knownPiece : tangram.knownPieces) {
					if (thisPiece == knownPiece) {
						isThisPieceKnown = true;
						break;
					}
				}
				if (isThisPieceKnown)
					continue;
				// find relationship between this piece and known piece
				PositionRelationship relationship = null;
				// find only one relationship
				for (Piece relativeToPiece : tangram.knownPieces) {
					// by match point
					switch (relationType) {
					case ByMatchPoint:
						relationship = PositionRelationship
								.getPositionRelationshipByMatchPoint(thisPiece,
										relativeToPiece);
						break;
					case ByPointOnLine:
						relationship = PositionRelationship
								.getPositionRelationshipByPointOnLine(
										thisPiece, relativeToPiece);
					}
					// find one relationship, then no need to find more
					if (relationship != null)
						break;
				}
				if (relationship != null) {
					hasNewRelationship = true;
					tangram.relationships.add(relationship);
					Log.v(Tag, "New relationship added: " + relationship + ".");
					tangram.knownPieces.add(thisPiece);
					// point on line only need one
					if (relationType == PositionRelationshipType.ByPointOnLine) {
						break;
					}
				}
			}
			// decide which relationship type to find next
			if (hasNewRelationship) {
				// always go back to find next match point.
				relationType = PositionRelationshipType.ByMatchPoint;
			} else {
				if (relationType == PositionRelationshipType.ByMatchPoint) {
					// if failed to find match point, check point on line
					relationType = PositionRelationshipType.ByPointOnLine;
					hasNewRelationship = true;
				} else {
					// failed point on line, no further relationship to be found
					hasNewRelationship = false;
				}
			}
		}

		for (Piece knownPiece : tangram.knownPieces) {
			Log.v(Tag, "\t " + knownPiece.getId());
		}
		if (tangram.knownPieces.size() != pieces.length) {
			Log.v(Tag, "Tangram not created. Known pieces size is "
					+ tangram.knownPieces.size() + ", while "
					+ " all pieces size is " + pieces.length + ".");
			tangram.relationships = null;
			tangram.knownPieces = null;
			return null;
		}

		Log.v(Tag, "Tangram created");

		return tangram;
	}

	private Vector<Piece> knownPieces = null;
	private Vector<PositionRelationship> relationships = null;

	private String name;

	private int id;

	private String category;

	private int difficulty;

	/**
	 * Move all pieces so that they are within the screen and centered.
	 * 
	 * @param pieces
	 * @param screenSize
	 */
	public void centerTheTangram(Piece[] pieces, Point screenSize) {
		// find leftmost, rightmost, toppest, lowest
		float leftmost = Float.MAX_VALUE;
		float rightmost = Float.MIN_VALUE;
		float topest = Float.MAX_VALUE;
		float lowest = Float.MIN_VALUE;

		for (Piece piece : pieces) {
			Point[] physicalPoints = piece.getPhysicalPoints();
			for (Point p : physicalPoints) {
				if (p.x > rightmost)
					rightmost = p.x;
				if (p.x < leftmost)
					leftmost = p.x;
				if (p.y < topest)
					topest = p.y;
				if (p.y > lowest)
					lowest = p.y;
			}
		}

		Point currentCenterPoint = new Point((rightmost + leftmost) / 2,
				(topest + lowest) / 2);
		Point desiredCenterPoint = new Point(screenSize.x / 2, screenSize.y / 2);

		Point offset = new Point(desiredCenterPoint.x - currentCenterPoint.x,
				desiredCenterPoint.y - currentCenterPoint.y);

		for (Piece piece : pieces) {
			piece.moveInScreen(screenSize, offset);
		}
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public boolean restorePieces(Piece[] pieces, Point screenSize) {
		if (relationships == null)
			return false;

		Piece centerPiece = relationships.get(0).getRelativeToPiece(pieces);
		centerPiece.setCenterPoint(0, 0);

		Vector<Piece> knownPieces = new Vector<Piece>();
		knownPieces.add(centerPiece);
		Vector<PositionRelationship> usedRelationships = new Vector<PositionRelationship>();
		Log.v(Tag, "Piece " + centerPiece.getId() + " is first settled.");
		while (knownPieces.size() != pieces.length) {
			for (PositionRelationship relationship : relationships) {
				if (!usedRelationships.contains(relationship)) {
					for (Piece piece : pieces) {
						if (!knownPieces.contains(piece)) {
							boolean restored = relationship.restore(piece,
									knownPieces);
							if (restored) {
								Log.v(Tag, "Piece " + piece.getId()
										+ " is settled.");
								Log.v(Tag, "\tUsed relationship "
										+ relationship);
								Log.v(Tag, "\t" + piece.getId() + ": " + piece);
								Log.v(Tag,
										"\t"
												+ relationship
														.getRelativeToPiece(
																pieces).getId()
												+ ": "
												+ relationship
														.getRelativeToPiece(pieces));
								usedRelationships.add(relationship);
								knownPieces.add(piece);
								break;
							}
						}
					}
				}
			}
		}

		centerTheTangram(pieces, screenSize);

		return true;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @param difficulty
	 *            the difficulty to set
	 */
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(id + ", " + name + ", "
				+ category + ", " + difficulty + ", ");
		string.append("rs(");
		for (PositionRelationship relationship : relationships) {
			string.append(relationship.toString());
			string.append("&");
		}

		string.append(")");
		return string.toString();
	}

	public Tangram fromString(String string) {
		String[] elements = string.split(",");

		this.id = Integer.parseInt(elements[0]);
		this.name = elements[1];
		this.category = elements[1];
		this.difficulty = Integer.parseInt(elements[0]);

		String[] relationshipStrings = string.substring(
				string.indexOf("rs(") + "rs(".length(), string.length() - 1)
				.split("&");

		this.relationships = new Vector<PositionRelationship>();
		for (String relationshipString : relationshipStrings) {
			PositionRelationship relationship = new PositionRelationship();
			Log.v("File", "\t" + relationshipString);
			relationship.fromString(relationshipString);
			Log.v("File", "\t" + relationshipString);
			this.relationships.add(relationship);
		}

		return this;
	}
}
