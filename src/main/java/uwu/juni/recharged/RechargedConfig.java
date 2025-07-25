package uwu.juni.recharged;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class RechargedConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	public static final ModConfigSpec.BooleanValue DISABLE_QUASI_CONNECTIVITY = BUILDER
		.comment(" Whether or not Quasi Connectivity should be disabled")
		.define("disable_quasi_connectivity", true);

	public static final ModConfigSpec.BooleanValue FIX_PORTAL_BREAK = BUILDER
		.comment(" Whether or not methods to break indestructible blocks (like portals) should be patched")
		.define("fix_portal_break", true);

	public static final ModConfigSpec.BooleanValue NON_POSITIONAL_REDSTONE = BUILDER
		.comment(" Whether redstone wire update order should be independant of position")
		.define("non_positional_redstone", true);

	public static final ModConfigSpec.BooleanValue CHAINSTONE = BUILDER
		.comment(" Whether chains should be sticky in the axis that they're facing")
		.define("chainstone", true);

	public static final ModConfigSpec.BooleanValue PISTONS_BREAK_CARPETS = BUILDER
		.comment(" Whether or not pistons should be able to break carpets")
		.define("pistons_break_carpets", true);

	public static final ModConfigSpec.BooleanValue BONEMEAL_SMALL_CORAL = BUILDER
		.comment(" Whether you should be able to bonemeal small coral into large coral")
		.define("bonemeal_small_coral", true);

	public static final ModConfigSpec.BooleanValue ERODE_DEAD_CORAL = BUILDER
		.comment(" Whether dead coral should erode into sand when in flowing water")
		.define("erode_dead_coral", true);

	public static final ModConfigSpec SPEC = BUILDER.build();

	public static <T> T getConfigValue(ConfigValue<T> value) {
		T val;

		try {
			val = value.get();
		} catch (Exception e) {
			val = value.getDefault();
		}

		return val;
	}
}
