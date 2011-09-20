/**
 * 
 */
package com.bs.studio.android.tangram.activities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

import com.bs.studio.android.tangram.model.Tangram;

/**
 * @author LemIST
 * 
 */
public class TangramManager {

	private final String TANGRAM_FILE = "tangram";

	private Context context;

	private static TangramManager manager;
	private static int maxId = 0;

	public static TangramManager createTamgramManager(Context context) {
		if (manager == null || manager.context == null) {
			manager = new TangramManager();
			manager.context = context;
		}
		return manager;
	}

	private Vector<Tangram> tangrams = null;
	private HashMap<String, Vector<Tangram>> tangramsByCategory = null;

	private TangramManager() {

	}

	public Tangram getTangramById(int id) {
		getTangrams();
		for (Tangram tangram : tangrams) {
			if (tangram.getId() == id)
				return tangram;
		}
		return null;
	}

	public Vector<Tangram> getTangramsByCategory(String category) {
		getTangrams();
		Vector<Tangram> tangrams = tangramsByCategory.get(category);
		return tangrams;
	}

	public String[] getTangramCategories() {
		getTangrams();
		return tangramsByCategory.keySet().toArray(
				new String[tangramsByCategory.size()]);
	}

	public Tangram getTangramByIndex(int index) {
		getTangrams();
		if (index < tangrams.size())
			return tangrams.get(index);
		else
			return null;
	}

	public Tangram getTangramByName(String name) {
		getTangrams();
		for (Tangram tangram : tangrams) {
			if (tangram.getName().equals(name))
				return tangram;
		}
		return null;
	}

	private Vector<Tangram> getTangrams() {
		if (tangrams == null)
			loadTangrams();
		return tangrams;
	}

	/**
	 * Either load local tangrams or from server NOTE: possible requirement of
	 * large memory
	 */
	private void loadTangrams() {
		tangrams = new Vector<Tangram>();
		tangramsByCategory = new HashMap<String, Vector<Tangram>>();
		try {
			FileInputStream fin = context.openFileInput(TANGRAM_FILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fin));
			String tangramString;
			try {
				while ((tangramString = reader.readLine()) != null) {
					Log.v("File", tangramString);
					Tangram loadedTangram = (new Tangram()
							.fromString(tangramString));
					addTangram(loadedTangram);
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Either save to file or save to database
	 * 
	 * @param tangram
	 */
	public void persistTangram(Tangram tangram) {
		try {
			FileOutputStream fos = context.openFileOutput(TANGRAM_FILE,
					Context.MODE_PRIVATE | Context.MODE_APPEND);
			PrintStream writer = new PrintStream(fos);
			writer.println(tangram.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void addTangram(Tangram tangram){
		getTangrams();
		tangrams.add(tangram);
		if (tangram.getId() > maxId)
			maxId = tangram.getId();
		else if (tangram.getId() == 0)
			tangram.setId(++maxId);
		Vector<Tangram> tangramCategory = tangramsByCategory
				.get(tangram.getCategory());
		if (tangramCategory == null) {
			tangramCategory = new Vector<Tangram>();
			tangramsByCategory.put(tangram.getCategory(),
					tangramCategory);
		}
		tangramCategory.add(tangram);
		
	}
	public void saveAndPersistTangram(Tangram tangram) {
		addTangram(tangram);
		persistTangram(tangram);
	}

	/**
	 * upload all to server
	 * 
	 * @param tangram
	 */
	public void uploadTangram(Tangram tangram) {

	}

}
