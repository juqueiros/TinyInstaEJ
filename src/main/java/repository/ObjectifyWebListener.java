package repository;

import javax.servlet.ServletContextEvent;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

@WebListener
public class ObjectifyWebListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent event) {
	  
	  if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
		  ObjectifyService.init();
	  } else {
		  ObjectifyService.init(new ObjectifyFactory(
				  DatastoreOptions.newBuilder().setHost("http://localhost:8484").setProjectId("tinyinsta-257116").build().getService()));
	  }
    // This is a good place to register your POJO entity classes.
    // ObjectifyService.register(YourEntity.class);
	  ObjectifyService.register(User.class);
	  ObjectifyService.register(Post.class);
	  ObjectifyService.begin();
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
  }
}