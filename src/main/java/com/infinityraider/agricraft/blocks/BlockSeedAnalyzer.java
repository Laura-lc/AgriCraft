package com.infinityraider.agricraft.blocks;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.items.blocks.ItemBlockAgricraft;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.blocks.RenderSeedAnalyzer;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSeedAnalyzer extends BlockTileCustomRenderedBase<TileEntitySeedAnalyzer> {

    public static final AxisAlignedBB BOX = new AxisAlignedBB(Constants.UNIT, 0, Constants.UNIT, Constants.UNIT * (Constants.WHOLE - 1), Constants.UNIT * Constants.QUARTER, Constants.UNIT * (Constants.WHOLE - 1));

    private final ItemBlockAgricraft itemBlock;

    public BlockSeedAnalyzer() {
        super("seed_analyzer", Material.GROUND);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
        this.hasTileEntity = true;
        this.setTickRandomly(false);
        //set mining statistics
        this.setHardness(1);
        this.setResistance(1);
        this.itemBlock = new ItemBlockAgricraft(this);
    }

    //creates a new tile entity every time a block of this type is placed
    @Override
    public TileEntitySeedAnalyzer createNewTileEntity(World world, int meta) {
        return new TileEntitySeedAnalyzer();
    }

    //called when the block is broken
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            world.removeTileEntity(pos);
            world.setBlockToAir(pos);
        }
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    //this gets called when the block is mined
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                this.dropBlockAsItem(world, pos, state, 0);
            }
            this.breakBlock(world, pos, state);
        }
    }

    //get a list with items dropped by the the crop
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(this, 1, 0));
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntitySeedAnalyzer) {
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) world.getTileEntity(pos);
            if (analyzer.getStackInSlot(ContainerSeedAnalyzer.SEED_SLOT_ID) != null) {
                items.add(analyzer.getStackInSlot(ContainerSeedAnalyzer.SEED_SLOT_ID));
            }
            if (analyzer.getStackInSlot(ContainerSeedAnalyzer.JOURNAL_SLOT_ID) != null) {
                items.add(analyzer.getStackInSlot(ContainerSeedAnalyzer.JOURNAL_SLOT_ID));
            }
        }
        return items;
    }

    //open the gui when the block is activated
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        if (!world.isRemote) {
            AgriCore.getLogger("agricraft").info("Opening Seed Analyzer GUI!");
            player.openGui(AgriCraft.instance, GuiHandler.ANALYZER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    //rendering stuff
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return false;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderSeedAnalyzer getRenderer() {
        return new RenderSeedAnalyzer(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getBlockModelResourceLocation() {
        return new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + getInternalName());
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[]{
            AgriProperties.JOURNAL,
            AgriProperties.FACING
        };
    }

    @Override
    public Optional<? extends ItemBlock> getItemBlock() {
        return Optional.of(this.itemBlock);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntitySeedAnalyzer) {
            TileEntitySeedAnalyzer ana = (TileEntitySeedAnalyzer) te;
            state = AgriProperties.FACING.applyToBlockState(state, ana.getOrientation());
            state = AgriProperties.JOURNAL.applyToBlockState(state, ana.hasJournal());
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

}
