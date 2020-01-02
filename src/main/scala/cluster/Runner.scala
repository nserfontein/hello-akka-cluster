package cluster

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.management.scaladsl.AkkaManagement
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future

object Runner extends App {

  if (args.length == 0) {
    startupClusterNodes(Seq("2551", "2552", "0"))
  } else {
    startupClusterNodes(args)
  }

  def startupClusterNodes(ports: Seq[String]): Unit = {
    println(s"Starting cluster on port(s) $ports")

    ports.foreach { port =>
      val actorSystem: ActorSystem = ActorSystem.create("cluster", setupClusterNodeConfig(port))

      AkkaManagement(actorSystem).start()

      actorSystem.actorOf(
        props = ClusterListenerActor.props,
        name = "clusterListener"
      )

      addCoordinatedShutdownTask(actorSystem, CoordinatedShutdown.PhaseClusterShutdown)

      actorSystem.log.info(s"Akka node TODO{actorSystem.provider.getDefaultAddress} ???")

    }
  }

  def setupClusterNodeConfig(port: String): Config = {
    ConfigFactory.parseString(
      s"""akka.remote.netty.tcp.port=$port
         |akka.remote.artery.canonical.port=$port
         |""".stripMargin)
      .withFallback(ConfigFactory.load())
  }

  def addCoordinatedShutdownTask(actorSystem: ActorSystem, coordinatedShutdownPhase: String): Unit = {
    CoordinatedShutdown(actorSystem)
      .addTask(coordinatedShutdownPhase, coordinatedShutdownPhase) { () =>
        actorSystem.log.warning(s"Coordinated shutdown phase $coordinatedShutdownPhase")
        Future.successful(Done)
      }
  }

}
