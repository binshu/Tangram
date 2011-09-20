/**
 * 
 */
package com.bs.studio.android.tangram.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.bs.studio.android.tangram.R;

/**
 * @author LemIST
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private Button btnToCreate = null;
	private Button btnToPlay = null;
	private Button btnToHelp = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.main);
		
		btnToCreate = (Button)findViewById(R.id.btnToCreate);
		btnToPlay = (Button)findViewById(R.id.btnToPlay);
		btnToHelp = (Button)findViewById(R.id.btnToHelp);
		
		btnToCreate.setOnClickListener(this);
		btnToPlay.setOnClickListener(this);
		btnToHelp.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent = null;
		switch(v.getId()){
		case R.id.btnToCreate:
			intent = new Intent(this, TangramCreationActivity.class);
			startActivity(intent);
			break;
		case R.id.btnToHelp:
			break;
		case R.id.btnToPlay:
			intent = new Intent(this, TangramSelectionActivity.class);
			startActivity(intent);
			break;
		}
	}
}
