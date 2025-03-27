package ro.mpp2025.domain;

public class Reservation extends Entity<Long>{

   String clientName;
   String clientPhone;
   Integer ticketCount;
   Trip trip;

   public Reservation(String clientName, String clientPhone, Integer ticketCount, Trip trip) {
       this.clientName = clientName;
       this.clientPhone = clientPhone;
       this.ticketCount = ticketCount;
       this.trip = trip;
   }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public Integer getTicketCount() {
        return ticketCount;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setClientName(String clientName) {
        clientName = clientName;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public String toString() { return clientName + "," + clientPhone + "," + ticketCount + "," + trip.getId(); }
}
