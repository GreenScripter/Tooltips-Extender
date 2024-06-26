package greenscripter.tooltips;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnchantmentLabeller {

	public static Map<String, String> names = new HashMap<>();
	public static Map<String, Integer> levels = new HashMap<>();
	public static Set<String> curses = new HashSet<>();
	static {
		names.put("protection", "PR");
		levels.put("protection", 4);
		names.put("fire_protection", "FP");
		levels.put("fire_protection", 4);
		names.put("feather_falling", "FF");
		levels.put("feather_falling", 4);
		names.put("blast_protection", "BP");
		levels.put("blast_protection", 4);
		names.put("projectile_protection", "PP");
		levels.put("projectile_protection", 4);
		names.put("respiration", "RP");
		levels.put("respiration", 3);
		names.put("aqua_affinity", "AQ");
		levels.put("aqua_affinity", 1);
		names.put("thorns", "TH");
		levels.put("thorns", 3);
		names.put("depth_strider", "DS");
		levels.put("depth_strider", 3);
		names.put("frost_walker", "FR");
		levels.put("frost_walker", 2);
		names.put("binding_curse", "BC");
		levels.put("binding_curse", 1);
		names.put("soul_speed", "SO");
		levels.put("soul_speed", 3);
		names.put("swift_sneak", "SS");
		levels.put("swift_sneak", 3);
		names.put("sharpness", "SH");
		levels.put("sharpness", 5);
		names.put("smite", "SZ");
		levels.put("smite", 5);
		names.put("bane_of_arthropods", "SA");
		levels.put("bane_of_arthropods", 5);
		names.put("knockback", "KN");
		levels.put("knockback", 2);
		names.put("fire_aspect", "FA");
		levels.put("fire_aspect", 2);
		names.put("looting", "LT");
		levels.put("looting", 3);
		names.put("sweeping", "SW");
		levels.put("sweeping", 3);
		names.put("efficiency", "EF");
		levels.put("efficiency", 5);
		names.put("silk_touch", "ST");
		levels.put("silk_touch", 1);
		names.put("unbreaking", "UB");
		levels.put("unbreaking", 3);
		names.put("fortune", "FT");
		levels.put("fortune", 3);
		names.put("power", "PW");
		levels.put("power", 5);
		names.put("punch", "PU");
		levels.put("punch", 2);
		names.put("flame", "FM");
		levels.put("flame", 1);
		names.put("infinity", "IF");
		levels.put("infinity", 1);
		names.put("luck_of_the_sea", "LK");
		levels.put("luck_of_the_sea", 3);
		names.put("lure", "LU");
		levels.put("lure", 3);
		names.put("loyalty", "LY");
		levels.put("loyalty", 3);
		names.put("impaling", "IP");
		levels.put("impaling", 5);
		names.put("riptide", "RP");
		levels.put("riptide", 3);
		names.put("channeling", "CH");
		levels.put("channeling", 1);
		names.put("multishot", "MU");
		levels.put("multishot", 1);
		names.put("quick_charge", "QC");
		levels.put("quick_charge", 3);
		names.put("piercing", "PI");
		levels.put("piercing", 4);
		names.put("mending", "MN");
		levels.put("mending", 1);
		names.put("vanishing_curse", "VC");
		levels.put("vanishing_curse", 1);
		
		curses.add("vanishing_curse");
		curses.add("binding_curse");
	}
}
