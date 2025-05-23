package me.ahoo.cosid.snowflake;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import me.ahoo.cosid.CosId;
import me.ahoo.cosid.converter.Radix62IdConverter;
import me.ahoo.cosid.test.ConcurrentGenerateSpec;
import me.ahoo.cosid.test.ConcurrentGenerateStingSpec;
import me.ahoo.cosid.test.ModSpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZoneId;
import java.util.concurrent.locks.LockSupport;

/**
 * SnowflakeIdTest .
 *
 * @author ahoo wang
 */
class MillisecondSnowflakeIdTest {
    public static final int TEST_MACHINE_ID = 1;
    SnowflakeFriendlyId snowflakeId;

    @BeforeEach
    void setup() {
        MillisecondSnowflakeId idGen = new MillisecondSnowflakeId(TEST_MACHINE_ID);
        snowflakeId = new DefaultSnowflakeFriendlyId(new ClockSyncSnowflakeId(idGen));
    }

    @Test
    public void generate() {
        long idFirst = snowflakeId.generate();
        long idSecond = snowflakeId.generate();
        Assertions.assertTrue(idSecond > idFirst);
        SnowflakeIdState idState = snowflakeId.getParser().parse(idFirst);
        Assertions.assertNotNull(idState);
        Assertions.assertEquals(TEST_MACHINE_ID, idState.getMachineId());
        Assertions.assertNotNull(idState.toString());
    }

    @Test
    public void friendlyId() {
        long id = snowflakeId.generate();
        SnowflakeIdState snowflakeIdState = snowflakeId.friendlyId(id);
        Assertions.assertNotNull(snowflakeIdState);
        Assertions.assertEquals(TEST_MACHINE_ID, snowflakeIdState.getMachineId());
        Assertions.assertEquals(id, snowflakeIdState.getId());
        SnowflakeIdState snowflakeIdState2 = snowflakeId.ofFriendlyId(snowflakeIdState.getFriendlyId());
        Assertions.assertEquals(snowflakeIdState2, snowflakeIdState);
    }

    @Test
    public void sequenceIncrement() {
        long id = snowflakeId.generate();
        SnowflakeIdState snowflakeIdState = snowflakeId.friendlyId(id);

        LockSupport.parkNanos(Duration.ofMillis(10).toNanos());

        long id2 = snowflakeId.generate();
        SnowflakeIdState snowflakeIdState2 = snowflakeId.friendlyId(id2);
        assertThat(
                snowflakeIdState2.getTimestamp() + ">" + snowflakeIdState.getTimestamp(),
                snowflakeIdState2.getTimestamp(), greaterThan(snowflakeIdState.getTimestamp())
        );
        assertThat(
                snowflakeIdState2.getSequence() + ">" + snowflakeIdState.getSequence(),
                snowflakeIdState2.getSequence(), greaterThan(snowflakeIdState.getSequence())
        );
    }


    @Test
    public void sequenceModUniformity() {
        new ModSpec(99999, 4, 100, snowflakeId::generate, ModSpec.DEFAULT_WAIT).verify();
    }

    @Test
    public void friendlyId2() {
        SnowflakeIdState snowflakeIdState = snowflakeId.friendlyId();
        Assertions.assertNotNull(snowflakeIdState);
        Assertions.assertEquals(TEST_MACHINE_ID, snowflakeIdState.getMachineId());
        SnowflakeIdState snowflakeIdState2 = snowflakeId.ofFriendlyId(snowflakeIdState.getFriendlyId());
        Assertions.assertEquals(snowflakeIdState2, snowflakeIdState);
    }

    @Test
    public void safeJavaScript() {
        SnowflakeId snowflakeId = SafeJavaScriptSnowflakeId.ofMillisecond(1);
        Assertions.assertTrue(snowflakeId.isSafeJavascript());
    }

    @Test
    public void customizeBits() {
        SnowflakeId snowflakeId = new MillisecondSnowflakeId(CosId.COSID_EPOCH, 41, 5, 10, 1);
        long id = snowflakeId.generate();

        MillisecondSnowflakeIdStateParser snowflakeIdStateParser = MillisecondSnowflakeIdStateParser.of(snowflakeId);
        SnowflakeIdState idState = snowflakeIdStateParser.parse(id);

        SnowflakeIdState idStateOfFriendlyId = snowflakeIdStateParser.parse(idState.getFriendlyId());
        Assertions.assertEquals(idState, idStateOfFriendlyId);
    }

    @Test
    public void customizeOverflowMachineId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MillisecondSnowflakeId(CosId.COSID_EPOCH, 41, 5, 10, 32));
    }

    @Test
    public void generateWhenConcurrent() {
        new ConcurrentGenerateSpec(snowflakeId) {
            @Override
            protected void assertGlobalFirst(long id) {
            }

            @Override
            protected void assertGlobalEach(long previousId, long id) {
                Assertions.assertTrue(id > previousId);
            }

            @Override
            protected void assertGlobalLast(long lastId) {
            }

        }.verify();
    }

    @Test
    public void generateWhenConcurrentString() {
        new ConcurrentGenerateStingSpec(new StringSnowflakeId(snowflakeId, Radix62IdConverter.PAD_START)).verify();
    }

    @Test
    public void generateFriendlyIdWhenConcurrentString() {
        new ConcurrentGenerateStingSpec(new DefaultSnowflakeFriendlyId(snowflakeId, MillisecondSnowflakeIdStateParser.of(snowflakeId, ZoneId.systemDefault(), true))).verify();
    }
}
