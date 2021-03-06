package de.cubeisland.ItemRepair.repair.repairblocks;

import de.cubeisland.ItemRepair.ItemRepair;
import static de.cubeisland.ItemRepair.ItemRepair._;
import de.cubeisland.ItemRepair.ItemRepairConfiguration;
import de.cubeisland.ItemRepair.RepairBlock;
import de.cubeisland.ItemRepair.repair.RepairRequest;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Repairs all blocks
 *
 * @author Phillip Schichtel
 */
public class NormalRepair extends RepairBlock
{
    private final ItemRepairConfiguration config;

    public NormalRepair(ItemRepair plugin)
    {
        super(plugin, "normal", _("repair"), plugin.getConfiguration().repairBlocks_normal_block);
        this.config = plugin.getConfiguration();
    }

    @Override
    public RepairRequest requestRepair(Inventory inventory)
    {
        final Player player = (Player)inventory.getHolder();
        Map<Integer, ItemStack> items = getRepairableItems(inventory);
        if (items.size() > 0)
        {
            double price = calculatePrice(items.values(), this.config.price_enchantMultiplier_factor, this.config.price_enchantMultiplier_base);

            player.sendMessage(_("headline"));
            player.sendMessage(_("clickAgain"));
            player.sendMessage(_("repairWouldCost", getEconomy().format(price)));
            player.sendMessage(_("youCurrentlyHave", getEconomy().format(getEconomy().getBalance(player.getName()))));

            return new RepairRequest(this, inventory, items, price);
        }
        else
        {
            player.sendMessage(_("noItems"));
        }
        return null;
    }

    @Override
    public void repair(RepairRequest request)
    {
        final double price = request.getPrice();
        final Player player = (Player)request.getInventory().getHolder();

        if (checkBalance(player, price))
        {
            if (withdrawPlayer(player, price))
            {
                repairItems(request);
                player.sendMessage(_("itemsRepaired", getEconomy().format(price)));
            }
            else
            {
                player.sendMessage(_("somethingWentWrong"));
            }
        }
        else
        {
            player.sendMessage(_("notEnoughMoney"));
        }
    }
}
