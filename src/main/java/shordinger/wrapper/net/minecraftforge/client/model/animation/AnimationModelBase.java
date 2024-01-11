/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.client.model.animation;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import shordinger.wrapper.net.minecraft.client.model.ModelBase;
import shordinger.wrapper.net.minecraft.client.renderer.BufferBuilder;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.RenderHelper;
import shordinger.wrapper.net.minecraft.client.renderer.Tessellator;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.BakedQuad;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.IBakedModel;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraftforge.client.model.IModel;
import shordinger.wrapper.net.minecraftforge.client.model.ModelLoader;
import shordinger.wrapper.net.minecraftforge.client.model.ModelLoaderRegistry;
import shordinger.wrapper.net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import shordinger.wrapper.net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import shordinger.wrapper.net.minecraftforge.common.animation.Event;
import shordinger.wrapper.net.minecraftforge.common.animation.IEventHandler;
import shordinger.wrapper.net.minecraftforge.common.model.IModelState;
import shordinger.wrapper.net.minecraftforge.common.model.animation.CapabilityAnimation;
import shordinger.wrapper.net.minecraftforge.common.model.animation.IAnimationStateMachine;

import java.util.List;

/**
 * ModelBase that works with the Forge model system and animations.
 * Some quirks are still left, deprecated for the moment.
 */
@Deprecated
public class AnimationModelBase<T extends Entity> extends ModelBase implements IEventHandler<T> {

    private final VertexLighterFlat lighter;
    private final ResourceLocation modelLocation;

    public AnimationModelBase(ResourceLocation modelLocation, VertexLighterFlat lighter) {
        this.modelLocation = modelLocation;
        this.lighter = lighter;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingSpeed, float timeAlive, float yawHead,
                       float rotationPitch, float scale) {
        IAnimationStateMachine capability = entity.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null);
        if (capability == null) {
            return;
        }
        Pair<IModelState, Iterable<Event>> pair = capability.apply(timeAlive / 20);
        handleEvents((T) entity, timeAlive / 20, pair.getRight());
        IModel model = ModelLoaderRegistry.getModelOrMissing(modelLocation);
        IBakedModel bakedModel = model
            .bake(pair.getLeft(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());

        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.height, entity.posZ);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        builder.setTranslation(-0.5, -1.5, -0.5);

        lighter.setParent(new VertexBufferConsumer(builder));
        lighter.setWorld(entity.world);
        lighter.setState(Blocks.AIR.getDefaultState());
        lighter.setBlockPos(pos);
        boolean empty = true;
        List<BakedQuad> quads = bakedModel.getQuads(null, null, 0);
        if (!quads.isEmpty()) {
            lighter.updateBlockInfo();
            empty = false;
            for (BakedQuad quad : quads) {
                quad.pipe(lighter);
            }
        }
        for (EnumFacing side : EnumFacing.values()) {
            quads = bakedModel.getQuads(null, side, 0);
            if (!quads.isEmpty()) {
                if (empty) lighter.updateBlockInfo();
                empty = false;
                for (BakedQuad quad : quads) {
                    quad.pipe(lighter);
                }
            }
        }

        // debug quad
        /*
         * VertexBuffer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
         * VertexBuffer.pos(0, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
         * VertexBuffer.pos(1, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
         * VertexBuffer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();
         */

        builder.setTranslation(0, 0, 0);

        tessellator.draw();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public void handleEvents(T instance, float time, Iterable<Event> pastEvents) {
    }
}
