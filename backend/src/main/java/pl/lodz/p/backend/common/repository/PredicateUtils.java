package pl.lodz.p.backend.common.repository;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.logging.log4j.util.Strings.isNotBlank;


public final class PredicateUtils {

    private static final String LIKE_CHAR = "%";

    private PredicateUtils() {
    }

    public static Predicate buildAndPredicates(final CriteriaBuilder criteriaBuilder, final Collection<Predicate> predicates) {
        return criteriaBuilder.and(predicates.stream().filter(Objects::nonNull).toArray(Predicate[]::new));
    }

    public static <T> void addEqualPredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<T> fieldPath,
                                             final T value) {
        if (nonNull(value)) {
            predicates.add(criteriaBuilder.equal(fieldPath, value));
        }
    }

    public static void addMultipleLikePredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final List<Path<String>> fieldPaths,
                                                final String value) {
        if (isNotBlank(value)) {
            final Predicate[] likePredicates = fieldPaths.stream()
                    .map(path -> createLikeCaseInsensitivePredicate(criteriaBuilder, path, value))
                    .toArray(Predicate[]::new);
            predicates.add(criteriaBuilder.or(likePredicates));
        }
    }

    public static void addLikePredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<String> fieldPath,
                                        final String value) {
        if (isNotBlank(value)) {
            predicates.add(createLikeCaseInsensitivePredicate(criteriaBuilder, fieldPath, value));
        }
    }

    public static Predicate createLikeCaseInsensitivePredicate(final CriteriaBuilder criteriaBuilder, final Path<String> path,
                                                               final String value) {
        return criteriaBuilder.like(criteriaBuilder.lower(path), LIKE_CHAR + value.toLowerCase() + LIKE_CHAR);
    }

    public static <T> void addInPredicate(final Collection<Predicate> predicates, final Path<T> fieldPath, final Collection<T> values) {
        if (isNotEmpty(values)) {
            predicates.add(fieldPath.in(values));
        }
    }

    public static void addLikePredicateAsFieldString(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<String> fieldPath,
                                                     final String value) {
        if (isNotBlank(value) || nonNull(value)) {
            predicates.add(criteriaBuilder.like(fieldPath.as(String.class), LIKE_CHAR + value.toLowerCase() + LIKE_CHAR));
        }
    }

    public static <T extends Number> void addBetweenPredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates,
                                                              final Path<T> path, T low, T high, Class<T> type) {

        if (low == null && high == null) {
            low = (T) getDefaultLowValue(type);
            high = (T) getDefaultHighValue(type);
        } else if (low == null) {
            low = (T) getDefaultLowValue(type);
        } else if (high == null) {
            high = (T) getDefaultHighValue(type);
        }


        // Dodanie predykatu do listy
        if (type.equals(Long.class)) {
            if ((Long) low >= (Long) high) {
                T temp = high;
                high = low;
                low = temp;
            }
            predicates.add(criteriaBuilder.between(path.as(Long.class), (Long) low, (Long) high));
        } else if (type.equals(Double.class)) {
            if ((Double) low >= (Double) high) {
                T temp = high;
                high = low;
                low = temp;
            }
            predicates.add(criteriaBuilder.between(path.as(Double.class), (Double) low, (Double) high));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private static Number getDefaultLowValue(Class<?> type) {
        if (type.equals(Long.class)) {
            return Long.MIN_VALUE; // or 0L if you prefer
        } else if (type.equals(Double.class)) {
            return Double.MIN_VALUE; // or 0.0 if you prefer
        } else if (type.equals(Integer.class)) {
            return Integer.MIN_VALUE; // or 0 if you prefer
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    private static Number getDefaultHighValue(Class<?> type) {
        if (type.equals(Long.class)) {
            return Long.MAX_VALUE; // or 0L if you prefer
        } else if (type.equals(Double.class)) {
            return Double.MAX_VALUE; // or 0.0 if you prefer
        } else if (type.equals(Integer.class)) {
            return Integer.MAX_VALUE; // or 0 if you prefer
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    public static void addBigDecimalBetweenPredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<BigDecimal> path,
                                                     BigDecimal low, BigDecimal high) {

        if (low == null && high == null) {
            low = BigDecimal.ZERO;
            high = BigDecimal.valueOf(Double.MAX_VALUE);
        } else if (low != null && high != null && low.compareTo(high) > 0) {
            BigDecimal temp = low;
            low = high;
            high = temp;
        }
        predicates.add(criteriaBuilder.between(path.as(BigDecimal.class), low, high));
    }

    public static void addLocalDateTimeBetweenPredicate(final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates, final Path<LocalDateTime> path,
                                                        LocalDateTime low, LocalDateTime high) {

        if (low == null && high == null) {
            low = LocalDateTime.MIN;
            high = LocalDateTime.MAX;
        } else if (low != null && high != null && low.isAfter(high)) {
            // Zamiana miejscami, jeśli low jest późniejsze od high
            LocalDateTime temp = low;
            low = high;
            high = temp;
        }
        predicates.add(criteriaBuilder.between(path.as(LocalDateTime.class), low, high));
    }
}