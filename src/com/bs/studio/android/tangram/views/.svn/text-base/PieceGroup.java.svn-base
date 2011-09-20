package com.bs.studio.android.tangram.views;

public enum PieceGroup {
	/**
	 * Sequence of this enumeration determines which group will be drawn first
	 */
	Tangram_0,
	Tangram_2,
	UserControlled_0,
	UserControlled_2;
	
	public boolean isTangram(){
		return this.toString().startsWith("Tangram");
	}
	
	public boolean isUserControlled(){
		return this.toString().startsWith("UserControlled");
	}
	
	public int getTao(){
		String value = this.toString();
		return Integer.parseInt(value.substring(value.indexOf("_") + 1));
	}
}
