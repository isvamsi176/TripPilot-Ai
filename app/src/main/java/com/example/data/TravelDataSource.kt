package com.example.data

import com.example.model.*

object TravelDataSource {

    val destinations = listOf(
        DestinationInfo(
            id = "goa",
            name = "Goa",
            state = "Goa",
            tagLine = "Sun, Sand, Seafood & Heritage",
            description = "India's premier beach & leisure destination with Portuguese architecture, coastal nightlife, serene beaches and water sports.",
            bestSeason = "Nov – Mar (Peak: Dec)",
            avgTemp = "28°C",
            vibeTags = listOf("Beaches", "Nightlife", "Water Sports", "Seafood"),
            imageDrawableName = "dest_goa",
            topAttractions = listOf("Candolim & Baga Beach", "Aguada Fort", "Dudhsagar Waterfalls", "Old Goa Churches", "Anjuna Flea Market")
        ),
        DestinationInfo(
            id = "hyderabad",
            name = "Hyderabad",
            state = "Telangana",
            tagLine = "City of Pearls & Royal Nizami Flavors",
            description = "A vibrant blend of 400-year-old Nizami heritage, world-famous biryani, historic fortresses, and futuristic cyber hubs.",
            bestSeason = "Oct – Feb",
            avgTemp = "25°C",
            vibeTags = listOf("Heritage", "Biryani & Food", "Shopping", "Tech Hub"),
            imageDrawableName = "dest_hyderabad",
            topAttractions = listOf("Charminar & Laad Bazaar", "Golconda Fort Sound & Light", "Hussain Sagar Lake", "Chowmahalla Palace", "Ramoji Film City")
        ),
        DestinationInfo(
            id = "jaipur",
            name = "Jaipur",
            state = "Rajasthan",
            tagLine = "The Royal Pink City",
            description = "Royal Rajasthan splendour with grand hilltop forts, ornate pink sandstone palaces, and vibrant craft bazaars.",
            bestSeason = "Oct – Mar",
            avgTemp = "22°C",
            vibeTags = listOf("Royal Heritage", "Architecture", "Handicrafts", "Culture"),
            imageDrawableName = "dest_jaipur",
            topAttractions = listOf("Hawa Mahal", "Amber Fort & Palace", "City Palace", "Jal Mahal", "Nahargarh Sunset Point")
        ),
        DestinationInfo(
            id = "manali",
            name = "Manali",
            state = "Himachal Pradesh",
            tagLine = "Valley of the Gods & Snow Peaks",
            description = "Magnificent Himalayan alpine paradise with snow-capped peaks, pine forests, Beas river adventure, and mountain cafes.",
            bestSeason = "Oct – Jun (Snow: Dec-Feb)",
            avgTemp = "12°C",
            vibeTags = listOf("Snow Peaks", "Adventure", "Pine Valleys", "Trekking"),
            imageDrawableName = "dest_manali",
            topAttractions = listOf("Solang Valley Sports", "Rohtang Pass Snow", "Atal Tunnel", "Old Manali Cafes", "Jogini Waterfalls")
        )
    )

    fun getFlightsForRoute(origin: String, destination: String): List<FlightOption> {
        return listOf(
            FlightOption(
                id = "fl-1",
                airline = "IndiGo",
                flightNumber = "6E-512",
                originCode = origin.take(3).uppercase(),
                destCode = destination.take(3).uppercase(),
                departureTime = "06:15 AM",
                arrivalTime = "07:35 AM",
                durationText = "1h 20m",
                durationHours = 1.33,
                baggageAllowance = "15 kg Check-in + 7 kg Cabin",
                pricePerPersonInr = 3850,
                onTimeRating = "95%",
                cabinClass = "Economy Saver"
            ),
            FlightOption(
                id = "fl-2",
                airline = "Air India",
                flightNumber = "AI-824",
                originCode = origin.take(3).uppercase(),
                destCode = destination.take(3).uppercase(),
                departureTime = "11:30 AM",
                arrivalTime = "12:55 PM",
                durationText = "1h 25m",
                durationHours = 1.42,
                baggageAllowance = "25 kg Check-in + 8 kg Cabin (Complimentary Meal)",
                pricePerPersonInr = 4920,
                onTimeRating = "89%",
                cabinClass = "Economy Flexi"
            ),
            FlightOption(
                id = "fl-3",
                airline = "Akasa Air",
                flightNumber = "QP-1392",
                originCode = origin.take(3).uppercase(),
                destCode = destination.take(3).uppercase(),
                departureTime = "04:40 PM",
                arrivalTime = "06:05 PM",
                durationText = "1h 25m",
                durationHours = 1.42,
                baggageAllowance = "15 kg Check-in + 7 kg Cabin",
                pricePerPersonInr = 3450,
                onTimeRating = "93%",
                cabinClass = "Akasa Smart"
            ),
            FlightOption(
                id = "fl-4",
                airline = "Vistara / Premium",
                flightNumber = "UK-884",
                originCode = origin.take(3).uppercase(),
                destCode = destination.take(3).uppercase(),
                departureTime = "08:10 PM",
                arrivalTime = "09:35 PM",
                durationText = "1h 25m",
                durationHours = 1.42,
                baggageAllowance = "30 kg Check-in + 10 kg Cabin + Lounge Access",
                pricePerPersonInr = 7800,
                onTimeRating = "96%",
                cabinClass = "Business Class"
            )
        )
    }

