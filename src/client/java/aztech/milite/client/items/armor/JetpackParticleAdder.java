package aztech.milite.client.items.armor;

import aztech.milite.items.armor.JetpackItem;
import aztech.milite.items.FluidFuelItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class JetpackParticleAdder {
    public static void addJetpackParticles(Minecraft client) {
        if (client.level != null && !client.isPaused()) {
            for (Player player : client.level.players()) {
                ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
                if (chest.getItem() instanceof JetpackItem jetpack) {
                    if (jetpack.isActivated(chest) && FluidFuelItemHelper.getAmount(chest) > 0) {
                        Random r = ThreadLocalRandom.current();
                        if (player.isFallFlying()) {
                            Vec3 velocity = player.getDeltaMovement();
                            client.level.addParticle(ParticleTypes.FLAME, player.getX(), player.getY(), player.getZ(), -velocity.x, -velocity.y,
                                    -velocity.z);
                        } else {
                            client.level.addParticle(ParticleTypes.FLAME, player.getX(), player.getY() + 1.0, player.getZ(), 0, -1, 0);
                        }
                    }
                }
            }
        }
    }
}
