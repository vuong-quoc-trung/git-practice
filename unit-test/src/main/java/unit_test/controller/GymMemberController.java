package unit_test.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import unit_test.dto.GymMemberRequest;
import unit_test.dto.GymMemberResponse;
import unit_test.service.GymMemberService;

@RestController
@RequestMapping("/api/members")
public class GymMemberController {

    private final GymMemberService memberService;

    public GymMemberController(GymMemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<GymMemberResponse> createMember(@Valid @RequestBody GymMemberRequest request) {
        GymMemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GymMemberResponse>> getAllMembers() {
        List<GymMemberResponse> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymMemberResponse> getMemberById(@PathVariable Long id) {
        GymMemberResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymMemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody GymMemberRequest request) {
        GymMemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<GymMemberResponse> renewMembership(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int months) {
        GymMemberResponse response = memberService.renewMembership(id, months);
        return ResponseEntity.ok(response);
    }
}
