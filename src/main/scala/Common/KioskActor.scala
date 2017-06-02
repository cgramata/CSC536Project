package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import Common._

class KioskActor extends Actor {

	var numberTicketsInKiosk = 0
	var numberMaxNumberOfTickets = ConfigFactory.load.getInt("number-TicketsPerKiosk")
	var isEventSoldOut = false
	var leftActorNeighbor = self
	var rightActorNeighbor = self

	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftActorNeighbor = leftNeighbor
			rightActorNeighbor = rightNeighbor
		case End => 
			println(self.path.name + " received end message, sending to " + rightActorNeighbor.path.name)
			rightActorNeighbor ! End
	}

}

