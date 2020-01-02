package cluster

import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import akka.cluster.ClusterEvent.{ClusterDomainEvent, CurrentClusterState, InitialStateAsEvents}
import akka.cluster.{Cluster, Member}
import cluster.ClusterListenerActor.ShowClusterState

import scala.concurrent.duration._

class ClusterListenerActor extends Actor with ActorLogging {

  implicit val ec = context.system.dispatcher

  val cluster = Cluster(context.system)
  var showClusterStateCancelable: Option[Cancellable] = None

  override def preStart(): Unit = {
    log.debug("Start")
    cluster.subscribe(self, InitialStateAsEvents, classOf[ClusterDomainEvent])
  }

  override def postStop(): Unit = {
    log.debug("Stop")
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    case ShowClusterState => showClusterState()
    case clusterEventMessage => logClusterEvent(clusterEventMessage)
  }

  private def showClusterState(): Unit = {
    log.info(s"ShowClusterState sent to ${cluster.selfMember}")
    logClusterMembers(cluster.state)
    showClusterStateCancelable = None
  }

  def logClusterEvent(clusterEventMessage: Any): Unit = {
    log.info(s"$clusterEventMessage sent to ${cluster.selfMember}")
    logClusterMembers()
  }

  def logClusterMembers(): Unit = {
    logClusterMembers(cluster.state)
    if (showClusterStateCancelable.isEmpty) {
      showClusterStateCancelable = Some(
        context.system.scheduler.scheduleOnce(
          delay = 15.seconds,
          receiver = self,
          message = ShowClusterState
        )
      )
    }
  }

  def logClusterMembers(currentClusterState: CurrentClusterState): Unit = {
    // FIXME: Or reduceLeftOption or fold?
    val old = currentClusterState.members.reduceOption { (older, member) =>
      if (older.isOlderThan(member)) older
      else member
    }
    val oldest = old.getOrElse(cluster.selfMember)

    def prefix(member: Member) = {
      var result = ""
      if (member.address == currentClusterState.getLeader) result += "(LEADER) "
      if (member == oldest) result += "(OLDEST) "
      result
    }

    currentClusterState.members.zipWithIndex.foreach {
      case (member, index) =>
        log.info(s"${index + 1} ${prefix(member)}$member")
    }
  }

}

object ClusterListenerActor {

  def props = Props(new ClusterListenerActor())

  case object ShowClusterState

}
