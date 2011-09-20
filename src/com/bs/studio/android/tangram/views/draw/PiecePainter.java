/**
 * 
 */
package com.bs.studio.android.tangram.views.draw;

import android.graphics.Canvas;
import android.graphics.Path;

import com.bs.studio.android.math.Point;
import com.bs.studio.android.tangram.model.Piece;
import com.bs.studio.android.tangram.views.PieceGroup;

/**
 * @author LemIST
 *
 */
public class PiecePainter {
	public static void drawPiece(Canvas canvas, Piece piece, PieceGroup group){
		Point[] points = piece.getPhysicalPoints();
		Path dpath = new Path();

		dpath.moveTo(points[0].x, points[0].y);
		for (int i = 1; i <= points.length; i++) {
			dpath.lineTo(points[i % points.length].x,
					points[i % points.length].y);
		}
		canvas.drawPath(dpath, Paints.getPaint(piece.getId(), group));
	}
}
