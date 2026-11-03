package com.example

import com.example.agent.TripOrchestrator
import com.example.model.AgentType
import com.example.model.TravelTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `verify trip orchestrator initial states`() {
        val orchestrator = TripOrchestrator()
        val agents = orchestrator.agentsState.value
        assertEquals(7, agents.size)
        assertTrue(agents.containsKey(AgentType.TRAVEL_PLANNER))
        assertTrue(agents.containsKey(AgentType.BUDGET_OPTIMIZER))
        assertTrue(agents.containsKey(AgentType.RAILWAY_BUS))

        // Check door-to-door calculation contains all three modes
        val d2d = orchestrator.doorToDoorComparisons.value
        assertEquals(3, d2d.size)
        assertTrue(d2d.any { it.mode == "TRAIN" })
        assertTrue(d2d.any { it.mode == "FLIGHT" })
        assertTrue(d2d.any { it.mode == "BUS" })
    }

    @Test
    fun `verify dynamic replanning reduces cost`() {
        val orchestrator = TripOrchestrator()
        val initialPlan = orchestrator.tierPlans.value.first { it.tier == TravelTier.BALANCED }

        // Replan with budget cut
        orchestrator.replanTrip(
            newBudgetInr = 18000,
            newDays = 4,
            newTravellers = 2,
            preferredTransport = "Train",
            preferredTier = TravelTier.ECONOMY
        )

        val replannedPlan = orchestrator.replannedTierPlan.value
        assertNotNull(replannedPlan)
        assertTrue(replannedPlan!!.breakdown.totalInr <= initialPlan.breakdown.totalInr)
    }

    @Test
    fun `verify booking and cancellation flow`() {
        val orchestrator = TripOrchestrator()
        val booking = orchestrator.createBooking(
            type = "Train",
            title = "17027 HYB VSG Express",
            routeOrLocation = "HYB ➔ MAO",
            amountInr = 2760,
            travellersText = "2 Travellers",
            seatOrRoom = "Berths 21, 24"
        )
        assertEquals("CONFIRMED", booking.status)
        assertTrue(booking.pnr.startsWith("TP-"))

        // Test cancel
        orchestrator.cancelBooking(booking.id)
        val cancelled = orchestrator.bookings.value.first { it.id == booking.id }
        assertEquals("CANCELLED", cancelled.status)
    }
}
