package net.headnutandpasci.arcaneabyss.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.util.MovementControlAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MovementControlPacket {
    public static final Identifier ID = new Identifier(ArcaneAbyss.MOD_ID, "movement_control");

    public MovementControlPacket() {
    }

    public static void send(boolean disableMovement, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(disableMovement);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void handle(MinecraftClient client, ClientPlayNetworkHandler ignoredHandler, PacketByteBuf buf, PacketSender ignoredPacketSender) {
        boolean disableMovement = buf.readBoolean();
        client.execute(() -> {
            if (client.player != null && client.player.input instanceof MovementControlAccess access) {
                access.arcane_Abyss$setMovementDisabled(disableMovement);
            }
        });
    }
}