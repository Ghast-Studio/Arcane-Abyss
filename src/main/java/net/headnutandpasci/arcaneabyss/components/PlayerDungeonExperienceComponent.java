package net.headnutandpasci.arcaneabyss.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class PlayerDungeonExperienceComponent implements AutoSyncedComponent, Component {
    private final PlayerEntity provider;
    private int dungeonXp = 0;
    private int dungeonLevel = 0;
    private float dungeonProgress = 0.0F;
    private int totalDungeonXp = 0;

    public PlayerDungeonExperienceComponent(PlayerEntity provider) {
        this.provider = provider;
    }

    public int getNextLevelExperience() {
        if (this.dungeonLevel >= 30) {
            return 112 + (this.dungeonLevel - 30) * 9;
        } else {
            return this.dungeonLevel >= 15 ? 37 + (this.dungeonLevel - 15) * 5 : 7 + this.dungeonLevel * 2;
        }
    }

    public void addDungeonXp(int experience) {
        this.dungeonXp += experience;
        this.dungeonProgress += (float) experience / (float) this.getNextLevelExperience();
        this.totalDungeonXp = MathHelper.clamp(this.totalDungeonXp + experience, 0, Integer.MAX_VALUE);

        while (this.dungeonProgress < 0.0F) {
            float f = this.dungeonProgress * (float) this.getNextLevelExperience();
            if (this.dungeonLevel > 0) {
                this.addDungeonLevels(-1);
                this.dungeonProgress = 1.0F + f / (float) this.getNextLevelExperience();
            } else {
                this.addDungeonLevels(-1);
                this.dungeonProgress = 0.0F;
            }
        }

        while (this.dungeonProgress >= 1.0F) {
            this.dungeonProgress = (this.dungeonProgress - 1.0F) * (float) this.getNextLevelExperience();
            this.addDungeonLevels(1);
            this.dungeonProgress /= (float) this.getNextLevelExperience();
        }

        ModComponents.DUNGEON_XP.sync(provider);
    }

    public void addDungeonLevels(int levels) {
        this.dungeonLevel += levels;
        if (this.dungeonLevel < 0) {
            this.dungeonLevel = 0;
            this.dungeonProgress = 0.0F;
            this.totalDungeonXp = 0;
        }

        if (levels > 0 && this.dungeonLevel % 5 == 0) {
            float f = this.dungeonLevel > 30 ? 1.0F : (float) this.dungeonLevel / 30.0F;
            this.provider.getWorld().playSound(null, this.provider.getX(), this.provider.getY(), this.provider.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, this.provider.getSoundCategory(), f * 0.75F, 1.0F);
        }
    }

    public int getDungeonXp() {
        return dungeonXp;
    }

    public void setDungeonXp(int dungeonXp) {
        this.dungeonXp = dungeonXp;
        this.dungeonProgress = (float) this.dungeonXp / (float) this.getNextLevelExperience();
        ModComponents.DUNGEON_XP.sync(provider);
    }

    public int getDungeonLevel() {
        return dungeonLevel;
    }

    public float getDungeonProgress() {
        return dungeonProgress;
    }

    public int getTotalDungeonXp() {
        return totalDungeonXp;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.dungeonXp = tag.getInt("dungeonXp");
        this.dungeonProgress = tag.getFloat("dungeonProgress");
        this.dungeonLevel = tag.getInt("dungeonLevel");
        this.totalDungeonXp = tag.getInt("totalDungeonXp");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("dungeonXp", this.dungeonXp);
        tag.putFloat("dungeonProgress", this.dungeonProgress);
        tag.putInt("dungeonLevel", this.dungeonLevel);
        tag.putInt("totalDungeonXp", this.totalDungeonXp);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.dungeonXp);
        buf.writeFloat(this.dungeonProgress);
        buf.writeInt(this.dungeonLevel);
        buf.writeInt(this.totalDungeonXp);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        this.dungeonXp = buf.readInt();
        this.dungeonProgress = buf.readFloat();
        this.dungeonLevel = buf.readInt();
        this.totalDungeonXp = buf.readInt();
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.provider;
    }
}
