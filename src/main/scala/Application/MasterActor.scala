package Application

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import common._

class MasterActor extends Actor {

	val numberKioskActors = ConfigFactory.load.getInt("number-kioskActors")
	val numberClientActors = ConfigFactory.load.getInt("number-clientActors")
	var numberTickets = ConfigFactory.load.getInt("number-TicketsA")
	var listOfKioskActorRefs = new ListBuffer[ActorRef]()
	var leftNeighbor = self
	var rightNeighbor = self

	for (i <- 1 to numberKioskActors) {
		listOfKioskActorRefs = context.actorOf(Props[KioskActors], name = "Kiosk"+i)::listOfKioskActorRefs
	}

	for (i <- 0 to listOfKioskActorRefs.size-1) {
		if (i == 0) {
			listOfKioskActorRefs(i) ! Neighbors(self, listOfKioskActorRefs(i+1))
		}
		if (i == listOfKioskActorRefs.size-1) {
			listOfKioskActorRefs(i) ! Neighbors(listOfKioskActorRefs(i-1), self)
		}
		else {
			listOfKioskActorRefs(i) ! Neighbors(listOfKioskActorRefs(i-1), listOfKioskActorRefs(i+1))
		}		
	}

	leftNeighbor = listOfKioskActorRefs(listOfKioskActorRefs.size-1)
	rightNeighbor = listOfKioskActorRefs(0)

	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftNeighbor = leftNeighbor
			rightNeighbor = rightNeighbor
	}


}