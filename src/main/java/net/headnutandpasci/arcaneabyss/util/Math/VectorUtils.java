package net.headnutandpasci.arcaneabyss.util.Math;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class VectorUtils {
    public static Vec3d rotateVectorCC(Vec3d vec, Vec3d axis, double theta) {
        double x, y, z;
        double u, v, w;
        x = vec.getX();
        y = vec.getY();
        z = vec.getZ();
        u = axis.getX();
        v = axis.getY();
        w = axis.getZ();
        double v1 = u * x + v * y + w * z;
        double xPrime = u * v1 * (1d - Math.cos(theta))
                + x * Math.cos(theta)
                + (-w * y + v * z) * Math.sin(theta);
        double yPrime = v * v1 * (1d - Math.cos(theta))
                + y * Math.cos(theta)
                + (w * x - u * z) * Math.sin(theta);
        double zPrime = w * v1 * (1d - Math.cos(theta))
                + z * Math.cos(theta)
                + (-v * x + u * y) * Math.sin(theta);
        return new Vec3d(xPrime, yPrime, zPrime);
    }

    public static Vec3d addRight(Vec3d spawn, double distance) {
        Vec3d right = new Vec3d(-spawn.z, spawn.y, spawn.x);
        right = right.normalize().multiply(distance);
        return spawn.add(right);
    }

    public static Vec3d blockPosToVec3d(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }
}
