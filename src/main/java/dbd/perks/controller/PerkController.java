package dbd.perks.controller;

import dbd.perks.domain.Perk;
import dbd.perks.service.PerkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PerkController {

    private final PerkService perkService;

    @GetMapping("/api/perks")
    public ResponseEntity<List<Perk>> findPerks() {
        List<Perk> perkList = perkService.getPerks();

        return ResponseEntity.ok()
                .body(perkList);
    }
}
