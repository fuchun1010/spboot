package com.tank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fuchun
 */
@SpringBootApplication
public class App {

  public static void main(final String... args) {

//    ApplicationContext context = SpringApplication.run(App.class, args);
//    ImportedDataProcessor processor = context.getBean(ImportedDataProcessor.class);
//    System.out.println(processor);
    SpringApplication.run(App.class, args);

  }


}
