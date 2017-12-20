package com.tank.common.toolkit;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SchemaToolKit {


    public void createSchema(@NonNull String schemaSql) throws DataAccessException {
        this.oracleJdbcTemplate.execute(schemaSql);
    }

    @Autowired
    private JdbcTemplate oracleJdbcTemplate;
}
