package dbd.perks.controller;

import dbd.perks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminViewController {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;
    private final AddonRepository addonRepository;
    private final OfferingRepository offeringRepository;
    private final ItemRepository itemRepository;
    private final WeaponRepository weaponRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/playable")
    public String getPlayableData(Model model) {
        model.addAttribute("playables", playableRepository.findAll());

        return "playable";
    }

    @GetMapping("/perk")
    public String getPerkData(Model model) {
        model.addAttribute("perks", perkRepository.findAll());

        return "perk";
    }

    @GetMapping("/addon")
    public String getAddonData(Model model) {
        model.addAttribute("addons", addonRepository.findAll());

        return "addon";
    }

    @GetMapping("/offering")
    public String getOfferingData(Model model) {
        model.addAttribute("offerings", offeringRepository.findAll());

        return "offering";
    }

    @GetMapping("/item")
    public String getItemData(Model model) {
        model.addAttribute("items", itemRepository.findAll());

        return "item";
    }

    @GetMapping("/weapon")
    public String getWeaponData(Model model) {
        model.addAttribute("weapons", weaponRepository.findAll());

        return "weapon";
    }
}
