package md.fuel.bot.infrastructure.configuration;

import static org.ehcache.config.units.EntryUnit.ENTRIES;
import static org.ehcache.config.units.MemoryUnit.MB;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.cache.CacheManager;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableCaching
public class EhcacheConfiguration {

  public static final String TELEGRAM_BOT_CACHE = "telegramBotCache";
  public static final String J_CACHE_CACHE_MANAGER = "jCacheCacheManager";

  @Value("${cache.onheap.size}")
  private Integer onHeapSize;

  @Value("${cache.offheap.size}")
  private Integer offHeapSize;

  @Value("${cache.expiry.time}")
  private Integer expiryTime;

  @Bean(J_CACHE_CACHE_MANAGER)
  public JCacheCacheManager jCacheCacheManager(@Qualifier("cacheManager") CacheManager cacheManager) {
    return new JCacheCacheManager(cacheManager);
  }

  @Bean(name = "cacheManager", destroyMethod = "close")
  public CacheManager cacheManager(EhcacheCachingProvider ehcacheCachingProvider) {
    log.info("Create cache manager with {} MB on heap size, {} MB off heap size and {} minutes expiry time", onHeapSize,
        offHeapSize, expiryTime);

    final ResourcePools resourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder()
        .heap(onHeapSize, ENTRIES)
        .offheap(offHeapSize, MB)
        .build();

    final CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
        .newCacheConfigurationBuilder(Object.class, Object.class, resourcePools)
        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(expiryTime)))
        .build();

    final Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();
    log.info("Create configuration for {}", TELEGRAM_BOT_CACHE);
    caches.put(TELEGRAM_BOT_CACHE, cacheConfiguration);

    final org.ehcache.config.Configuration configuration = new DefaultConfiguration(caches,
        ehcacheCachingProvider.getDefaultClassLoader());

    return ehcacheCachingProvider.getCacheManager(ehcacheCachingProvider.getDefaultURI(), configuration);
  }

  @Bean
  public EhcacheCachingProvider ehcacheCachingProvider() {
    return new EhcacheCachingProvider();
  }
}