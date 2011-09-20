package com.bs.studio.android.math;


public class Angle {
	/**
	 * Angle <AMB
	 * @param M
	 * @param A
	 * @param B
	 */
	public static double getValue(Point M, Point A, Point B){
		double ma_x = A.x - M.x;
		double ma_y = A.y - M.y;
		double mb_x = B.x - M.x;
		double mb_y = B.y - M.y;
		double v1 = (ma_x * mb_x) + (ma_y * mb_y);
		double ma_val = Math.sqrt(ma_x * ma_x + ma_y * ma_y);
		double mb_val = Math.sqrt(mb_x * mb_x + mb_y * mb_y);
		double cosM = v1 / (ma_val * mb_val);
		return Math.acos(cosM);
	}
}
