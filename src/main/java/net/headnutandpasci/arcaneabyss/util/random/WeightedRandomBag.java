package net.headnutandpasci.arcaneabyss.util.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedRandomBag<T> {
    private final List<Entry<T>> entries;

    public WeightedRandomBag() {
        entries = new ArrayList<>();
    }

    public void addEntry(T object, double weight) {
        entries.add(new Entry<>(object, weight));
    }

    public T getRandom() {
        double totalWeight = 0;
        for (Entry<T> entry : entries) {
            totalWeight += entry.weight;
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * totalWeight;

        for (Entry<T> entry : entries) {
            randomValue -= entry.weight;
            if (randomValue <= 0) {
                return entry.object;
            }
        }

        return null; // Should never happen if weights are correct
    }

    private record Entry<T>(T object, double weight) {
    }
}