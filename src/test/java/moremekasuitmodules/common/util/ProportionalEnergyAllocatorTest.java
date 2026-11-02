package moremekasuitmodules.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProportionalEnergyAllocatorTest {

    @Test
    void allocatesTheFullCostToASingleProvider() {
        assertArrayEquals(new long[]{10_000_000L},
                ProportionalEnergyAllocator.allocate(new long[]{15_000_000L}, 10_000_000L));
    }

    @Test
    void splitsTheCostProportionallyAcrossProviders() {
        assertArrayEquals(new long[]{6_000_000L, 4_000_000L},
                ProportionalEnergyAllocator.allocate(new long[]{12_000_000L, 8_000_000L}, 10_000_000L));
    }

    @Test
    void neverConsumesMoreThanTheTotalAvailableEnergy() {
        assertArrayEquals(new long[]{2L, 3L},
                ProportionalEnergyAllocator.allocate(new long[]{2L, 3L}, 10_000_000L));
    }

    @Test
    void ignoresEmptyAndNegativeProviderValues() {
        assertArrayEquals(new long[]{0L, 5L, 0L},
                ProportionalEnergyAllocator.allocate(new long[]{0L, 10L, -4L}, 5L));
    }

    @Test
    void handlesEnergyTotalsThatExceedLongRange() {
        assertArrayEquals(new long[]{5_000_000L, 5_000_000L},
                ProportionalEnergyAllocator.allocate(new long[]{Long.MAX_VALUE, Long.MAX_VALUE}, 10_000_000L));
    }

    @Test
    void saturatesThresholdTotalInsteadOfOverflowing() {
        assertEquals(Long.MAX_VALUE,
                ProportionalEnergyAllocator.saturatedTotal(new long[]{Long.MAX_VALUE, 1L}));
        assertEquals(7L,
                ProportionalEnergyAllocator.saturatedTotal(new long[]{4L, -2L, 3L}));
    }
}