    fun getTrainsForRoute(origin: String, destination: String): List<TrainOption> {
        return listOf(
            TrainOption(
                id = "tr-1",
                trainNumber = "17027",
                trainName = "HYB VSG Express (Direct)",
                fromStation = "HYB (Hyderabad Deccan)",
                toStation = "MAO (Madgaon Junction)",
                departureTime = "09:15 AM",
                arrivalTime = "05:45 AM (Next Day)",
                durationText = "20h 30m",
                durationHours = 20.5,
                travelClass = "3A (AC 3 Tier)",
                fareInr = 1380,
                availabilityStatus = "AVAILABLE 34",
                confirmationProbability = "Confirmed"
            ),
            TrainOption(
                id = "tr-2",
                trainNumber = "17027-2A",
                trainName = "HYB VSG Express",
                fromStation = "SC (Secunderabad)",
                toStation = "MAO (Madgaon Junction)",
                departureTime = "09:15 AM",
                arrivalTime = "05:45 AM (Next Day)",
                durationText = "20h 30m",
                durationHours = 20.5,
                travelClass = "2A (AC 2 Tier)",
                fareInr = 1980,
                availabilityStatus = "AVAILABLE 16",
                confirmationProbability = "Confirmed"
            ),
            TrainOption(
                id = "tr-3",
                trainNumber = "17027-SL",
                trainName = "HYB VSG Express (Super Budget)",
                fromStation = "HYB (Hyderabad Deccan)",
                toStation = "MAO (Madgaon Junction)",
                departureTime = "09:15 AM",
                arrivalTime = "05:45 AM (Next Day)",
                durationText = "20h 30m",
                durationHours = 20.5,
                travelClass = "SL (Sleeper Class)",
                fareInr = 510,
                availabilityStatus = "RAC 12",
                confirmationProbability = "High (91%)"
            ),
            TrainOption(
                id = "tr-4",
                trainNumber = "20601",
                trainName = "Vande Bharat Express Connector",
                fromStation = "Kacheguda",
                toStation = "Hubballi Jn (Fast Connect)",
                departureTime = "06:00 AM",
                arrivalTime = "02:30 PM",
                durationText = "8h 30m (+3h cab to Goa)",
                durationHours = 11.5,
                travelClass = "CC (AC Chair Car)",
                fareInr = 1540,
                availabilityStatus = "AVAILABLE 88",
                confirmationProbability = "Confirmed"
            )
        )
    }

