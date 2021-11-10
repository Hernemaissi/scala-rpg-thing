package repos

import javax.inject.{Inject, Singleton}
import akka.actor.ActorSystem
import play.libs.concurrent.CustomExecutionContext

@Singleton
class DatabaseExecutionContext @Inject() (system: ActorSystem) extends CustomExecutionContext(system, "database.dispatcher")
