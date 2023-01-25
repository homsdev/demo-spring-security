package com.homsdev.springsecuritybasic.controller;

import com.homsdev.springsecuritybasic.domain.Card;
import com.homsdev.springsecuritybasic.domain.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardController {
    private CardRepository cardsRepository;

    @Autowired
    public CardController(CardRepository cardsRepository) {
        this.cardsRepository = cardsRepository;
    }


    @GetMapping("/card")
    public List<Card> getCardDetails(@RequestParam int id) {
        List<Card> cards = cardsRepository.findByCustomerId(id);
        if (cards != null) {
            return cards;
        } else {
            return null;
        }
    }
}
