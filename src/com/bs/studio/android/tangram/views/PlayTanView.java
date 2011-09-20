/**
 * 
 */
package com.bs.studio.android.tangram.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.bs.studio.android.tangram.model.Piece;
import com.bs.studio.android.tangram.model.Tangram;
import com.bs.studio.android.tangram.views.draw.PiecePainter;

/**
 * @author LemIST
 *
 */
public class PlayTanView extends GameView {

	private Tangram currentTangramInPlaying = null;
	
	/**
	 * @param context
	 */
	public PlayTanView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PlayTanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setTangram(Tangram tangramToPlay){
		currentTangramInPlaying = tangramToPlay;
		if (tangramToPlay == null)
			return ;
		currentTangramInPlaying.restorePieces(this.getPiecesByGroup(PieceGroup.Tangram_0), screenSize);
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas){
		// first draw the tangram
		super.onDraw(canvas);
		drawTangram(canvas);
		super.drawPieces(canvas);
	}
	
	private void drawTangram(Canvas canvas){
		for (Piece piece : this.getPiecesByGroup(PieceGroup.Tangram_0)){
			PiecePainter.drawPiece(canvas, piece, PieceGroup.Tangram_0);
		}
	}

	@Override
	protected boolean hasTangram() {
		// TODO Auto-generated method stub
		return true;
	}
}
