package express.field.agent.Helpers.db.filters;

import com.couchbase.lite.Collation;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.SelectResult;

import express.field.agent.Helpers.db.enums.DbObjectModel;
import express.field.agent.Helpers.db.enums.DbObjectSyncStatus;
import express.field.agent.Utils.Constants;


public class FilterFactory {

    public static Filter getCount(final Expression where) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.selectDistinct(
                    SelectResult.expression(Function.count(Expression.all())).as(Constants.COUNT_KEY))
                    .from(DataSource.database(database))
                    .where(where)
                    .limit(Expression.intValue(Integer.MAX_VALUE));
            }
        };
    }


    public static Filter getFilterWithPagination(final String[] selectFields, final Expression where, final int limit, final int offset) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(getProperties(selectFields))
                    .from(DataSource.database(database))
                    .where(where)
                    .limit(Expression.intValue(validLimitSize(limit)), Expression.intValue(offset));
            }
        };
    }


    public static Filter getFilter(final String[] selectFields, final Expression where, final int limit) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(getProperties(selectFields))
                    .from(DataSource.database(database))
                    .where(where)
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }

    public static Filter getFilter(final String[] selectFields, final Expression where) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(getProperties(selectFields))
                    .from(DataSource.database(database))
                    .where(where)
                    .limit(Expression.intValue(Constants.DEFAULT_FILTER_LIMIT_SIZE));
            }
        };
    }

    public static Filter getFilter(final String[] selectFields, final Expression where, final int limit, final String orderByProperty, boolean isAscendingOrder) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                Expression expression = Expression.property(orderByProperty);

                Collation.Unicode collation = Collation.unicode().setLocale(null).setIgnoreCase(true).setIgnoreAccents(true);

                return QueryBuilder.select(getProperties(selectFields))
                    .from(DataSource.database(database))
                    .where(where)
                    .groupBy(expression.collate(collation))
                    .orderBy(((isAscendingOrder) ? Ordering.expression(expression.collate(collation)).ascending() : Ordering.expression(expression.collate(collation)).descending()))
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }

    public static Filter getFilter(final SelectResult.As[] selectFields, final Expression where, final int limit) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                SelectResult.As[] defaultProps = getDefaultProps();
                SelectResult.As[] res = new SelectResult.As[selectFields.length + defaultProps.length];
                System.arraycopy(defaultProps, 0, res, 0, defaultProps.length);
                System.arraycopy(selectFields, 0, res, defaultProps.length, selectFields.length);
                return QueryBuilder.select(res)
                    .from(DataSource.database(database))
                    .where(where)
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }

    public static Filter getFilter(final String[] selectFields, final DbObjectModel modelname, final int limit) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(getProperties(selectFields))
                    .from(DataSource.database(database))
                    .where(Expression.property(Constants.MODELNAME).equalTo(Expression.string(modelname.name())))
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }

    public static Filter getFilter(final String[] selectFields, final DbObjectSyncStatus status, final int limit) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(getProperties(selectFields))
                    .from(DataSource.database(database))
                    .where(Expression.property(Constants.SYNC_STATUS).equalTo(Expression.string(status.name())))
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }

    public static Filter getFilter(final DbObjectSyncStatus status, final int limit) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(database))
                    .where(Expression.property(Constants.SYNC_STATUS).equalTo(Expression.string(status.name())))
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }

    public static Filter getFilter(final DbObjectModel model, final int limit) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(database))
                    .where(Expression.property(Constants.MODELNAME).equalTo(Expression.string(model.name())))
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }

    public static Filter getFilter(final Expression where, final int limit) {
        return new Filter() {
            @Override
            protected Query getQuery() {
                return QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(database))
                    .where(where)
                    .limit(Expression.intValue(validLimitSize(limit)));
            }
        };
    }
}
