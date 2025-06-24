// 📌 Xử lý chuyển tab giữa các phần như Dashboard, Truyện, Người dùng...
document.querySelectorAll('.sidebar-nav a').forEach(link => {
    link.addEventListener('click', () => {
        // Ẩn toàn bộ section
        document.querySelectorAll('.section').forEach(section => section.classList.remove('active'));

        // Hiện section tương ứng với tab được click (theo data-tab)
        document.getElementById(link.getAttribute('data-tab')).classList.add('active');

        // Bỏ class active khỏi các link khác
        document.querySelectorAll('.sidebar-nav a').forEach(a => a.classList.remove('active'));

        // Thêm class active cho tab đang click
        link.classList.add('active');
    });
});

// 📌 Hiện một form (ví dụ: form thêm truyện)
function showForm(formId) {
    document.querySelectorAll('.form-section').forEach(form => form.classList.remove('active'));
    document.getElementById(formId).classList.add('active');
}

// 📌 Ẩn một form theo id
function hideForm(formId) {
    document.getElementById(formId).classList.remove('active');
}

// 📌 Lọc bảng theo từ khóa tìm kiếm
function filterTable(tableId, query) {
    const table = document.getElementById(tableId);
    const rows = table.getElementsByTagName('tr');

    for (let i = 1; i < rows.length; i++) { // bỏ dòng tiêu đề
        let text = rows[i].textContent.toLowerCase();
        rows[i].style.display = text.includes(query.toLowerCase()) ? '' : 'none';
    }
}

// 📌 Thiết lập phân trang cho các bảng
function setupPagination(tableId, itemsPerPage = 5) {
    const table = document.getElementById(tableId);
    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
    const pagination = document.getElementById(`${tableId}Pagination`);
    let currentPage = 1;

    // Hiển thị trang hiện tại
    function displayPage(page) {
        const start = (page - 1) * itemsPerPage;
        const end = start + itemsPerPage;

        for (let i = 0; i < rows.length; i++) {
            rows[i].style.display = (i >= start && i < end) ? '' : 'none';
        }
    }

    // Tạo các nút chuyển trang
    function setupButtons(totalPages) {
        pagination.innerHTML = '';

        for (let i = 1; i <= totalPages; i++) {
            const button = document.createElement('button');
            button.textContent = i;

            button.addEventListener('click', () => {
                currentPage = i;
                displayPage(currentPage);

                // Cập nhật nút active
                document.querySelectorAll('#' + tableId + 'Pagination button').forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');
            });

            if (i === 1) button.classList.add('active'); // Trang đầu tiên mặc định active
            pagination.appendChild(button);
        }
    }

    const totalPages = Math.ceil(rows.length / itemsPerPage);
    setupButtons(totalPages);
    displayPage(1); // Hiển thị trang đầu tiên
}

// 📌 Khởi tạo phân trang cho nhiều bảng
[
    'comicsTable', 'chaptersTable', 'usersTable',
    'reportsTable', 'transactionsTable',
    'commentsTable', 'ratingsTable', 'notificationsTable'
].forEach(id => setupPagination(id));

// 📊 Biểu đồ thống kê lượt xem theo truyện
const ctx = document.getElementById('viewChart').getContext('2d');
new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ['One Piece', 'Solo Leveling', 'Attack on Titan', 'Tower of God', 'Naruto', 'Berserk'],
        datasets: [{
            label: 'Lượt xem',
            data: [10000, 8000, 6500, 4200, 9000, 3800],
            backgroundColor: 'rgba(59, 130, 246, 0.6)',
            borderColor: 'rgba(59, 130, 246, 1)',
            borderWidth: 1
        }]
    },
    options: {
        scales: {
            y: { beginAtZero: true } // Bắt đầu từ 0
        }
    }
});

// 🔔 Thêm thông báo hiển thị ở góc
function addNotification(message, type = 'info') {
    const panel = document.getElementById('notificationPanel');
    const notif = document.createElement('div');
    notif.className = `notification ${type === 'success' ? 'success' : 'error'}`;
    notif.textContent = message;
    panel.appendChild(notif);

    // Tự động xóa sau 5 giây
    setTimeout(() => notif.remove(), 5000);
}

// ✅ Xử lý submit các form, hiển thị thông báo & ẩn form

// Thêm truyện
document.getElementById('addComicForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Truyện đã được thêm thành công!', 'success');
    hideForm('add-comic');
});

