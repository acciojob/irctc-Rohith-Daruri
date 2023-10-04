package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.controllers.TicketController;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db
        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
        List<Ticket>ticketList = train.getBookedTickets();
        int reservedSeats = 0;
        for(Ticket ticket:ticketList){
            reservedSeats += ticket.getPassengersList().size();
        }
        int remSeats = train.getNoOfSeats()-reservedSeats;
        if(remSeats<bookTicketEntryDto.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }
        String arr[] = train.getRoute().split(",");
        Ticket ticket = new Ticket();
        String from = bookTicketEntryDto.getFromStation().toString();
        String to = bookTicketEntryDto.getToStation().toString();
        int x = -1;
        int y = -1;
        for(int i=0;i<arr.length;i++){
            if(arr[i].equals(from)){
                x =i;
                break;
            }
        }
        for(int i=0;i<arr.length;i++){
            if(arr[i].equals(to)){
                y =i;
                break;
            }
        }

        if(x==-1 || y==-1){
            throw new Exception("Invalid stations");
        }
        int fare = (y-x)*300;
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        List<Passenger>passengerList  =new ArrayList<>();
        List<Integer>id = bookTicketEntryDto.getPassengerIds();
        for(int i:id){
            Passenger passenger = passengerRepository.findById(i).get();
            passengerList.add(passenger);
        }
        ticket.setPassengersList(passengerList);
        ticket.setTrain(train);
        ticket.setTotalFare(bookTicketEntryDto.getNoOfSeats()*fare);
        Ticket savedticket = ticketRepository.save(ticket);
        train.getBookedTickets().add(savedticket);
        Passenger passenger  = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        passenger.getBookedTickets().add(savedticket);
        trainRepository.save(train);
        passengerRepository.save(passenger);
        return savedticket.getTicketId();
//       return null;

    }
}
