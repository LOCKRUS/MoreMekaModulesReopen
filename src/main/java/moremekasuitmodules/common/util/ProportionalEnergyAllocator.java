package moremekasuitmodules.common.util;

import java.math.BigInteger;

/**
 * Allocates a requested energy debit across providers in proportion to their
 * available energy, without exceeding any provider's stored energy.
 */
public final class ProportionalEnergyAllocator {

    private ProportionalEnergyAllocator() {
    }

    /**
     * @param availableEnergy energy currently stored in each provider
     * @param requestedTotal total energy to debit; capped at total available energy
     * @return the debit for each provider, in the same order as {@code availableEnergy}
     */
    public static long[] allocate(long[] availableEnergy, long requestedTotal) {
        long[] allocation = new long[availableEnergy.length];
        if (requestedTotal <= 0) {
            return allocation;
        }

        BigInteger[] available = new BigInteger[availableEnergy.length];
        BigInteger totalAvailable = BigInteger.ZERO;
        for (int i = 0; i < availableEnergy.length; i++) {
            long value = Math.max(0, availableEnergy[i]);
            available[i] = BigInteger.valueOf(value);
            totalAvailable = totalAvailable.add(available[i]);
        }
        if (totalAvailable.signum() == 0) {
            return allocation;
        }

        BigInteger target = BigInteger.valueOf(requestedTotal).min(totalAvailable);
        BigInteger allocatedTotal = BigInteger.ZERO;
        BigInteger[] remainders = new BigInteger[available.length];
        for (int i = 0; i < available.length; i++) {
            if (available[i].signum() == 0) {
                remainders[i] = BigInteger.valueOf(-1);
                continue;
            }
            BigInteger[] share = target.multiply(available[i]).divideAndRemainder(totalAvailable);
            allocation[i] = share[0].longValueExact();
            remainders[i] = share[1];
            allocatedTotal = allocatedTotal.add(share[0]);
        }

        int remaining = target.subtract(allocatedTotal).intValueExact();
        while (remaining > 0) {
            int bestIndex = -1;
            for (int i = 0; i < available.length; i++) {
                if (remainders[i] == null || remainders[i].signum() < 0
                        || BigInteger.valueOf(allocation[i]).compareTo(available[i]) >= 0) {
                    continue;
                }
                if (bestIndex < 0 || remainders[i].compareTo(remainders[bestIndex]) > 0) {
                    bestIndex = i;
                }
            }
            if (bestIndex < 0) {
                throw new IllegalStateException("Unable to distribute the remaining energy debit");
            }
            allocation[bestIndex]++;
            remainders[bestIndex] = BigInteger.valueOf(-1);
            remaining--;
        }
        return allocation;
    }

    /** Sums positive provider charges, saturating at {@link Long#MAX_VALUE}. */
    public static long saturatedTotal(long[] availableEnergy) {
        long total = 0;
        for (long energy : availableEnergy) {
            long positiveEnergy = Math.max(0, energy);
            if (Long.MAX_VALUE - total < positiveEnergy) {
                return Long.MAX_VALUE;
            }
            total += positiveEnergy;
        }
        return total;
    }
}
