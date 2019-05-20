package T145.crankshaft.config;

import T145.crankshaft.api.constants.RegistryCS;
import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;

@Config(name = "T145/" + RegistryCS.ID)
@Config.Gui.Background(RegistryCS.ID + ":icon.png")
public class ModConfig implements ConfigData {

	@ConfigEntry.Gui.PrefixText
	public final boolean debug = false;
}