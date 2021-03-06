/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mod.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import tk.wurst_client.Client;
import tk.wurst_client.event.EventManager;
import tk.wurst_client.event.listeners.LeftClickListener;
import tk.wurst_client.event.listeners.UpdateListener;
import tk.wurst_client.mod.Mod;
import tk.wurst_client.mod.Mod.Category;
import tk.wurst_client.mod.Mod.Info;

@Info(category = Category.COMBAT,
	description = "Automatically uses the best weapon in your hotbar to attack\n"
		+ "entities. Tip: This works with Killaura.",
	name = "AutoSword")
public class AutoSword extends Mod implements LeftClickListener, UpdateListener
{
	private int oldSlot;
	private int timer;
	
	@Override
	public void onEnable()
	{
		if(Client.wurst.modManager.getModByClass(YesCheat.class)
			.isEnabled())
		{
			noCheatMessage();
			setEnabled(false);
			return;
		}
		oldSlot = -1;
		EventManager.addLeftClickListener(this);
	}
	
	@Override
	public void onUpdate()
	{
		if(timer > 0)
		{
			timer--;
			return;
		}
		Minecraft.getMinecraft().thePlayer.inventory.currentItem = oldSlot;
		EventManager.removeUpdateListener(this);
	}
	
	@Override
	public void onDisable()
	{
		EventManager.removeLeftClickListener(this);
	}
	
	@Override
	public void onLeftClick()
	{
		if(Client.wurst.modManager.getModByClass(YesCheat.class)
			.isEnabled())
		{
			noCheatMessage();
			setEnabled(false);
			return;
		}
		if(Minecraft.getMinecraft().objectMouseOver != null
			&& Minecraft.getMinecraft().objectMouseOver.entityHit instanceof EntityLivingBase)
			setSlot();
	}
	
	public static void setSlot()
	{
		float bestSpeed = 1F;
		int bestSlot = -1;
		for(int i = 0; i < 9; i++)
		{
			ItemStack item =
				Minecraft.getMinecraft().thePlayer.inventory
					.getStackInSlot(i);
			if(item == null)
				continue;
			float speed = 0;
			if(item.getItem() instanceof ItemSword)
				speed = ((ItemSword)item.getItem()).func_150931_i();
			else if(item.getItem() instanceof ItemTool)
				speed =
					((ItemTool)item.getItem()).getToolMaterial()
						.getDamageVsEntity();
			if(speed > bestSpeed)
			{
				bestSpeed = speed;
				bestSlot = i;
			}
		}
		if(bestSlot != -1
			&& bestSlot != Minecraft.getMinecraft().thePlayer.inventory.currentItem)
		{
			AutoSword instance =
				(AutoSword)Client.wurst.modManager
					.getModByClass(AutoSword.class);
			instance.oldSlot =
				Minecraft.getMinecraft().thePlayer.inventory.currentItem;
			Minecraft.getMinecraft().thePlayer.inventory.currentItem =
				bestSlot;
			instance.timer = 4;
			EventManager.addUpdateListener(instance);
		}
	}
}