// Sửa truyện
document.getElementById('editComicForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Truyện đã được cập nhật thành công!', 'success');
    hideForm('edit-comic-1');
});

// Thêm chương
document.getElementById('addChapterForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Chương đã được thêm thành công!', 'success');
    hideForm('add-chapter');
});

// Sửa chương
document.getElementById('editChapterForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Chương đã được cập nhật thành công!', 'success');
    hideForm('edit-chapter-1');
});

// Cập nhật người dùng
document.getElementById('editUserForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Thông tin người dùng đã được cập nhật!', 'success');
    hideForm('edit-user-1');
});

// Xử lý báo cáo
document.getElementById('resolveReportForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Báo cáo đã được xử lý!', 'success');
    hideForm('view-report-1');
});

// Cập nhật giao dịch
document.getElementById('updateTransactionForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Trạng thái giao dịch đã được cập nhật!', 'success');
    hideForm('view-transaction-1');
});

// Sửa bình luận
document.getElementById('editCommentForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Bình luận đã được cập nhật!', 'success');
    hideForm('edit-comment-1');
});

// Sửa đánh giá
document.getElementById('editRatingForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Đánh giá đã được cập nhật!', 'success');
    hideForm('edit-rating-1');
});

// Gửi thông báo
document.getElementById('addNotificationForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Thông báo đã được gửi!', 'success');
    hideForm('add-notification');
});

// Sửa thông báo
document.getElementById('editNotificationForm1')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Thông báo đã được cập nhật!', 'success');
    hideForm('edit-notification-1');
});

// Cập nhật cài đặt
document.getElementById('settingsForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    addNotification('Cài đặt đã được lưu!', 'success');
});

// ⚙️ Các hành động quản trị

// Xoá truyện
function deleteComic(id) {
    if (confirm('Bạn có chắc muốn xóa truyện này?')) {
        addNotification('Truyện đã được xóa!', 'success');
        document.querySelector(`#comicsTable tr[data-id="${id}"]`)?.remove();
    }
}

// Xoá chương
function deleteChapter(id) {
    if (confirm('Bạn có chắc muốn xóa chương này?')) {
        addNotification('Chương đã được xóa!', 'success');
        document.querySelector(`#chaptersTable tr[data-id="${id}"]`)?.remove();
    }
}

// Khoá hoặc mở khoá người dùng
function banUser(id, action) {
    if (confirm(`Bạn có chắc muốn ${action === 'ban' ? 'khóa' : 'mở khóa'} người dùng này?`)) {
        addNotification(`Người dùng đã bị ${action === 'ban' ? 'khóa' : 'mở khóa'}!`, 'success');
        const row = document.querySelector(`#usersTable tr[data-id="${id}"]`);
        if (row) {
            const statusCell = row.cells[5];
            statusCell.textContent = action === 'ban' ? 'Khóa' : 'Hoạt động';
        }
    }
}

// Đánh dấu báo cáo là đã xử lý
function resolveReport(id, action) {
    if (confirm('Bạn có chắc muốn giải quyết báo cáo này?')) {
        addNotification('Báo cáo đã được giải quyết!', 'success');
        const row = document.querySelector(`#reportsTable tr[data-id="${id}"]`);
        if (row) {
            row.cells[4].textContent = 'Đã giải quyết';
        }
    }
}

// Xoá bình luận
function deleteComment(id) {
    if (confirm('Bạn có chắc muốn xóa bình luận này?')) {
        addNotification('Bình luận đã được xóa!', 'success');
        document.querySelector(`#commentsTable tr[data-id="${id}"]`)?.remove();
    }
}

// Xoá đánh giá
function deleteRating(id) {
    if (confirm('Bạn có chắc muốn xóa đánh giá này?')) {
        addNotification('Đánh giá đã được xóa!', 'success');
        document.querySelector(`#ratingsTable tr[data-id="${id}"]`)?.remove();
    }
}

// Xoá thông báo
function deleteNotification(id) {
    if (confirm('Bạn có chắc muốn xóa thông báo này?')) {
        addNotification('Thông báo đã được xóa!', 'success');
        document.querySelector(`#notificationsTable tr[data-id="${id}"]`)?.remove();
    }
}

// ✨ Gửi lời chào khi vào trang admin
setTimeout(() => addNotification('Chào mừng đến với AsuraComic Admin!', 'success'), 1000);
