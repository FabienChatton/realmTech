package ch.realmtech.server.mod.factory;

import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.level.cell.CraftingTableEditEntity;
import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.registry.*;
import ch.realmtech.server.uuid.UuidSupplierOrRandom;

import java.util.List;
import java.util.UUID;

public class EditEntityFactory extends Entry {
    private List<CraftRecipeEntry> craftRecipes;

    public EditEntityFactory() {
        super("Factory");
    }

    @Override
    @SuppressWarnings("unchecked")
    @EvaluateAfter("#craftingTableRecipes")
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        String tagQuery = "#craftingTableRecipes";
        craftRecipes = (List<CraftRecipeEntry>) RegistryUtils.findEntries(rootRegistry, tagQuery);
    }

    public CraftingTableEditEntity createCraftingTable(int craftingNumberOfSlotParRow, int craftingNumberOfRow) {
        return new CraftingTableEditEntity(new UuidSupplierOrRandom(), new int[craftingNumberOfSlotParRow * craftingNumberOfRow][InventoryComponent.DEFAULT_STACK_LIMITE], craftingNumberOfSlotParRow, craftingNumberOfRow, new UuidSupplierOrRandom(), craftRecipes);
    }

    public CraftingTableEditEntity createSetCraftingTable(UUID craftingInventoryUuid, int[][] inventory, int craftingNumberOfSlotParRow, int craftingNumberOfRow, UUID craftingResultInventoryUuid) {
        return new CraftingTableEditEntity(new UuidSupplierOrRandom(craftingInventoryUuid), inventory, craftingNumberOfSlotParRow, craftingNumberOfRow, new UuidSupplierOrRandom(craftingResultInventoryUuid), craftRecipes);
    }
}
