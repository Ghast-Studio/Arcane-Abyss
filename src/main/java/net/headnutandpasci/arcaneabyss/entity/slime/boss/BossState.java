package net.headnutandpasci.arcaneabyss.entity.slime.boss;

public enum BossState implements BossStateEnum {
    SPAWNING {
        public int toInt() {
            return 0;
        }
    },

    AWAKENING {
        public int toInt() {
            return 1;
        }
    },

    IDLE {
        public int toInt() {
            return 2;
        }
    },

    SHOOT_SLIME_BULLET {
        public int toInt() {
            return 3;
        }
    };

    public Enum<? extends BossStateEnum> getSpawningState() {
        return SPAWNING;
    }
}
