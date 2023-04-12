package trofers.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import trofers.Trofers;
import trofers.forge.common.block.TrophyBlock;
import trofers.forge.common.init.ModBlocks;
import trofers.data.providers.loottables.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LootTables extends net.minecraft.data.loot.LootTableProvider {

    private final List<SubProviderEntry> lootTables = new ArrayList<>();

    public LootTables(PackOutput packOutput) {
        super(packOutput, Set.of(), List.of());
    }

    @Override
    public List<SubProviderEntry> getTables() {
        lootTables.clear();

        addBlockLootTables();
        addTrophyLootTables(new VanillaLootTables());
        if (ModList.get().isLoaded("alexsmobs"))
            addTrophyLootTables(new AlexsMobsLootTables());
        if (ModList.get().isLoaded("quark"))
            addTrophyLootTables(new QuarkLootTables());
        if (ModList.get().isLoaded("thermal"))
            addTrophyLootTables(new ThermalLootTables());
        if (ModList.get().isLoaded("tinkers_construct"))
            addTrophyLootTables(new TinkersConstructLootTables());

        return lootTables;
    }

    private void addTrophyLootTables(LootTableProvider builder) {
        lootTables.addAll(builder.getLootTables());
    }

    private void addBlockLootTables() {
        CopyNbtFunction.Builder copyNbtBuilder = CopyNbtFunction
                .copyData(ContextNbtProvider.BLOCK_ENTITY)
                .copy("Trophy", "BlockEntityTag.Trophy");

        for (RegistryObject<TrophyBlock> trophy : ModBlocks.TROPHIES) {
            ResourceLocation location = new ResourceLocation(Trofers.MOD_ID, "blocks/" + trophy.getId().getPath());
            LootTable.Builder lootTable = LootTable.lootTable().withPool(
                    LootPool.lootPool().add(
                            LootItem.lootTableItem(
                                    trophy.get()
                            ).apply(copyNbtBuilder)
                    )
            );
            lootTables.add(new SubProviderEntry(() -> builder -> builder.accept(location, lootTable), LootContextParamSets.BLOCK));
        }
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationTracker) {
        map.forEach((location, lootTable) -> net.minecraft.world.level.storage.loot.LootTables.validate(validationTracker, location, lootTable));
    }
}
