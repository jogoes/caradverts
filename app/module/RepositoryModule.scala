package module

import com.google.inject.AbstractModule
import repository.{CarAdvertRepository, TransientInMemoryCarAdvertRepository}

class RepositoryModule extends AbstractModule {
  override def configure(): Unit = {
    // for now bind this to our transient repository implementation in order to getting started
    bind(classOf[CarAdvertRepository])
      .to(classOf[TransientInMemoryCarAdvertRepository])
  }
}
