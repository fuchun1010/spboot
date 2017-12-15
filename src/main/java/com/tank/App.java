package com.tank;

import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

/**
 * @author fuchun
 */
@SpringBootApplication
public class App {

  public static void main(final String... args) {

    ApplicationContext context = SpringApplication.run(App.class);

    BlockingQueue<String> queue = (BlockingQueue<String>) context.getBean("importSqlQueue");
    Executors.newCachedThreadPool().execute(() -> {
      while (true) {
        try {
          System.out.println("start retrieve sql");
          String insertSql = queue.take();
          System.out.println(".....sql...." + insertSql);
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
