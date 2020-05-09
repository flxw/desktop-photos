package de.flxw.demo.configuration;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.CustomType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class PersistenceConfiguration extends H2Dialect {
    public PersistenceConfiguration() {
        super();
        Type arrayType = new CustomType(StringArrayUserType.INSTANCE);
        registerFunction("array_agg", new StandardSQLFunction("ARRAY_AGG", arrayType));
    }
}