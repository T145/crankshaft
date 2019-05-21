package T145.crankshaft.blocks;

import T145.crankshaft.api.ICustomPiston;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PistonBlock;

public class CustomPistonBlock extends PistonBlock implements ICustomPiston {

	private int length = 1;

	public CustomPistonBlock(boolean sticky) {
		super(sticky, Block.Settings.of(Material.PISTON).strength(0.5F, 0.5F));
	}

	public CustomPistonBlock(boolean sticky, int length) {
		this(sticky);
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	@Override
	public boolean inDestroyMode() {
		return false;
	}
}
