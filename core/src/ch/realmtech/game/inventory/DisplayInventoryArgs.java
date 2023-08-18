package ch.realmtech.game.inventory;

import ch.realmtech.game.ecs.component.InventoryComponent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public record DisplayInventoryArgs(InventoryComponent inventoryComponent, Table inventoryTable, boolean clickAndDropSrc,
                                   boolean clickAndDropDst, boolean isCrafting, boolean isCraftResult) {

    public static DisplayInventoryArgsBuilder builder(InventoryComponent inventoryComponent, Table inventoryTable) {
        return new DisplayInventoryArgsBuilder(inventoryComponent, inventoryTable);
    }

    public static class DisplayInventoryArgsBuilder {
        private InventoryComponent inventoryComponent;
        private Table inventoryTable;
        private boolean clickAndDropSrc = true;
        private boolean clickAndDropDst = true;
        private boolean isCrafting = false;
        private boolean isCraftResult = false;


        public DisplayInventoryArgsBuilder(InventoryComponent inventoryComponent, Table inventoryTable) {
            this.inventoryComponent = inventoryComponent;
            this.inventoryTable = inventoryTable;
        }

        public DisplayInventoryArgsBuilder notClickAndDropSrc() {
            clickAndDropSrc = false;
            return this;
        }

        public DisplayInventoryArgsBuilder notClickAndDropDst() {
            clickAndDropDst = false;
            return this;
        }

        public DisplayInventoryArgsBuilder crafting() {
            isCrafting = true;
            return this;
        }

        public DisplayInventoryArgsBuilder craftResult() {
            isCraftResult = true;
            return this;
        }

        public DisplayInventoryArgs build() {
            return new DisplayInventoryArgs(inventoryComponent, inventoryTable, clickAndDropSrc, clickAndDropDst, isCrafting, isCraftResult);
        }
    }
}