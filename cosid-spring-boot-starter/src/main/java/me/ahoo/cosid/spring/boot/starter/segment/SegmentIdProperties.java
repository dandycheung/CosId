/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.cosid.spring.boot.starter.segment;

import static me.ahoo.cosid.segment.IdSegment.TIME_TO_LIVE_FOREVER;

import me.ahoo.cosid.CosId;
import me.ahoo.cosid.jdbc.JdbcIdSegmentDistributor;
import me.ahoo.cosid.jdbc.JdbcIdSegmentInitializer;
import me.ahoo.cosid.segment.IdSegmentDistributor;
import me.ahoo.cosid.segment.SegmentChainId;
import me.ahoo.cosid.segment.concurrent.PrefetchWorkerExecutorService;
import me.ahoo.cosid.spring.boot.starter.IdConverterDefinition;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import jakarta.annotation.Nonnull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * SegmentId Properties.
 *
 * @author ahoo wang
 */
@ConfigurationProperties(prefix = SegmentIdProperties.PREFIX)
public class SegmentIdProperties {

    public static final String PREFIX = CosId.COSID_PREFIX + "segment";

    private boolean enabled = false;
    private Mode mode = Mode.CHAIN;
    /**
     * idSegment time to live.
     * unit {@link java.util.concurrent.TimeUnit#SECONDS}
     */
    private long ttl = TIME_TO_LIVE_FOREVER;
    private Distributor distributor;
    private Chain chain;
    private ShardIdDefinition share;
    private Map<String, IdDefinition> provider;

