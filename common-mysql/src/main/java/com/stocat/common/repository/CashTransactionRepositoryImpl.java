package com.stocat.common.repository;

import static com.stocat.common.domain.cash.QCashTransactionEntity.cashTransactionEntity;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.stocat.common.domain.Currency;
import com.stocat.common.domain.cash.CashTransactionEntity;
import com.stocat.common.domain.cash.CashTransactionType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CashTransactionRepositoryImpl implements CashTransactionCustomRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

    @Override
    public Page<CashTransactionEntity> findTransactions(Long userId, Currency currency, CashTransactionType type,
                                                        Pageable pageable) {
        // 1. 실제 데이터를 가져오는 쿼리 (페이징 적용)
        List<CashTransactionEntity> content = jpqlQueryFactory
                .selectFrom(cashTransactionEntity)
                .where(
                        cashTransactionEntity.userId.eq(userId),
                        cashTransactionEntity.currency.eq(currency),
                        cashTransactionEntity.transactionType.eq(type)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 전체 데이터 개수를 가져오는 카운트 쿼리
        JPQLQuery<Long> countQuery = jpqlQueryFactory
                .select(cashTransactionEntity.count())
                .from(cashTransactionEntity)
                .where(
                        cashTransactionEntity.userId.eq(userId),
                        cashTransactionEntity.currency.eq(currency),
                        cashTransactionEntity.transactionType.eq(type)
                );

        // 3. PageableExecutionUtils 를 사용하여 Page 객체 생성
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
