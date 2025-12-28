/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myyzw.myweb2.domain.entity.GoodsSnapshotEntity;
import com.myyzw.myweb2.domain.entity.SkuEntity;
import com.myyzw.myweb2.domain.repository.GoodsSnapshotRepository;
import com.myyzw.myweb2.domain.repository.SkuRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * 商品数据服务
 */
@Service
public class GoodsLocalService {

    private static final Logger log = LoggerFactory.getLogger(GoodsLocalService.class);

    private final ApiCacheService apiCacheService;
    private final ExternalApiService externalApiService;
    private final GoodsSnapshotRepository goodsSnapshotRepository;
    private final SkuRepository skuRepository;
    private final ObjectMapper objectMapper;

    public GoodsLocalService(ApiCacheService apiCacheService,
                             ExternalApiService externalApiService,
                             GoodsSnapshotRepository goodsSnapshotRepository,
                             SkuRepository skuRepository,
                             ObjectMapper objectMapper) {
        this.apiCacheService = apiCacheService;
        this.externalApiService = externalApiService;
        this.goodsSnapshotRepository = goodsSnapshotRepository;
        this.skuRepository = skuRepository;
        this.objectMapper = objectMapper;
    }

    public Mono<Object> getGoodsDetail(Long spuId, String token) {
        Map<String, Object> params = Map.of("id", spuId);
        return apiCacheService.getOrFetch(
                        "goods-detail",
                        params,
                        () -> externalApiService.get("/goods", params, token))
                .doOnNext(payload -> persistGoodsSnapshot(spuId, payload));
    }

    public Mono<Object> getHotGoods(Long spuId, Integer type, Integer limit, String token) {
        Map<String, Object> params = Map.of("id", spuId, "type", type, "limit", limit);
        return apiCacheService.getOrFetch(
                "goods-hot",
                params,
                () -> externalApiService.get("/goods/hot", params, token)
        );
    }

    /**
     * 供购物车/订单读取SKU元数据。
     */
    public Optional<SkuEntity> findSku(Long skuId) {
        return skuRepository.findBySkuId(skuId).or(() -> fetchSkuFromUpstream(skuId));
    }

    @Transactional
    protected void persistGoodsSnapshot(Long spuId, Object payload) {
        try {
            JsonNode root = objectMapper.valueToTree(payload);
            JsonNode result = root.path("result");
            if (result.isMissingNode() || result.isNull()) {
                return;
            }
            String json = objectMapper.writeValueAsString(result);
            GoodsSnapshotEntity snapshot = goodsSnapshotRepository.findBySpuId(spuId)
                    .orElseGet(() -> new GoodsSnapshotEntity(spuId, json));
            snapshot.setPayload(json);
            snapshot.setUpdatedAt(LocalDateTime.now());
            goodsSnapshotRepository.save(snapshot);
            extractSku(spuId, result);
        } catch (JsonProcessingException e) {
            log.error("商品数据持久化失败, spuId={}", spuId, e);
        }
    }

    private void extractSku(Long spuId, JsonNode result) {
        JsonNode skus = result.path("skus");
        if (!skus.isArray()) {
            return;
        }
        skus.forEach(node -> {
            long skuId = node.path("id").asLong();
            SkuEntity sku = skuRepository.findBySkuId(skuId).orElseGet(SkuEntity::new);
            sku.setSkuId(skuId);
            sku.setSpuId(spuId);
            sku.setName(node.path("name").asText(""));
            sku.setPrice(new BigDecimal(node.path("price").asText("0")));
            sku.setOldPrice(new BigDecimal(node.path("oldPrice").asText("0")));
            sku.setInventory(node.path("inventory").asInt(0));
            sku.setPicture(node.path("picture").asText(""));
            // 拼接规格文案
            StringBuilder specs = new StringBuilder();
            JsonNode specsArr = node.path("specs");
            if (specsArr.isArray()) {
                specsArr.forEach(spec -> specs.append(spec.path("name").asText(""))
                        .append("：")
                        .append(spec.path("valueName").asText(""))
                        .append(" "));
            }
            sku.setAttrsText(specs.toString().trim());
            skuRepository.save(sku);
        });
    }

    private Optional<SkuEntity> fetchSkuFromUpstream(Long skuId) {
        try {
            Object response = externalApiService.get("/goods/sku/" + skuId, null, null)
                    .block(Duration.ofSeconds(5));
            if (response == null) {
                return Optional.empty();
            }
            Map<String, Object> map = objectMapper.convertValue(
                    response, new TypeReference<Map<String, Object>>() {});
            Object resultObj = map.get("result");
            if (resultObj instanceof Map<?, ?> resultMap) {
                Object spuIdObj = resultMap.get("spuId");
                if (spuIdObj != null) {
                    long spuId = Long.parseLong(spuIdObj.toString());
                    getGoodsDetail(spuId, null).block(Duration.ofSeconds(5));
                    return skuRepository.findBySkuId(skuId);
                }
            }
        } catch (Exception e) {
            log.warn("尝试从上游补全SKU失败: {}", skuId, e);
        }
        return Optional.empty();
    }
}

