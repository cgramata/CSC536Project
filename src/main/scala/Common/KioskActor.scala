package Common

import akka.actor._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import Common._

class KioskActor extends Actor with Stash with ActorLogging{

	var numberTicketsInKiosk = 0
	var numberOfTicketsNeededPerKiosk = ConfigFactory.load.getInt("number-TicketsPerKiosk")
	var isEventSoldOut = false
	var leftActorNeighbor = self
	var rightActorNeighbor = self
	var requestingClient = self

	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftActorNeighbor = leftNeighbor
			rightActorNeighbor = rightNeighbor
		case NoMoreTickets => 
			isEventSoldOut = true
			rightActorNeighbor ! NoMoreTickets
		case TicketsFromMaster(ticketsSentAround) => {
			context.become(waiting, discardOld = false)
			Future{
				var ticketsNeededFromMaster = numberOfTicketsNeededPerKiosk - numberTicketsInKiosk
				if (ticketsNeededFromMaster != 0 && ticketsSentAround >= ticketsNeededFromMaster) {
					println(self.path.name + " needs and takes " + ticketsNeededFromMaster)
					numberTicketsInKiosk = numberTicketsInKiosk + ticketsNeededFromMaster
					var newTicketsSentAroundAmount = ticketsSentAround - ticketsNeededFromMaster
					rightActorNeighbor ! TicketsFromMaster(newTicketsSentAroundAmount)
					println(self.path.name + " sending " + newTicketsSentAroundAmount + " to " + rightActorNeighbor.path.name)
				}
				if (ticketsNeededFromMaster == 0) {
					var newTicketsSentAroundAmount = ticketsSentAround - ticketsNeededFromMaster
					rightActorNeighbor ! TicketsFromMaster(newTicketsSentAroundAmount)
					println(self.path.name + " doesn't need any tickets, sending " + newTicketsSentAroundAmount + " to " + rightActorNeighbor.path.name)
				}
			}.onComplete{
				case Failure(e) =>
					log.error(e, "Error occured at case Start MasterActor")
					self ! ResumeMessageProcessing
				case Success(v) =>
					self ! ResumeMessageProcessing
			}
		}
		case CanIHaveMoreTickets => {
			if (numberTicketsInKiosk > 1) {
				numberTicketsInKiosk = numberTicketsInKiosk - 1
				var giveNeighborTicket = 1
				sender ! giveNeighborTicket
			} else {
				sender ! IHaveNothing
			}
		}
		case IHaveNothing => {
			requestingClient ! ComeBackLater
		}
		case BuyTicket => {
			requestingClient = sender
			context.become(waiting, discardOld = false)
			Future{
				if (numberTicketsInKiosk != 0 && isEventSoldOut != true) {
					numberTicketsInKiosk = numberTicketsInKiosk - 1
					println(self.path.name + " selling ticket to " + sender.path.name)
					sender ! TicketSold
				} else if (numberTicketsInKiosk == 0 && isEventSoldOut == true) {
					println(self.path.name + " to " + sender.path.name + ": sorry, all sold out.")
					sender ! NoMoreTickets
				} else if (numberTicketsInKiosk == 0 && isEventSoldOut != false) {
					sender ! ComeBackLater
					rightActorNeighbor ! CanIHaveMoreTickets
				}
			}.onComplete{
				case Failure(e) =>
					log.error(e, "Error occured at case Start MasterActor")
					self ! ResumeMessageProcessing
				case Success(v) =>
					self ! ResumeMessageProcessing
			}		
		}
	}
	def waiting: Receive = {
		case ResumeMessageProcessing => 
			context.unbecome()
			unstashAll()
		case _ => 
			stash()
	}
	

}

