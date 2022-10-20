package com.brandon3055.draconicevolution.client;

import codechicken.lib.texture.AtlasRegistrar;
import codechicken.lib.texture.IIconRegister;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.function.Consumer;

/**
 * Created by brandon3055 on 31/08/2016.
 */
@Deprecated
public class DETextures implements IIconRegister, ResourceManagerReloadListener {

//    private static final String PARTICLES_ = "draconicevolution:particle/";
//    private static final String MC_PARTICLES_ = "minecraft:particle/";
    private static AtlasRegistrar map;

//    public static ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
//        public void begin(BufferBuilder builder, TextureManager manager) {
//            RenderSystem.depthMask(false);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
//            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
//        }
//
//        public void end(Tesselator tessellator) {
//            tessellator.end();
//        }
//
//        public String toString() {
//            return "TERRAIN_SHEET_TRANSLUCENT";
//        }
//    };

    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {

    }

//    public static TextureAtlasSprite GENERATOR;

    @Override
    public void registerIcons(AtlasRegistrar registrar) {
        map = registrar;

//        registerDE("block/generator/generator_2", e -> GENERATOR = e);
//
//        ENERGY_PARTICLE = new TextureAtlasSprite[5];
//        for (int i = 0; i < ENERGY_PARTICLE.length; i++) {
//            int finalI = i;
//            register(PARTICLES_ + "energy_" + i, sprite -> ENERGY_PARTICLE[finalI] = sprite);
//        }
//        MIXED_PARTICLE = new TextureAtlasSprite[14];
//        SPARK_PARTICLE = new TextureAtlasSprite[7];
//        for (int i = 0; i < SPARK_PARTICLE.length; i++) {
//            int finalI = i;
//            register(PARTICLES_ + "spark_" + i, sprite -> {
//                SPARK_PARTICLE[finalI] = sprite;
//                MIXED_PARTICLE[finalI] = sprite;
//            });
//        }
//        SPELL_PARTICLE = new TextureAtlasSprite[7];
//        for (int i = 0; i < SPELL_PARTICLE.length; i++) {
//            int finalI = i;
//            register(PARTICLES_ + "spell_" + i, sprite -> {
//                SPELL_PARTICLE[finalI] = sprite;
//                MIXED_PARTICLE[finalI + 7] = sprite;
//            });
//        }
//        register(PARTICLES_ + "white_orb", sprite -> ORB_PARTICLE = sprite);
//        register(PARTICLES_ + "portal", sprite -> PORTAL_PARTICLE = sprite);
    }

    private static void register(String sprite, Consumer<TextureAtlasSprite> onReady) {
        map.registerSprite(new ResourceLocation(sprite), onReady);
    }

    private static void registerDE(String sprite, Consumer<TextureAtlasSprite> onReady) {
        map.registerSprite(new ResourceLocation(DraconicEvolution.MODID, sprite), onReady);
    }

//    public static TextureAtlasSprite[] ENERGY_PARTICLE;
//    public static TextureAtlasSprite[] SPARK_PARTICLE;
//    public static TextureAtlasSprite[] SPELL_PARTICLE;
//    public static TextureAtlasSprite[] MIXED_PARTICLE;
//    public static TextureAtlasSprite ORB_PARTICLE;
//    public static TextureAtlasSprite PORTAL_PARTICLE;


    //TODO in 1.11 or 1.12 make there full names with mod prefix and ether remove texture cache or have it auto detect when the modid is already present.
    public static final String ENERGY_INFUSER_DECORATION = "textures/block/energy_infuser/energy_infuser_decoration.png";
    public static final String FUSION_PARTICLE = "textures/block/fusion_crafting/fusion_particle.png";
//    public static final String STABILIZER_LARGE = "textures/block/stabilizer_large.png";

    public static final String CHAOS_GUARDIAN_CRYSTAL = "textures/entity/guardian_crystal.png";
    public static final String PROJECTILE_CHAOS = "textures/entity/projectile_chaos.png";
    public static final String PROJECTILE_ENERGY = "textures/entity/projectile_energy.png";
    public static final String PROJECTILE_FIRE = "textures/entity/projectile_fire.png";
    public static final String PROJECTILE_IGNITION = "textures/entity/projectile_ignition.png";
    public static final String GUI_DISLOCATOR_ADVANCED = "textures/gui/dislocator_advanced.png";
    public static final String GUI_ENERGY_INFUSER = "textures/gui/energy_infuser.png";
    public static final String GUI_FUSION_CRAFTING = "textures/gui/fusion_crafting.png";
    public static final String GUI_GENERATOR = "textures/gui/generator.png";
    public static final String GUI_GRINDER = "textures/gui/grinder.png";
    public static final String GUI_HUD = "textures/gui/hud.png";
    public static final String GUI_JEI_FUSION = "textures/gui/jei_fusion_background.png";
    public static final String GUI_WIDGETS = "textures/gui/widgets.png";
    public static final String GUI_PARTICLE_GENERATOR = "textures/gui/particle_generator.png";
    public static final String DRAGON_HEART = "textures/items/components/dragon_heart.png";
    public static final String REACTOR_CORE = "textures/models/reactor_core.png";
    public static final String REACTOR_SHIELD = "textures/models/reactor_shield.png";
    //    public static final String STABILIZER_BEAM = "textures/models/stabilizer_beam.png";
    public static final String CELESTIAL_PARTICLE = "textures/particle/celestial_manipulator.png";
    //    public static final String ENERGY_CRYSTAL_BASE = "textures/models/crystal_base.png";
//    public static final String ENERGY_CRYSTAL_NO_SHADER = "textures/models/crystal_no_shader.png";
//    public static final String ENERGY_BEAM_BASIC = "textures/particle/energy_beam_basic.png";
//    public static final String ENERGY_BEAM_WYVERN = "textures/particle/energy_beam_wyvern.png";
//    public static final String ENERGY_BEAM_DRACONIC = "textures/particle/energy_beam_draconic.png";
    public static final String REACTOR_BEAM = "textures/particle/reactor_beam.png";
    public static final String REACTOR_ENERGY_BEAM = "textures/particle/reactor_energy_beam.png";
}
