package com.tank.utils;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class TaskQueue {


  @Bean(name = "taskProcessor")
  public ExecutorService taskProcessor() {
    val minThreads = 0;
    val maxThreads = Math.min(4, Runtime.getRuntime().availableProcessors());
    val keepAlive = 20L;
    return new ThreadPoolExecutor(minThreads, maxThreads, keepAlive, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
  }

}
