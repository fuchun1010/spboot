package com.tank;

import static com.tank.common.toolkit.DirectoryToolKit.*;
import com.tank.dao.ImportLogDAO;
import com.tank.domain.ImportedUnit;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.logging.Level.*;

import java.util.logging.Logger;

/**
 * @author fuchun
 */
@SpringBootApplication
public class App {

  public static void main(final String... args) {

    ApplicationContext context = SpringApplication.run(App.class);
    Logger logger = Logger.getLogger(App.class.getName());
    BlockingQueue<ImportedUnit> queue = (BlockingQueue<ImportedUnit>) context.getBean("importSqlQueue");
    JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("oracleJdbcTemplate");
    ImportLogDAO importLogDAO = (ImportLogDAO) context.getBean("importLog");
    createOrGetUpLoadPath("data");
    createOrGetUpLoadPath("schema");
    createLogDir("logs");
    //将队列里的导入批次单元一组一组取出来，并开始正式写入到oracle里去
    Executors.newCachedThreadPool().execute(() -> {
      while (true) {
        try {
          ImportedUnit importedUnit = queue.take();
          if (importedUnit.getTotalRows() != 0) {
            importLogDAO.updateTotalRecordsByRecordFlag(importedUnit);
          }
          if(importedUnit.getSuccess_records() != 0) {
            importLogDAO.updateSuccessRecords(importedUnit);
          }
          if (importedUnit.isOver()) {
            importLogDAO.endImportedLog(importedUnit);
          } else {
            try {
              jdbcTemplate.execute(importedUnit.getInsertSql());

              System.out.println("inserted ok");
            } catch (DataAccessException e) {
              importLogDAO.importFailed(importedUnit, e.getLocalizedMessage());
              e.printStackTrace();
              System.out.println(e.getLocalizedMessage());
              //TODO call api
            }
          }



        } catch (InterruptedException e) {
          logger.log(WARNING, " write oracle exception:" + e.getLocalizedMessage());
          e.printStackTrace();
        }
      }
    });

  }

  @Bean(name = "importSqlQueue")
  public BlockingQueue<ImportedUnit> importSqlQueue() {
    return new LinkedBlockingDeque<>();
  }

  @Bean(name = "importLog")
  public ImportLogDAO importLogDAO() {
    return this.importLogDAO;
  }

  @Autowired
  public ImportLogDAO importLogDAO;

}
