package Application

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import common._

class MasterActor extends Actor {

	val numberKioskActors = ConfigFactory.load.getInt("number-kioskActors")
	val numberClientActors = ConfigFactory.load.getInt("number-clientActors")
	var numberTickets = ConfigFactory.load.getInt("number-TicketsA")
	var listOfActorRefs = new ListBuffer[ActorRef]()
	var leftNeighbor = self
	var rightNeighbor = self

	for (i <- 1 to numberKioskActors) {
		listOfActorRefs = context.actorOf(Props[KioskActors], name = "Kiosk"+i)::listOfActorRefs
	}

	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftNeighbor = leftNeighbor
			rightNeighbor = rightNeighbor
	}


}