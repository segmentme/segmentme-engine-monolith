package io.segmentme.core.db.service.rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SegmentationAnalysisService {
//
//    private final FirebaseService firebaseService;
//
//    private final ConditionMatcher conditionMatcher;
//
//    public FeatureFlagSettings analyze(FeatureAnalysisContext context) {
//
//        FeatureFlagAnalysisRule featureFlagAnalysisRule = firebaseService.getAnalysisRule();
//
//        if (featureFlagAnalysisRule == null) {
//            return new FeatureFlagSettings(Collections.emptyMap());
//        }
//
//        Map<String, Boolean> groups = analyze(featureFlagAnalysisRule.getGroups(), context);
//
//        Map<String, Boolean> flags = analyze(featureFlagAnalysisRule.getFlags(), context);
//
//        groups.forEach((k, v) -> flags.compute(k, (key, value) -> value == null ? v : v && value));
//
//        return new FeatureFlagSettings(new HashMap<>(flags));
//    }
//
//    private boolean match(BooleanAnalysisRule rule, FeatureAnalysisContext context) {
//        return rule.getValue().equals(isMatch(rule, context));
//    }
//
//    public <T> T getRuleValueIfSatisfy(AbstractAnalysisRule<T> rule, FeatureAnalysisContext context) {
//        return isMatch(rule, context) ? rule.getValue() : null;
//    }
//
//    private boolean isMatch(AbstractAnalysisRule<?> rule, FeatureAnalysisContext context) {
//        if (CollectionUtils.isEmpty(rule.getConditions())) {
//            return true;
//        }
//
//        return rule.getAggregation() == AggregationType.OR
//                ? rule.getConditions().stream().anyMatch(condition -> match(condition, context))
//                : rule.getConditions().stream().allMatch(condition -> match(condition, context));
//
//    }
//
//    private boolean match(AbstractCondition<?> condition, FeatureAnalysisContext context) {
//        return conditionMatcher.match(condition, context);
//    }
//
//    private Map<String, Boolean> analyze(List<BooleanAnalysisRule> analysisRules, FeatureAnalysisContext context) {
//        return Optional.ofNullable(analysisRules).orElse(new ArrayList<>())
//                .stream()
//                .collect(HashMap::new, (map, value) -> {
//                    boolean match = match(value, context);
//                    value.getFlags().forEach(it -> map.put(it, match));
//                }, (map, value) -> {
//                });
//    }
}

