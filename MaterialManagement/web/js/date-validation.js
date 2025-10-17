// Date validation functions for CreateExportRequest
function validateDeliveryDate(showError = false) {
    const deliveryDateInput = document.getElementById('deliveryDate');
    const dateError = document.getElementById('dateError');
    if (!deliveryDateInput) return true;

    // Nếu chưa nhập gì và không phải showError, thì không validate
    if (!deliveryDateInput.value && !showError) {
        deliveryDateInput.classList.remove('is-invalid');
        if (dateError) dateError.style.display = 'none';
        return true;
    }

    const selectedDate = new Date(deliveryDateInput.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    selectedDate.setHours(0, 0, 0, 0);

    if (deliveryDateInput.value && selectedDate < today) {
        if (showError) {
            deliveryDateInput.classList.add('is-invalid');
            if (dateError) dateError.style.display = 'block';
        } else {
            deliveryDateInput.classList.remove('is-invalid');
            if (dateError) dateError.style.display = 'none';
        }
        return false;
    } else {
        deliveryDateInput.classList.remove('is-invalid');
        if (dateError) dateError.style.display = 'none';
        return true;
    }
}

// Set minimum date to today
function setMinDate() {
    const deliveryDateInput = document.getElementById('deliveryDate');
    if (!deliveryDateInput) return;
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    const todayString = `${year}-${month}-${day}`;
    deliveryDateInput.min = todayString;
}

// Initialize date validation
function initDateValidation() {
    setMinDate();
    const deliveryDateInput = document.getElementById('deliveryDate');
    if (deliveryDateInput) {
        // Đảm bảo không có class is-invalid khi load trang
        deliveryDateInput.classList.remove('is-invalid');
        
        deliveryDateInput.addEventListener('blur', function() {
            validateDeliveryDate(true);
        });
        deliveryDateInput.addEventListener('change', function() {
            validateDeliveryDate(true);
        });
        deliveryDateInput.addEventListener('input', function() {
            validateDeliveryDate(false);
        });
    }
    const form = document.querySelector('form');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateDeliveryDate(true)) {
                e.preventDefault();
                alert('Please select a valid delivery date (cannot be in the past).');
                return false;
            }
        });
    }
}

// Export functions for use in other scripts
window.dateValidation = {
    validateDeliveryDate: validateDeliveryDate,
    setMinDate: setMinDate,
    initDateValidation: initDateValidation
}; 