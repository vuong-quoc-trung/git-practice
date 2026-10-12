package unit_test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unit_test.dto.GymMemberRequest;
import unit_test.dto.GymMemberResponse;
import unit_test.exception.DuplicateResourceException;
import unit_test.model.GymMember;
import unit_test.model.MembershipPlan;
import unit_test.repository.GymMemberRepository;
import unit_test.service.GymMemberService;

@ExtendWith(MockitoExtension.class)
public class GymMemberServiceTest {

    @Mock
    private GymMemberRepository gymMemberRepository;

    private GymMemberService gymMemberService;
    private Clock fixedClock;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        // Cố định thời gian test tại một ngày cụ thể (2026-10-12)
        today = LocalDate.of(2026, 10, 12);
        fixedClock = Clock.fixed(
                today.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );

        // Khởi tạo Service với Mock Repository và Fixed Clock
        gymMemberService = new GymMemberService(gymMemberRepository, fixedClock);
    }

    @Test
    void testCreateMember_Success() {
        // 1. Arrange: Chuẩn bị dữ liệu và hành vi giả lập (mock)
        GymMemberRequest request = new GymMemberRequest();
        request.setFullName("Nguyen Van An");
        request.setEmail("an@example.com");
        request.setPhone("0905123456");
        request.setPlanType(MembershipPlan.BASIC);

        when(gymMemberRepository.existsByEmail("an@example.com")).thenReturn(false);
        when(gymMemberRepository.save(any(GymMember.class))).thenAnswer(invocation -> {
            GymMember member = invocation.getArgument(0);
            member.setId(1L);
            return member;
        });

        // 2. Act: Gọi phương thức nghiệp vụ
        GymMemberResponse response = gymMemberService.createMember(request);

        // 3. Assert: Kiểm tra kết quả trả về
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Nguyen Van An", response.getFullName());
        assertEquals("an@example.com", response.getEmail());
        assertEquals("0905123456", response.getPhone());
        assertEquals(MembershipPlan.BASIC, response.getPlanType());

        // Kiểm tra theo Quy tắc 7: startDate phải bằng ngày hiện tại
        assertEquals(today, response.getStartDate());

        // Kiểm tra theo Quy tắc 8: endDate = startDate + 1 tháng - 1 ngày
        LocalDate expectedEndDate = today.plusMonths(1).minusDays(1);
        assertEquals(expectedEndDate, response.getEndDate());

        verify(gymMemberRepository, times(1)).save(any(GymMember.class));
    }

    @Test
    void testCreateMember_ThrowsException_WhenEmailAlreadyExists() {
        // 1. Arrange
        GymMemberRequest request = new GymMemberRequest();
        request.setFullName("Tran Van B");
        request.setEmail("trung@example.com");
        request.setPhone("0912345678");
        request.setPlanType(MembershipPlan.PREMIUM);

        when(gymMemberRepository.existsByEmail("trung@example.com")).thenReturn(true);

        // 2 & 3. Act & Assert: Kỳ vọng ném DuplicateResourceException
        assertThrows(DuplicateResourceException.class, () -> {
            gymMemberService.createMember(request);
        });

        verify(gymMemberRepository, never()).save(any(GymMember.class));
    }
}
