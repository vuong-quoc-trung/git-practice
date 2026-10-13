package unit_test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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
    @Test
    void testGetAllMembers() {
        // 1. Arrange
        GymMember member1 = new GymMember();
        member1.setId(1L);
        member1.setFullName("Nguyen Van A");
        member1.setEmail("trung@example.com");
        member1.setPhone("0912345678");
        member1.setPlanType(MembershipPlan.BASIC);
        member1.setStartDate(today);
        member1.setEndDate(today.plusMonths(1));

        GymMember member2 = new GymMember();
        member2.setId(2L);
        member2.setFullName("Tran Van B");
        member2.setEmail("hieu@example.com");
        member2.setPhone("0987654321");
        member2.setPlanType(MembershipPlan.PREMIUM);
        member2.setStartDate(today);
        member2.setEndDate(today.plusMonths(1));

        when(gymMemberRepository.findAll()).thenReturn(List.of(member1, member2));

        // 2. Act
        List<GymMemberResponse> responses = gymMemberService.getAllMembers();

        // 3. Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Nguyen Van A", responses.get(0).getFullName());
        assertEquals("Tran Van B", responses.get(1).getFullName());
        assertEquals("trung@example.com", responses.get(0).getEmail());
        assertEquals("hieu@example.com", responses.get(1).getEmail());
        assertEquals("0912345678", responses.get(0).getPhone());
        assertEquals("0987654321", responses.get(1).getPhone());
        assertEquals(MembershipPlan.BASIC, responses.get(0).getPlanType());
        assertEquals(MembershipPlan.PREMIUM, responses.get(1).getPlanType());
        verify(gymMemberRepository, times(1)).findAll();
    }

    @Test
    void testGetAllMembers_WhenNoMembersExist() {
        // 1. Arrange: Giả lập database chưa có hội viên nào
        when(gymMemberRepository.findAll()).thenReturn(List.of());

        // 2. Act
        List<GymMemberResponse> responses = gymMemberService.getAllMembers();

        // 3. Assert: Danh sách trả về không được null và phải rỗng
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(gymMemberRepository, times(1)).findAll();
    }
}