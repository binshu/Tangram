/**
 * 
 */
package com.bs.studio.android.tangram.activities;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.bs.studio.android.tangram.R;
import com.bs.studio.android.tangram.model.Tangram;
import com.bs.studio.android.tangram.views.buttons.TangramCategoryButton;
import com.bs.studio.android.tangram.views.buttons.TangramSelectionButton;

/**
 * @author LemIST
 *
 */
public class TangramSelectionActivity extends Activity {
	private TangramManager tangramManager = null;
	
	private ViewGroup container; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.tangramselection);
		
		container = (ViewGroup)findViewById(R.id.tangramSelectionsContatiner);
		tangramManager = TangramManager.createTamgramManager(this);
		
		createSelectionsView();
	}
	
	public void createSelectionsView(){
		String[] categories = tangramManager.getTangramCategories();
		for (String category : categories){
			createSelectionViewForCategory(category);
		}
	}
	
	public void createSelectionViewForCategory(String category){
		container.addView(new TangramCategoryButton(this, category));
		Vector<Tangram> tangrams = tangramManager.getTangramsByCategory(category);
		for (Tangram tangram : tangrams){
			container.addView(new TangramSelectionButton(this, tangram));
		}
	}
	
}
