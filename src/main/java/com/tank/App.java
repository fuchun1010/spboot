package com.tank;

import com.tank.common.toolkit.DirectoryToolKit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author fuchun
 */
@SpringBootApplication
public class App {

  public static void main(final String... args) {

    ApplicationContext context = SpringApplication.run(App.class);

    BlockingQueue<String> queue = (BlockingQueue<String>) context.getBean("importSqlQueue");
    JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("oracleJdbcTemplate");
    DirectoryToolKit.upLoadPath("data");
    DirectoryToolKit.upLoadPath("schema");
    Executors.newCachedThreadPool().execute(() -> {
      while (true) {
        try {
          String insertSql = queue.take();
          jdbcTemplate.execute(insertSql);
          System.out.println("inserted ok");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

  }

  @Bean(name = "importSqlQueue")
  public BlockingQueue<String> importSqlQueue() {
    return new LinkedBlockingDeque<>();
  }


}
