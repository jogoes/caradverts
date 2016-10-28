package module

import com.google.inject.AbstractModule
import repository.{CarAdvertRepository, JdbcCarAdvertRepository}

class RepositoryModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CarAdvertRepository])
//      .to(classOf[TransientInMemoryCarAdvertRepository])
      .to(classOf[JdbcCarAdvertRepository])
  }
}
