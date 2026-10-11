/**
 * Gym Master Management - Frontend Application Script
 * Vanilla JavaScript communicating with Spring Boot REST API
 */

document.addEventListener('DOMContentLoaded', () => {
    // API Endpoints
    const API_BASE = '/api/members';

    // State
    let membersList = [];
    let editingMemberId = null;
    let pendingDeleteMember = null;

    // DOM Elements - Dashboard Stats
    const totalMembersCountEl = document.getElementById('totalMembersCount');
    const activeMembersCountEl = document.getElementById('activeMembersCount');
    const expiredMembersCountEl = document.getElementById('expiredMembersCount');
    const backendStatusBadge = document.getElementById('backendStatusBadge');

    // DOM Elements - Form
    const memberForm = document.getElementById('memberForm');
    const formTitle = document.getElementById('formTitle');
    const formModeBadge = document.getElementById('formModeBadge');
    const memberIdInput = document.getElementById('memberId');
    const fullNameInput = document.getElementById('fullName');
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    const planTypeInput = document.getElementById('planType');
    const saveMemberBtn = document.getElementById('saveMemberBtn');
    const cancelEditBtn = document.getElementById('cancelEditBtn');

    // Form Error Elements
    const fullNameError = document.getElementById('fullNameError');
    const emailError = document.getElementById('emailError');
    const phoneError = document.getElementById('phoneError');
    const planTypeError = document.getElementById('planTypeError');

    // DOM Elements - Table & States
    const membersTable = document.getElementById('membersTable');
    const membersTableBody = document.getElementById('membersTableBody');
    const tableCountBadge = document.getElementById('tableCountBadge');
    const refreshBtn = document.getElementById('refreshBtn');
    const retryBtn = document.getElementById('retryBtn');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const emptyState = document.getElementById('emptyState');
    const serverErrorState = document.getElementById('serverErrorState');
    const serverErrorMessage = document.getElementById('serverErrorMessage');

    // DOM Elements - Toast Container
    const toastContainer = document.getElementById('toastContainer');

    // DOM Elements - Renew Modal
    const renewModal = document.getElementById('renewModal');
    const renewForm = document.getElementById('renewForm');
    const renewMemberId = document.getElementById('renewMemberId');
    const renewMemberSummary = document.getElementById('renewMemberSummary');
    const renewMonths = document.getElementById('renewMonths');
    const renewMonthsError = document.getElementById('renewMonthsError');
    const closeRenewModalBtn = document.getElementById('closeRenewModalBtn');
    const cancelRenewBtn = document.getElementById('cancelRenewBtn');

    // DOM Elements - Delete Modal
    const deleteModal = document.getElementById('deleteModal');
    const deleteMemberId = document.getElementById('deleteMemberId');
    const deleteMemberName = document.getElementById('deleteMemberName');
    const closeDeleteModalBtn = document.getElementById('closeDeleteModalBtn');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

    // Initialize
    loadMembers();

    // Event Listeners
    refreshBtn.addEventListener('click', () => loadMembers());
    retryBtn.addEventListener('click', () => loadMembers());
    memberForm.addEventListener('submit', handleFormSubmit);
    cancelEditBtn.addEventListener('click', resetForm);

    // Renew Modal Events
    renewForm.addEventListener('submit', handleRenewSubmit);
    closeRenewModalBtn.addEventListener('click', closeRenewModal);
    cancelRenewBtn.addEventListener('click', closeRenewModal);

    // Delete Modal Events
    closeDeleteModalBtn.addEventListener('click', closeDeleteModal);
    cancelDeleteBtn.addEventListener('click', closeDeleteModal);
    confirmDeleteBtn.addEventListener('click', handleConfirmDelete);

    // Close modal on backdrop click
    window.addEventListener('click', (e) => {
        if (e.target === renewModal) closeRenewModal();
        if (e.target === deleteModal) closeDeleteModal();
    });

    /**
     * Show Toast notification
     */
    function showToast(message, type = 'info', title = '') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        const iconMap = {
            success: '✅',
            error: '❌',
            info: 'ℹ️'
        };

        const defaultTitles = {
            success: 'Thành công',
            error: 'Lỗi',
            info: 'Thông báo'
        };

        const content = document.createElement('div');
        content.className = 'toast-content';

        const toastTitle = document.createElement('div');
        toastTitle.className = 'toast-title';
        toastTitle.textContent = `${iconMap[type] || 'ℹ️'} ${title || defaultTitles[type] || 'Thông báo'}`;

        const toastMsg = document.createElement('div');
        toastMsg.className = 'toast-message';
        toastMsg.textContent = message;

        content.appendChild(toastTitle);
        content.appendChild(toastMsg);

        const closeBtn = document.createElement('button');
        closeBtn.className = 'toast-close';
        closeBtn.innerHTML = '&times;';
        closeBtn.onclick = () => toast.remove();

        toast.appendChild(content);
        toast.appendChild(closeBtn);
        toastContainer.appendChild(toast);

        setTimeout(() => {
            if (toast.parentElement) {
                toast.remove();
            }
        }, 4500);
    }

    /**
     * Set Backend Connectivity status
     */
    function setBackendStatus(isOnline) {
        if (isOnline) {
            backendStatusBadge.className = 'status-indicator online';
            backendStatusBadge.innerHTML = '<span class="dot"></span> Backend sẵn sàng';
        } else {
            backendStatusBadge.className = 'status-indicator offline';
            backendStatusBadge.innerHTML = '<span class="dot"></span> Mất kết nối backend';
        }
    }

    /**
     * Clear field validation errors
     */
    function clearFormErrors() {
        fullNameError.textContent = '';
        emailError.textContent = '';
        phoneError.textContent = '';
        planTypeError.textContent = '';
        renewMonthsError.textContent = '';
    }

    /**
     * Fetch all members from REST API
     */
    async function loadMembers() {
        showLoadingState();
        clearFormErrors();

        try {
            const response = await fetch(API_BASE, {
                headers: { 'Accept': 'application/json' }
            });

            if (!response.ok) {
                throw new Error(`Máy chủ phản hồi với mã lỗi HTTP ${response.status}`);
            }

            const data = await response.json();
            membersList = data;
            setBackendStatus(true);
            updateDashboardStats();
            renderMembersTable();
        } catch (error) {
            console.error('Lỗi khi tải danh sách hội viên:', error);
            setBackendStatus(false);
            showErrorState(error.message || 'Không thể kết nối đến máy chủ Spring Boot');
            showToast('Không thể kết nối tới backend. Vui lòng kiểm tra ứng dụng.', 'error', 'Mất kết nối');
        }
    }

    /**
     * Update Dashboard Counters
     */
    function updateDashboardStats() {
        const total = membersList.length;
        const active = membersList.filter(m => m.active).length;
        const expired = total - active;

        totalMembersCountEl.textContent = total;
        activeMembersCountEl.textContent = active;
        expiredMembersCountEl.textContent = expired;
        tableCountBadge.textContent = `${total} hội viên`;
    }

    /**
     * Render Table
     */
    function renderMembersTable() {
        membersTableBody.innerHTML = '';

        if (membersList.length === 0) {
            showEmptyState();
            return;
        }

        hideAllStates();
        membersTable.classList.remove('hidden');

        membersList.forEach(member => {
            const row = document.createElement('tr');

            // ID
            const tdId = document.createElement('td');
            tdId.textContent = `#${member.id}`;
            row.appendChild(tdId);

            // Họ tên
            const tdName = document.createElement('td');
            const nameBold = document.createElement('strong');
            nameBold.textContent = member.fullName;
            tdName.appendChild(nameBold);
            row.appendChild(tdName);

            // Email
            const tdEmail = document.createElement('td');
            tdEmail.textContent = member.email;
            row.appendChild(tdEmail);

            // Phone
            const tdPhone = document.createElement('td');
            tdPhone.textContent = member.phone;
            row.appendChild(tdPhone);

            // Plan Type
            const tdPlan = document.createElement('td');
            const planBadge = document.createElement('span');
            planBadge.className = `plan-badge ${member.planType === 'PREMIUM' ? 'plan-premium' : 'plan-basic'}`;
            planBadge.textContent = member.planType;
            tdPlan.appendChild(planBadge);
            row.appendChild(tdPlan);

            // Start Date
            const tdStartDate = document.createElement('td');
            tdStartDate.textContent = formatDate(member.startDate);
            row.appendChild(tdStartDate);

            // End Date
            const tdEndDate = document.createElement('td');
            tdEndDate.textContent = formatDate(member.endDate);
            row.appendChild(tdEndDate);

            // Status
            const tdStatus = document.createElement('td');
            const statusBadge = document.createElement('span');
            if (member.active) {
                statusBadge.className = 'status-badge status-active';
                statusBadge.textContent = '● Còn hạn';
            } else {
                statusBadge.className = 'status-badge status-expired';
                statusBadge.textContent = '● Hết hạn';
            }
            tdStatus.appendChild(statusBadge);
            row.appendChild(tdStatus);

            // Actions
            const tdActions = document.createElement('td');
            tdActions.className = 'text-center';

            const actionContainer = document.createElement('div');
            actionContainer.className = 'action-buttons';

            // Edit button
            const editBtn = document.createElement('button');
            editBtn.className = 'btn btn-secondary btn-sm';
            editBtn.title = 'Sửa thông tin';
            editBtn.textContent = '✏️ Sửa';
            editBtn.addEventListener('click', () => startEditMember(member));

            // Renew button
            const renewBtn = document.createElement('button');
            renewBtn.className = 'btn btn-success btn-sm';
            renewBtn.title = 'Gia hạn gói';
            renewBtn.textContent = '➕ Gia hạn';
            renewBtn.addEventListener('click', () => openRenewModal(member));

            // Delete button
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'btn btn-danger btn-sm';
            deleteBtn.title = 'Xóa hội viên';
            deleteBtn.textContent = '🗑️ Xóa';
            deleteBtn.addEventListener('click', () => openDeleteModal(member));

            actionContainer.appendChild(editBtn);
            actionContainer.appendChild(renewBtn);
            actionContainer.appendChild(deleteBtn);
            tdActions.appendChild(actionContainer);
            row.appendChild(tdActions);

            membersTableBody.appendChild(row);
        });
    }

    /**
     * Start Edit Member Mode
     */
    function startEditMember(member) {
        editingMemberId = member.id;
        memberIdInput.value = member.id;
        fullNameInput.value = member.fullName;
        emailInput.value = member.email;
        phoneInput.value = member.phone;
        planTypeInput.value = member.planType;

        formTitle.textContent = 'Cập Nhật Hội Viên';
        formModeBadge.textContent = `ID #${member.id}`;
        formModeBadge.className = 'badge badge-warning';
        saveMemberBtn.querySelector('.btn-text').textContent = 'Lưu thay đổi';
        cancelEditBtn.classList.remove('hidden');

        clearFormErrors();
        memberForm.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    /**
     * Reset form back to Add Mode
     */
    function resetForm() {
        editingMemberId = null;
        memberIdInput.value = '';
        memberForm.reset();
        planTypeInput.value = 'BASIC';

        formTitle.textContent = 'Thêm Hội Viên Mới';
        formModeBadge.textContent = 'Tạo mới';
        formModeBadge.className = 'badge badge-primary';
        saveMemberBtn.querySelector('.btn-text').textContent = 'Lưu hội viên';
        cancelEditBtn.classList.add('hidden');

        clearFormErrors();
    }

    /**
     * Handle Create or Update Submit
     */
    async function handleFormSubmit(e) {
        e.preventDefault();
        clearFormErrors();

        const payload = {
            fullName: fullNameInput.value.trim(),
            email: emailInput.value.trim(),
            phone: phoneInput.value.trim(),
            planType: planTypeInput.value
        };

        // Basic client validation
        let hasError = false;
        if (!payload.fullName) {
            fullNameError.textContent = 'Họ tên không được để trống';
            hasError = true;
        }
        if (!payload.email) {
            emailError.textContent = 'Email không được để trống';
            hasError = true;
        }
        if (!payload.phone) {
            phoneError.textContent = 'Số điện thoại không được để trống';
            hasError = true;
        }
        if (hasError) return;

        const isUpdating = editingMemberId !== null;
        const targetUrl = isUpdating ? `${API_BASE}/${editingMemberId}` : API_BASE;
        const method = isUpdating ? 'PUT' : 'POST';

        saveMemberBtn.disabled = true;

        try {
            const response = await fetch(targetUrl, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (!response.ok) {
                handleApiError(response.status, data);
                return;
            }

            showToast(
                isUpdating ? `Cập nhật hội viên #${data.id} thành công!` : `Thêm mới hội viên ${data.fullName} thành công!`,
                'success'
            );
            resetForm();
            await loadMembers();
        } catch (error) {
            console.error('Lỗi khi gửi form:', error);
            showToast('Không thể gửi yêu cầu lên máy chủ. Vui lòng thử lại.', 'error');
        } finally {
            saveMemberBtn.disabled = false;
        }
    }

    /**
     * Handle API Error Response
     */
    function handleApiError(status, data) {
        if (status === 400) {
            if (data.validationErrors) {
                if (data.validationErrors.fullName) fullNameError.textContent = data.validationErrors.fullName;
                if (data.validationErrors.email) emailError.textContent = data.validationErrors.email;
                if (data.validationErrors.phone) phoneError.textContent = data.validationErrors.phone;
                if (data.validationErrors.planType) planTypeError.textContent = data.validationErrors.planType;
            }
            showToast(data.message || 'Dữ liệu không hợp lệ', 'error', 'Yêu cầu không hợp lệ (400)');
        } else if (status === 409) {
            emailError.textContent = data.message || 'Email này đã tồn tại';
            showToast(data.message || 'Email bị trùng lặp', 'error', 'Xung đột dữ liệu (409)');
        } else if (status === 404) {
            showToast(data.message || 'Không tìm thấy hội viên', 'error', 'Không tìm thấy (404)');
        } else {
            showToast(data.message || 'Đã xảy ra lỗi máy chủ', 'error', `Mã lỗi HTTP ${status}`);
        }
    }

    /**
     * Open Renewal Modal
     */
    function openRenewModal(member) {
        renewMemberId.value = member.id;
        renewMonths.value = 1;
        renewMonthsError.textContent = '';

        renewMemberSummary.innerHTML = '';
        const p1 = document.createElement('p');
        p1.innerHTML = `Hội viên: <strong>${escapeHtml(member.fullName)}</strong> (ID: #${member.id})`;
        const p2 = document.createElement('p');
        p2.innerHTML = `Gói tập: <strong>${escapeHtml(member.planType)}</strong> | Trạng thái: <strong>${member.active ? 'Còn hạn' : 'Hết hạn'}</strong>`;
        const p3 = document.createElement('p');
        p3.innerHTML = `Ngày hết hạn hiện tại: <strong>${formatDate(member.endDate)}</strong>`;

        renewMemberSummary.appendChild(p1);
        renewMemberSummary.appendChild(p2);
        renewMemberSummary.appendChild(p3);

        renewModal.classList.remove('hidden');
    }

    function closeRenewModal() {
        renewModal.classList.add('hidden');
    }

    /**
     * Handle Renewal Submit
     */
    async function handleRenewSubmit(e) {
        e.preventDefault();
        renewMonthsError.textContent = '';

        const id = renewMemberId.value;
        const months = parseInt(renewMonths.value, 10);

        if (isNaN(months) || months < 1 || months > 12) {
            renewMonthsError.textContent = 'Số tháng gia hạn phải từ 1 đến 12 tháng.';
            return;
        }

        const confirmBtn = document.getElementById('confirmRenewBtn');
        confirmBtn.disabled = true;

        try {
            const response = await fetch(`${API_BASE}/${id}/renew?months=${months}`, {
                method: 'POST',
                headers: { 'Accept': 'application/json' }
            });

            const data = await response.json();

            if (!response.ok) {
                renewMonthsError.textContent = data.message || 'Lỗi khi gia hạn gói tập';
                showToast(data.message || 'Gia hạn không thành công', 'error', `Lỗi HTTP ${response.status}`);
                return;
            }

            closeRenewModal();
            showToast(`Gia hạn thành công ${months} tháng cho hội viên #${id}! Ngày hết hạn mới: ${formatDate(data.endDate)}`, 'success');
            await loadMembers();
        } catch (error) {
            console.error('Lỗi khi gia hạn:', error);
            renewMonthsError.textContent = 'Mất kết nối đến máy chủ';
            showToast('Không thể kết nối đến máy chủ để gia hạn', 'error');
        } finally {
            confirmBtn.disabled = false;
        }
    }

    /**
     * Open Delete Modal
     */
    function openDeleteModal(member) {
        pendingDeleteMember = member;
        deleteMemberId.textContent = member.id;
        deleteMemberName.textContent = member.fullName;
        deleteModal.classList.remove('hidden');
    }

    function closeDeleteModal() {
        deleteModal.classList.add('hidden');
        pendingDeleteMember = null;
    }

    /**
     * Confirm Delete Member
     */
    async function handleConfirmDelete() {
        if (!pendingDeleteMember) return;

        confirmDeleteBtn.disabled = true;
        const id = pendingDeleteMember.id;
        const name = pendingDeleteMember.fullName;

        try {
            const response = await fetch(`${API_BASE}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok && response.status !== 204) {
                let errorData = {};
                try { errorData = await response.json(); } catch(_) {}
                showToast(errorData.message || 'Xóa hội viên thất bại', 'error');
                return;
            }

            closeDeleteModal();
            showToast(`Đã xóa hội viên ${name} (#${id}) thành công!`, 'success');

            // If we were editing this member, reset form
            if (editingMemberId === id) {
                resetForm();
            }

            await loadMembers();
        } catch (error) {
            console.error('Lỗi khi xóa hội viên:', error);
            showToast('Không thể kết nối đến máy chủ để xóa', 'error');
        } finally {
            confirmDeleteBtn.disabled = false;
        }
    }

    /**
     * UI State Helpers
     */
    function showLoadingState() {
        membersTable.classList.add('hidden');
        emptyState.classList.add('hidden');
        serverErrorState.classList.add('hidden');
        loadingIndicator.classList.remove('hidden');
    }

    function showEmptyState() {
        membersTable.classList.add('hidden');
        loadingIndicator.classList.add('hidden');
        serverErrorState.classList.add('hidden');
        emptyState.classList.remove('hidden');
    }

    function showErrorState(msg) {
        membersTable.classList.add('hidden');
        loadingIndicator.classList.add('hidden');
        emptyState.classList.add('hidden');
        serverErrorMessage.textContent = msg;
        serverErrorState.classList.remove('hidden');
    }

    function hideAllStates() {
        loadingIndicator.classList.add('hidden');
        emptyState.classList.add('hidden');
        serverErrorState.classList.add('hidden');
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const parts = dateStr.split('-');
        if (parts.length === 3) {
            return `${parts[2]}/${parts[1]}/${parts[0]}`;
        }
        return dateStr;
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }
});
