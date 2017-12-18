package com.tank;

import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.*;

/**
 * @author fuchun
 */
@SpringBootApplication
public class App {

  public static void main(final String... args) {

    ApplicationContext context = SpringApplication.run(App.class);

    BlockingQueue<String> queue = (BlockingQueue<String>) context.getBean("importSqlQueue");
    JdbcTemplate oracleJdbcTemplate = (JdbcTemplate) context.getBean("oracleJdbcTemplate");
    Executors.newCachedThreadPool().execute(() -> {
      while (true) {
        try {
          String insertSql = queue.take();
          oracleJdbcTemplate.execute(insertSql);
          System.out.println("inserted ok");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

  }

  @Bean(name = "importSqlQueue")
  public BlockingQueue<String> importSqlQueue() {
    return new ArrayBlockingQueue<String>(20);
  }


}
