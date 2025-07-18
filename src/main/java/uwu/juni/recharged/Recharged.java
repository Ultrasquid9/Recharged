package uwu.juni.recharged;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import uwu.juni.recharged.content.RechargedBlockEntities;
import uwu.juni.recharged.content.RechargedBlocks;
import uwu.juni.recharged.content.RechargedItems;

@Mod(Recharged.MODID)
public class Recharged {
	public static final String MODID = "recharged";
	public static final Logger LOGGER = LogUtils.getLogger();

	static final DeferredRegister<?>[] REGISTERS = {
		RechargedBlockEntities.REGISTER,
		RechargedBlocks.REGISTER,
		RechargedItems.REGISTER,
	};

	public Recharged(IEventBus bussin, ModContainer modContainer) {
		for (var register : REGISTERS) {
			register.register(bussin);
		}

		modContainer.registerConfig(ModConfig.Type.COMMON, RechargedConfig.SPEC);
	}

	public static ResourceLocation rLoc(String str) {
		return ResourceLocation.fromNamespaceAndPath(MODID, str);
	}
}
