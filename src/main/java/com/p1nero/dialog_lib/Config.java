package com.p1nero.dialog_lib;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DialogueLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue ENABLE_TYPEWRITER_EFFECT;
    public static final ForgeConfigSpec.IntValue TYPEWRITER_EFFECT_SPEED;
    public static final ForgeConfigSpec.IntValue TYPEWRITER_EFFECT_INTERVAL;
    static final ForgeConfigSpec SPEC;

    static{
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        ENABLE_TYPEWRITER_EFFECT = createBool(clientBuilder, "enable_typewriter_effect", true, "剧情对话是否使用打字机效果");
        TYPEWRITER_EFFECT_SPEED = createInt(clientBuilder, "typewriter_effect_speed", 2, 1, "打字机效果打字速度");
        TYPEWRITER_EFFECT_INTERVAL = createInt(clientBuilder, "typewriter_effect_interval", 2, 1, "打字机效果打字间隔");
        SPEC = clientBuilder.build();
    }

    private static ForgeConfigSpec.BooleanValue createBool(ForgeConfigSpec.Builder builder, String key, boolean defaultValue, String... comment) {
        return builder
                .translation("config."+ DialogueLib.MOD_ID+".common."+key)
                .comment(comment)
                .define(key, defaultValue);
    }

    private static ForgeConfigSpec.IntValue createInt(ForgeConfigSpec.Builder builder, String key, int defaultValue, int min, String... comment) {
        return builder
                .translation("config."+ DialogueLib.MOD_ID+".common."+key)
                .comment(comment)
                .defineInRange(key, defaultValue, min, Integer.MAX_VALUE);
    }

    private static ForgeConfigSpec.DoubleValue createDouble(ForgeConfigSpec.Builder builder, String key, double defaultValue, double min, String... comment) {
        return builder
                .translation("config."+ DialogueLib.MOD_ID+".common."+key)
                .comment(comment)
                .defineInRange(key, defaultValue, min, Double.MAX_VALUE);
    }
}
