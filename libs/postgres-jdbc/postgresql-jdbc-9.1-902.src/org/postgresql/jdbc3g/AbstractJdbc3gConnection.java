/*-------------------------------------------------------------------------
*
* Copyright (c) 2008-2011, PostgreSQL Global Development Group
*
*
*-------------------------------------------------------------------------
*/
package org.postgresql.jdbc3g;

import java.sql.SQLException;
import java.util.Properties;

import org.postgresql.core.Oid;
import org.postgresql.core.TypeInfo;

public abstract class AbstractJdbc3gConnection extends org.postgresql.jdbc3.AbstractJdbc3Connection
{

    public AbstractJdbc3gConnection(String host, int port, String user, String database, Properties info, String url) throws SQLException {
        super(host, port, user, database, info, url);

        TypeInfo types = getTypeInfo();
        if (haveMinimumServerVersion("8.3")) {
            types.addCoreType("uuid", Oid.UUID, java.sql.Types.OTHER, "java.util.UUID", Oid.UUID_ARRAY);
        }
    }

}

