package com.example.officerbumble.gameentities;

import java.util.HashMap;

public class CartoonFont {

	private static final HashMap<String, FontProperty> fontProperties = new HashMap<String, FontProperty>();
	
	static {
		fontProperties.put("A", new FontProperty("A", "LETTER_A_CARTOON", 0, 38));
		fontProperties.put("B", new FontProperty("B", "LETTER_B_CARTOON", 0, 33));
		fontProperties.put("C", new FontProperty("C", "LETTER_C_CARTOON", 0, 39));
		fontProperties.put("D", new FontProperty("D", "LETTER_D_CARTOON", 0, 39));
		fontProperties.put("E", new FontProperty("E", "LETTER_E_CARTOON", 0, 29));
		fontProperties.put("F", new FontProperty("F", "LETTER_F_CARTOON", 0, 26));
		fontProperties.put("G", new FontProperty("G", "LETTER_G_CARTOON", 0, 41));
		fontProperties.put("H", new FontProperty("H", "LETTER_H_CARTOON", 0, 35));
		fontProperties.put("I", new FontProperty("I", "LETTER_I_CARTOON", 0, 13));
		fontProperties.put("J", new FontProperty("J", "LETTER_J_CARTOON", 0, 28));
	
		fontProperties.put("K", new FontProperty("K", "LETTER_K_CARTOON", 0, 34));
		fontProperties.put("L", new FontProperty("L", "LETTER_L_CARTOON", 0, 24));
		fontProperties.put("M", new FontProperty("M", "LETTER_M_CARTOON", 0, 45));
		fontProperties.put("N", new FontProperty("N", "LETTER_N_CARTOON", 0, 34));
		fontProperties.put("O", new FontProperty("O", "LETTER_O_CARTOON", 0, 41));
		fontProperties.put("P", new FontProperty("P", "LETTER_P_CARTOON", 0, 30));
		fontProperties.put("Q", new FontProperty("Q", "LETTER_Q_CARTOON", 0, 41));
		fontProperties.put("R", new FontProperty("R", "LETTER_R_CARTOON", 0, 32));
		fontProperties.put("S", new FontProperty("S", "LETTER_S_CARTOON", 0, 36));
		fontProperties.put("T", new FontProperty("T", "LETTER_T_CARTOON", 0, 34));
		fontProperties.put("U", new FontProperty("U", "LETTER_U_CARTOON", 0, 34));
		fontProperties.put("V", new FontProperty("V", "LETTER_V_CARTOON", 0, 35));
		fontProperties.put("W", new FontProperty("W", "LETTER_W_CARTOON", 0, 49));
		fontProperties.put("X", new FontProperty("X", "LETTER_X_CARTOON", 0, 36));
		fontProperties.put("Y", new FontProperty("Y", "LETTER_Y_CARTOON", 0, 36));
		fontProperties.put("Z", new FontProperty("Z", "LETTER_Z_CARTOON", 0, 37));
		
		fontProperties.put("a", new FontProperty("a", "LETTER_a_CARTOON", 0, 25));
		fontProperties.put("b", new FontProperty("b", "LETTER_b_CARTOON", 0, 27));
		fontProperties.put("c", new FontProperty("c", "LETTER_c_CARTOON", 0, 26));
		fontProperties.put("d", new FontProperty("d", "LETTER_d_CARTOON", 0, 28));
		fontProperties.put("e", new FontProperty("e", "LETTER_e_CARTOON", 0, 29));
		fontProperties.put("f", new FontProperty("f", "LETTER_f_CARTOON", 0, 20));
		fontProperties.put("g", new FontProperty("g", "LETTER_g_CARTOON", 0, 28));
		fontProperties.put("h", new FontProperty("h", "LETTER_h_CARTOON", 0, 25));
		fontProperties.put("i", new FontProperty("i", "LETTER_i_CARTOON", 0, 9));
		fontProperties.put("j", new FontProperty("j", "LETTER_j_CARTOON", -7, 24));
		fontProperties.put("k", new FontProperty("k", "LETTER_k_CARTOON", 0, 26));
		fontProperties.put("l", new FontProperty("l", "LETTER_l_CARTOON", 0, 10));
		fontProperties.put("m", new FontProperty("m", "LETTER_m_CARTOON", 0, 38));
		fontProperties.put("n", new FontProperty("n", "LETTER_n_CARTOON", 0, 24));
		fontProperties.put("o", new FontProperty("o", "LETTER_o_CARTOON", 0, 28));
		fontProperties.put("p", new FontProperty("p", "LETTER_p_CARTOON", 0, 27));
		fontProperties.put("q", new FontProperty("q", "LETTER_q_CARTOON", 0, 29));
		fontProperties.put("r", new FontProperty("r", "LETTER_r_CARTOON", 0, 20));
		fontProperties.put("s", new FontProperty("s", "LETTER_s_CARTOON", 0, 25));
		fontProperties.put("t", new FontProperty("t", "LETTER_t_CARTOON", 0, 23));
		fontProperties.put("u", new FontProperty("u", "LETTER_u_CARTOON", 0, 25));
		fontProperties.put("v", new FontProperty("v", "LETTER_v_CARTOON", 0, 27));
		fontProperties.put("w", new FontProperty("w", "LETTER_w_CARTOON", 0, 37));
		fontProperties.put("x", new FontProperty("x", "LETTER_x_CARTOON", 0, 28));
		fontProperties.put("y", new FontProperty("y", "LETTER_y_CARTOON", 0, 26));
		fontProperties.put("z", new FontProperty("z", "LETTER_z_CARTOON", 0, 25));
		
		fontProperties.put(" ", new FontProperty(" ", "LETTER_ _CARTOON", 0, 17));
		fontProperties.put("!", new FontProperty("!", "LETTER_!_CARTOON", 0, 11));
		fontProperties.put("&", new FontProperty("&", "LETTER_&_CARTOON", 0, 41));
		fontProperties.put(";", new FontProperty(";", "LETTER_;_CARTOON", 0, 13));
		fontProperties.put(":", new FontProperty(":", "LETTER_:_CARTOON", 0, 11));
		fontProperties.put("\"", new FontProperty("\"", "LETTER_\"_CARTOON", 0, 17));
		fontProperties.put("'", new FontProperty("'", "LETTER_'_CARTOON", 0, 7));
		fontProperties.put(",", new FontProperty(",", "LETTER_,_CARTOON", 0, 13));
		fontProperties.put(".", new FontProperty(".", "LETTER_._CARTOON", 0, 12));
		fontProperties.put("?", new FontProperty("?", "LETTER_?_CARTOON", 0, 29));
		fontProperties.put("\\", new FontProperty("\\", "LETTER_\\_CARTOON", 0, 29));
		fontProperties.put("/", new FontProperty("/", "LETTER_/_CARTOON", 0, 29));			
		
		fontProperties.put("1", new FontProperty("1", "LETTER_1_CARTOON", 0, 24));
		fontProperties.put("2", new FontProperty("2", "LETTER_2_CARTOON", 0, 34));
		fontProperties.put("3", new FontProperty("3", "LETTER_3_CARTOON", 0, 34));
		fontProperties.put("4", new FontProperty("4", "LETTER_4_CARTOON", 0, 34));
		fontProperties.put("5", new FontProperty("5", "LETTER_5_CARTOON", 0, 34));
		fontProperties.put("6", new FontProperty("6", "LETTER_6_CARTOON", 0, 34));
		fontProperties.put("7", new FontProperty("7", "LETTER_7_CARTOON", 0, 34));
		fontProperties.put("8", new FontProperty("8", "LETTER_8_CARTOON", 0, 34));
		fontProperties.put("9", new FontProperty("9", "LETTER_9_CARTOON", 0, 34));
		fontProperties.put("0", new FontProperty("0", "LETTER_0_CARTOON", 0, 34));
	}	
	
	public static FontProperty GetFontProperty(String _character) {
		return fontProperties.get(_character);
	}
	
}
