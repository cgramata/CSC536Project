package Application 

import akka.actor.{ActorSystem, Props}

object MapReduceClient extends App {

	val system = ActorSystem("ticketKiosk")

	val master = system.actorOf(Props[MasterActor], name = "master")

	master ! Start
}