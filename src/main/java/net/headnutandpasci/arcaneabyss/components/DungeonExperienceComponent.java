package net.headnutandpasci.arcaneabyss.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class DungeonExperienceComponent implements AutoSyncedComponent, Component {
    private final ExperienceOrbEntity provider;
    private boolean isDungeonExperience = false;

    public DungeonExperienceComponent(ExperienceOrbEntity provider) {
        this.provider = provider;
    }

    public boolean isDungeonExperience() {
        return isDungeonExperience;
    }

    public void setDungeonExperience(boolean dungeonExperience) {
        isDungeonExperience = dungeonExperience;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.isDungeonExperience = tag.getBoolean("isDungeonExperience");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isDungeonExperience", this.isDungeonExperience);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeBoolean(this.isDungeonExperience);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        this.isDungeonExperience = buf.readBoolean();
    }
}
