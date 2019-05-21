package T145.crankshaft.lib;

import java.util.ArrayDeque;

import T145.crankshaft.api.ICustomPiston;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonLogic {

	private final World world;
	private final BlockPos from;
	private final Direction moving;
	private final Direction front;
	private final int length;
	private final int movableBlocks;
	private final ArrayDeque<BlockPos> affectedBlocks = new ArrayDeque<>();

	// use dynamic programming to handle slime block cases
	private final ArrayDeque<BlockPos> slimeBlocks = new ArrayDeque<>();

	public PistonLogic(World world, BlockPos from, Direction front, int length, boolean extended, int movableBlocks) {
		this.world = world;
		this.from = from;
		this.front = front;
		this.movableBlocks = movableBlocks;

		if (length > movableBlocks) {
			this.length = movableBlocks;
		} else {
			this.length = length;
		}

		if (extended) {
			this.moving = front.getOpposite();
		} else {
			this.moving = front;
		}
	}

	public PistonLogic(World world, BlockPos from, Direction front, int length, boolean extending) {
		this(world, from, front, length, extending, 12);
	}

	public PistonLogic(World world, BlockPos from, Direction front, boolean extending, int movableBlocks) {
		this(world, from, front, 1, extending, movableBlocks);
	}

	public PistonLogic(World world, BlockPos from, Direction front, boolean extending) {
		this(world, from, front, 1, extending);
	}

	public PistonLogic(World world, BlockPos from, Direction front) {
		this(world, from, front, 1, false);
	}

	public ArrayDeque<BlockPos> getAffectedBlocks() {
		return affectedBlocks;
	}

	private static boolean hasDestroyMode(World world, BlockPos from) {
		BlockState fromState = world.getBlockState(from);
		Block fromBlock = fromState.getBlock();

		if (fromBlock instanceof ICustomPiston) {
			ICustomPiston piston = (ICustomPiston) fromBlock;

			if (piston.inDestroyMode()) {
				return true;
			}
		}

		return false;
	}

	// using my own "isMovable" method so we can customize how the pistons behave
	// it's also more clean
	public static boolean canPistonPush(World world, BlockPos from, BlockPos to, Direction moving, Direction front) {
		BlockState destState = world.getBlockState(to);
		Block destBlock = destState.getBlock();

		if (destState.getHardness(world, to) >= Blocks.OBSIDIAN.getHardness(destState, world, to)
				|| !world.getWorldBorder().contains(to)
				|| to.getY() < 0
				|| moving == Direction.DOWN && to.getY() == 0) {
			return false;
		} else if (to.getY() <= world.getHeight() - 1
				&& (moving != Direction.UP || to.getY() != world.getHeight() - 1)) {
			if (destBlock instanceof PistonBlock && destState.get(PistonBlock.EXTENDED)) {
				return false;
			} else {
				if (hasDestroyMode(world, from)) {
					return true;
				}

				if (destState.getHardness(world, to) == -1.0F) {
					return false;
				}

				switch(destState.getPistonBehavior()) {
				case BLOCK:
					return false;
				case DESTROY:
					return true;
				case PUSH_ONLY:
					return moving == front;
				default:
					return !destBlock.hasBlockEntity();
				}
			}
		}

		return false;
	}

	public boolean canPistonExtend() {
		this.affectedBlocks.clear();
		this.slimeBlocks.clear();

		short i = 1;
		BlockPos dest = from.offset(front);

		if (hasDestroyMode(world, from)) {
			for (; i < length && canPistonPush(world, from, dest, moving, front); ++i) {
				this.affectedBlocks.add(dest = from.offset(front, i));
			}
			return true;
		}

		// if there's enough space between the range up to double the piston's length
		// (including blocks w/ the piston behavior destroy)
		// and it can push all of the blocks leading into this space,
		// then the piston can extend

		// affectedBlocks.size() is always >= length and <= movableBlocks

		// first we need to find out how big this gap is
		// (and handle slime block cases)
		for (i = 1, dest = from.offset(front); i < length; ++i) {
			if (!canPistonPush(world, from, dest = from.offset(front, i), moving, front)) {
				return false;
			}

			BlockState destState = world.getBlockState(dest);

			if (destState.getBlock() instanceof SlimeBlock) {
				slimeBlocks.add(dest);
				// compute into push count later
			}

			this.affectedBlocks.add(dest);
		}

		while (!this.slimeBlocks.isEmpty() && this.affectedBlocks.size() <= movableBlocks) {
			dest = this.slimeBlocks.poll();

			for (Direction dir : moving.getAxis().getType()) {
				BlockPos offPos = dest.offset(dir);

				if (!canPistonPush(world, from, offPos, moving, front)) {
					return false;
				}

				BlockState offState = world.getBlockState(offPos);

				if (offState.getBlock() instanceof SlimeBlock) {
					slimeBlocks.addFirst(dest);
				}
			}
		}

		return true;
	}

	// should be called after `canPistonExtend()`
	public void pushBlocks() {

	}
}
