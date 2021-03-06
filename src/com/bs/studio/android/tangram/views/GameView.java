/**
 * 
 */
package com.bs.studio.android.tangram.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bs.studio.android.math.Point;
import com.bs.studio.android.tangram.model.Parallelogram;
import com.bs.studio.android.tangram.model.Piece;
import com.bs.studio.android.tangram.model.PieceId;
import com.bs.studio.android.tangram.model.RightAngledTriangle;
import com.bs.studio.android.tangram.model.Square;
import com.bs.studio.android.tangram.views.draw.PiecePainter;

/**
 * @author LemIST
 * 
 */
public abstract class GameView extends View {

	// / parameters
	private int tao = 1;

	/**
	 * @return the tao
	 */
	public int getTao() {
		return tao;
	}

	/**
	 * @param tao the tao to set
	 */
	public void setTao(int tao) {
		if (this.tao != tao){
			checkTao();
		}
		this.tao = tao;
	}

	private void checkTao() {
		// TODO Auto-generated method stub
		
	}

	protected Point screenSize;

	private float scale = 1;

	private Piece selectedPiece;

	private HashMap<PieceGroup, LinkedList<Piece>> piecesByGroup;
	private LinkedList<Piece> userControlledPieces;

	public GameView(Context context) {
		super(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void drawBackground(Canvas canvas) {

	}

	private void drawMark(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		canvas.drawLine(5, 0, 5, 1000, paint);

		for (int i = 0; i < 1000; i++) {
			canvas.drawLine(5, i * 5, 10, i * 5, paint);
		}

		for (int i = 0; i < 1000; i++) {
			canvas.drawLine(5, i * 10, 15, i * 10, paint);
		}

		for (int i = 0; i < 1000; i++) {
			canvas.drawText((i * 20) + "", 15, i * 20, paint);
		}
	}

	private LinkedList<Piece> getPieceListByGroup(PieceGroup group) {
		if (piecesByGroup == null)
			return null;
		return piecesByGroup.get(group);
	}

	private Piece getPieceByGroupAndId(PieceGroup group, PieceId id) {
		LinkedList<Piece> pieces = getPieceListByGroup(group);
		if (pieces == null)
			return null;
		Iterator<Piece> it = pieces.iterator();
		while (it.hasNext()) {
			Piece piece = it.next();
			if (piece.getId() == id) {
				return piece;
			}
		}
		return null;
	}

	public Piece[] getPiecesByGroup(PieceGroup group) {
		LinkedList<Piece> pieces = getPieceListByGroup(group);
		if (pieces == null)
			return null;

		Piece[] pieces2Return = new Piece[pieces.size()];
		for (int i = 0; i < pieces.size(); i++) {
			pieces2Return[i] = pieces.get(i);
		}
		return pieces2Return;
	}

	private float getScale() {
		return scale;
	}

	public void initialize(Point screenSize) {
		this.screenSize = screenSize;
		scale = screenSize.x / 6f;
		initializePieces();
		resetPiecesPosition();
	}

	private void initializePieces() {
		piecesByGroup = new HashMap<PieceGroup, LinkedList<Piece>>();
		userControlledPieces = new LinkedList<Piece>();
		for (int i = 0; i < tao; i++) {
			for (PieceGroup group : PieceGroup.values()) {
				if (group.getTao() == i && (this.hasTangram() || !group.isTangram())) {
					LinkedList<Piece> pieces = new LinkedList<Piece>();
					
					for (PieceId id : PieceId.values()) {
						Piece piece = Piece.CreatePiece(id, getScale());
						pieces.addLast(piece);
						if (group.isUserControlled())
							userControlledPieces.addLast(piece);
					}

					piecesByGroup.put(group, pieces);
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw marker
		//drawMark(canvas);

		// draw background
		drawBackground(canvas);
	}

	protected void drawPieces(Canvas canvas) {
		// draw pieces
		for (PieceGroup group : PieceGroup.values()) {
			LinkedList<Piece> pieces = piecesByGroup.get(group);
			if (pieces == null)
				continue;
			if (group.isTangram()){
				for (Piece piece : pieces) {
					PiecePainter.drawPiece(canvas, piece, group);
				}
			}
		}
	
		ListIterator<Piece> it = userControlledPieces.listIterator();
		while (it.hasNext())
			it.next();
		while (it.hasPrevious()) {
			Piece piece = it.previous();
			PiecePainter.drawPiece(canvas, piece, PieceGroup.UserControlled_0);
		}
	}

	private Point touchDownPoint = new Point(0, 0);
	private Point moveOffset = new Point(0, 0);

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		Rect rectToInvalidate = new Rect();
		int[] location = new int[2];
		this.getLocationOnScreen(location);
		
		float eventX = event.getRawX() - location[0];
		float eventY = event.getRawY() - location[1];
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// get down point
			touchDownPoint.set(eventX, eventY);
			// pick the selected piece
			selectedPiece = null;
			for (int i = 0; i < userControlledPieces.size(); i++) {
				Piece piece = userControlledPieces.get(i);
				if (piece.containsPoint(touchDownPoint)) {
					selectedPiece = piece;
					userControlledPieces.remove(i);
					userControlledPieces.addFirst(selectedPiece);
					Log.v("Event", "Point is in " + selectedPiece.getId());
					break;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (selectedPiece != null) {
				moveOffset.set(eventX - touchDownPoint.x,
						eventY - touchDownPoint.y);
				rectToInvalidate.set(selectedPiece.getContainingRect());
				selectedPiece.moveInScreen(screenSize, moveOffset);
				rectToInvalidate.union(selectedPiece.getContainingRect());
				touchDownPoint.set(eventX, eventY);
			}
			this.invalidate(rectToInvalidate);
			break;
		case MotionEvent.ACTION_UP:
			if (selectedPiece != null) {
				selectedPiece.autoAdjustPositionTo(
						userControlledPieces.toArray(), screenSize);
				if (event.getEventTime() - event.getDownTime() < 300) {
					rectToInvalidate.set(selectedPiece.getContainingRect());
					selectedPiece.rotateRight(touchDownPoint);
					rectToInvalidate.union(selectedPiece.getContainingRect());
				}
			}
			this.invalidate();
			break;
		}

		

		return true;
	}

	public void resetPiecesPosition() {

		int paddingX = 5;
		int paddingY = 5;

		float screenWidth = screenSize.x;
		float screenPadding = 0;
		float titleSize = 0;

		for (PieceGroup group : piecesByGroup.keySet()) {
			if (group.isTangram())
				continue;
			LinkedList<Piece> pieces = piecesByGroup.get(group);
			float goffset = group.getTao() * 10;
			for (Piece piece : pieces) {
				switch (piece.getId()) {
				case LargeTriangle1:
					RightAngledTriangle tri = (RightAngledTriangle) piece;
					tri.setCenterPoint(paddingX + goffset, screenPadding
							+ paddingY + goffset);
					tri.setRotation(2);
					break;
				case LargeTriangle2:
					tri = (RightAngledTriangle) piece;
					tri.setCenterPoint(paddingX + goffset, screenSize.y
							- screenPadding - paddingY - titleSize + goffset);
					tri.setRotation(0);
					break;
				case MiddleTriangle:
					tri = (RightAngledTriangle) piece;
					tri.setCenterPoint(screenSize.x - paddingX + goffset,
							screenSize.y - screenPadding - paddingY - titleSize
									+ goffset);
					tri.setRotation(6);
					break;
				case Square:
					Square square = (Square) piece;
					square.setCenterPoint(
							screenWidth - paddingX - square.getSize() / 2
									+ goffset, screenPadding + paddingY
									+ square.getSize() / 2 + goffset);

					square.setRotation(0);
					break;
				case SmallTriangle1:
					tri = (RightAngledTriangle) piece;
					tri.setCenterPoint(
							screenWidth - paddingX
									- tri.getRightAngleSideSize() + goffset,
							screenPadding
									+ paddingY
									+ ((Square) getPieceByGroupAndId(
											PieceGroup.UserControlled_0,
											PieceId.Square)).getSize() + 10
									+ goffset);

					tri.setRotation(2);
					break;
				case SmallTriangle2:
					tri = (RightAngledTriangle) piece;
					tri.setCenterPoint(
							screenWidth - paddingX + goffset,
							screenPadding
									+ paddingY
									+ ((Square) getPieceByGroupAndId(
											PieceGroup.UserControlled_0,
											PieceId.Square)).getSize()
									+ tri.getRightAngleSideSize() + 10
									+ goffset);
					tri.setRotation(6);
					break;
				case Parallelogram:
					Parallelogram parallel = (Parallelogram) piece;
					parallel.setCenterPoint(
							screenSize.x - paddingX - 15 + goffset,
							screenPadding
									+ paddingY
									+ 30
									+ ((Square) getPieceByGroupAndId(
											PieceGroup.UserControlled_0,
											PieceId.Square)).getSize() * 2
									+ parallel.getHeight() + goffset);
					parallel.setRotation(1);
					break;
				}
			}
		}
		this.invalidate();
	}
	
	protected abstract boolean hasTangram();
}
