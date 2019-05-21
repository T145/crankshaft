package T145.crankshaft.init;

import T145.crankshaft.api.constants.RegistryCS;
import T145.crankshaft.blocks.CustomPistonBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModInit implements ModInitializer {

	public static ItemGroup GROUP;

	public static final CustomPistonBlock DOUBLE_PISTON = new CustomPistonBlock(false, 2);

	private void registerBlock(Block block, String name) {
		Identifier id = RegistryCS.getIdentifier(name);

		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().itemGroup(GROUP)));
	}

	@Override
	public void onInitialize() {
		GROUP = FabricItemGroupBuilder.create(RegistryCS.getIdentifier("items")).icon(() -> new ItemStack(DOUBLE_PISTON)).build();
		registerBlock(DOUBLE_PISTON, "double_piston");
	}
}
