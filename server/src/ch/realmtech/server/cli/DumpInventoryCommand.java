package ch.realmtech.server.cli;


import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name="inventory", description = "dump all inventories")
public class DumpInventoryCommand implements Callable<Integer> {
    @ParentCommand
    private DumpCommand dumpCommand;

    @Option(names = {"-v", "--verbose"}, description = "Show more detail about items result")
    private boolean verbose;

    @Override
    public Integer call() throws Exception {
        IntBag inventoryEntities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager().get(Aspect.all(InventoryComponent.class, UuidComponent.class)).getEntities();
        int[] inventoryData = inventoryEntities.getData();
        ComponentMapper<InventoryComponent> mInventory = dumpCommand.masterCommand.getWorld().getMapper(InventoryComponent.class);
        ComponentMapper<UuidComponent> mUuid = dumpCommand.masterCommand.getWorld().getMapper(UuidComponent.class);
        if (verbose) {
            for (int i = 0; i < inventoryEntities.size(); i++) {
                int inventoryId = inventoryData[i];
                UuidComponent uuidComponent = mUuid.get(inventoryId);
                InventoryComponent inventoryComponent = mInventory.get(inventoryId);
                dumpCommand.masterCommand.output.println(String.format("%s, %s", uuidComponent, inventoryComponent));
            }
        }
        if (inventoryEntities.size() == 0) dumpCommand.masterCommand.output.println("no inventories loaded");
        else dumpCommand.masterCommand.output.println("inventories count: " + inventoryEntities.size());
        return 0;
    }
}