    fun getBusesForRoute(origin: String, destination: String): List<BusOption> {
        return listOf(
            BusOption(
                id = "bus-1",
                operator = "Orange Tours & Travels",
                busType = "BharatBenz Multi-Axle AC Sleeper (2+1)",
                boardingPoint = "Ameerpet / Gachibowli",
                droppingPoint = "Panjim / Mapusa",
                departureTime = "05:30 PM",
                arrivalTime = "08:30 AM (Next Day)",
                durationText = "15h 00m",
                durationHours = 15.0,
                fareInr = 1650,
                rating = 4.6,
                seatsAvailable = 14
            ),
            BusOption(
                id = "bus-2",
                operator = "IntrCity SmartBus",
                busType = "Volvo 9600 AC Sleeper with Washroom",
                boardingPoint = "Miyapur / Kondapur",
                droppingPoint = "Panjim Bus Stand",
                departureTime = "06:45 PM",
                arrivalTime = "09:15 AM (Next Day)",
                durationText = "14h 30m",
                durationHours = 14.5,
                fareInr = 1950,
                rating = 4.8,
                seatsAvailable = 8
            ),
            BusOption(
                id = "bus-3",
                operator = "TSRTC Garuda Plus",
                busType = "Multi-Axle AC Semi-Sleeper",
                boardingPoint = "MGBS Bus Station",
                droppingPoint = "KTC Bus Stand Panaji",
                departureTime = "04:30 PM",
                arrivalTime = "08:00 AM (Next Day)",
                durationText = "15h 30m",
                durationHours = 15.5,
                fareInr = 1290,
                rating = 4.3,
                seatsAvailable = 22
            ),
            BusOption(
                id = "bus-4",
                operator = "Zingbus Electric SuperFast",
                busType = "Premium AC Sleeper + Live Tracking",
                boardingPoint = "Kukatpally / Hitec City",
                droppingPoint = "Mapusa / Calangute Circle",
                departureTime = "07:15 PM",
                arrivalTime = "09:45 AM (Next Day)",
                durationText = "14h 30m",
                durationHours = 14.5,
                fareInr = 1480,
                rating = 4.5,
                seatsAvailable = 19
            )
        )
    }

    fun getHotelsForDestination(destination: String, nights: Int): List<HotelOption> {
        return listOf(
            HotelOption(
                id = "ht-eco",
                name = "Zostel / Casa Seaside Backpacker Villa",
                location = "Anjuna Beach Rd, North Goa",
                starRating = 3,
                userRating = 4.6,
                tier = TravelTier.ECONOMY,
                pricePerNightInr = 1200,
                totalNightsCostInr = 1200 * nights,
                amenities = listOf("Free Fast Wi-Fi", "AC Dorm/Private Room", "Swimming Pool", "Communal Cafe", "Scooter Rental Desk"),
                cancellationPolicy = "Free cancellation up to 24 hrs before check-in",
                distanceInfo = "350 meters from Anjuna Beach",
                imageDrawableName = "dest_goa"
            ),
            HotelOption(
                id = "ht-bal",
                name = "BloomSuites / Lemon Tree Beach Resort",
                location = "Candolim Beach Promenade",
                starRating = 4,
                userRating = 4.7,
                tier = TravelTier.BALANCED,
                pricePerNightInr = 2850,
                totalNightsCostInr = 2850 * nights,
                amenities = listOf("Complimentary Buffet Breakfast", "Infinity Pool", "Private Balcony", "Cocktail Bar", "Free Beach Shuttle"),
                cancellationPolicy = "Free cancellation up to 48 hrs before check-in",
                distanceInfo = "5-minute walk to Candolim Beach",
                imageDrawableName = "dest_goa"
            ),
            HotelOption(
                id = "ht-lux",
                name = "Taj Fort Aguada / W Goa Luxury Beach Retreat",
                location = "Sinquerim / Vagator Cliff",
                starRating = 5,
                userRating = 4.9,
                tier = TravelTier.PREMIUM_LUXURY,
                pricePerNightInr = 8500,
                totalNightsCostInr = 8500 * nights,
                amenities = listOf("Private Ocean Beachfront", "Spa & Wellness", "Butler Service", "Gourmet Coastal Dining", "Airport Chauffeur Transfer"),
                cancellationPolicy = "Free cancellation up to 7 days before arrival",
                distanceInfo = "Direct private beachfront access",
                imageDrawableName = "dest_goa"
            )
        )
    }

