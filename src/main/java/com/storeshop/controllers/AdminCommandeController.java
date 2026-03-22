package com.storeshop.controllers;

import com.storeshop.services.CommandeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

/*
    controller for admin to manage commandes, list all commandes and update their status
    and also to view details of a commande (not implemented yet)
*/

@Controller
@RequestMapping("/admin/commandes")
@AllArgsConstructor
public class AdminCommandeController {

  private final CommandeService commandeService;

  @GetMapping
  public String listOrders(Model model) {
    // return all commandes to the admin view
    model.addAttribute("orders", commandeService.listAllOrders());
    return "admin/commandes";
  }

  @PostMapping("/status")
  public String updateStatus(
      @RequestParam(name = "id") Long id, @RequestParam(name = "status") String status) {
    commandeService.updateStatus(id, status);
    return "redirect:/admin/commandes?success";
  }
}
