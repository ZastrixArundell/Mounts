package com.github.zastrixarundell.mounts.citizens;

import com.github.zastrixarundell.mounts.utils.GUIUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

public class MountTrainerTrait extends Trait
{

    public MountTrainerTrait()
    {
        super("mounttrainer");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event)
    {
        if(event.getNPC() != this.getNPC())
            return;

        GUIUtils.openGUI(event.getClicker(), 1);
    }
}
