package com.sy.coladay;

import static java.lang.System.getenv;

import com.sy.coladay.init.ApplicationInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Boot loader of the cola application.
 *
 * @author selim
 */
@SpringBootApplication
public class Bootstrap {

  public static void main(String[] args) {

    if ("true".equalsIgnoreCase(getenv("ENABLE_COLADAY_INIT_MODE"))) {
      var initializer = new ApplicationInitializer();
      initializer.initApplication();
      return;
    }
    if ("true".equalsIgnoreCase(getenv("ENABLE_COLADAY_LOCAL_DEV"))) {
      var initializer = new ApplicationInitializer();
      initializer.initApplication();
      SpringApplication.run(Bootstrap.class, args);
      return;
    }
    SpringApplication.run(Bootstrap.class, args);
  }


}
