package com.vitor.events.controller;

import com.vitor.events.EventsApplication;
import com.vitor.events.model.Event;
import com.vitor.events.repo.EventRepo;
import com.vitor.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventService service;

    @PostMapping("/events")
    public Event addNewEvent(@RequestBody Event newEvent){
        return service.addNewEvent(newEvent);
    }

    @GetMapping("/events")
    public List<Event> getAllEvents(){
        return service.getAllEvents();
    }

    @GetMapping("/events/{prettyName}")
    public  Event getEventByPrettyName(@PathVariable String prettyName){
        return service.getByPrettyName(prettyName);
    }


}
