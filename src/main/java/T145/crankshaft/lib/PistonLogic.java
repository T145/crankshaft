package T145.crankshaft.lib;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

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
	private final BlockPos to;
	private final Direction moving;
	private final Direction front;
	private final int length;
	private final int movableBlocks;
	private final List<BlockPos> affectedBlocks = new ArrayList<>();

	// use dynamic programming to handle slime block cases
	private final ArrayDeque<BlockPos> slimeBlocks = new ArrayDeque<>();

	public PistonLogic(World world, BlockPos from, Direction front, int length, boolean extending, int movableBlocks) {
		this.world = world;
		this.from = from;
		this.front = front;
		this.length = length;
		this.movableBlocks = movableBlocks;

		if (extending) {
			this.moving = front;
			this.to = from.offset(moving, length);
		} else {
			this.moving = front.getOpposite();
			this.to = from.offset(moving, length + 2);
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

	public List<BlockPos> getAffectedBlocks() {
		return affectedBlocks;
	}

	// using my own "isMovable" method so we can customize how the pistons behave
	// it's also more clean
	public static boolean canPistonPush(World world, BlockPos to, Direction moving, Direction front) {
		BlockState destState = world.getBlockState(to);
		Block destBlock = destState.getBlock();

		if (destBlock == Blocks.OBSIDIAN
				|| !world.getWorldBorder().contains(to)
				|| to.getY() < 0
				|| moving == Direction.DOWN && to.getY() == 0) {
			return false;
		} else if (to.getY() <= world.getHeight() - 1
				&& (moving != Direction.UP || to.getY() != world.getHeight() - 1)) {
			if (destBlock instanceof PistonBlock && destState.get(PistonBlock.EXTENDED)) {
				return false;
			} else {
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
		} else {
			return false;
		}
	}

	private void affectBlockPos(BlockPos pos) {
		this.affectedBlocks.add(pos);

		if (world.getBlockState(pos).getBlock() instanceof SlimeBlock) {
			this.slimeBlocks.add(pos);
		}
	}

	public boolean canPistonExtend() {
		this.affectedBlocks.clear();

		short i = 0;
		BlockPos pos = from.offset(front);
		boolean canPushNeighbor = canPistonPush(world, pos, moving, front);

		if (canPushNeighbor) {
			this.affectBlockPos(pos);

			if (length > 1) {
				for (i = 2; i < length && i < movableBlocks; ++i) {
					pos = from.offset(front, i);

					if (!canPistonPush(world, pos, moving, front)) {
						return false;
					}

					this.affectBlockPos(pos);
				}
			}
		}

		Direction.Type slimePlane = moving.getAxis().getType() == Direction.Type.HORIZONTAL ? Direction.Type.VERTICAL : Direction.Type.HORIZONTAL;

		while (!slimeBlocks.isEmpty()) {
			pos = slimeBlocks.poll();

			for (Direction dir : slimePlane) {
				BlockPos slimeNeighbor = pos.offset(dir);

				if (!canPistonPush(world, slimeNeighbor, moving, front)) {
					return false;
				}

				if (world.getBlockState(slimeNeighbor).getBlock() instanceof SlimeBlock) {
					slimeBlocks.add(slimeNeighbor);
				}
			}
		}

		return true;
	}
}
