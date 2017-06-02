package Application 

import akka.actor.{ActorSystem, Props}
import Common._

object TicketKioskMain extends App {

	val system = ActorSystem("ticketKiosk")

	val master = system.actorOf(Props[MasterActor], name = "master")

	master ! Start

	Thread.sleep(5000)

	system.terminate
}