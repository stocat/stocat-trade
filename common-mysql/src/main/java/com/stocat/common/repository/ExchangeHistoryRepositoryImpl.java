package com.stocat.common.repository;

import static com.stocat.common.domain.exchange.QExchangeHistoryEntity.exchangeHistoryEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.stocat.common.domain.exchange.ExchangeHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExchangeHistoryRepositoryImpl implements ExchangeHistoryCustomRepository {

    private final JPQLQueryFactory jpqlQueryFactory;

    @Override
    public Page<ExchangeHistoryEntity> findExchanges(Long userId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        List<ExchangeHistoryEntity> content = jpqlQueryFactory
                .selectFrom(exchangeHistoryEntity)
                .where(
                        exchangeHistoryEntity.userId.eq(userId),
                        afterOrEqual(from),
                        beforeOrEqual(to)
                )
                .orderBy(exchangeHistoryEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<Long> countQuery = jpqlQueryFactory
                .select(exchangeHistoryEntity.count())
                .from(exchangeHistoryEntity)
                .where(
                        exchangeHistoryEntity.userId.eq(userId),
                        afterOrEqual(from),
                        beforeOrEqual(to)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression afterOrEqual(LocalDateTime from) {
        return from != null ? exchangeHistoryEntity.createdAt.goe(from) : null;
    }

    private BooleanExpression beforeOrEqual(LocalDateTime to) {
        return to != null ? exchangeHistoryEntity.createdAt.loe(to) : null;
    }
}