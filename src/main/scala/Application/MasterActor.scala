package Application

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import Common._

class MasterActor extends Actor {

	val numberKioskActors = ConfigFactory.load.getInt("number-kioskActors")
	val numberClientActors = ConfigFactory.load.getInt("number-clientActors")
	var numberOfEventTickets = ConfigFactory.load.getInt("number-TicketsA")
	var numberOfTicketsNeededPerKiosk = ConfigFactory.load.getInt("number-TicketsPerKiosk")
	var listOfKioskActorRefs = new ListBuffer[ActorRef]()
	var listOfClientsActorRefs = new ListBuffer[ActorRef]()	
	var leftActorNeighbor = self
	var rightActorNeighbor = self

	for (i <- 1 to numberKioskActors) {
		listOfKioskActorRefs += context.actorOf(Props[KioskActor], name = "Kiosk"+i)
	}


	for (i <- 0 to listOfKioskActorRefs.size-1) {
		if (i == 0) {
			listOfKioskActorRefs(i) ! Neighbors(self, listOfKioskActorRefs(i+1))
		} else if (i == listOfKioskActorRefs.size-1) {
			listOfKioskActorRefs(i) ! Neighbors(listOfKioskActorRefs(i-1), self)
		} else {
			listOfKioskActorRefs(i) ! Neighbors(listOfKioskActorRefs(i-1), listOfKioskActorRefs(i+1))
		}		
	}


	leftActorNeighbor = listOfKioskActorRefs(listOfKioskActorRefs.size-1)
	rightActorNeighbor = listOfKioskActorRefs(0)


	def receive = {
		case Start => 
			var numberOfTicketsSentAround = numberOfTicketsNeededPerKiosk * numberKioskActors
			numberOfEventTickets = numberOfEventTickets - numberOfTicketsSentAround
			println(self.path.name + " sending " + numberOfTicketsSentAround + " tickets")
			rightActorNeighbor ! TicketsFromMaster(numberOfTicketsSentAround)
		case TicketsFromMaster(ticketsSentAround) =>
		var numberOfTicketsSentAround = numberOfTicketsNeededPerKiosk * numberKioskActors
			println(self.path.name + " receiving " + ticketsSentAround + " tickets.")
			if (ticketsSentAround > 0) {
				numberOfEventTickets = numberOfEventTickets + ticketsSentAround				
			}
			println(self.path.name + " has " + numberOfEventTickets + " tickets.")
			if (numberOfEventTickets > 0) {
				if (numberOfEventTickets > numberOfTicketsSentAround) {
					numberOfEventTickets = numberOfEventTickets - numberOfTicketsSentAround
					println(self.path.name + " sending " + numberOfTicketsSentAround + " tickets to " + rightActorNeighbor.path.name)
					rightActorNeighbor ! TicketsFromMaster(numberOfTicketsSentAround)
				} else if (numberOfEventTickets < numberOfTicketsSentAround) {
					numberOfEventTickets = numberOfEventTickets - numberOfEventTickets
					println(self.path.name + " sending " + numberOfEventTickets + " tickets to " + rightActorNeighbor.path.name)
					rightActorNeighbor ! TicketsFromMaster(numberOfEventTickets)
				}
			} else if (numberOfEventTickets == 0) {
				rightActorNeighbor ! NoMoreTickets
			}
		case NoMoreTickets => 
			println(self.path.name + ": sold out message delivered successfully.")
	}

	for (i <- 1 to numberClientActors) {
		listOfClientsActorRefs += context.actorOf(Props[ClientActor], name = "Client"+i)
		listOfClientsActorRefs(listOfClientsActorRefs.size-1) ! TheKioskActors(listOfKioskActorRefs)
	}

}