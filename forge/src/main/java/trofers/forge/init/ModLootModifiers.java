package trofers.forge.init;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import trofers.Trofers;
import trofers.forge.loot.AddEntityTrophy;
import trofers.forge.loot.AddTrophy;

@SuppressWarnings("unused")
public class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Trofers.MOD_ID);

    public static final RegistryObject<Codec<AddEntityTrophy>> ADD_ENTITY_TROPHY = LOOT_MODIFIERS.register("add_entity_trophy", AddEntityTrophy.CODEC);
    public static final RegistryObject<Codec<AddTrophy>> ADD_TROPHY = LOOT_MODIFIERS.register("add_trophy", AddTrophy.CODEC);
}
