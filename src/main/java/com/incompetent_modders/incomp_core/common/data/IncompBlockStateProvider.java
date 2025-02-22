package com.incompetent_modders.incomp_core.common.data;

import com.incompetent_modders.incomp_core.common.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public abstract class IncompBlockStateProvider extends BlockStateProvider {
    public final String MODID;
    public IncompBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
        MODID = modid;
    }
    public ResourceLocation location(String path) {
        return Utils.location(MODID, path);
    }
    
    
    
    
    
    
    
    
    private void simpleBlockItem(Supplier<? extends Block> block) {
        super.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()))));
    }
    private void simpleBlockWithItem(Supplier<? extends Block> block) {
        super.simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()))));
    }
    private void simpleBlockAndItem(Supplier<? extends Block> block) {
        this.simpleBlock(block.get());
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()))));
    }
    private void cubeBottomTop(Supplier<? extends Block> block, ResourceLocation top, ResourceLocation bottom) {
        String name = this.name(block.get());
        ResourceLocation side = location("block/" + name);
        this.simpleBlock(block.get(), this.models().cubeBottomTop(name, side, top, bottom));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()))));
    }
    private void blockBottomTop(Supplier<? extends Block> block) {
        String name = this.name(block.get());
        ResourceLocation side = location("block/" + name);
        ResourceLocation top = location("block/" + name + "_top");
        ResourceLocation bottom = location("block/" + name + "_bottom");
        this.simpleBlock(block.get(), this.models().cubeBottomTop(name, side, bottom, top));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()))));
    }
    private void sidedSlab(Supplier<? extends Block> block, ResourceLocation side, ResourceLocation top, ResourceLocation bottom) {
        String name = this.name(block.get());
        ResourceLocation doubleSlab = location("block/" + name.replace("_slab", ""));
        this.slabBlock((SlabBlock) block.get(), doubleSlab, side, top, bottom);
        this.simpleBlockItem(block.get(), models().slab(name + "_inventory", side, top, bottom));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    private void sidedStairs(Supplier<? extends Block> block, ResourceLocation side, ResourceLocation top, ResourceLocation bottom) {
        String name = this.name(block.get());
        this.stairsBlock((StairBlock) block.get(), name, side, top, bottom);
        this.simpleBlockItem(block.get(), models().stairs(name + "_inventory", side, top, bottom));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    
    private void simpleSlab(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        String name = this.name(block.get());
        ResourceLocation main = blockTexture(texture.get());
        ResourceLocation doubleSlab = location("block/" + this.name(texture.get()));
        this.slabBlock((SlabBlock) block.get(), doubleSlab, main, main, main);
        this.simpleBlockItem(block.get(), models().slab(name + "_inventory", main, main, main));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simpleStairs(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        String name = this.name(block.get());
        ResourceLocation main = blockTexture(texture.get());
        this.stairsBlock((StairBlock) block.get(), name, main, main, main);
        this.simpleBlockItem(block.get(), models().stairs(name + "_inventory", main, main, main));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simpleWall(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        String name = this.name(block.get());
        this.wallBlock((WallBlock) block.get(), name, blockTexture(texture.get()));
        this.simpleBlockItem(block.get(), models().wallInventory(name + "_inventory", blockTexture(texture.get())));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simpleFence(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        String name = this.name(block.get());
        this.fenceBlock((FenceBlock) block.get(), name, blockTexture(texture.get()));
        this.simpleBlockItem(block.get(), models().fenceInventory(name + "_inventory", blockTexture(texture.get())));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simpleFenceGate(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        String name = this.name(block.get());
        this.fenceGateBlock((FenceGateBlock) block.get(), name, blockTexture(texture.get()));
        this.simpleBlockItem(block.get(), models().fenceGate(name + "_inventory", blockTexture(texture.get())));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simplePressurePlate(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        this.pressurePlateBlock((PressurePlateBlock) block.get(), blockTexture(texture.get()));
        this.simpleBlockItem(block);
    }
    private void simpleButton(Supplier<? extends Block> block, Supplier<? extends Block>  texture) {
        String name = this.name(block.get());
        this.buttonBlock((ButtonBlock) block.get(), blockTexture(texture.get()));
        this.simpleBlockItem(block.get(), models().buttonInventory(name + "_inventory", blockTexture(texture.get())));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(location("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simpleLogBlock(Supplier<? extends Block> block) {
        this.logBlock((RotatedPillarBlock) block.get());
        this.simpleBlockItem(block);
    }
    private void simpleWoodBlock(Supplier<? extends Block> block) {
        this.axisBlock((RotatedPillarBlock) block.get(), blockTexture(block.get()), extend(blockTexture(block.get()), ""));
        this.simpleBlockItem(block);
    }
    private void simpleDoorBlock(Supplier<? extends Block> block) {
        String name = this.name(block.get());
        ResourceLocation bottomLocation = location("block/" + name + "_bottom");
        ResourceLocation topLocation = location("block/" + name + "_top");
        this.doorBlockWithRenderType((DoorBlock) block.get(), bottomLocation, topLocation, "cutout");
    }
    private void simpleTrapDoorBlock(Supplier<? extends Block> block) {
        ResourceLocation location = location("block/" + this.name(block.get()));
        this.trapdoorBlockWithRenderType((TrapDoorBlock) block.get(), location, true, "cutout");
        this.simpleBlockItem(block.get(), models().trapdoorBottom(this.name(block.get()), location));
    }
    private void simpleSignBlock(Supplier<? extends Block> block, Supplier<? extends Block> wallBlock, Supplier<? extends Block> texture) {
        this.signBlock((StandingSignBlock) block.get(), ((WallSignBlock) wallBlock.get()),
                blockTexture(texture.get()));
    }
    private void simpleHangingSignBlock(Supplier<? extends Block> block, Supplier<? extends Block> wallBlock, Supplier<? extends Block> texture) {
        ModelFile sign = models().sign(this.name(block.get()), blockTexture(texture.get()));
        this.simpleBlock(block.get(), sign);
        this.simpleBlock(wallBlock.get(), sign);
    }
    private void simpleSawBlock(Supplier<? extends Block> block) {
        String name  = this.name(block.get());
        this.models().withExistingParent(name, "block/stonecutter")
                .texture("particle", location("block/" + name + "/"+ name + "_bottom"))
                .texture("bottom", location("block/" + name + "/"+ name + "_bottom"))
                .texture("top", location("block/" + name + "/"+ name + "_top"))
                .texture("side", location("block/" + name + "/"+ name + "_side"))
                .texture("saw", location("block/" + name + "/"+ name + "_saw"))
                .renderType("cutout");
        this.horizontalBlock(block.get(), new ModelFile.UncheckedModelFile(location("block/" + name)));
        this.simpleBlockItem(block);
    }
    
    
    private ResourceLocation extend(ResourceLocation rl, String suffix) {return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);}
    
    private String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