    fun buildItinerary(destination: String, days: Int): List<DayItinerary> {
        return listOf(
            DayItinerary(
                dayNumber = 1,
                title = "Arrival, Check-in & Sun-drenched Coastal Welcome",
                morning = "Arrival in Goa via terminal; transfer to hotel, unpack & sip fresh tender coconut water.",
                afternoon = "Relax by Candolim beach, leisurely lunch with Goan fish curry or crispy paneer shashlik at shoreline shack.",
                evening = "Golden sunset walk along Sinquerim beach; explore Aguada Fort lighthouse vista.",
                dinnerSpot = "Fisherman's Wharf (Riverfront live music & authentic Goan cuisine)",
                insiderTip = "Rent a self-drive scooter (Activa ₹400/day) right outside Candolim market for cheap local commute.",
                dayExpenseEstimateInr = 1600
            ),
            DayItinerary(
                dayNumber = 2,
                title = "North Goa Heritage, Water Adventures & Vibrant Vibes",
                morning = "Early morning water sports at Calangute/Baga (Parasailing, jet ski, banana boat ride).",
                afternoon = "Traditional thali lunch at Ritz Classic; visit Reis Magos Fort and Chapora Fort ('Dil Chahta Hai' viewpoint).",
                evening = "Anjuna cliff sunset at Curlies or Shiva Valley; browse Bohemian artisan jewelry and spices.",
                dinnerSpot = "Britto's on Baga Beach (Fresh seafood & seaside candlelit tables)",
                insiderTip = "Book water sport combo packages directly on the beach instead of third-party agents to save ₹600/person.",
                dayExpenseEstimateInr = 2200
            ),
            DayItinerary(
                dayNumber = 3,
                title = "South Goa Tranquility, Heritage Churches & Spice Plantation",
                morning = "Scenic drive to Old Goa; marvel at Basilica of Bom Jesus and Se Cathedral's Portuguese heritage.",
                afternoon = "Authentic Sahakari Spice Plantation tour with organic traditional buffet lunch served on banana leaves.",
                evening = "Unwind at pristine Colva or Palolem beach; serene acoustic sunset.",
                dinnerSpot = "Martin's Corner, Betalbatim (Legendary Goan crab xec xec & bebinca dessert)",
                insiderTip = "Spices bought inside the plantation are 100% farm fresh — buy the green cardamom and cinnamon sticks.",
                dayExpenseEstimateInr = 1900
            ),
            DayItinerary(
                dayNumber = 4,
                title = "Mandovi River Cruise, Island Exploration & Night Markets",
                morning = "Ferry ride to Divar Island; cycle through quaint colorful Portuguese villas and peaceful paddy fields.",
                afternoon = "Fontainhas Latin Quarter heritage walk in Panaji; sample freshly baked poee bread and pastries.",
                evening = "Sunset river cruise on River Mandovi with Goan folk dance performances and DJ music.",
                dinnerSpot = "Viva Panjim (Heritage Indo-Portuguese boutique dining in Fontainhas)",
                insiderTip = "Latin Quarter photography is best between 4:30 PM - 5:30 PM when the pastel facades glow in warm light.",
                dayExpenseEstimateInr = 1800
            ),
            DayItinerary(
                dayNumber = 5,
                title = "Souvenir Shopping, Coastal Brunch & Return Journey",
                morning = "Morning beach yoga or last swim in the Arabian Sea; breakfast with Goan poi and cafe latte.",
                afternoon = "Pick up authentic cashews, feni, bebinca cake and beachwear from Panjim local market.",
                evening = "Check-out, head to airport/railway station; onboard return journey back home with unforgettable memories.",
                dinnerSpot = "Airport Lounge or Terminal Cafe",
                insiderTip = "Pre-weigh your luggage at the hotel lobby to avoid excess baggage fees at the airline counter.",
                dayExpenseEstimateInr = 1100
            )
        ).take(days)
    }

    val sampleDisruptions = listOf(
        TravelDisruption(
            id = "ds-1",
            title = "Train 17027 On-Time Status",
            severity = "Safe",
            message = "Hyderabad-Vasco Express is running on time. Expected platform #4 at Hyderabad Deccan.",
            recommendedAction = "Arrive 30 minutes before scheduled departure.",
            timeAgo = "12m ago"
        ),
        TravelDisruption(
            id = "ds-2",
            title = "Candolim Beach High Tide Notice",
            severity = "Notice",
            message = "Lifeguards advisory: Red flags posted between 6:30 PM and 8:00 PM due to seasonal high tide.",
            recommendedAction = "Enjoy sunset from shoreline cafes rather than entering deep water.",
            timeAgo = "35m ago"
        ),
        TravelDisruption(
            id = "ds-3",
            title = "Goa Dabolim Airport Road Maintenance",
            severity = "Alert",
            message = "Paving work on NH66 near Zuari bridge causing 15-minute traffic slowdowns.",
            recommendedAction = "Travel Planner Agent recommends departing 45 minutes earlier for airport returns.",
            timeAgo = "1h ago"
        )
    )
}
