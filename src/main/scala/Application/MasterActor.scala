package Application

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import Common._

class MasterActor extends Actor {

	val numberKioskActors = ConfigFactory.load.getInt("number-kioskActors")
	val numberClientActors = ConfigFactory.load.getInt("number-clientActors")
	var numberTickets = ConfigFactory.load.getInt("number-TicketsA")
	var listOfKioskActorRefs = new ListBuffer[ActorRef]()
	var leftActorNeighbor = self
	var rightActorNeighbor = self

	for (i <- 1 to numberKioskActors) {
		listOfKioskActorRefs += context.actorOf(Props[KioskActor], name = "Kiosk"+i)
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

	leftActorNeighbor = listOfKioskActorRefs(listOfKioskActorRefs.size-1)
	rightActorNeighbor = listOfKioskActorRefs(0)

	if (numberTickets == 0) {
		rightActorNeighbor ! SoldOut
	}

	def receive = {
		case Start => 
			rightActorNeighbor ! End
		case End =>
			println("Start messaged received, cycle test complete.")
	}


}