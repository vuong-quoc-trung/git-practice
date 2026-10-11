package unit_test.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unit_test.dto.GymMemberRequest;
import unit_test.dto.GymMemberResponse;
import unit_test.exception.BadRequestException;
import unit_test.exception.DuplicateResourceException;
import unit_test.exception.ResourceNotFoundException;
import unit_test.model.GymMember;
import unit_test.repository.GymMemberRepository;

@Service
public class GymMemberService {

    private final GymMemberRepository memberRepository;
    private final Clock clock;

    public GymMemberService(GymMemberRepository memberRepository, Clock clock) {
        this.memberRepository = memberRepository;
        this.clock = clock;
    }

    @Transactional
    public GymMemberResponse createMember(GymMemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại trong hệ thống: " + request.getEmail());
        }

        LocalDate startDate = LocalDate.now(clock);
        LocalDate endDate = startDate.plusMonths(1);

        GymMember member = new GymMember();
        member.setFullName(request.getFullName().trim());
        member.setEmail(request.getEmail().trim().toLowerCase());
        member.setPhone(request.getPhone().trim());
        member.setPlanType(request.getPlanType());
        member.setStartDate(startDate);
        member.setEndDate(endDate);

        GymMember saved = memberRepository.save(member);
        return toResponse(saved);
    }

    public List<GymMemberResponse> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GymMemberResponse getMemberById(Long id) {
        GymMember member = findMemberById(id);
        return toResponse(member);
    }

    @Transactional
    public GymMemberResponse updateMember(Long id, GymMemberRequest request) {
        GymMember member = findMemberById(id);

        if (memberRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Email đã được sử dụng bởi hội viên khác: " + request.getEmail());
        }

        member.setFullName(request.getFullName().trim());
        member.setEmail(request.getEmail().trim().toLowerCase());
        member.setPhone(request.getPhone().trim());
        member.setPlanType(request.getPlanType());

        GymMember updated = memberRepository.save(member);
        return toResponse(updated);
    }

    @Transactional
    public void deleteMember(Long id) {
        GymMember member = findMemberById(id);
        memberRepository.delete(member);
    }

    @Transactional
    public GymMemberResponse renewMembership(Long id, int months) {
        if (months < 1 || months >= 12) {
            throw new BadRequestException("Số tháng gia hạn phải từ 1 đến 12 tháng.");
        }

        GymMember member = findMemberById(id);
        LocalDate today = LocalDate.now(clock);

        if (isMemberActive(member) && !member.getStartDate().isAfter(today)) {
            member.setEndDate(member.getEndDate().plusMonths(months));
        } else {
            LocalDate newEndDate = today.plusMonths(months).minusDays(1);
            member.setEndDate(newEndDate);
        }

        GymMember updated = memberRepository.save(member);
        return toResponse(updated);
    }

    public boolean isMemberActive(GymMember member) {
        if (member == null || member.getStartDate() == null || member.getEndDate() == null) {
            return false;
        }

        LocalDate today = LocalDate.now(clock);
        return !today.isBefore(member.getStartDate()) && today.isBefore(member.getEndDate());
    }

    public GymMember findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên với mã ID: " + id));
    }

    public GymMemberResponse toResponse(GymMember member) {
        boolean active = isMemberActive(member);
        long monthlyFee = member.getPlanType() != null ? member.getPlanType().getMonthlyFee() : 0L;

        return new GymMemberResponse(
                member.getId(),
                member.getFullName(),
                member.getEmail(),
                member.getPhone(),
                member.getPlanType(),
                member.getStartDate(),
                member.getEndDate(),
                active,
                monthlyFee
        );
    }
}
