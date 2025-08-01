package uwu.juni.recharged.content.blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import uwu.juni.recharged.content.blocks.block_entities.ResistorBlockEntity;

@ParametersAreNonnullByDefault
public class ResistorBlock extends DiodeBlock implements EntityBlock, SimpleWaterloggedBlock {
	public static final MapCodec<ResistorBlock> CODEC = simpleCodec(ResistorBlock::new);
	public static final IntegerProperty RESISTANCE = IntegerProperty.create("resistance", 0, 15);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	@Override
	protected MapCodec<? extends ResistorBlock> codec() {
		return CODEC;
	}

	public ResistorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
			.setValue(RESISTANCE, 0)	
			.setValue(POWERED, Boolean.FALSE)
			.setValue(WATERLOGGED, Boolean.FALSE)
		);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, RESISTANCE, WATERLOGGED);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ResistorBlockEntity(pos, state);
	}

	@Override
	protected int getDelay(BlockState state) {
		return 0;
	}

	@Override
	protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
		var signal = super.getInputSignal(level, pos, state);

		if (level.getBlockEntity(pos) instanceof ResistorBlockEntity entity) {
			entity.setOutputSignal(signal);
		}

		return signal;
	}

	@Override
	protected InteractionResult useWithoutItem(
		BlockState state,
		Level level,
		BlockPos pos,
		Player player,
		BlockHitResult hitResult
	) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide) {
			level.setBlock(
				pos,
				state.setValue(RESISTANCE, this.getAndWrapResistance(state)),
				3
			);
		} else {
			level.playLocalSound(
				pos,
				SoundEvents.STONE_BUTTON_CLICK_ON,
				SoundSource.BLOCKS,
				1F,
				1 + ((float)this.getAndWrapResistance(state) / 15F),
				false
			);
		}

		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
		return level.getBlockEntity(pos) instanceof ResistorBlockEntity entity
			? entity.calcOutputSignal(state.getValue(RESISTANCE))
			: 0;
	}

	public static int color(
		BlockState state,
		@Nullable BlockAndTintGetter level,
		@Nullable BlockPos pos,
		int tintIndex
	) {
		return RedStoneWireBlock.getColorForPower(state.getValue(RESISTANCE));
	}

	int getAndWrapResistance(BlockState state) {
		var resistance = state.getValue(RESISTANCE);
		resistance++;
		if (resistance > 15) {
			resistance = 0;
		}

		return resistance;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		var flag = fluidstate.getType() == Fluids.WATER;
		return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, flag);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED)
			? Fluids.WATER.getSource(false)
			: super.getFluidState(state);
	}

	@Override
	protected BlockState updateShape(
		BlockState state,
		Direction dir,
		BlockState neighborState,
		LevelAccessor level,
		BlockPos pos,
		BlockPos neighborPos
	) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
	}
}
