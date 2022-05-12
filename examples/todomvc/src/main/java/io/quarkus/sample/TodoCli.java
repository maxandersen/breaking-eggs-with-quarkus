package io.quarkus.sample;

import javax.enterprise.context.ApplicationScoped;
import static java.lang.System.*; 
import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain(name = "todo")   
public class TodoCli implements QuarkusApplication {
  @Override
  public int run(String... args) throws Exception {   
    Arc.container().requestContext().activate();

    return 0;
 }

 public static void main(String... args) {
     Quarkus.run(TodoCli.class, args);
 }
}