package mykim.democommerce.web.controller;

import lombok.RequiredArgsConstructor;
import mykim.democommerce.data.domain.Member;
import mykim.democommerce.data.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(member -> ResponseEntity.ok(member))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        Member savedMember = memberRepository.save(member);
        return ResponseEntity.ok(savedMember);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return memberRepository.findById(id)
                .map(existingMember -> {
                    Member updatedMember = Member.builder()
                            .id(existingMember.getId())
                            .memberId(member.getMemberId())
                            .password(member.getPassword())
                            .email(member.getEmail())
                            .name(member.getName())
                            .build();
                    return ResponseEntity.ok(memberRepository.save(updatedMember));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
