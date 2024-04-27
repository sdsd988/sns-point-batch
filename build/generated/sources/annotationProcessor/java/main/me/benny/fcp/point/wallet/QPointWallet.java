package me.benny.fcp.point.wallet;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPointWallet is a Querydsl query type for PointWallet
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPointWallet extends EntityPathBase<PointWallet> {

    private static final long serialVersionUID = -1476744741L;

    public static final QPointWallet pointWallet = new QPointWallet("pointWallet");

    public final me.benny.fcp.point.QIdEntity _super = new me.benny.fcp.point.QIdEntity(this);

    public final NumberPath<java.math.BigInteger> amount = createNumber("amount", java.math.BigInteger.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath userId = createString("userId");

    public QPointWallet(String variable) {
        super(PointWallet.class, forVariable(variable));
    }

    public QPointWallet(Path<? extends PointWallet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPointWallet(PathMetadata metadata) {
        super(PointWallet.class, metadata);
    }

}

