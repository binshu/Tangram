/**
 * 
 */
package com.bs.studio.android.tangram.views.buttons;

import com.bs.studio.android.tangram.activities.TangramPlayActivity;
import com.bs.studio.android.tangram.activities.TangramSelectionActivity;
import com.bs.studio.android.tangram.model.Tangram;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * @author LemIST
 *
 */
public class TangramSelectionButton extends Button implements OnClickListener{
	private Tangram tangram;

	/**
	 * @param context
	 */
	public TangramSelectionButton(Context context, Tangram tangram) {
		super(context);
		this.tangram = tangram;
		this.setText("Tangram: " + tangram.getName());
		this.setOnClickListener(this);
	}

	
	/**
	 * @param context
	 */
	public TangramSelectionButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public TangramSelectionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public TangramSelectionButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}


	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == this){
			Intent intent = new Intent(this.getContext(), TangramPlayActivity.class);
			intent.putExtra("TangramId", tangram.getId());
			this.getContext().startActivity(intent);
		}
	}

}
