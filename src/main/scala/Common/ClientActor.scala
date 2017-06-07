package Common

import akka.actor._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import Common._

class ClientActor extends Actor with Stash with ActorLogging{
	var listOfKioskActors = new ListBuffer[ActorRef]()
	val numberOfKioskActors = ConfigFactory.load.getInt("number-kioskActors")
	var isTheEventSoldOut = false
	val randomNumberGenerator = scala.util.Random


	def receive = {
		case TheKioskActors(theKioskActors) => {
			listOfKioskActors = theKioskActors
			Thread.sleep(50)
			context.become(waiting, discardOld = false)
			Future{
				var randomIndex = randomNumberGenerator.nextInt(numberOfKioskActors)
				
					listOfKioskActors(randomIndex) ! BuyTicket
					println(self.path.name + " bought ticket from " + listOfKioskActors(randomIndex).path.name)
				
			}.onComplete{
				case Failure(e) =>
					log.error(e, "Error occured at case Start MasterActor")
					self ! ResumeMessageProcessing
				case Success(v) =>
					self ! ResumeMessageProcessing
			}			
		}
		case TicketSold => {
			Thread.sleep(10)
			context.become(waiting, discardOld = false)
			Future{
				println("Received ticket from " + sender.path.name)
				var randomIndex = randomNumberGenerator.nextInt(numberOfKioskActors)
				
					listOfKioskActors(randomIndex) ! BuyTicket
					println(self.path.name + " bought ticket from " + listOfKioskActors(randomIndex).path.name)
				
			}.onComplete{
				case Failure(e) =>
					log.error(e, "Error occured at case Start MasterActor")
					self ! ResumeMessageProcessing
				case Success(v) =>
					self ! ResumeMessageProcessing
			}
		}
		case NoMoreTickets => 
			isTheEventSoldOut = false
			println("Awe shucks! " + sender.path.name + " I..." + self.path.name + ", will be back again!")
	}
	def waiting: Receive = {
		case ResumeMessageProcessing => 
			context.unbecome()
			unstashAll()
		case _ => 
			stash()
	}
}