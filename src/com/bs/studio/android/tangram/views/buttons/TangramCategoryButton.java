/**
 * 
 */
package com.bs.studio.android.tangram.views.buttons;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

/**
 * @author LemIST
 *
 */
public class TangramCategoryButton extends Button {
	private String name;
	
	public TangramCategoryButton(Context context, String name){
		super(context);
		this.name = name;
		this.setText("Category: " + name);
	}
	
	/**
	 * @param context
	 */
	public TangramCategoryButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TangramCategoryButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	

}