    public SegmentIdProperties() {
        share = new ShardIdDefinition();
        distributor = new Distributor();
        chain = new Chain();
        provider = new HashMap<>();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public Chain getChain() {
        return chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }

    public ShardIdDefinition getShare() {
        return share;
    }

    public void setShare(ShardIdDefinition share) {
        this.share = share;
    }

    @Nonnull
    public Map<String, IdDefinition> getProvider() {
        return provider;
    }

    public void setProvider(Map<String, IdDefinition> provider) {
        this.provider = provider;
    }

    public enum Mode {
        SEGMENT,
        CHAIN
    }

    public static class Chain {
        private int safeDistance = SegmentChainId.DEFAULT_SAFE_DISTANCE;
        private PrefetchWorker prefetchWorker;

        public Chain() {
            prefetchWorker = new PrefetchWorker();
        }

        public int getSafeDistance() {
            return safeDistance;
        }

        public void setSafeDistance(int safeDistance) {
            this.safeDistance = safeDistance;
        }

        public PrefetchWorker getPrefetchWorker() {
            return prefetchWorker;
        }

        public void setPrefetchWorker(PrefetchWorker prefetchWorker) {
            this.prefetchWorker = prefetchWorker;
        }

        public static class PrefetchWorker {

            private Duration prefetchPeriod = PrefetchWorkerExecutorService.DEFAULT_PREFETCH_PERIOD;
            private int corePoolSize = Runtime.getRuntime().availableProcessors();
            private boolean shutdownHook = true;

            public Duration getPrefetchPeriod() {
                return prefetchPeriod;
            }

            public void setPrefetchPeriod(Duration prefetchPeriod) {
                this.prefetchPeriod = prefetchPeriod;
            }

            public int getCorePoolSize() {
                return corePoolSize;
            }

            public void setCorePoolSize(int corePoolSize) {
                this.corePoolSize = corePoolSize;
            }

            public boolean isShutdownHook() {
                return shutdownHook;
            }

            public void setShutdownHook(boolean shutdownHook) {
                this.shutdownHook = shutdownHook;
            }
        }
    }

    public static class Distributor {
        public static final String TYPE = PREFIX + ".distributor.type";
        private Type type = Type.REDIS;
        private Redis redis;
        private Jdbc jdbc;
        private Mongo mongo;

        public Distributor() {
            this.redis = new Redis();
            this.jdbc = new Jdbc();
            this.mongo = new Mongo();
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Redis getRedis() {
            return redis;
        }

        public void setRedis(Redis redis) {
            this.redis = redis;
        }

        public Jdbc getJdbc() {
            return jdbc;
        }

        public void setJdbc(Jdbc jdbc) {
            this.jdbc = jdbc;
        }

        public Mongo getMongo() {
            return mongo;
        }

        public void setMongo(Mongo mongo) {
            this.mongo = mongo;
        }

        public static class Redis {

            private Duration timeout = Duration.ofSeconds(1);

            public Duration getTimeout() {
                return timeout;
            }

            public void setTimeout(Duration timeout) {
                this.timeout = timeout;
            }
        }

        public static class Jdbc {

            private String incrementMaxIdSql = JdbcIdSegmentDistributor.INCREMENT_MAX_ID_SQL;
            private String fetchMaxIdSql = JdbcIdSegmentDistributor.FETCH_MAX_ID_SQL;
            private boolean enableAutoInitCosidTable = false;
            private String initCosidTableSql = JdbcIdSegmentInitializer.INIT_COSID_TABLE_SQL;
            private boolean enableAutoInitIdSegment = true;
            private String initIdSegmentSql = JdbcIdSegmentInitializer.INIT_ID_SEGMENT_SQL;

            public String getIncrementMaxIdSql() {
                return incrementMaxIdSql;
            }

            public void setIncrementMaxIdSql(String incrementMaxIdSql) {
                this.incrementMaxIdSql = incrementMaxIdSql;
            }

            public String getFetchMaxIdSql() {
                return fetchMaxIdSql;
            }

            public void setFetchMaxIdSql(String fetchMaxIdSql) {
                this.fetchMaxIdSql = fetchMaxIdSql;
            }

            public boolean isEnableAutoInitCosidTable() {
                return enableAutoInitCosidTable;
            }

            public void setEnableAutoInitCosidTable(boolean enableAutoInitCosidTable) {
                this.enableAutoInitCosidTable = enableAutoInitCosidTable;
            }

            public String getInitCosidTableSql() {
                return initCosidTableSql;
            }

            public void setInitCosidTableSql(String initCosidTableSql) {
                this.initCosidTableSql = initCosidTableSql;
            }

            public boolean isEnableAutoInitIdSegment() {
                return enableAutoInitIdSegment;
            }

            public void setEnableAutoInitIdSegment(boolean enableAutoInitIdSegment) {
                this.enableAutoInitIdSegment = enableAutoInitIdSegment;
            }

            public String getInitIdSegmentSql() {
                return initIdSegmentSql;
            }

            public void setInitIdSegmentSql(String initIdSegmentSql) {
                this.initIdSegmentSql = initIdSegmentSql;
            }

        }

        public static class Mongo {
            private String database = "cosid_db";

            public String getDatabase() {
                return database;
            }

            public void setDatabase(String database) {
                this.database = database;
            }
        }

        public enum Type {
            REDIS,
            JDBC,
            MONGO,
            ZOOKEEPER,
            PROXY
        }
    }

    public static class IdDefinition {
        private String namespace;
        private Mode mode;
        private long offset = IdSegmentDistributor.DEFAULT_OFFSET;
        private long step = IdSegmentDistributor.DEFAULT_STEP;
        /**
         * idSegment Ttl.
         * unit {@link java.util.concurrent.TimeUnit#SECONDS}
         */
        private Long ttl;
        private Chain chain;
        @NestedConfigurationProperty
        private IdConverterDefinition converter = new IdConverterDefinition();
        private Group group = new Group();

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public long getStep() {
            return step;
        }

        public void setStep(long step) {
            this.step = step;
        }

        public Long getTtl() {
            return ttl;
        }

        public void setTtl(Long ttl) {
            this.ttl = ttl;
        }

        public Chain getChain() {
            return chain;
        }

        public void setChain(Chain chain) {
            this.chain = chain;
        }

        public IdConverterDefinition getConverter() {
            return converter;
        }

        public void setConverter(IdConverterDefinition converter) {
            this.converter = converter;
        }

        public Group getGroup() {
            return group;
        }

        public void setGroup(Group group) {
            this.group = group;
        }

        public static class Group {
            private GroupBy by = GroupBy.NEVER;
            private String pattern;

            public GroupBy getBy() {
                return by;
            }

            public Group setBy(GroupBy by) {
                this.by = by;
                return this;
            }

            public String getPattern() {
                return pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }
        }

        public enum GroupBy {
            YEAR,
            YEAR_MONTH,
            YEAR_MONTH_DAY,
            NEVER
        }
    }

    public static class ShardIdDefinition extends IdDefinition {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
