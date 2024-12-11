package net.headnutandpasci.arcaneabyss.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class DungeonXpComponent implements AutoSyncedComponent, Component {
    private final PlayerEntity provider;
    private int dungeonXp = 0;

    public DungeonXpComponent(PlayerEntity provider) {
        this.provider = provider;
    }

    public void addDungeonXp(int xp) {
        this.dungeonXp += xp;
        ModComponents.DUNGEON_XP.sync(provider);
    }

    public int getDungeonXp() {
        return dungeonXp;
    }

    public void setDungeonXp(int dungeonXp) {
        this.dungeonXp = dungeonXp;
        ModComponents.DUNGEON_XP.sync(provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.dungeonXp = tag.getInt("dungeonXp");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("dungeonXp", this.dungeonXp);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.dungeonXp);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        this.dungeonXp = buf.readInt();
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.provider;
    }
}
