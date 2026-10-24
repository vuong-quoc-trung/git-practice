package unit_test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.constraints.AssertTrue;
import unit_test.dto.GymMemberRequest;
import unit_test.dto.GymMemberResponse;
import unit_test.exception.BadRequestException;
import unit_test.exception.DuplicateResourceException;
import unit_test.exception.ResourceNotFoundException;
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

    @Test
    void testGetMemberById_Success() {
        // 1. Arrange: Chuẩn bị dữ liệu hội viên mẫu và mock repository tìm thấy hội viên theo ID
        GymMember member = new GymMember(
                1L,
                "Nguyen Van A",
                "an@example.com",
                "0905123456",
                MembershipPlan.BASIC,
                today,
                today.plusMonths(1).minusDays(1)
        );

        when(gymMemberRepository.findById(1L)).thenReturn(Optional.of(member));

        // 2. Act: Gọi phương thức lấy chi tiết hội viên theo ID
        GymMemberResponse response = gymMemberService.getMemberById(1L);

        // 3. Assert: Kiểm tra đầy đủ thông tin trả về
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Nguyen Van A", response.getFullName());
        assertEquals("an@example.com", response.getEmail());
        assertEquals("0905123456", response.getPhone());
        assertEquals(MembershipPlan.BASIC, response.getPlanType());
        assertEquals(today, response.getStartDate());
        assertEquals(today.plusMonths(1).minusDays(1), response.getEndDate());

        verify(gymMemberRepository, times(1)).findById(1L);
    }

    @Test
    void testGetMemberById_ThrowsException_WhenNotFound() {
        // 1. Arrange: Giả lập repository không tìm thấy ID = 999L
        when(gymMemberRepository.findById(999L)).thenReturn(Optional.empty());

        // 2 & 3. Act & Assert: Kỳ vọng ném ngoại lệ ResourceNotFoundException
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            gymMemberService.getMemberById(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
        verify(gymMemberRepository, times(1)).findById(999L);
    }

    @Test 
    void updateMember_Success() {
        // 1. Arrange: 
        GymMember oldMember = new GymMember(
                1L,
                "Nguyen Van A",
                "an@example.com",
                "0905123456",
                MembershipPlan.BASIC,
                today,
                today.plusMonths(1).minusDays(1)
        );

        GymMemberRequest updateRequest = new GymMemberRequest();
        updateRequest.setFullName("Nguyen Van Update");
        updateRequest.setEmail("an.new@example.com");
        updateRequest.setPhone("0999888777");
        updateRequest.setPlanType(MembershipPlan.PREMIUM);

        when(gymMemberRepository.findById(1L)).thenReturn(Optional.of(oldMember));
        when(gymMemberRepository.existsByEmailAndIdNot(updateRequest.getEmail(), 1L)).thenReturn(false);
        when(gymMemberRepository.save(any(GymMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        GymMemberResponse response = gymMemberService.updateMember(1L, updateRequest);

        // 3. Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Nguyen Van Update", response.getFullName());
        assertEquals("an.new@example.com", response.getEmail());
        assertEquals("0999888777", response.getPhone());
        assertEquals(MembershipPlan.PREMIUM, response.getPlanType());
        assertEquals(today, response.getStartDate());
        assertEquals(today.plusMonths(1).minusDays(1), response.getEndDate());

        verify(gymMemberRepository, times(1)).findById(1L);
        verify(gymMemberRepository, times(1)).save(any(GymMember.class));
    }

    @Test
    void updateMember_ThrowsException_WhenMemberNotFound() {
        // 1. Arrange: ID = 999L không tồn tại trong hệ thống
        GymMemberRequest request = new GymMemberRequest();
        request.setFullName("Nguyen Van A");
        request.setEmail("an@example.com");
        request.setPhone("0905123456");
        request.setPlanType(MembershipPlan.BASIC);

        when(gymMemberRepository.findById(999L)).thenReturn(Optional.empty());

        // 2 & 3. Act & Assert: Kỳ vọng ném lỗi ResourceNotFoundException
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            gymMemberService.updateMember(999L, request);
        });

        assertTrue(ex.getMessage().contains("999"));
        verify(gymMemberRepository, times(1)).findById(999L);
        verify(gymMemberRepository, never()).save(any(GymMember.class));
    }

    @Test
    void updateMember_ThrowsException_WhenEmailAlreadyExists() {
        // 1. Arrange: Tìm thấy hội viên ID = 1L, nhưng email mới bị trùng với hội viên khác
        GymMember member = new GymMember(
                1L,
                "Nguyen Van A",
                "an@example.com",
                "0905123456",
                MembershipPlan.BASIC,
                today,
                today.plusMonths(1).minusDays(1)
        );

        GymMemberRequest request = new GymMemberRequest();
        request.setFullName("Nguyen Van A");
        request.setEmail("trung@example.com"); // Email bị trùng với người khác
        request.setPhone("0905123456");
        request.setPlanType(MembershipPlan.BASIC);

        when(gymMemberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(gymMemberRepository.existsByEmailAndIdNot("trung@example.com", 1L)).thenReturn(true);

        // 2 & 3. Act & Assert: Kỳ vọng ném lỗi DuplicateResourceException
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            gymMemberService.updateMember(1L, request);
        });

        assertTrue(ex.getMessage().contains("trung@example.com"));
        verify(gymMemberRepository, times(1)).findById(1L);
        verify(gymMemberRepository, never()).save(any(GymMember.class));
    }

    @Test 
    void deleteMember_Success() {
        // 1. Arrange: 
        GymMember member = new GymMember(
            1L,
            "Nguyen Van A",
            "an@example.com",
            "0905123456",
            MembershipPlan.BASIC,
            today,
            today.plusMonths(1).minusDays(1)
    );   

    when(gymMemberRepository.findById(1L)).thenReturn(Optional.of(member));

    // 2. Act
    gymMemberService.deleteMember(1L);

    // 3. Assert
    verify(gymMemberRepository, times(1)).findById(1L);
    verify(gymMemberRepository, times(1)).delete(member);
    }

    @Test 
    void isMemberActive_Active() {
            // 1. Arrange: 
        GymMember member = new GymMember(
            1L,
            "Nguyen Van A",
            "an@example.com",
            "0905123456",
            MembershipPlan.BASIC,
            today,
            today.plusMonths(1).minusDays(1)
    );   

    // 2. Act
    boolean isActive = gymMemberService.isMemberActive(member);

    // 3. Assert 
    assertTrue(isActive);
    }

    @Test
    void renewMembership_Success_WhenActive() {
        // 1. Arrange: Hội viên đang còn hạn
        LocalDate initialEndDate = today.plusMonths(1).minusDays(1);
        GymMember member = new GymMember(
                1L,
                "Nguyen Van A",
                "an@example.com",
                "0905123456",
                MembershipPlan.BASIC,
                today,
                initialEndDate
        );

        when(gymMemberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(gymMemberRepository.save(any(GymMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act: Gia hạn thêm 3 tháng
        int renewMonths = 3;
        GymMemberResponse response = gymMemberService.renewMembership(1L, renewMonths);

        // 3. Assert: 
        // Quy tắc 12: Nếu hội viên còn hạn, gia hạn nối tiếp: endDate mới = endDate cũ + số tháng gia hạn
        LocalDate expectedEndDate = initialEndDate.plusMonths(renewMonths);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(expectedEndDate, response.getEndDate());
        assertEquals(today, response.getStartDate()); // Ngày bắt đầu không bị thay đổi (Quy tắc 12, 14)

        verify(gymMemberRepository, times(1)).findById(1L);
        verify(gymMemberRepository, times(1)).save(any(GymMember.class));
    }

    @Test
    void renewMembership_Success_WhenExpired() {
        // 1. Arrange: Hội viên đã hết hạn từ trước (ví dụ hết hạn từ 1 tháng trước)
        LocalDate oldStartDate = today.minusMonths(2);
        LocalDate oldEndDate = today.minusMonths(1);
        GymMember member = new GymMember(
                1L,
                "Nguyen Van A",
                "an@example.com",
                "0905123456",
                MembershipPlan.BASIC,
                oldStartDate,
                oldEndDate
        );

        when(gymMemberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(gymMemberRepository.save(any(GymMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act: Gia hạn 2 tháng
        int renewMonths = 2;
        GymMemberResponse response = gymMemberService.renewMembership(1L, renewMonths);

        // 3. Assert:
        // Quy tắc 13: Nếu hội viên đã hết hạn, thời hạn mới tính từ ngày hiện tại: 
        // endDate mới = today + số tháng gia hạn - 1 ngày
        LocalDate expectedEndDate = today.plusMonths(renewMonths).minusDays(1);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(expectedEndDate, response.getEndDate());

        verify(gymMemberRepository, times(1)).findById(1L);
        verify(gymMemberRepository, times(1)).save(any(GymMember.class));
    }

    @Test
    void renewMembership_ThrowsException_WhenInvalidMonths() {
        // 1. Arrange & 2. Act & 3. Assert:
        // Quy tắc 11: Cho phép gia hạn từ 1 đến 12 tháng. Nếu số tháng < 1 hoặc > 12 -> lỗi BadRequestException
        assertThrows(BadRequestException.class, () -> {
            gymMemberService.renewMembership(1L, 0); // Nhỏ hơn 1
        });

        assertThrows(BadRequestException.class, () -> {
            gymMemberService.renewMembership(1L, -5); // Số âm
        });

        verify(gymMemberRepository, never()).save(any(GymMember.class));
    }

    @Test
    void renewMembership_ThrowsException_WhenMemberNotFound() {
        // 1. Arrange: Không tìm thấy hội viên ID = 999L
        when(gymMemberRepository.findById(999L)).thenReturn(Optional.empty());

        // 2 & 3. Act & Assert: Quy tắc 15 -> 404 ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> {
            gymMemberService.renewMembership(999L, 3);
        });

        verify(gymMemberRepository, times(1)).findById(999L);
        verify(gymMemberRepository, never()).save(any(GymMember.class));
    }
}