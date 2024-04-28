package me.benny.fcp.point;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static me.benny.fcp.point.QPoint.point;


public class PointCustomRepositoryImpl implements PointCustomRepository {
    
    public PointCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<ExpiredPointSummary> sumByExpiredDate(LocalDate alarmCriteriaDate, Pageable pageable) {

        QueryResults<ExpiredPointSummary> results = queryFactory
                .select(new QExpiredPointSummary(
                        point.pointWallet.userId,
                        point.amount.sum().coalesce(BigInteger.ZERO)))
                .from(point)
                .where(point.expired.eq(true))
                .where(point.used.eq(false))
                .where(point.expireDate.eq(alarmCriteriaDate))
                .groupBy(point.pointWallet)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ExpiredPointSummary> point = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(point, pageable, total);


    }
}
