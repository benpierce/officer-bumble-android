package com.example.officerbumble.engine;

import java.util.List;
import java.util.UUID;

public class Utility {
	public static String GetNumericSuffix(int _number) {
		String result = "";
		String number = String.valueOf(_number);
		int lastNumber = Integer.parseInt(number.substring(number.length() - 1));

        if (_number >= 11 && _number <= 19 ) {
            result = "th";
        } else {
            switch (lastNumber) {
                case 0:
                    result = "th";
                    break;
                case 1:
                    result = "st";
                    break;
                case 2:
                    result = "nd";
                    break;
                case 3:
                    result = "rd";
                    break;
                case 4:
                    result = "th";
                    break;
                case 5:
                    result = "th";
                    break;
                case 6:
                    result = "th";
                    break;
                case 7:
                    result = "th";
                    break;
                case 8:
                    result = "th";
                    break;
                case 9:
                    result = "th";
                    break;
            }
        }
		
		return result;
	}
	
	public static int[] ConvertListToArray(List<Integer> _list) {
		int[] ret = new int[_list.size()];
	    
		for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = _list.get(i).intValue();
	    }
	    
	    return ret;
	}

    public static String NewId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
