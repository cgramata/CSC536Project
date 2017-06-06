package Application 

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import Common._

object TicketKioskMain extends App {

	val config = ConfigFactory.parseString("""
		akka {
			actor {
				queued-dispatcher {
					mailbox-type ="akka.dispatch.UnboundedDequeueBasedMailbox"
				}
			}
		}""")

	val system = ActorSystem("ticketKiosk", ConfigFactory.load(config))

	val master = system.actorOf(Props[MasterActor], name = "master")

	master ! Start

	Thread.sleep(2000)

	system.terminate
}