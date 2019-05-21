package T145.crankshaft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PistonBlock;

public class ResizablePistonBlock extends PistonBlock {

	private int length = 1;

	public ResizablePistonBlock(boolean sticky) {
		super(sticky, Block.Settings.of(Material.PISTON).strength(0.5F, 0.5F));
	}

	public ResizablePistonBlock(boolean sticky, int length) {
		this(sticky);
		this.length = length;
	}

	public int getLength() {
		return length;
	}
}
