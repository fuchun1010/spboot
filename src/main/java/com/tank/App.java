package com.tank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fuchun
 */
@SpringBootApplication
public class App {

  public static void main(final String... args) {

    ApplicationContext context = SpringApplication.run(App.class, args);
    ImportedDataProcessor processor = context.getBean(ImportedDataProcessor.class);
    System.out.print(processor);
  }

  @Component
  class ImportedDataProcessor {

    @Autowired
    private ExecutorService taskExecutors;

    public void execute() {
      if (!Objects.isNull(taskExecutors)) {
        taskExecutors.execute(() -> System.out.print("xxx"));
      }

    }
  }

  @Bean
  public ExecutorService taskExecutors() {
    int coreSize = Math.min(4, Runtime.getRuntime().availableProcessors());

    ExecutorService tasks = new ThreadPoolExecutor(0,
        coreSize,
        200,
        TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<Runnable>(200));

    return tasks;
  }

}
