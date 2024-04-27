package me.benny.fcp.point;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPoint is a Querydsl query type for Point
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPoint extends EntityPathBase<Point> {

    private static final long serialVersionUID = 564491081L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPoint point = new QPoint("point");

    public final QIdEntity _super = new QIdEntity(this);

    public final NumberPath<java.math.BigInteger> amount = createNumber("amount", java.math.BigInteger.class);

    public final DatePath<java.time.LocalDate> earnedDate = createDate("earnedDate", java.time.LocalDate.class);

    public final BooleanPath expired = createBoolean("expired");

    public final DatePath<java.time.LocalDate> expireDate = createDate("expireDate", java.time.LocalDate.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final me.benny.fcp.point.wallet.QPointWallet pointWallet;

    public final BooleanPath used = createBoolean("used");

    public QPoint(String variable) {
        this(Point.class, forVariable(variable), INITS);
    }

    public QPoint(Path<? extends Point> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPoint(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPoint(PathMetadata metadata, PathInits inits) {
        this(Point.class, metadata, inits);
    }

    public QPoint(Class<? extends Point> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pointWallet = inits.isInitialized("pointWallet") ? new me.benny.fcp.point.wallet.QPointWallet(forProperty("pointWallet")) : null;
    }

}

