package net.blay09.mods.clienttweaks.tweak;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.client.RenderHandEvent;
import net.blay09.mods.clienttweaks.ClientTweaksConfig;
import net.blay09.mods.clienttweaks.ClientTweaksConfigData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;

public class HideShieldUnlessHoldingWeapon extends AbstractClientTweak {

    public HideShieldUnlessHoldingWeapon() {
        super("hideShieldUnlessHoldingWeapon");

        Balm.getEvents().onEvent(RenderHandEvent.class, this::onRenderHand, EventPriority.Highest);
    }

    public void onRenderHand(RenderHandEvent event) {
        if (!isEnabled() || event.getHand() != InteractionHand.OFF_HAND) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ResourceLocation registryName = Balm.getRegistries().getKey(event.getItemStack().getItem());
        boolean isShield = Balm.getHooks().isShield(event.getItemStack()) || ClientTweaksConfig.getActive().customization.shieldItems.contains(registryName.toString());
        if (!isShield) {
            return;
        }

        boolean isBlocking = player.getUsedItemHand() == InteractionHand.OFF_HAND && player.isBlocking();
        if (!hasWeaponInHand(player) && !isBlocking) {
            event.setCanceled(true);
        }
    }

    private boolean hasWeaponInHand(Player player) {
        ItemStack mainItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (mainItem.getItem() instanceof SwordItem || mainItem.getItem() instanceof AxeItem) {
            return true;
        }

        /* float attackDamage = mainItem.getItem() instanceof DiggerItem ? ((DiggerItem) mainItem.getItem()).getAttackDamage() : 0;
        if (attackDamage >= 3) {
            return true;
        } */

        ResourceLocation mainItemRegistryName = Balm.getRegistries().getKey(mainItem.getItem());
        return ClientTweaksConfig.getActive().customization.shieldWeapons.contains(mainItemRegistryName.toString());
    }

    @Override
    public boolean isEnabled() {
        return ClientTweaksConfig.getActive().tweaks.hideShieldUnlessHoldingWeapon;
    }

    @Override
    public void setEnabled(boolean enabled) {
        Balm.getConfig().updateConfig(ClientTweaksConfigData.class, it -> it.tweaks.hideShieldUnlessHoldingWeapon = enabled);
    }

}
