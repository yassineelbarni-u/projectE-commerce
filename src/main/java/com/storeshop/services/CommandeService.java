package com.storeshop.services;

import com.storeshop.entities.Commande;
import com.storeshop.entities.User;
import java.util.List;
import java.util.Map;

public interface CommandeService {

  Commande createOrder(User user, Map<Long, Integer> items);

  List<Commande> listUserOrders(User user);
}
