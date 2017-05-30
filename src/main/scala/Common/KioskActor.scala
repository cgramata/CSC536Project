package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import common._

class KioskActor extends Actor {

	var numberTicketsInKiosk = ConfigFactory.load.getInt("number-TicketsPerKiosk")
	var leftNeighbor = self
	var rightNeighbor = self

	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftNeighbor = leftNeighbor
			rightNeighbor = rightNeighbor
	}

}

