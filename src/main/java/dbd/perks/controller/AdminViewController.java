package dbd.perks.controller;

import dbd.perks.repository.PlayableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminViewController {

    private final PlayableRepository playableRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/playable")
    public String getPlayerData(Model model) {
        model.addAttribute("playables", playableRepository.findAll());

        return "playable";
    }
}
