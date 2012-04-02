package de.codeinfection.quickwango.ItemRepair.RepairBlocks;

import de.codeinfection.quickwango.ItemRepair.Item;
import de.codeinfection.quickwango.ItemRepair.ItemRepair;
import de.codeinfection.quickwango.ItemRepair.RepairBlock;
import de.codeinfection.quickwango.ItemRepair.RepairRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Repairs all blocks
 *
 * @author Phillip Schichtel
 */
public class NormalRepair extends RepairBlock
{

    public NormalRepair(Material material)
    {
        super(ItemRepair.getInstance(), "complete", material);
    }

    public NormalRepair(int blockId)
    {
        this(Material.getMaterial(blockId));
    }

    public NormalRepair(String blockName)
    {
        this(Material.getMaterial(blockName));
    }

    @Override
    public RepairRequest requestRepair(Player player)
    {
        if (hasPermission(player))
        {
            double price;
            ArrayList<ItemStack> allItems = new ArrayList<ItemStack>();
            Collections.addAll(allItems, player.getInventory().getArmorContents());
            Collections.addAll(allItems, player.getInventory().getContents());
            List<ItemStack> items = new ArrayList<ItemStack>();
            ItemStack itemInHand = player.getItemInHand();

            if (itemInHand != null && Item.getByMaterial(itemInHand.getType()) != null)
            {
                for (ItemStack itemStack : allItems)
                {
                    if (itemStack != null && Item.getByMaterial(itemStack.getType()) != null && itemStack.getDurability() > 0)
                    {
                        items.add(itemStack);
                    }
                }

                if (items.size() > 0)
                {
                    price = calculatePrice(items);

                    player.sendMessage(ChatColor.GREEN + "[" + ChatColor.DARK_RED + "ItemRepair" + ChatColor.GREEN + "]");
                    player.sendMessage(ChatColor.AQUA + "Rightclick" + ChatColor.WHITE + " again to repair all your damaged items.");
                    player.sendMessage("The repair would cost " + ChatColor.AQUA + getEconomy().format(price) + ChatColor.WHITE + ".");
                    player.sendMessage("You have currently " + ChatColor.AQUA + getEconomy().format(getEconomy().getBalance(player.getName())));

                    return new RepairRequest(this, player, items, price);
                }
                else
                {
                    player.sendMessage(ChatColor.RED + "You don't have any items to repair!");
                }
            }
        }
        return null;
    }

    @Override
    public void repair(RepairRequest request)
    {
        double price = request.getPrice();
        Player player = request.getPlayer();

        if (getEconomy().getBalance(player.getName()) >= price)
        {
            List<ItemStack> items = request.getItems();
            ItemStack itemInHand = player.getItemInHand();
            if (Item.getByMaterial(itemInHand.getType()) != null)
            {
                items.add(itemInHand);
            }
            if (getEconomy().withdrawPlayer(player.getName(), price).transactionSuccess())
            {
                repairItems(items);
                player.sendMessage(ChatColor.GREEN + "Your items have been repaired for " + ChatColor.AQUA + getEconomy().format(price));
            }
            else
            {
                player.sendMessage(ChatColor.RED + "Something went wrong, report this failure to your administrator!");
            }
        }
        else
        {
            player.sendMessage(ChatColor.RED + "You don't have enough money to repair all your items!");
        }
    }
}