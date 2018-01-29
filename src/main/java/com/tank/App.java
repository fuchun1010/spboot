package com.tank;

import com.tank.common.toolkit.DirectoryToolKit;
import com.tank.dao.ImportLogDAO;
import com.tank.domain.ImportedUnit;
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
    DirectoryToolKit.createOrGetUpLoadPath("data");
    DirectoryToolKit.createOrGetUpLoadPath("schema");
    DirectoryToolKit.createLogDir("logs");
    Executors.newCachedThreadPool().execute(() -> {
      while (true) {
        try {
          ImportedUnit importedUnit = queue.take();
          if (importedUnit.isOver()) {
            importLogDAO.endImportedLog(importedUnit);
          } else {
            try {
              jdbcTemplate.execute(importedUnit.getInsertSql());
            }catch(DataAccessException e) {
              e.printStackTrace();
              //TODO call api
            }

          }

          System.out.println("inserted ok");
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
