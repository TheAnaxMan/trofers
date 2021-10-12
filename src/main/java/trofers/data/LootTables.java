package trofers.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fmllegacy.RegistryObject;
import trofers.Trofers;
import trofers.common.block.TrophyBlock;
import trofers.common.init.ModBlocks;
import trofers.common.init.ModItems;
import trofers.common.loot.RandomTrophyChanceCondition;
import trofers.common.trophy.Trophy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTables extends LootTableProvider {

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = new ArrayList<>();
    private final Trophies trophies;

    public LootTables(DataGenerator generator, Trophies trophies) {
        super(generator);
        this.trophies = trophies;
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        lootTables.clear();

        addBlockLootTables();
        addEntityLootTables();
        addTrophyLootTables();

        return lootTables;
    }

    private void addBlockLootTables() {
        CopyNbtFunction.Builder copyNbtBuilder = CopyNbtFunction
                .copyData(ContextNbtProvider.BLOCK_ENTITY)
                .copy("Trophy", "BlockEntityTag.Trophy");

        for (RegistryObject<TrophyBlock> trophy : ModBlocks.TROPHIES) {
            ResourceLocation location = new ResourceLocation(Trofers.MODID, "blocks/" + trophy.getId().getPath());
            LootTable.Builder lootTable = LootTable.lootTable().withPool(
                    LootPool.lootPool().add(
                            LootItem.lootTableItem(
                                    trophy.get()
                            ).apply(copyNbtBuilder)
                    )
            );
            lootTables.add(Pair.of(() -> builder -> builder.accept(location, lootTable), LootContextParamSets.BLOCK));
        }
    }

    private void addEntityLootTables() {
        for (Pair<Trophy, String> pair : trophies.trophies) {
            Trophy trophy = pair.getFirst();
            String modid = pair.getSecond();

            // noinspection ConstantConditions
            String entityName = trophy.entity().getType().getRegistryName().getPath();
            modid = Trofers.MODID.equals(modid) ? "" : modid + "/";
            ResourceLocation location = new ResourceLocation(Trofers.MODID, "inject/entities/" + modid + entityName);
            CompoundTag nbt = new CompoundTag();
            nbt.put("BlockEntityTag", new CompoundTag());
            nbt.getCompound("BlockEntityTag").putString("Trophy", String.format("%s:%s", Trofers.MODID, entityName));
            LootTable.Builder lootTable = LootTable.lootTable().withPool(
                    LootPool.lootPool()
                            .name("main")
                            .when(RandomTrophyChanceCondition.randomTrophyChance())
                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            .add(
                                    LootItem.lootTableItem(ModItems.SMALL_PLATE.get())
                            )
                            .apply(SetNbtFunction.setTag(nbt))
            );
            lootTables.add(Pair.of(() -> builder -> builder.accept(location, lootTable), LootContextParamSets.ENTITY));
        }
    }

    private void addTrophyLootTables() {
        Map<EntityType<?>, LootTable.Builder> lootTables = new HashMap<>();
        lootTables.put(EntityType.AXOLOTL, table(
                entry(Items.AMETHYST_SHARD)
        ));
        lootTables.put(EntityType.BEE, table(
                entry(Items.HONEY_BOTTLE),
                entry(Items.HONEYCOMB, 2)
        ));
        lootTables.put(EntityType.BLAZE, table(
                1, 3,
                entry(Items.BLAZE_ROD),
                entry(Items.BLAZE_POWDER, 2)
        ));
        lootTables.put(EntityType.CAVE_SPIDER, table(
                entry(Items.COBWEB)
        ));
        lootTables.put(EntityType.COD, table(
                1, 3,
                entry(Items.COD)
        ));
        lootTables.put(EntityType.COW, table(
                1, 3,
                entry(Items.BEEF)
        ));
        lootTables.put(EntityType.CREEPER, table(
                1, 3,
                entry(Items.GUNPOWDER, 3),
                entry(Items.TNT)
        ));
        lootTables.put(EntityType.DONKEY, table(
                entry(Items.GOLDEN_APPLE)
        ));
        lootTables.put(EntityType.ENDERMAN, table(
                entry(Items.ENDER_PEARL)
        ));
        lootTables.put(EntityType.ENDERMITE, table(
                3, 12,
                entry(Items.END_STONE)
        ));
        lootTables.put(EntityType.EVOKER, table(
                entry(Items.TOTEM_OF_UNDYING)
        ));
        lootTables.put(EntityType.FOX, table(
                1, 3,
                entry(Items.SWEET_BERRIES, 2),
                entry(Items.GLOW_BERRIES, 1)
        ));
        lootTables.put(EntityType.GHAST, table(
                entry(Items.GHAST_TEAR)
        ));
        lootTables.put(EntityType.GOAT, table(
                entry(Items.POWDER_SNOW_BUCKET)
        ));
        lootTables.put(EntityType.GUARDIAN, table(
                1, 6,
                entry(Items.PRISMARINE_SHARD, 2),
                entry(Items.PRISMARINE_CRYSTALS)
        ));
        lootTables.put(EntityType.HOGLIN, table(
                entry(Items.LEATHER)
        ));
        lootTables.put(EntityType.HORSE, table(
                entry(Items.HAY_BLOCK)
        ));
        lootTables.put(EntityType.HUSK, table(
                1, 3,
                entry(Items.ROTTEN_FLESH),
                entry(Items.IRON_INGOT)
        ));
        lootTables.put(EntityType.LLAMA, table(
                1, 6,
                entry(Items.BLACK_CARPET),
                entry(Items.CYAN_CARPET),
                entry(Items.BLUE_CARPET),
                entry(Items.BROWN_CARPET),
                entry(Items.GRAY_CARPET),
                entry(Items.GREEN_CARPET),
                entry(Items.LIGHT_BLUE_CARPET),
                entry(Items.LIGHT_GRAY_CARPET),
                entry(Items.LIME_CARPET),
                entry(Items.MAGENTA_CARPET),
                entry(Items.ORANGE_CARPET),
                entry(Items.PINK_CARPET),
                entry(Items.PURPLE_CARPET),
                entry(Items.RED_CARPET),
                entry(Items.WHITE_CARPET),
                entry(Items.YELLOW_CARPET)
        ));
        lootTables.put(EntityType.MAGMA_CUBE, table(
                1, 3,
                entry(Items.MAGMA_CREAM)
        ));
        lootTables.put(EntityType.MOOSHROOM, table(
                1, 3,
                entry(Items.BROWN_MUSHROOM),
                entry(Items.RED_MUSHROOM)
        ));
        lootTables.put(EntityType.MULE, table(
                entry(Items.GOLDEN_CARROT)
        ));
        lootTables.put(EntityType.PANDA, table(
                entry(Items.CAKE)
        ));
        lootTables.put(EntityType.PARROT, table(
                entry(Items.COOKIE)
        ));
        lootTables.put(EntityType.PHANTOM, table(
                entry(Items.PHANTOM_MEMBRANE)
        ));
        lootTables.put(EntityType.PIG, table(
                1, 3,
                entry(Items.PORKCHOP)
        ));
        lootTables.put(EntityType.PILLAGER, table(
                3, 12,
                entry(Items.ARROW)
        ));
        lootTables.put(EntityType.POLAR_BEAR, table(
                1, 4,
                entry(Items.SALMON),
                entry(Items.COD)
        ));
        lootTables.put(EntityType.PUFFERFISH, table(
                entry(Items.PUFFERFISH)
        ));
        lootTables.put(EntityType.RAVAGER, table(
                entry(Items.SADDLE)
        ));
        lootTables.put(EntityType.SALMON, table(
                1, 3,
                entry(Items.SALMON)
        ));
        lootTables.put(EntityType.SHEEP, table(
                1, 6,
                entry(Items.BLACK_WOOL),
                entry(Items.CYAN_WOOL),
                entry(Items.BLUE_WOOL),
                entry(Items.BROWN_WOOL),
                entry(Items.GRAY_WOOL),
                entry(Items.GREEN_WOOL),
                entry(Items.LIGHT_BLUE_WOOL),
                entry(Items.LIGHT_GRAY_WOOL),
                entry(Items.LIME_WOOL),
                entry(Items.MAGENTA_WOOL),
                entry(Items.ORANGE_WOOL),
                entry(Items.PINK_WOOL),
                entry(Items.PURPLE_WOOL),
                entry(Items.RED_WOOL),
                entry(Items.WHITE_WOOL),
                entry(Items.YELLOW_WOOL)
        ));
        lootTables.put(EntityType.SILVERFISH, table(
                1, 3,
                entry(Items.ENDER_EYE)
        ));
        lootTables.put(EntityType.SKELETON, table(
                1, 4,
                entry(Items.BONE, 2)
        ));
        lootTables.put(EntityType.SKELETON_HORSE, table(
                1, 4,
                entry(Items.BONE_BLOCK)
        ));
        lootTables.put(EntityType.SLIME, table(
                1, 3,
                entry(Items.SLIME_BLOCK)
        ));
        lootTables.put(EntityType.SNOW_GOLEM, table(
                2, 6,
                entry(Items.SNOW_BLOCK),
                entry(Items.SNOWBALL, 2)
        ));
        lootTables.put(EntityType.SPIDER, table(
                1, 3,
                entry(Items.STRING)
        ));
        lootTables.put(EntityType.SQUID, table(
                1, 3,
                entry(Items.INK_SAC)
        ));
        lootTables.put(EntityType.STRAY, table(
                2, 8,
                entry(Items.TIPPED_ARROW).apply(SetNbtFunction.setTag(Util.make(new CompoundTag(), (tag) -> tag.putString("Potion", "minecraft:slowness"))))
        ));
        lootTables.put(EntityType.TRADER_LLAMA, table(
                entry(Items.LEAD)
        ));
        lootTables.put(EntityType.TROPICAL_FISH, table(
                entry(Items.TROPICAL_FISH)
        ));
        lootTables.put(EntityType.VEX, table(
                entry(Items.EMERALD)
        ));
        lootTables.put(EntityType.VINDICATOR, table(
                entry(Items.IRON_AXE)
        ));
        lootTables.put(EntityType.WITHER_SKELETON, table(
                entry(Items.WITHER_SKELETON_SKULL)
        ));
        lootTables.put(EntityType.WOLF, table(
                entry(Items.WHITE_WOOL)
        ));
        lootTables.put(EntityType.ZOGLIN, table(
                1, 3,
                entry(Items.ROTTEN_FLESH)
        ));
        lootTables.put(EntityType.ZOMBIE, table(
                1, 3,
                entry(Items.ROTTEN_FLESH)
        ));
        lootTables.put(EntityType.ZOMBIE_VILLAGER, table(
                1, 3,
                entry(Items.EMERALD),
                entry(Items.ROTTEN_FLESH, 2)
        ));
        lootTables.put(EntityType.ZOMBIFIED_PIGLIN, table(
                1, 3,
                entry(Items.GOLD_NUGGET, 2),
                entry(Items.GOLD_INGOT)
        ));

        lootTables.forEach((type, lootTable) -> {
            // noinspection ConstantConditions
            ResourceLocation location = new ResourceLocation(Trofers.MODID, String.format("trophies/%s", type.getRegistryName().getPath()));
            this.lootTables.add(Pair.of(() -> builder -> builder.accept(location, lootTable), LootContextParamSets.ALL_PARAMS));

        });
    }

    public LootPoolSingletonContainer.Builder<?> entry(Item item) {
        return LootItem.lootTableItem(item);
    }

    public LootPoolEntryContainer.Builder<?> entry(Item item, int weight) {
        return entry(item).setWeight(weight);
    }

    public LootTable.Builder table(int minRolls, int maxRolls, LootPoolEntryContainer.Builder<?>... entries) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(UniformGenerator.between(minRolls, maxRolls));
        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            pool.add(entry);
        }
        return LootTable.lootTable().withPool(pool);
    }

    public LootTable.Builder table(LootPoolEntryContainer.Builder<?>... entries) {
        LootPool.Builder pool = LootPool.lootPool();
        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            pool.add(entry);
        }
        return LootTable.lootTable().withPool(pool);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationTracker) {
        map.forEach((location, lootTable) -> net.minecraft.world.level.storage.loot.LootTables.validate(validationTracker, location, lootTable));
    }
}
