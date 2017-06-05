package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import Common._

class KioskActor extends Actor {

	var numberTicketsInKiosk = 0
	var numberOfTicketsNeededPerKiosk = ConfigFactory.load.getInt("number-TicketsPerKiosk")
	var isEventSoldOut = false
	var leftActorNeighbor = self
	var rightActorNeighbor = self

	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftActorNeighbor = leftNeighbor
			rightActorNeighbor = rightNeighbor
		case NoMoreTickets => 
			isEventSoldOut = true
			rightActorNeighbor ! NoMoreTickets
		case TicketsFromMaster(ticketsSentAround) =>
			var ticketsNeededFromMaster = numberOfTicketsNeededPerKiosk - numberTicketsInKiosk
			if (ticketsNeededFromMaster != 0 && ticketsSentAround >= ticketsNeededFromMaster) {
				println(self.path.name + " needs and takes " + ticketsNeededFromMaster)
				numberTicketsInKiosk = numberTicketsInKiosk + ticketsNeededFromMaster
				var newTicketsSentAroundAmount = ticketsSentAround - ticketsNeededFromMaster
				println(self.path.name + " sending " + newTicketsSentAroundAmount + " to " + rightActorNeighbor.path.name)
				rightActorNeighbor ! TicketsFromMaster(newTicketsSentAroundAmount)
			}
			if (ticketsNeededFromMaster == 0) {
				var newTicketsSentAroundAmount = ticketsSentAround - ticketsNeededFromMaster
				println(self.path.name + " doesn't need any tickets, sending " + newTicketsSentAroundAmount + " to " + rightActorNeighbor.path.name)
				rightActorNeighbor ! TicketsFromMaster(newTicketsSentAroundAmount)
			}
		case BuyTicket => 
			numberTicketsInKiosk = numberTicketsInKiosk - 1
			println(self.path.name + " selling ticket to " + sender.path.name)
			sender ! TicketSold
	}

}

