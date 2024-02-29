package com.p1nero.dialogue_lib.entity;

import com.p1nero.dialogue_lib.DialogueLib;
import com.p1nero.dialogue_lib.entity.custom.Stationary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DialogueLib.MOD_ID);
	public static final RegistryObject<EntityType<Stationary>> SAMPLE =
			REGISTRY.register("sample",
					() -> EntityType.Builder.of(Stationary::new, MobCategory.CREATURE)
							.build(new ResourceLocation(DialogueLib.MOD_ID, "sample").toString()));

}
