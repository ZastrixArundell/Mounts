package com.github.zastrixarundell.mounts.citizens;

import com.github.zastrixarundell.mounts.gui.PlayerGUI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

/**
 * Citizen trait where you can train your mount riding skill and buy mounts as well.
 */
public class HostlerTrait extends Trait
{

    public HostlerTrait()
    {
        super("hostler");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event)
    {
        if(event.getNPC() != this.getNPC())
            return;

        PlayerGUI.openMountsToPlayer(event.getClicker(), 1);
    }
}
