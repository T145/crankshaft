package T145.crankshaft.client.init;

import java.util.function.Function;

import T145.crankshaft.api.constants.RegistryCS;
import T145.crankshaft.config.ModConfig;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuInit implements ModMenuApi {

	@Override
	public String getModId() {
		return RegistryCS.ID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> AutoConfig.getConfigScreen(ModConfig.class, screen).get();
	}
}