package shordinger.wrapper.net.minecraft.client.renderer.block.model;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ItemOverrideList {

    public static final ItemOverrideList NONE = new ItemOverrideList();
    private final List<ItemOverride> overrides = Lists.<ItemOverride>newArrayList();

    private ItemOverrideList() {
    }

    public ItemOverrideList(List<ItemOverride> overridesIn) {
        for (int i = overridesIn.size() - 1; i >= 0; --i) {
            this.overrides.add(overridesIn.get(i));
        }
    }

    @Nullable
    @Deprecated
    public ResourceLocation applyOverride(ItemStack stack, @Nullable World worldIn,
                                          @Nullable EntityLivingBase entityIn) {
        if (!this.overrides.isEmpty()) {
            for (ItemOverride itemoverride : this.overrides) {
                if (itemoverride.matchesItemStack(stack, worldIn, entityIn)) {
                    return itemoverride.getLocation();
                }
            }
        }

        return null;
    }

    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world,
                                       @Nullable EntityLivingBase entity) {
        if (!stack.isEmpty() && stack.getItem()
            .hasCustomProperties()) {
            ResourceLocation location = applyOverride(stack, world, entity);
            if (location != null) {
                return net.minecraft.client.Minecraft.getMinecraft()
                    .getRenderItem()
                    .getItemModelMesher()
                    .getModelManager()
                    .getModel(net.minecraftforge.client.model.ModelLoader.getInventoryVariant(location.toString()));
            }
        }
        return originalModel;
    }

    public com.google.common.collect.ImmutableList<ItemOverride> getOverrides() {
        return com.google.common.collect.ImmutableList.copyOf(overrides);
    }
}
