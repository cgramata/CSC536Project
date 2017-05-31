package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import common._

class KioskActor extends Actor {

	var numberTicketsInKiosk = 0
	var numberMaxNumberOfTickets = ConfigFactory.load.getInt("number-TicketsPerKiosk")
	var isEventSoldOut = False
	var leftNeighbor = self
	var rightNeighbor = self

	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftNeighbor = leftNeighbor
			rightNeighbor = rightNeighbor
	}

}

